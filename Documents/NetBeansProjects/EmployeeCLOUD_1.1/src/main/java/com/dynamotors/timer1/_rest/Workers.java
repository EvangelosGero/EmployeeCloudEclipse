/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.timer1._rest;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "WORKERS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Workers.findAll", query = "SELECT w FROM Workers w")})
public class Workers implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "LAST_NAME")
    private String lastName;
    @Size(max = 40)
    @Column(name = "JOB_TITLE")
    private String jobTitle;
    @Column(name = "HIRE_DATE")
    @Temporal(TemporalType.DATE)
    private Date hireDate;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "SALARY")
    private Double salary;
    @Size(max = 5)
    @Column(name = "CODE")
    private String code;
    @Size(max = 20)
    @Column(name = "PHONE1")
    private String phone1;
    @Size(max = 20)
    @Column(name = "PHONE2")
    private String phone2;
    @Size(max = 20)
    @Column(name = "ADT")
    private String adt;
    @Size(max = 20)
    @Column(name = "AMKA")
    private String amka;
    @Size(max = 15)
    @Column(name = "AFM")
    private String afm;
    @Size(max = 25)
    @Column(name = "STREET")
    private String street;
    @Column(name = "STREET_NUMBER")
    private Short streetNumber;
    @Size(max = 25)
    @Column(name = "DEMOS")
    private String demos;
    @Size(max = 10)
    @Column(name = "TK")
    private String tk;
    @Column(name = "YEARS_BEFORE")
    private Short yearsBefore;
    @Size(max = 25)
    @Column(name = "EDUCATION")
    private String education;
    @Size(max = 5)
    @Column(name = "MARRIAGE")
    private String marriage;
    @Column(name = "CHILDREN")
    private Short children;
    @Column(name = "ENTITLED_DAYS")
    private Short entitledDays;
    @Size(max = 20)
    @Column(name = "AM_IKA")
    private String amIka;
    @Column(name = "BIRTHDATE")
    @Temporal(TemporalType.DATE)
    private Date birthdate;
    @Column(name = "FIRST_HIRE_DATE")
    @Temporal(TemporalType.DATE)
    private Date firstHireDate;
    @Size(max = 20)
    @Column(name = "FATHER_NAME")
    private String fatherName;
    @Column(name = "REASON_ISFORES")
    private Short reasonIsfores;
    @Column(name = "SUBSIDIARY")
    private Short subsidiary;
    @Column(name = "COMPANY")
    private Short company;
    @Column(name = "KAT_ASFALISIS")
    private Short katAsfalisis;
    @Column(name = "TAPIT")
    private Short tapit;
    @Column(name = "RELATION")
    private Short relation;
    @Size(max = 40)
    @Column(name = "KENTRO_KOSTOUS")
    private String kentroKostous;
    @Size(max = 20)
    @Column(name = "AM_EPIKOURIKOU")
    private String amEpikourikou;
    @Size(max = 30)
    @Column(name = "MOTHER_NAME")
    private String motherName;
    @Size(max = 10)
    @Column(name = "AR_PARARTIMATOS")
    private String arParartimatos;
    @Size(max = 10)
    @Column(name = "KAD")
    private String kad;
    @Size(max = 10)
    @Column(name = "KODIKOS_IDIKOTITAS")
    private String kodikosIdikotitas;
    @Size(max = 10)
    @Column(name = "IDIKI_PER_ASFALISIS")
    private String idikiPerAsfalisis;
    @Column(name = "APOLISI")
    private Short apolisi;
    @Column(name = "DIAKOPI")
    @Temporal(TemporalType.DATE)
    private Date diakopi;
    // @Pattern(regexp="[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?", message="Invalid email")//if the field contains email address consider using this annotation to enforce field validation
    @Size(max = 100)
    @Column(name = "EMAIL")
    private String email;

    public Workers() {
    }

    public Workers(Integer id) {
        this.id = id;
    }

    public Workers(Integer id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPhone1() {
        return phone1;
    }

    public void setPhone1(String phone1) {
        this.phone1 = phone1;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getAdt() {
        return adt;
    }

    public void setAdt(String adt) {
        this.adt = adt;
    }

    public String getAmka() {
        return amka;
    }

    public void setAmka(String amka) {
        this.amka = amka;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Short getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(Short streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getDemos() {
        return demos;
    }

    public void setDemos(String demos) {
        this.demos = demos;
    }

    public String getTk() {
        return tk;
    }

    public void setTk(String tk) {
        this.tk = tk;
    }

    public Short getYearsBefore() {
        return yearsBefore;
    }

    public void setYearsBefore(Short yearsBefore) {
        this.yearsBefore = yearsBefore;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getMarriage() {
        return marriage;
    }

    public void setMarriage(String marriage) {
        this.marriage = marriage;
    }

    public Short getChildren() {
        return children;
    }

    public void setChildren(Short children) {
        this.children = children;
    }

    public Short getEntitledDays() {
        return entitledDays;
    }

    public void setEntitledDays(Short entitledDays) {
        this.entitledDays = entitledDays;
    }

    public String getAmIka() {
        return amIka;
    }

    public void setAmIka(String amIka) {
        this.amIka = amIka;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public Date getFirstHireDate() {
        return firstHireDate;
    }

    public void setFirstHireDate(Date firstHireDate) {
        this.firstHireDate = firstHireDate;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public Short getReasonIsfores() {
        return reasonIsfores;
    }

    public void setReasonIsfores(Short reasonIsfores) {
        this.reasonIsfores = reasonIsfores;
    }

    public Short getSubsidiary() {
        return subsidiary;
    }

    public void setSubsidiary(Short subsidiary) {
        this.subsidiary = subsidiary;
    }

    public Short getCompany() {
        return company;
    }

    public void setCompany(Short company) {
        this.company = company;
    }

    public Short getKatAsfalisis() {
        return katAsfalisis;
    }

    public void setKatAsfalisis(Short katAsfalisis) {
        this.katAsfalisis = katAsfalisis;
    }

    public Short getTapit() {
        return tapit;
    }

    public void setTapit(Short tapit) {
        this.tapit = tapit;
    }

    public Short getRelation() {
        return relation;
    }

    public void setRelation(Short relation) {
        this.relation = relation;
    }

    public String getKentroKostous() {
        return kentroKostous;
    }

    public void setKentroKostous(String kentroKostous) {
        this.kentroKostous = kentroKostous;
    }

    public String getAmEpikourikou() {
        return amEpikourikou;
    }

    public void setAmEpikourikou(String amEpikourikou) {
        this.amEpikourikou = amEpikourikou;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getArParartimatos() {
        return arParartimatos;
    }

    public void setArParartimatos(String arParartimatos) {
        this.arParartimatos = arParartimatos;
    }

    public String getKad() {
        return kad;
    }

    public void setKad(String kad) {
        this.kad = kad;
    }

    public String getKodikosIdikotitas() {
        return kodikosIdikotitas;
    }

    public void setKodikosIdikotitas(String kodikosIdikotitas) {
        this.kodikosIdikotitas = kodikosIdikotitas;
    }

    public String getIdikiPerAsfalisis() {
        return idikiPerAsfalisis;
    }

    public void setIdikiPerAsfalisis(String idikiPerAsfalisis) {
        this.idikiPerAsfalisis = idikiPerAsfalisis;
    }

    public Short getApolisi() {
        return apolisi;
    }

    public void setApolisi(Short apolisi) {
        this.apolisi = apolisi;
    }

    public Date getDiakopi() {
        return diakopi;
    }

    public void setDiakopi(Date diakopi) {
        this.diakopi = diakopi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
        if (!(object instanceof Workers)) {
            return false;
        }
        Workers other = (Workers) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.timer1._rest.Workers[ id=" + id + " ]";
    }
    
}
