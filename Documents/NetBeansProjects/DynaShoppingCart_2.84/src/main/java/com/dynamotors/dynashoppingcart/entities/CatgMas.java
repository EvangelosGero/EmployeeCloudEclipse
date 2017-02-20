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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author e
 */
@Entity
@Table(name = "catg_mas", catalog = "dynashoppingcart", schema = "")
@XmlRootElement

public class CatgMas implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 20)
    @Column(name = "catg_code", length = 20)
    private String catgCode;
    @Size(max = 10)
    @Column(name = "catg_discount_scheme", length = 10)
    private String catgDiscountScheme;

    public CatgMas() {
    }

    public CatgMas(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCatgCode() {
        return catgCode;
    }

    public void setCatgCode(String catgCode) {
        this.catgCode = catgCode;
    }

    public String getCatgDiscountScheme() {
        return catgDiscountScheme;
    }

    public void setCatgDiscountScheme(String catgDiscountScheme) {
        this.catgDiscountScheme = catgDiscountScheme;
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
        if (!(object instanceof CatgMas)) {
            return false;
        }
        CatgMas other = (CatgMas) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.CatgMas[ id=" + id + " ]";
    }
    
}
