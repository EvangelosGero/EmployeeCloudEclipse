/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "SICK_DAYS_MORE_THAN_THREE", catalog = "", schema = "APP")
@XmlRootElement
public class SickDaysMoreThanThree implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "ID")
    private Short id;
    @Column(name = "EXIT_DAY")
    @Temporal(TemporalType.DATE)
    private Date exitDay;
    @Column(name = "BACK_DAY")
    @Temporal(TemporalType.DATE)
    private Date backDay;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "PK", nullable = false)
    private Integer pk;
    @Transient
    private String firstName;
    @Transient
    private String lastName;
    @Transient
    private String fatherName;

    public SickDaysMoreThanThree() {
    }

    public SickDaysMoreThanThree(Integer pk) {
        this.pk = pk;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Date getExitDay() {
        return exitDay;
    }

    public void setExitDay(Date exitDay) {
        this.exitDay = exitDay;
    }

    public Date getBackDay() {
        return backDay;
    }

    public void setBackDay(Date backDay) {
        this.backDay = backDay;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
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
        hash += (pk != null ? pk.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SickDaysMoreThanThree)) {
            return false;
        }
        SickDaysMoreThanThree other = (SickDaysMoreThanThree) object;
        if ((this.pk == null && other.pk != null) || (this.pk != null && !this.pk.equals(other.pk))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Entities.SickDaysMoreThanThree[ pk=" + pk + " ]";
    }
    
}
