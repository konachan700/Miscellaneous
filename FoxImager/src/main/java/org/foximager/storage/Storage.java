package org.foximager.storage;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import org.foximager.dao.StorageFile;
import org.foximager.db.Hibernate;
import org.foximager.db.KeyValue;
import org.foximager.utils.Utils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/*******************************************************************
 * Когда-то я от такого метода хранения отказался. Но все оказалось сложнее, и пришлось его вернуть.
 * Хранить множество мелких файлов на диске крайне неудобно: бекап тормозит, копирование идет на порядок медленнее, торрент-файл с базой неприлично раздувает.
 *******************************************************************/
public class Storage {
    public static enum CryptType {
        AES256, AES128, None
    }
    
    public static enum ScrambleType {
        XOR, None
    }
    
    private final CryptType
            cryptType;
    
    private final ScrambleType
            scrambleType;
    
    private final StorageCrypto
            crypt;
    
    private final StorageScrambler
            scrambler;
    
    private final File 
            storagePath;
    
    private final KeyValue
            kvStorage;
    
    public Storage(String storageName) throws IOException, Exception {
        this(storageName, CryptType.AES128, ScrambleType.XOR);
    }
    
    public Storage(String storageName, CryptType ct, ScrambleType st) throws IOException, Exception {
        cryptType = ct;
        scrambleType = st;
        
        storagePath = new File(storageName);
        
        if (!storagePath.exists()) {
            storagePath.mkdirs();
            if (!storagePath.exists())
                throw new IOException("can't create storage directory!");
        }
        
        switch (scrambleType) {
            case XOR:
                scrambler = new StorageScrambler(storagePath);
                break;
            default:
                scrambler = null;
        }
        
        switch (cryptType) {
            case AES128:
                crypt = new StorageCrypto(cryptType);
                crypt.init(storagePath);
                break;
            case AES256:
                crypt = new StorageCrypto(cryptType);
                if (!crypt.isAES256Support())
                    throw new IOException("AES256 not supported!");
                crypt.init(storagePath);
                break;
            default:
                crypt = null;
        } 
        
        kvStorage = new KeyValue(storagePath);
        Hibernate.init(storagePath);
    }
    
    public final void dispose() throws IOException {
        Hibernate.dispose();
        kvStorage.dispose();
    }
    
    public final void openTransaction() {
        Hibernate.beginTransaction();
    }
    
    public final void commit() {
        Hibernate.endTransaction();
        kvStorage.commit();
    }
    
    public final StorageFile save(File f) throws IOException {
        if ((!f.exists()) || (!f.canRead()) || (f.isDirectory())) 
            throw new IOException("File not exist");
        
        final byte[] file = Utils.readFile(f);
        return save(file, f.getName());
    }
    
    public final StorageFile save(byte[] file, String fileName) throws IOException {
        if (file == null)
            throw new IOException("File cannot be null");
        
        final byte[] md5 = MD5Hash(file);
        
        final StorageFile sf = new StorageFile();
        sf.setBinaryMD5(md5);
        sf.setBinarySize(file.length);
        sf.setBinaryOffset((scrambleType != ScrambleType.None) ? new Random().nextInt() : 0);
        sf.setFileName(fileName);
        sf.setDeleted(false);
        sf.setEncrypted(cryptType != CryptType.None);
        
        if ((scrambler != null) && (scrambleType != ScrambleType.None))
            file = scrambler.encodeBytes(file, sf.getBinaryOffset());
        
        final byte[] out;
        if ((crypt != null) && (cryptType != CryptType.None))
            out = crypt.crypt(file);
        else 
            out = file;
        
        Hibernate.pushObjectInTransaction(sf);
        kvStorage.write(md5, out); 
        
        return sf;
    }
    
    public final int getRecordsCount() {
        final Number ret = (Number) Hibernate
                .getSession()
                .createCriteria(StorageFile.class)
                .setProjection(
                        Projections.rowCount()
                )
                .uniqueResult();
        return (ret == null) ? 0 : ret.intValue();
    }
    
    public final int getRecordsCount(Criterion crtrn) {
        final Number ret = (Number) Hibernate
                .getSession()
                .createCriteria(StorageFile.class)
                .add(crtrn)
                .setProjection(
                        Projections.rowCount()
                )
                .uniqueResult();
        return (ret == null) ? 0 : ret.intValue();
    }
    
    public final byte[] read(byte[] md5) throws IOException {
        final StorageFile sf = (StorageFile) Hibernate
                .getSession()
                .createCriteria(StorageFile.class)
                .add(
                        Restrictions.eq("binaryMD5", md5)
                )
                .uniqueResult();

        return postProcess(sf);
    }
    
    public final byte[] read(long id) throws IOException {
        final StorageFile sf = (StorageFile) Hibernate
                .getSession()
                .createCriteria(StorageFile.class)
                .add(
                        Restrictions.eq("ID", id)
                )
                .uniqueResult();
                
        return postProcess(sf);
    }
    
    private byte[] postProcess(StorageFile sf) throws IOException {
        if (sf == null) 
            throw new IOException("Element not exist!");
        
        final byte[] 
                outRaw = kvStorage.read(sf.getBinaryMD5()),
                outDecrypted, 
                out;
        
        if (sf.isEncrypted()) {
            if ((crypt != null) && (cryptType != CryptType.None))
                outDecrypted = crypt.decrypt(outRaw);
            else
                outDecrypted = outRaw;
        } else
            outDecrypted = outRaw;
        
        if ((scrambler != null) && (scrambleType != ScrambleType.None))
            out = scrambler.encodeBytes(outDecrypted, sf.getBinaryOffset());
        else
            out = outDecrypted;
        
        return out;
    }

    private byte[] MD5Hash(byte[] unsafe) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(unsafe);
            return md.digest();
        } catch (NoSuchAlgorithmException ex) { }
        return null;
    }
}
