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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "zip_codes", catalog = "dynashoppingcart", schema = "", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"zp_code"})})
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ZipCodes.findAll", query = "SELECT z FROM ZipCodes z")})
public class ZipCodes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 6)
    @Column(name = "zp_code", nullable = false, length = 6)
    private String zpCode;
    @Size(max = 30)
    @Column(name = "zp_descr", length = 30)
    private String zpDescr;
    @Size(max = 2)
    @Column(name = "zpg", length = 2)
    private String zpg;
    @Size(max = 100)
    @Column(name = "zpg_descr", length = 100)
    private String zpgDescr;

    public ZipCodes() {
    }

    public ZipCodes(Integer id) {
        this.id = id;
    }

    public ZipCodes(Integer id, String zpCode) {
        this.id = id;
        this.zpCode = zpCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getZpCode() {
        return zpCode;
    }

    public void setZpCode(String zpCode) {
        this.zpCode = zpCode;
    }

    public String getZpDescr() {
        return zpDescr;
    }

    public void setZpDescr(String zpDescr) {
        this.zpDescr = zpDescr;
    }

    public String getZpg() {
        return zpg;
    }

    public void setZpg(String zpg) {
        this.zpg = zpg;
    }

    public String getZpgDescr() {
        return zpgDescr;
    }

    public void setZpgDescr(String zpgDescr) {
        this.zpgDescr = zpgDescr;
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
        if (!(object instanceof ZipCodes)) {
            return false;
        }
        ZipCodes other = (ZipCodes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.ZipCodes[ id=" + id + " ]";
    }
    
}
