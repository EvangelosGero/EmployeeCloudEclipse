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
@Table(name = "order_items", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
public class OrderItems implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;  
    @Size(max = 30)
    @Column(name = "item_code",  length = 30)
    private String itemCode;
    @Basic(optional = false)   
    @Column(name = "item_price_old")
    private double itemPriceOld;     
    @Size(max = 80)
    @Column(name = "item_long_descr",  length = 80)
    private String itemLongDescr;    
    @Column(name = "item_price_retail")
    private double itemPriceRetail;     
    @Column(name = "item_discount")
    private int itemDiscount;     
    @Size(max = 100)
    @Column(name = "item_size", length = 100)
    private String itemSize;       
    @Size(max = 100)
    @Column(name = "item_color",  length = 100)
    private String itemColor;
    @Basic(optional = false)
    @NotNull    
    @Column(name = "quantity",nullable = false)
    private int quantity;    
    @Column(name = "returns_count")
    private int returnsCount;        
    @Column(name = "item_price_wholesale")
    private double itemPriceWholesale;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id", nullable = false)
    private int userId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "item_id", nullable = false)
    private int itemId;
    @JoinColumn(name = "orderid", referencedColumnName = "orderid")
    @ManyToOne(optional = false)
    private Orders orders;

    public OrderItems() {
    }

    public OrderItems(Integer id) {
        this.id = id;
    }

    public OrderItems(Integer id, String itemCode, double itemPriceOld, String itemLongDescr, double itemPriceRetail, int itemDiscount, String itemSize, String itemColor, int quantity, int returnsCount, double itemPriceWholesale, int userId, int itemId) {
        this.id = id;
        this.itemCode = itemCode;
        this.itemPriceOld = itemPriceOld;
        this.itemLongDescr = itemLongDescr;
        this.itemPriceRetail = itemPriceRetail;
        this.itemDiscount = itemDiscount;
        this.itemSize = itemSize;
        this.itemColor = itemColor;
        this.quantity = quantity;
        this.returnsCount = returnsCount;
        this.itemPriceWholesale = itemPriceWholesale;
        this.userId = userId;
        this.itemId = itemId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public double getItemPriceOld() {
        return itemPriceOld;
    }

    public void setItemPriceOld(double itemPriceOld) {
        this.itemPriceOld = itemPriceOld;
    }

    public String getItemLongDescr() {
        return itemLongDescr;
    }

    public void setItemLongDescr(String itemLongDescr) {
        this.itemLongDescr = itemLongDescr;
    }

    public double getItemPriceRetail() {
        return itemPriceRetail;
    }

    public void setItemPriceRetail(double itemPriceRetail) {
        this.itemPriceRetail = itemPriceRetail;
    }

    public int getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(int itemDiscount) {
        this.itemDiscount = itemDiscount;
    }

    public String getItemSize() {
        return itemSize;
    }

    public void setItemSize(String itemSize) {
        this.itemSize = itemSize;
    }

    public String getItemColor() {
        return itemColor;
    }

    public void setItemColor(String itemColor) {
        this.itemColor = itemColor;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getReturnsCount() {
        return returnsCount;
    }

    public void setReturnsCount(int returnsCount) {
        this.returnsCount = returnsCount;
    }

    public double getItemPriceWholesale() {
        return itemPriceWholesale;
    }

    public void setItemPriceWholesale(double itemPriceWholesale) {
        this.itemPriceWholesale = itemPriceWholesale;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
        if (!orders.getOrderItemsList().contains(this)) { 
            orders.getOrderItemsList().add(this);            
        }
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof OrderItems)) {
            return false;
        }
        OrderItems other = (OrderItems) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.OrderItems[ id=" + id + " ]";
    }
    
}
