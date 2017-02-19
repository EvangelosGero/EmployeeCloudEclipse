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
@Table(name = "categ2", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Categ2.findAll", query = "SELECT c FROM Categ2 c")})
public class Categ2 implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "c2_id", nullable = false)
    private Integer c2Id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "c2_descr", nullable = false, length = 60)
    private String c2Descr;
    @Basic(optional = false)
    @NotNull
    @Column(name = "c2_parent_id", nullable = false)
    private Integer c2ParentId;

    public Categ2() {
    }

    public Categ2(Integer c2Id) {
        this.c2Id = c2Id;
    }

    public Categ2(Integer c2Id, String c2Descr, Integer c2ParentId) {
        this.c2Id = c2Id;
        this.c2Descr = c2Descr;
        this.c2ParentId = c2ParentId;
    }

    public Integer getC2Id() {
        return c2Id;
    }

    public void setC2Id(Integer c2Id) {
        this.c2Id = c2Id;
    }

    public String getC2Descr() {
        return c2Descr;
    }

    public void setC2Descr(String c2Descr) {
        this.c2Descr = c2Descr;
    }

    public Integer getC2ParentId() {
        return c2ParentId;
    }

    public void setC2ParentId(Integer c2ParentId) {
        this.c2ParentId = c2ParentId;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (c2Id != null ? c2Id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Categ2)) {
            return false;
        }
        Categ2 other = (Categ2) object;
        if ((this.c2Id == null && other.c2Id != null) || (this.c2Id != null && !this.c2Id.equals(other.c2Id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.Categ2[ c2Id=" + c2Id + " ]";
    }
    
}
