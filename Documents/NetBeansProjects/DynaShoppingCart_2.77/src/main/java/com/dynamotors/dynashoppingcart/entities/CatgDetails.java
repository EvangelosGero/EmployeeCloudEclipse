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
@Table(name = "catg_details", catalog = "dynashoppingcart", schema = "")
@XmlRootElement

public class CatgDetails implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 10)
    @Column(name = "discounts_provider_name", length = 10)
    private String discountsProviderName;
    @Size(max = 50)
    @Column(name = "discounts_details", length = 50)
    private String discountsDetails;
    @Column(name = "discounts_value")
    private Integer discountsValue;
    @Column(name = "discounts_pers")
    private Integer discountsPers;

    public CatgDetails() {
    }

    public CatgDetails(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDiscountsProviderName() {
        return discountsProviderName;
    }

    public void setDiscountsProviderName(String discountsProviderName) {
        this.discountsProviderName = discountsProviderName;
    }

    public String getDiscountsDetails() {
        return discountsDetails;
    }

    public void setDiscountsDetails(String discountsDetails) {
        this.discountsDetails = discountsDetails;
    }

    public Integer getDiscountsValue() {
        return discountsValue;
    }

    public void setDiscountsValue(Integer discountsValue) {
        this.discountsValue = discountsValue;
    }

    public Integer getDiscountsPers() {
        return discountsPers;
    }

    public void setDiscountsPers(Integer discountsPers) {
        this.discountsPers = discountsPers;
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
        if (!(object instanceof CatgDetails)) {
            return false;
        }
        CatgDetails other = (CatgDetails) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.CatgDetails[ id=" + id + " ]";
    }
    
}
