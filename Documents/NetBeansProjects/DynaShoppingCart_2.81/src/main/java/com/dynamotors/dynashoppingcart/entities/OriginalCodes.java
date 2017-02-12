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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "original_codes", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
public class OriginalCodes implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "oc_id", nullable = false)
    private Integer ocId;
    @Size(max = 30)
    @Column(name = "oc_val", length = 30)
    private String ocVal;
    @JoinColumn(name = "oc_FK_item", referencedColumnName = "item_id")
    @ManyToOne
    private Items items;

    public OriginalCodes() {
    }

    public OriginalCodes(Integer ocId) {
        this.ocId = ocId;
    }

    public Integer getOcId() {
        return ocId;
    }    
        
    public void setOcId(Integer ocId) {
        this.ocId = ocId;
    }

    public String getOcVal() {
        return ocVal;
    }

    public void setOcVal(String ocVal) {
        this.ocVal = ocVal;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
        if (!items.getOriginalCodesList().contains(this)) { 
            items.getOriginalCodesList().add(this);
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ocId != null ? ocId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OriginalCodes)) {
            return false;
        }
        OriginalCodes other = (OriginalCodes) object;
        if ((this.ocId == null && other.ocId != null) || (this.ocId != null && !this.ocId.equals(other.ocId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.OriginalCodes[ ocId=" + ocId + " ]";
    }
    
}
