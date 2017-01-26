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
@Table(name = "categ1", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Categ1.findAll", query = "SELECT c FROM Categ1 c")})
public class Categ1 implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "c1_id", nullable = false)
    private Integer c1Id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "c1_descr", nullable = false, length = 60)
    private String c1Descr;

    public Categ1() {
    }

    public Categ1(Integer c1Id) {
        this.c1Id = c1Id;
    }

    public Categ1(Integer c1Id, String c1Descr) {
        this.c1Id = c1Id;
        this.c1Descr = c1Descr;
    }

    public Integer getC1Id() {
        return c1Id;
    }

    public void setC1Id(Integer c1Id) {
        this.c1Id = c1Id;
    }

    public String getC1Descr() {
        return c1Descr;
    }

    public void setC1Descr(String c1Descr) {
        this.c1Descr = c1Descr;
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (c1Id != null ? c1Id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Categ1)) {
            return false;
        }
        Categ1 other = (Categ1) object;
        if ((this.c1Id == null && other.c1Id != null) || (this.c1Id != null && !this.c1Id.equals(other.c1Id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.Categ1[ c1Id=" + c1Id + " ]";
    }
    
}
