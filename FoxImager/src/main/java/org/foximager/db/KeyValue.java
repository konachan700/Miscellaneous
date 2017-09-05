package org.foximager.db;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

public class KeyValue {
    private final DB kvDatabase;
    private final ConcurrentMap<byte[], byte[]> dbMap;
    
    public KeyValue(File db) throws IOException {
        kvDatabase = DBMaker
                .fileDB(db.getAbsolutePath() + File.separator + "storage.kvs")
                .closeOnJvmShutdown()
                .fileMmapEnable()
                .transactionEnable()
                .make();
        
        dbMap = kvDatabase
                .hashMap("map", Serializer.BYTE_ARRAY, Serializer.BYTE_ARRAY)
                .createOrOpen();
    }
    
    public void dispose() throws IOException {
        kvDatabase.close();
    }
    
    public byte[] read(byte[] key) {
        return dbMap.get(key);
    }
    
    public byte[] read(byte[] key, byte[] defaultValue) {
        return dbMap.getOrDefault(key, defaultValue);
    }

    public void write(byte[] key, byte[] value) {
        dbMap.put(key, value);
    }
    
    public void commit() {
        kvDatabase.commit();
    }
    
    public void rollback() {
        kvDatabase.rollback();
    }
    
    public ConcurrentMap<byte[], byte[]> getRawMap() {
        return dbMap;
    }
}
