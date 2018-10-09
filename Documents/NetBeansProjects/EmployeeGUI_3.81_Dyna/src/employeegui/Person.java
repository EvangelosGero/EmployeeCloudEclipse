/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Home
 */
public class Person {
    
    private final SimpleIntegerProperty id = new SimpleIntegerProperty (0);
    private final SimpleStringProperty firstName= new SimpleStringProperty ("");
    private final SimpleStringProperty lastName = new SimpleStringProperty ("");
    private final SimpleStringProperty jobTitle = new SimpleStringProperty ("");
    private final SimpleStringProperty hireDate = new SimpleStringProperty ("");
    private final SimpleDoubleProperty salary = new SimpleDoubleProperty (0);
    private final SimpleStringProperty relation = new SimpleStringProperty ("");
    private final SimpleStringProperty  phone1 = new SimpleStringProperty ("");
    private final SimpleStringProperty phone2 = new SimpleStringProperty ("");
    private final SimpleStringProperty adt = new SimpleStringProperty ("");
    private final SimpleStringProperty amka = new SimpleStringProperty ("");
    private final SimpleStringProperty afm = new SimpleStringProperty ("");
    private final SimpleStringProperty street = new SimpleStringProperty ("");
    private final SimpleIntegerProperty streetNumber = new SimpleIntegerProperty (0);
    private final SimpleStringProperty demos = new SimpleStringProperty ("");
    private final SimpleStringProperty tk = new SimpleStringProperty ("");
    private final SimpleIntegerProperty yearsBefore = new SimpleIntegerProperty (0);
    private final SimpleStringProperty education = new SimpleStringProperty ("");
    private final SimpleStringProperty marriage = new SimpleStringProperty ("");
    private final SimpleIntegerProperty children = new SimpleIntegerProperty (0);
    private final SimpleStringProperty amIka = new SimpleStringProperty ("");
    private final SimpleStringProperty birthDate = new SimpleStringProperty ("");
    private final SimpleStringProperty firstHireDate = new SimpleStringProperty ("");
    private final SimpleStringProperty fatherName= new SimpleStringProperty ("");
    private final SimpleIntegerProperty reasonIsfores = new SimpleIntegerProperty (0);
    private final SimpleStringProperty tapit = new SimpleStringProperty ("");
    private final SimpleStringProperty subsidiary= new SimpleStringProperty ("");
    private final SimpleStringProperty company= new SimpleStringProperty ("");
    private final SimpleIntegerProperty katAsfalisis = new SimpleIntegerProperty (0);
    private final SimpleStringProperty kentroKostous = new SimpleStringProperty ("");
    private final SimpleStringProperty amEpikourikou = new SimpleStringProperty ("");
    private final SimpleStringProperty motherName = new SimpleStringProperty ("");
    private final SimpleStringProperty arParart = new SimpleStringProperty ("");
    private final SimpleStringProperty kad = new SimpleStringProperty ("");
    private final SimpleStringProperty kodikosIdikotitas = new SimpleStringProperty ("");
    private final SimpleStringProperty idikiPeriptosiAsfalisis = new SimpleStringProperty ("");
    private final SimpleStringProperty apolisi = new SimpleStringProperty ("");
    private final SimpleStringProperty diakopi = new SimpleStringProperty ("");
    private final SimpleStringProperty email = new SimpleStringProperty ("");
    
    
    
    
public  Person() {    
        this(0,"","","","",0,"","","","","","","",0,"","",0,"","",0,"","","","",0,"","","",0,"","",
                "","","","","","","",""); /* Null Constructor */
        }
        
        /*General Constructor */
        public Person (int id, String firstName, String lastName, String jobTitle,
                String hireDate, double salary, String relation, String phone1, 
                String phone2, String adt, String amka, String afm, String street,
                int streetNumber, String demos, String tk, int yearsBefore, 
                String education, String marriage, int children, String amIka, 
                String birthDate, String firstHireDate, String fatherName, int reasonIsfores,
                String tapit, String subsidiary, String company, int katAsfalisis,
                String kentroKostous, String amEpikourikou, String motherName, String arParart, 
                String kad, String kodikosIdikotitas, String idikiPeriptosiAsfalisis, 
                String apolisi, String diakopi, String email){
                setId(id);
                setFirstName(firstName);
                setLastName(lastName);
                setJobTitle(jobTitle);
                setHireDate(hireDate);
                setSalary(salary);
                setRelation(relation);
                setPhone1(phone1);
                setPhone2(phone2);
                setAdt(adt);
                setAmka(amka);
                setAfm(afm);
                setStreet(street);
                setStreetNumber(streetNumber);
                setDemos(demos);
                setTk(tk);
                setYearsBefore(yearsBefore);
                setEducation(education);
                setMarriage(marriage);
                setChildren(children);
                setAmIka(amIka);
                setBirthDate(birthDate);
                setFirstHireDate(firstHireDate);
                setFatherName(fatherName);
                setReasonIsfores(reasonIsfores);
                setTapit(tapit);
                setSubsidiary(subsidiary);
                setCompany(company);
                setKatAsfalisis(katAsfalisis);
                setKentroKostous(kentroKostous);
                setAmEpikourikou(amEpikourikou);
                setMotherName(motherName);
                setArParart(arParart); 
                setKad(kad);
                setKodikosIdikotitas(kodikosIdikotitas);
                setIdikiPeriptosiAsfalisis(idikiPeriptosiAsfalisis);
                setApolisi(apolisi);
                setDiakopi(diakopi);
                setEmail(email);
        }

        public int getId () {
            return id.get();
        } 
        public void setId (int id) {
            this.id.set(id);
        }
         public String getFirstName () {
            return firstName.get();
        } 
        public void setFirstName (String firstName) {
            this.firstName.set(firstName);
        }
        public String getLastName () {
            return lastName.get();
        } 
        public void setLastName (String lastName) {
            this.lastName.set(lastName);
        }
        public String getJobTitle () {
            return jobTitle.get();
        } 
        public void setJobTitle (String jobTitle) {
            this.jobTitle.set(jobTitle);
        }
        public String getHireDate () {
            return hireDate.get();
        } 
        public void setHireDate (String hireDate) {
            this.hireDate.set(hireDate);
        }
        public double getSalary () {
            return salary.get();
        } 
        public void setSalary (double salary) {
            this.salary.set(salary);
        }   
        public String getRelation () {
            return relation.get();
        } 
        public void setRelation (String relation) {
            this.relation.set(relation);
        }
        public String getPhone1 () {
            return phone1.get();
        } 
        public void setPhone1 (String phone1) {
            this.phone1.set(phone1);
        }
        public String getPhone2 () {
            return phone2.get();
        } 
        public void setPhone2 (String phone2) {
            this.phone2.set(phone2);
        }
        public String getAdt () {
            return adt.get();
        } 
        public void setAdt (String adt) {
            this.adt.set(adt);
        }
        public String getAmka () {
            return amka.get();
        } 
        public void setAmka (String amka) {
            this.amka.set(amka);
        }
        public String getAfm () {
            return afm.get();
        } 
        public void setAfm (String afm) {
            this.afm.set(afm);
        }
        public String getStreet () {
            return street.get();
        } 
        public void setStreet (String street) {
            this.street.set(street);
        }
        public int getStreetNumber () {
            return streetNumber.get();
        } 
        public void setStreetNumber (int streetNumber) {
            this.streetNumber.set(streetNumber);
        }
        public String getDemos () {
            return demos.get();
        } 
        public void setDemos (String demos) {
            this.demos.set(demos);
        }
        public String getTk () {
            return tk.get();
        } 
        public void setTk (String tk) {
            this.tk.set(tk);
        }
        public int getYearsBefore () {
            return yearsBefore.get();
        } 
        public void setYearsBefore (int yearsBefore) {
            this.yearsBefore.set(yearsBefore);
        }
        public String getEducation () {
            return education.get();
        } 
        public void setEducation (String education) {
            this.education.set(education);
        }
        public String getMarriage () {
            return marriage.get();
        } 
        public void setMarriage (String marriage) {
            this.marriage.set(marriage);
        }
        public int getChildren () {
            return children.get();
        } 
        public void setChildren (int children) {
            this.children.set(children);
        }
        public String getAmIka () {
            return amIka.get();
        } 
        public void setAmIka (String amIka) {
            this.amIka.set(amIka);
        }
        public String getBirthDate () {
            return birthDate.get();
        } 
        public void setBirthDate (String birthDate) {
            this.birthDate.set(birthDate);
        }
        public String getFirstHireDate () {
            return firstHireDate.get();
        } 
        public void setFirstHireDate (String firstHireDate) {
            this.firstHireDate.set(firstHireDate);
        }
        public String getFatherName () {
            return fatherName.get();
        } 
        public void setFatherName (String fatherName) {
            this.fatherName.set(fatherName);
        }
        public int getReasonIsfores () {
            return reasonIsfores.get();
        } 
        public void setReasonIsfores (int reasonIsfores) {
            this.reasonIsfores.set(reasonIsfores);
        }
         public String getTapit () {
            return tapit.get();
        } 
        public void setTapit (String tapit) {
            this.tapit.set(tapit);
        }    
        public String getSubsidiary () {
            return subsidiary.get();
        } 
        public void setSubsidiary (String subsidiary) {
            this.subsidiary.set(subsidiary);
        }
        public String getCompany () {
            return company.get();
        } 
        public void setCompany (String company) {
            this.company.set(company);
        }
        public int getKatAsfalisis () {
            return katAsfalisis.get();
        } 
        public void setKatAsfalisis (int katAsfalisis) {
            this.katAsfalisis.set(katAsfalisis);
        }
        public String getKentroKostous () {
            return kentroKostous.get();
        } 
        public void setKentroKostous (String kentroKostous) {
            this.kentroKostous.set(kentroKostous);
        }
        public String getAmEpikourikou () {
            return amEpikourikou.get();
        } 
        public void setAmEpikourikou (String amEpikourikou) {
            this.amEpikourikou.set(amEpikourikou);
        }
        public String getMotherName () {
            return motherName.get();
        } 
        public void setMotherName (String motherName) {
            this.motherName.set(motherName);
        }
        public String getArParart () {
            return arParart.get();
        } 
        public void setArParart (String arParart) {
            this.arParart.set(arParart);
        }
        public String getKad () {
            return kad.get();
        } 
        public void setKad (String kad) {
            this.kad.set(kad);
        }
        public String getKodikosIdikotitas() {
            return kodikosIdikotitas.get();
        } 
        public void setKodikosIdikotitas(String kodikosIdikotitas) {
            this.kodikosIdikotitas.set(kodikosIdikotitas);
        }
        public String getiIdikiPeriptosiAsfalisis() {
            return idikiPeriptosiAsfalisis.get();
        } 
        public void setIdikiPeriptosiAsfalisis(String idikiPeriptosiAsfalisis) {
            this.idikiPeriptosiAsfalisis.set(idikiPeriptosiAsfalisis);
        }
        public String getApolisi () {
            return apolisi.get();
        } 
        public void setApolisi (String apolisi) {
            this.apolisi.set(apolisi);
        }
        public String getDiakopi () {
            return diakopi.get();
        } 
        public void setDiakopi (String diakopi) {
            this.diakopi.set(diakopi);
        }
        public String getEmail () {
            return email.get();
        } 
        public void setEmail (String email) {
            this.email.set(email);
        }
}
