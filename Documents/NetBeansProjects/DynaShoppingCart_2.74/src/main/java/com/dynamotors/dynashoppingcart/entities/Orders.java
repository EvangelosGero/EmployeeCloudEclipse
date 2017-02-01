/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "orders", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
public class Orders implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "orderid", nullable = false)
    private Long orderid;
    @Basic(optional = false)
    @NotNull
    @Column(name = "order_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;    
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;
    @Size(max = 6)
    @Column(name = "discount_coupon", length = 6)
    private String discountCoupon;
    @Basic(optional = false)
    @NotNull
    @Column(name = "discount_percent", nullable = false)
    private int discountPercent;
    @Basic(optional = false)
    @NotNull
    @Column(name = "discount_price", nullable = false, precision = 6, scale = 2)
    private BigDecimal discountPrice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "total_final", nullable = false, precision = 7, scale = 2)
    private BigDecimal totalFinal;
    @Basic(optional = false)
    @NotNull
    @Column(name = "payment_method_id", nullable = false)
    private long paymentMethodId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "deltapayId", nullable = false)
    private int deltapayId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost;
    @Basic(optional = false)
    @NotNull
    @Column(name = "shipping_cost_id", nullable = false)
    private boolean shippingCostId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "affiliate", nullable = false, length = 100)
    private String affiliate;
    @Column(name = "order_state")
    private Short orderState;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "cust_name", nullable = false, length = 200)
    private String custName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "cust_email", nullable = false, length = 200)
    private String custEmail;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "cust_phone", nullable = false, length = 200)
    private String custPhone;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "shipping_address", nullable = false, length = 150)
    private String shippingAddress;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "shipping_zip", nullable = false, length = 150)
    private String shippingZip;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 150)
    @Column(name = "shipping_city", nullable = false, length = 150)
    private String shippingCity;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "shipping_country", nullable = false, length = 100)
    private String shippingCountry;
    @Basic(optional = false)
    @NotNull
    @Column(name = "requested_invoice", nullable = false)
    private int requestedInvoice;
    @Basic(optional = false)
    @NotNull
    @Column(name = "exported", nullable = false)
    private int exported;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "shipping_country_code", nullable = false, length = 2)
    private String shippingCountryCode;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "country_code_session", nullable = false, length = 2)
    private String countryCodeSession;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "client_ip", nullable = false, length = 20)
    private String clientIp;
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_id", nullable = false)
    private int userId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "orders")
    private List<OrderItems> orderItemsList;

    public Orders() {
    }

    public Orders(Long orderid) {
        this.orderid = orderid;
    }

    public Orders(Long orderid, Date orderDate,  BigDecimal total, int discountPercent, BigDecimal discountPrice, BigDecimal totalFinal, long paymentMethodId, int deltapayId, BigDecimal shippingCost, boolean shippingCostId, String affiliate, String custName, String custEmail, String custPhone, String shippingAddress, String shippingZip, String shippingCity, String shippingCountry, int requestedInvoice, int exported, String shippingCountryCode, String countryCodeSession, String clientIp, int userId) {
        this.orderid = orderid;
        this.orderDate = orderDate;        
        this.total = total;
        this.discountPercent = discountPercent;
        this.discountPrice = discountPrice;
        this.totalFinal = totalFinal;
        this.paymentMethodId = paymentMethodId;
        this.deltapayId = deltapayId;
        this.shippingCost = shippingCost;
        this.shippingCostId = shippingCostId;
        this.affiliate = affiliate;
        this.custName = custName;
        this.custEmail = custEmail;
        this.custPhone = custPhone;
        this.shippingAddress = shippingAddress;
        this.shippingZip = shippingZip;
        this.shippingCity = shippingCity;
        this.shippingCountry = shippingCountry;
        this.requestedInvoice = requestedInvoice;
        this.exported = exported;
        this.shippingCountryCode = shippingCountryCode;
        this.countryCodeSession = countryCodeSession;
        this.clientIp = clientIp;
        this.userId = userId;
    }

    public Long getOrderid() {
        return orderid;
    }

    public void setOrderid(Long orderid) {
        this.orderid = orderid;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
   

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getDiscountCoupon() {
        return discountCoupon;
    }

    public void setDiscountCoupon(String discountCoupon) {
        this.discountCoupon = discountCoupon;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getTotalFinal() {
        return totalFinal;
    }

    public void setTotalFinal(BigDecimal totalFinal) {
        this.totalFinal = totalFinal;
    }

    public long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public int getDeltapayId() {
        return deltapayId;
    }

    public void setDeltapayId(int deltapayId) {
        this.deltapayId = deltapayId;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public boolean getShippingCostId() {
        return shippingCostId;
    }

    public void setShippingCostId(boolean shippingCostId) {
        this.shippingCostId = shippingCostId;
    }

    public String getAffiliate() {
        return affiliate;
    }

    public void setAffiliate(String affiliate) {
        this.affiliate = affiliate;
    }

    public Short getOrderState() {
        return orderState;
    }

    public void setOrderState(Short orderState) {
        this.orderState = orderState;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustEmail() {
        return custEmail;
    }

    public void setCustEmail(String custEmail) {
        this.custEmail = custEmail;
    }

    public String getCustPhone() {
        return custPhone;
    }

    public void setCustPhone(String custPhone) {
        this.custPhone = custPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getShippingZip() {
        return shippingZip;
    }

    public void setShippingZip(String shippingZip) {
        this.shippingZip = shippingZip;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingCountry() {
        return shippingCountry;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    public int getRequestedInvoice() {
        return requestedInvoice;
    }

    public void setRequestedInvoice(int requestedInvoice) {
        this.requestedInvoice = requestedInvoice;
    }

    public int getExported() {
        return exported;
    }

    public void setExported(int exported) {
        this.exported = exported;
    }

    public String getShippingCountryCode() {
        return shippingCountryCode;
    }

    public void setShippingCountryCode(String shippingCountryCode) {
        this.shippingCountryCode = shippingCountryCode;
    }

    public String getCountryCodeSession() {
        return countryCodeSession;
    }

    public void setCountryCodeSession(String countryCodeSession) {
        this.countryCodeSession = countryCodeSession;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @XmlTransient
    public List<OrderItems> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(List<OrderItems> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderid != null ? orderid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Orders)) {
            return false;
        }
        Orders other = (Orders) object;
        if ((this.orderid == null && other.orderid != null) || (this.orderid != null && !this.orderid.equals(other.orderid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.Orders[ orderid=" + orderid + " ]";
    }
    
}
