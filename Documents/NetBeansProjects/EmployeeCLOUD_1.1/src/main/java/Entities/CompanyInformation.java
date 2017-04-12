/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "COMPANY_INFORMATION", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CompanyInformation.findAll", query = "SELECT c FROM CompanyInformation c")})
public class CompanyInformation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID", nullable = false)
    private Short id;
    @Size(max = 70)
    @Column(name = "NAME", length = 70)
    private String name;
    @Size(max = 100)
    @Column(name = "IDOS_EPIHIRISIS", length = 100)
    private String idosEpihirisis;
    @Size(max = 40)
    @Column(name = "PHONE_NUMBER", length = 40)
    private String phoneNumber;
    @Size(max = 40)
    @Column(name = "AR_MITR_ERG", length = 40)
    private String arMitrErg;
    @Size(max = 100)
    @Column(name = "IPEFTHINOS", length = 100)
    private String ipefthinos;
    @Size(max = 100)
    @Column(name = "ADDRESS", length = 100)
    private String address;
    @Size(max = 40)
    @Column(name = "TOWN", length = 40)
    private String town;
    @Size(max = 40)
    @Column(name = "EDRA", length = 40)
    private String edra;
    @Size(max = 40)
    @Column(name = "AFM", length = 40)
    private String afm;
    @Size(max = 40)
    @Column(name = "IPOKATASTIMA_IKA", length = 40)
    private String ipokatastimaIka;
    @Size(max = 30)
    @Column(name = "PATRONYMO", length = 30)
    private String patronymo;
    @Size(max = 10)
    @Column(name = "ARITHMOS_ODOU", length = 10)
    private String arithmosOdou;
    @Size(max = 10)
    @Column(name = "TK", length = 10)
    private String tk;
    @Size(max = 10)
    @Column(name = "KODIKOS_IPOK_IKA", length = 10)
    private String kodikosIpokIka;

    public CompanyInformation() {
    }

    public CompanyInformation(Short id) {
        this.id = id;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdosEpihirisis() {
        return idosEpihirisis;
    }

    public void setIdosEpihirisis(String idosEpihirisis) {
        this.idosEpihirisis = idosEpihirisis;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getArMitrErg() {
        return arMitrErg;
    }

    public void setArMitrErg(String arMitrErg) {
        this.arMitrErg = arMitrErg;
    }

    public String getIpefthinos() {
        return ipefthinos;
    }

    public void setIpefthinos(String ipefthinos) {
        this.ipefthinos = ipefthinos;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getEdra() {
        return edra;
    }

    public void setEdra(String edra) {
        this.edra = edra;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public String getIpokatastimaIka() {
        return ipokatastimaIka;
    }

    public void setIpokatastimaIka(String ipokatastimaIka) {
        this.ipokatastimaIka = ipokatastimaIka;
    }

    public String getPatronymo() {
        return patronymo;
    }

    public void setPatronymo(String patronymo) {
        this.patronymo = patronymo;
    }

    public String getArithmosOdou() {
        return arithmosOdou;
    }

    public void setArithmosOdou(String arithmosOdou) {
        this.arithmosOdou = arithmosOdou;
    }

    public String getTk() {
        return tk;
    }

    public void setTk(String tk) {
        this.tk = tk;
    }

    public String getKodikosIpokIka() {
        return kodikosIpokIka;
    }

    public void setKodikosIpokIka(String kodikosIpokIka) {
        this.kodikosIpokIka = kodikosIpokIka;
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
        if (!(object instanceof CompanyInformation)) {
            return false;
        }
        CompanyInformation other = (CompanyInformation) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Controllers.CompanyInformation[ id=" + id + " ]";
    }
    
}
