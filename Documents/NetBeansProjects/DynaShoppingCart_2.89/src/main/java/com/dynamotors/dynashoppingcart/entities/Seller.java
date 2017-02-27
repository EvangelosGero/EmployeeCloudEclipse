/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author e
 */
@Entity
@Table(name = "seller", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
public class Seller implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)    
    @Column(name = "id", nullable = false)
    private Integer id;
    @Transient       
    private String sellerCode;    
    @Transient    
    private String sellerName;    
    @Transient    
    private String sellerAddress;
    @Transient    
    private Integer sellerMob;    
    @Transient    
    private String itemCode;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Transient    
    private Double itemValue;
    @Transient    
    private Integer itemId;
    @Column(name = "usernm_id")
    private Integer usernmId;
    @Size(max = 20)
    @Column(name = "seller_AFM", length = 20)
    private String sellerAFM;

    public Seller() {
    }

    public String getSellerAFM() {
        return sellerAFM;
    }

    public void setSellerAFM(String sellerAFM) {
        this.sellerAFM = sellerAFM;
    }

    public Seller(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSellerCode() {
        return sellerCode;
    }

    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerAddress() {
        return sellerAddress;
    }

    public void setSellerAddress(String sellerAddress) {
        this.sellerAddress = sellerAddress;
    }

    public Integer getSellerMob() {
        return sellerMob;
    }

    public void setSellerMob(Integer sellerMob) {
        this.sellerMob = sellerMob;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public Double getItemValue() {
        return itemValue;
    }

    public void setItemValue(Double itemValue) {
        this.itemValue = itemValue;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }    

    public Integer getUsernmId() {
        return usernmId;
    }

    public void setUsernmId(Integer usernmId) {
        this.usernmId = usernmId;
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
        if (!(object instanceof Seller)) {
            return false;
        }
        Seller other = (Seller) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.Seller[ id=" + id + " ]";
    }
    
}
