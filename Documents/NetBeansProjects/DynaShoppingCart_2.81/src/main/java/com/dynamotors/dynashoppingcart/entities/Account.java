/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.entities;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author evgero
 */
public class Account implements Comparable<Account>{
    private String invoiceImg;
    private String transactionType;     //Χρέωση ή Πίστωση
    private Date date;
    private String transactionCode;     //invoice or payment code
    private String bank;
    private Integer paymentMethod;
    private BigDecimal total;

    public Account() {
    }
       
    public Account(String invoiceImg, String transactionType, Date date, String transactionCode, String bank, 
            Integer paymentMethod, BigDecimal total) {
        this.invoiceImg = invoiceImg;
        this.transactionType = transactionType;
        this.date = date;
        this.transactionCode = transactionCode;
        this.bank = bank;
        this.paymentMethod = paymentMethod;
        this.total = total;
    }

    public String getInvoiceImg() {
        return invoiceImg;
    }

    public void setInvoiceImg(String invoiceImg) {
        this.invoiceImg = invoiceImg;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public Integer getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(Integer paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    @Override
        public int compareTo(Account o) {
            if (getDate() == null || o.getDate() == null)
                return 0;
            return getDate().compareTo(o.getDate());
  }
    @Override
    public String toString() {
        return "Account{" + "invoiceImg=" + invoiceImg + ", transactionType=" + transactionType + 
                ", date=" + date + ", transactionCode=" + transactionCode + ", bank=" + bank + ", paymentMethod=" 
                + paymentMethod + ", total=" + total + '}';
    }
    
    
    
}
