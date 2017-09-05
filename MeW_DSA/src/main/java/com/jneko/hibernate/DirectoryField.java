package com.jneko.hibernate;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="DirectoryField")
public class DirectoryField implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    @Column(name="ID", unique = true, nullable = false)
    private long ID;
    
    @Column(name="GID", unique = false, nullable = false)
    private long GID;
    
    @Column(name="optName", unique = false, nullable = false, length = 256)
    private String optName;

    @Column(name="optValue", unique = false, nullable = false, length = 2048)
    private String optValue;  
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="groups1",
        joinColumns=
            @JoinColumn(name="IDX"),
        inverseJoinColumns=
            @JoinColumn(name="IDY")
        )
    private Set<StringField> stringElements = new HashSet<>();
   
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="groups2",
        joinColumns=
            @JoinColumn(name="IDX"),
        inverseJoinColumns=
            @JoinColumn(name="IDY")
        )
    private Set<LongField> longElements = new HashSet<>();

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
    public String getOptValue() {
        return optValue;
    }

    /**
     * @param optValue the optValue to set
     */
    public void setOptValue(String optValue) {
        this.optValue = optValue;
    }

    /**
     * @return the stringElements
     */
    public Set<StringField> getStringElements() {
        return stringElements;
    }

    /**
     * @param stringElements the stringElements to set
     */
    public void setStringElements(Set<StringField> stringElements) {
        this.stringElements = stringElements;
    }

    /**
     * @return the longElements
     */
    public Set<LongField> getLongElements() {
        return longElements;
    }

    /**
     * @param longElements the longElements to set
     */
    public void setLongElements(Set<LongField> longElements) {
        this.longElements = longElements;
    }
}
