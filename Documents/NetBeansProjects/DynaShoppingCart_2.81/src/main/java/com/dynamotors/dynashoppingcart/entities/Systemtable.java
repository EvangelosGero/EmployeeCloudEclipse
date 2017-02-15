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
@Table(catalog = "dynashoppingcart", schema = "", name = "systemtable")
@XmlRootElement
public class Systemtable implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 100)
    @Column(name = "company_name", length = 100)
    private String companyName;
    @Size(max = 100)
    @Column(name = "add1", length = 100)
    private String add1;
    @Size(max = 100)
    @Column(name = "add3", length = 100)
    private String add3;
    @Size(max = 200)
    @Column(name = "bottom_message", length = 200)
    private String bottomMessage;
    @Size(max = 200)
    @Column(name = "about_us", length = 200)
    private String aboutUs;
    @Size(max = 200)
    @Column(name = "contact_us", length = 200)
    private String contactUs;
    @Size(max = 2000)
    @Column(name = "faq", length = 2000)
    private String faq;

    public Systemtable() {
    }

    public Systemtable(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAdd1() {
        return add1;
    }

    public void setAdd1(String add1) {
        this.add1 = add1;
    }

    public String getAdd3() {
        return add3;
    }

    public void setAdd3(String add3) {
        this.add3 = add3;
    }

    public String getBottomMessage() {
        return bottomMessage;
    }

    public void setBottomMessage(String bottomMessage) {
        this.bottomMessage = bottomMessage;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getContactUs() {
        return contactUs;
    }

    public void setContactUs(String contactUS) {
        this.contactUs = contactUS;
    }

    public String getFaq() {
        return faq;
    }

    public void setFaq(String faq) {
        this.faq = faq;
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
        if (!(object instanceof Systemtable)) {
            return false;
        }
        Systemtable other = (Systemtable) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.Systemtable[ id=" + id + " ]";
    }
    
}
