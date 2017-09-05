package org.foximager.storage;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import org.foximager.utils.Utils;

/*************************************************************
 * АХТУНГ! 
 * Это не криптография! Не пытайтесь применить этот класс для шифрования чего-либо!
 * Сие кодирование нужно лишь для сокрытия контента от любопытных ботов и авто-индексаторов облачных хранилищ при отключеном шифровании.
 * Шифрование идет отдельным модулем, и включается самим пользователем по надобности.
 *************************************************************/
public class StorageScrambler {
    private static final int 
            scodeBlockSize      = 1024 * 16,
            scodeBlocksCount    = 8,
            scodeSize           = scodeBlockSize * scodeBlocksCount;
    
    private static final String 
            pathToStorage = "storage.scode";
    
    private final byte[][]
            scode;
    
    private final int
            bitlen;

    public StorageScrambler(File storageDir) throws IOException {
        scode = new byte[scodeBlocksCount][];
        bitlen = getBit1Len(scodeBlockSize - 1);
        if (bitlen <= 0)
            throw new RuntimeException("The \"scodeBlockSize\" fileld is incorrect!");
        
        final String scodeFilePath = storageDir.getAbsolutePath() + File.separator + pathToStorage;
        final File scodeFile = new File(scodeFilePath);
        if (scodeFile.exists() && scodeFile.canRead()) {
            byte[] scodeBinary = alignScode(Utils.readFile(scodeFile));
            for (int i=0; i<scodeBlocksCount; i++) {
                scode[i] = Arrays.copyOfRange(scodeBinary, i*scodeBlockSize, (i+1)*scodeBlockSize);
            }
        } else {
            final SecureRandom sr = new SecureRandom();
            final byte[] scodeOut = new byte[scodeSize];
            sr.nextBytes(scodeOut);
            for (int i=0; i<scodeBlocksCount; i++) {
                scode[i] = Arrays.copyOfRange(scodeOut, i*scodeBlockSize, (i+1)*scodeBlockSize);
            }
            Utils.writeFile(scodeFile, scodeOut); 
        }
    }

    public byte encodeByte(byte b, long offset) {
        byte pbyte = b;
        for (int i=0; i<scodeBlocksCount; i++) {
            final long loff = (offset >> (i * bitlen)) & (scodeBlockSize - 1);
            pbyte ^= scode[i][(int)(loff)]; 
        }
        
        return pbyte;
    }
    
    public byte[] encodeBytes(byte b[], long offset) {
        for (int i=0; i<b.length; i++)
            b[i] = encodeByte(b[i], offset + i);
        return b;
    }
    
    private int getBit1Len(long number) {
        long off = number;
        for (int i=0; i<64; i++) {
            if ((off & 0x01) == 0)
                return i-1;
            off = off >> 1;
        }
        return -1;
    }
    
    private byte[] alignScode(byte[] key) {
        if (key.length == scodeSize) 
            return key;
        else 
            return Arrays.copyOf(key, scodeSize);
    }
}
