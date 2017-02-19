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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "categ3", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Categ3.findAll", query = "SELECT c FROM Categ3 c")})
public class Categ3 implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "c3_id", nullable = false)
    private Integer c3Id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "c3_descr", nullable = false, length = 60)
    private String c3Descr;
    @Basic(optional = false)
    @NotNull
    @Column(name = "c3_parent_id", nullable = false)
    private Integer c3ParentId;

    public Categ3() {
    }

    public Categ3(Integer c3Id) {
        this.c3Id = c3Id;
    }

    public Categ3(Integer c3Id, String c3Descr, Integer c3ParentId) {
        this.c3Id = c3Id;
        this.c3Descr = c3Descr;
        this.c3ParentId = c3ParentId;
    }

    public Integer getC3Id() {
        return c3Id;
    }

    public void setC3Id(Integer c3Id) {
        this.c3Id = c3Id;
    }

    public String getC3Descr() {
        return c3Descr;
    }

    public void setC3Descr(String c3Descr) {
        this.c3Descr = c3Descr;
    }

    public Integer getC3ParentId() {
        return c3ParentId;
    }

    public void setC3ParentId(Integer c3ParentId) {
        this.c3ParentId = c3ParentId;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (c3Id != null ? c3Id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Categ3)) {
            return false;
        }
        Categ3 other = (Categ3) object;
        if ((this.c3Id == null && other.c3Id != null) || (this.c3Id != null && !this.c3Id.equals(other.c3Id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.Categ3[ c3Id=" + c3Id + " ]";
    }
    
}
