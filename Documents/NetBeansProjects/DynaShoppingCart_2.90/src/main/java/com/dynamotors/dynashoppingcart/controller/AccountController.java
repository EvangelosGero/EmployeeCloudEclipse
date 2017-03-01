/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.Account;
import com.dynamotors.dynashoppingcart.entities.Invoice;
import com.dynamotors.dynashoppingcart.entities.PaymentCustomer;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author evgero
 */
@Named("accountController")
@ViewScoped
public class AccountController implements Serializable{
    @Inject
    private InvoiceController invoiceController;
    @Inject
    private PaymentCustomerController paymentCustomerController;
    @Inject
    private UsernmController usernmController;
    private List<Account> accountList ;
    private BigDecimal total= BigDecimal.ZERO;

    public AccountController() {
    }

    public InvoiceController getInvoiceController() {
        return invoiceController;
    }

    public void setInvoiceController(InvoiceController invoiceController) {
        this.invoiceController = invoiceController;
    }

    public PaymentCustomerController getPaymentCustomerController() {
        return paymentCustomerController;
    }

    public void setPaymentCustomerController(PaymentCustomerController paymentCustomerController) {
        this.paymentCustomerController = paymentCustomerController;
    }

    public List<Account> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<Account> accountList) {
        this.accountList = accountList;
    }

    public UsernmController getUsernmController() {
        return usernmController;
    }

    public void setUsernmController(UsernmController usernmController) {
        this.usernmController = usernmController;
    } 

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
       
    
    @PostConstruct    
    public void generateAccount(){      
       if(usernmController.isLoggedIn()) {
           accountList = new ArrayList<>();
           List<Invoice> invoiceList = invoiceController.getItems().stream().
                   filter(s -> s.getUsernmId().equals(usernmController.getUsernmLogged().getId()))
                           .collect(Collectors.toList());
           List<PaymentCustomer> cpList = paymentCustomerController.getItems().stream().
                   filter(s -> s.getUsernmId().equals(usernmController.getUsernmLogged().getId()))
                           .collect(Collectors.toList());
           
           //fill the accountList and sort by date
           for (Invoice i : invoiceList){
            Account record = new Account();
            record.setInvoiceImg(i.getInvoiceImg());
            record.setTransactionType("ΧΡΕΩΣΗ");
            record.setDate(i.getDate());
            record.setTransactionCode(i.getInvoiceCode());
            record.setBank(null);
            record.setPaymentMethod(null);
            record.setTotal(i.getTotal());
            total = total.add(i.getTotal());
            
            accountList.add(record);
           }
           for (PaymentCustomer p : cpList){
            Account record = new Account();
            record.setInvoiceImg(null);
            record.setTransactionType("ΠΙΣΤΩΣΗ");
            record.setDate(p.getDate());
            record.setTransactionCode(p.getPaymentCode());
            record.setBank(p.getBank());
            record.setPaymentMethod(p.getPaymentMethod());
            record.setTotal(p.getTotal());
            total = total.subtract(p.getTotal());
            
            accountList.add(record);
           }
           Collections.sort(accountList);
       }
    }
    
    
}
