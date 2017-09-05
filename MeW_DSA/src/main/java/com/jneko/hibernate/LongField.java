package com.jneko.hibernate;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="LongField")
public class LongField implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", unique = true, nullable = false)
    private long ID;
    
    @Column(name="GID", unique = false, nullable = false)
    private long GID;
    
    @Column(name="optName", unique = false, nullable = false, length = 256)
    private String optName;

    @Column(name="optValue", unique = false, nullable = false)
    private Long optValue;  
    
    @ManyToMany(mappedBy = "longElements", fetch = FetchType.LAZY)
    private Set<DirectoryField> directories;

    /**
     * @return the ID
     */
    public long getID() {
        return ID;
    }

    /**
     * @param ID the ID to set
     */
    public void setID(long ID) {
        this.ID = ID;
    }

    /**
     * @return the GID
     */
    public long getGID() {
        return GID;
    }

    /**
     * @param GID the GID to set
     */
    public void setGID(long GID) {
        this.GID = GID;
    }

    /**
     * @return the optName
     */
    public String getOptName() {
        return optName;
    }

    /**
     * @param optName the optName to set
     */
    public void setOptName(String optName) {
        this.optName = optName;
    }

    /**
     * @return the optValue
     */
    public Long getOptValue() {
        return optValue;
    }

    /**
     * @param optValue the optValue to set
     */
    public void setOptValue(Long optValue) {
        this.optValue = optValue;
    }

    /**
     * @return the directories
     */
    public Set<DirectoryField> getDirectories() {
        return directories;
    }

    /**
     * @param directories the directories to set
     */
    public void setDirectories(Set<DirectoryField> directories) {
        this.directories = directories;
    }
}
