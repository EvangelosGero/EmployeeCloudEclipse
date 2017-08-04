/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.timer1._rest;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "TIMER")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Timer.findAll", query = "SELECT t FROM Timer t")})
public class Timer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 5)
    @Column(name = "CODE")
    private String code;
    @Column(name = "STARTTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date starttime;
    @Column(name = "ENDTIME")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endtime;
    @Column(name = "INTERVAL_TIME")
    private BigInteger intervalTime;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Long id;
    @Size(max = 20)
    @Column(name = "PC_NAME_IN")
    private String pcNameIn;
    @Size(max = 20)
    @Column(name = "PC_IP_IN")
    private String pcIpIn;
    @Size(max = 20)
    @Column(name = "PC_NAME_OUT")
    private String pcNameOut;
    @Size(max = 20)
    @Column(name = "PC_IP_OUT")
    private String pcIpOut;
    @Transient
    private String firstName;
    @Transient
    private String lastName;
    @Transient
    private String fatherName;

    public Timer() {
    }

    public Timer(Long id) {
        this.id = id;
    }

    public Timer(Long id, String code) {
        this.id = id;
        this.code = code;
    }

    public Timer(String code, Date starttime, Date endtime, BigInteger intervalTime, Long id, String pcNameIn, String pcIpIn, String pcNameOut, String pcIpOut, String firstName, String lastName, String fatherName) {
        this.code = code;
        this.starttime = starttime;
        this.endtime = endtime;
        this.intervalTime = intervalTime;
        this.id = id;
        this.pcNameIn = pcNameIn;
        this.pcIpIn = pcIpIn;
        this.pcNameOut = pcNameOut;
        this.pcIpOut = pcIpOut;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fatherName = fatherName;
    }
    
    

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getEndtime() {
        return endtime;
    }

    public void setEndtime(Date endtime) {
        this.endtime = endtime;
    }

    public BigInteger getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(BigInteger intervalTime) {
        this.intervalTime = intervalTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPcNameIn() {
        return pcNameIn;
    }

    public void setPcNameIn(String pcNameIn) {
        this.pcNameIn = pcNameIn;
    }

    public String getPcIpIn() {
        return pcIpIn;
    }

    public void setPcIpIn(String pcIpIn) {
        this.pcIpIn = pcIpIn;
    }

    public String getPcNameOut() {
        return pcNameOut;
    }

    public void setPcNameOut(String pcNameOut) {
        this.pcNameOut = pcNameOut;
    }

    public String getPcIpOut() {
        return pcIpOut;
    }

    public void setPcIpOut(String pcIpOut) {
        this.pcIpOut = pcIpOut;
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

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
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
        if (!(object instanceof Timer)) {
            return false;
        }
        Timer other = (Timer) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.timer1._rest.Timer[ id=" + id + " ]";
    }
    
}
