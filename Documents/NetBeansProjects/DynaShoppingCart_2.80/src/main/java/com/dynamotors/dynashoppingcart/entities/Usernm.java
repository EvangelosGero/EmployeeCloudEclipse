/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author e
 */
@Entity
@Table(catalog = "dynashoppingcart", schema = "", name = "usernm", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"password"}),
    @UniqueConstraint(columnNames = {"user_email"}),
    @UniqueConstraint(columnNames = {"user_name"})})
@XmlRootElement
public class Usernm implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)    
    @Column(name = "id", nullable = false)
    private Integer id;
    @Size(max = 25)
    @Column(name = "usertype", length = 25)
    private String usertype;
    @Size(max = 25)    
    @Column(name = "password" ,length = 25, unique = true)
    private String password;
    @Size(max = 15)
    @Column(name = "roleuser", length = 15)
    private String roleuser;
    @Column(name = "create_date")
    @Temporal(TemporalType.DATE)
    private Date createDate;
    @Size(max = 50)
    @Column(name = "hint", length = 50)
    private String hint;
    @Size(max = 15)
    @Column(name = "hint_ans", length = 15)
    private String hintAns;
    @Size(max = 25)
    @Column(name = "user_name", length = 25, unique = true)
    private String userName;
    @Size(max = 50)
    @Column(name = "user_email", length = 50, unique = true)
    private String userEmail;
    @Size(max = 1)
    @Column(name="gender" ,length = 1)
    private String gender;
    @Column(name = "dob")
    @Temporal(TemporalType.DATE)
    private Date dob;
    @Size(max = 20)
    @Column(name = "mobile", length = 20)
    private String mobile;
    @Size(max = 100)
    @Column(name = "address", length = 100)
    private String address;
    @Size(max = 25)
    @Column(name = "user_firstname", length = 25)
    private String firstName;
    @Size(max = 25)
    @Column(name = "user_lastname", length = 25)
    private String lastName;
    @Size(max = 40)
    @Column(name = "country", length = 40)
    private String country;
    @Size(max = 40)
    @Column(name = "city", length = 40)
    private String city;
    @Size(max = 20)
    @Column(name = "postal_code", length = 20)
    private String postalCode;

    public Usernm() {
    }

    public Usernm(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoleuser() {
        return roleuser;
    }

    public void setRoleuser(String roleuser) {
        this.roleuser = roleuser;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getHintAns() {
        return hintAns;
    }

    public void setHintAns(String hintAns) {
        this.hintAns = hintAns;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;        
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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
        if (!(object instanceof Usernm)) {
            return false;
        }
        Usernm other = (Usernm) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.Usernm[ id=" + id + " ]";
    }
    
}
