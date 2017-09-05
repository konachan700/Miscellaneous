package org.foximager.dao;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class StorageFile implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", unique = true, nullable = false)
    private long ID;
    
    @Column(name="binaryMD5", unique = true, nullable = false, length = 16)
    private byte[] binaryMD5;
    
    @Column(name="binaryOffset", unique = false, nullable = false)
    private long binaryOffset;
    
    @Column(name="binarySize", unique = false, nullable = false)
    private long binarySize;
    
    @Column(name="fileName", unique = false, nullable = false, length = 2048)
    private String fileName;
    
    @Column(name="deleted", unique = false)
    private boolean deleted;
    
    @Column(name="encrypted", unique = false)
    private boolean encrypted;

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public byte[] getBinaryMD5() {
        return binaryMD5;
    }

    public void setBinaryMD5(byte[] binaryMD5) {
        this.binaryMD5 = binaryMD5;
    }

    public long getBinaryOffset() {
        return binaryOffset;
    }

    public void setBinaryOffset(long binaryOffset) {
        this.binaryOffset = binaryOffset;
    }

    public long getBinarySize() {
        return binarySize;
    }

    public void setBinarySize(long binarySize) {
        this.binarySize = binarySize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isEncrypted() {
        return encrypted;
    }

    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }
}
