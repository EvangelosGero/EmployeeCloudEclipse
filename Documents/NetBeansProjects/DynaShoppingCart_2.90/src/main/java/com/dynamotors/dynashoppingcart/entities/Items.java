/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "items", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
public class Items implements Serializable {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "items")
    private List<OriginalCodes> originalCodesList;

    @Column(name = "usernm_seller_id")
    private Integer usernmSellerId;
    @Column(name = "catg_details_id")
    private Integer catgDetailsId; 
    @Column(name = "delivery_available_pin_id")
    private Integer deliveryAvailablePinId; 
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "items", fetch=FetchType.EAGER)
    private List<ItemsImages> itemsImagesList;
    
    @ManyToMany
    @JoinTable(
      name="ITEM_MAKES",
      joinColumns=@JoinColumn(name="ITEM_ID", referencedColumnName="ITEM_ID"),
      inverseJoinColumns=@JoinColumn(name="MAKE_ID", referencedColumnName="MK_ID"))
    private List<CategMake> categMakesList ;   
    
    @ManyToMany
    @JoinTable(
      name="ITEM_MODELS",
      joinColumns=@JoinColumn(name="ITEM_ID", referencedColumnName="ITEM_ID"),
      inverseJoinColumns=@JoinColumn(name="MODEL_ID", referencedColumnName="ML_ID"))
    private List<CategModel> categModelsList;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "item_id", nullable = false)
    private Integer itemId;    
    @Size(max = 30)
    @Column(name = "item_code", length = 30)
    private String itemCode;
    @Size(max = 45)
    @Column(name = "item_short_desc", length = 45)
    private String itemShortDesc;
    @Size(max = 80)
    @Column(name = "item_long_desc", length = 80)
    private String itemLongDesc;    
    @Size(max = 30)
    @Column(name = "item_color", length = 30)
    private String itemColor;
    @Column(name = "item_warranty")
    private Integer itemWarranty;
    @Size(max = 10)
    @Column(name = "item_size", length = 10)
    private String itemSize;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "item_retail_value", precision = 8, scale = 2)
    private Double itemRetailValue;
    @Column(name = "delivery_days")
    private Integer deliveryDays;    
    @Column(name = "item_categ1_id")
    private Integer itemCateg1Id;
    @Column(name = "item_categ2_id")
    private Integer itemCateg2Id;
    @Column(name = "item_categ3_id")
    private Integer itemCateg3Id;
    @Column(name = "item_wholesales_value", precision = 22)
    private Double itemWholesalesValue;
    @Column(name = "item_availability")
    private Integer itemAvailability;    
    @Column(name = "item_enabled")
    private boolean itemEnabled;
    @Transient
    private Integer quantity;
    @Transient
    private double discountPrice;
    @Transient
    private double totalFinal;
    

    public Items() {
    }

    public Items(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }    

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemShortDesc() {
        return itemShortDesc;
    }

    public void setItemShortDesc(String itemShortDesc) {
        this.itemShortDesc = itemShortDesc;
    }

    public String getItemLongDesc() {
        return itemLongDesc;
    }

    public void setItemLongDesc(String itemLongDesc) {
        this.itemLongDesc = itemLongDesc;
    }
   
    public String getItemColor() {
        return itemColor;
    }

    public void setItemColor(String itemColor) {
        this.itemColor = itemColor;
    }

    public Integer getItemWarranty() {
        return itemWarranty;
    }

    public void setItemWarranty(Integer itemWarranty) {
        this.itemWarranty = itemWarranty;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public Double getItemRetailValue() {
        return itemRetailValue;
    }

    public void setItemRetailValue(Double itemRetailValue) {
        this.itemRetailValue = itemRetailValue;
    }

    public Integer getDeliveryDays() {
        return deliveryDays;
    }

    public void setDeliveryDays(Integer deliveryDays) {
        this.deliveryDays = deliveryDays;
    }

    public Integer getUsernmSellerId() {
        return usernmSellerId;
    }

    public void setUsernmSellerId(Integer usernmSellerId) {
        this.usernmSellerId = usernmSellerId;
    }

    public Integer getCatgDetailsId() {
        return catgDetailsId;
    }

    public void setCatgDetailsId(Integer catgDetailsId) {
        this.catgDetailsId = catgDetailsId;
    }
    
    public Integer getItemCateg1Id() {
        return itemCateg1Id;
    }

    public void setItemCateg1Id(Integer itemCateg1Id) {
        this.itemCateg1Id = itemCateg1Id;
    }

    public Integer getItemCateg2Id() {
        return itemCateg2Id;
    }

    public void setItemCateg2Id(Integer itemCateg2Id) {
        this.itemCateg2Id = itemCateg2Id;
    }

    public Integer getItemCateg3Id() {
        return itemCateg3Id;
    }

    public void setItemCateg3Id(Integer itemCateg3Id) {
        this.itemCateg3Id = itemCateg3Id;
    }

    public Double getItemWholesalesValue() {
        return itemWholesalesValue;
    }

    public void setItemWholesalesValue(Double itemWholesalesValue) {
        this.itemWholesalesValue = itemWholesalesValue;
    }

    public Integer getItemAvailability() {
        return itemAvailability;
    }

    public void setItemAvailability(Integer itemAvailability) {
        this.itemAvailability = itemAvailability;
    }
   
    public boolean getItemEnabled() {
        return itemEnabled;
    }

    public void setItemEnabled(boolean itemEnabled) {
        this.itemEnabled = itemEnabled;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(double totalFinal) {
        this.totalFinal = totalFinal;
    }    

    public Integer getDeliveryAvailablePinId() {
        return deliveryAvailablePinId;
    }

    public void setDeliveryAvailablePinId(Integer deliveryAvailablePinId) {
        this.deliveryAvailablePinId = deliveryAvailablePinId;
    }
       

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (itemId != null ? itemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Items)) {
            return false;
        }
        Items other = (Items) object;
        if ((this.itemId == null && other.itemId != null) || (this.itemId != null && !this.itemId.equals(other.itemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.Items[ itemId=" + itemId + " ]";
    }   
    

    @XmlTransient
    public List<ItemsImages> getItemsImagesList() {
        return itemsImagesList;
    }

    public void setItemsImagesList(List<ItemsImages> itemsImagesList) {
        this.itemsImagesList = itemsImagesList;
    }  

    @XmlTransient
    public List<OriginalCodes> getOriginalCodesList() {
        return originalCodesList;
    }

    public void setOriginalCodesList(List<OriginalCodes> originalCodesList) {
        this.originalCodesList = originalCodesList;
    }

    @XmlTransient
    public List<CategMake> getCategMakesList() {
        return categMakesList;
    }

    public void setCategMakesList(List<CategMake> categMakesList) {
        this.categMakesList = categMakesList;
    }
    
    @XmlTransient
    public List<CategModel> getCategModelsList() {
        return categModelsList;
    }

    public void setCategModelsList(List<CategModel> categModelsList) {
        this.categModelsList = categModelsList;
    }
    
    public void addImage(ItemsImages image) {
        this.itemsImagesList.add(image);
        if (image.getItems() != this) {
            image.setItems(this);
        }
    }
    
}
