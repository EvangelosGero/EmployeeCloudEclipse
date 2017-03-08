/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "countries", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
public class Countries implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "code", nullable = false, length = 2)
    private String code;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "full_name", nullable = false, length = 255)
    private String fullName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 3)
    @Column(name = "iso3", nullable = false, length = 3)
    private String iso3;
    @Basic(optional = false)
    @NotNull
    @Column(name = "number", nullable = false)
    private short number;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "continent_code", nullable = false, length = 2)
    private String continentCode;
    @Basic(optional = false)
    @NotNull
    @Column(name = "in_EU", nullable = false)
    private short inEU;
    @Basic(optional = false)
    @NotNull
    @Column(name = "enabled", nullable = false)
    private Integer enabled;

    public Countries() {
    }

    public Countries(Integer id) {
        this.id = id;
    }

    public Countries(Integer id, String code, String name, String fullName, String iso3, short number, String continentCode, short inEU, Integer enabled) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.fullName = fullName;
        this.iso3 = iso3;
        this.number = number;
        this.continentCode = continentCode;
        this.inEU = inEU;
        this.enabled = enabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIso3() {
        return iso3;
    }

    public void setIso3(String iso3) {
        this.iso3 = iso3;
    }

    public short getNumber() {
        return number;
    }

    public void setNumber(short number) {
        this.number = number;
    }

    public String getContinentCode() {
        return continentCode;
    }

    public void setContinentCode(String continentCode) {
        this.continentCode = continentCode;
    }

    public short getInEU() {
        return inEU;
    }

    public void setInEU(short inEU) {
        this.inEU = inEU;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Countries)) {
            return false;
        }
        Countries other = (Countries) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.Countries[ id=" + id + " ]";
    }
    
}
