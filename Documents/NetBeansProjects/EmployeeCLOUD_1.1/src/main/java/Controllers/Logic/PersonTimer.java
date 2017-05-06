/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Home
 */
public class PersonTimer {
    private final SimpleIntegerProperty id = new SimpleIntegerProperty (0);
    private final SimpleStringProperty firstName= new SimpleStringProperty ("");
    private final SimpleStringProperty lastName = new SimpleStringProperty ("");
    private final SimpleIntegerProperty workDays = new SimpleIntegerProperty (0);
    private final SimpleIntegerProperty absentDays = new SimpleIntegerProperty (0);
    private final SimpleIntegerProperty occupiedDays = new SimpleIntegerProperty (0);
    private final SimpleIntegerProperty sickDaysLess3 = new SimpleIntegerProperty (0);
    private final SimpleIntegerProperty sickDaysMore3 = new SimpleIntegerProperty (0);
    private final SimpleIntegerProperty vacationDays = new SimpleIntegerProperty (0);
    private final SimpleDoubleProperty cutHours = new SimpleDoubleProperty (0);
    private final SimpleIntegerProperty superjobHours = new SimpleIntegerProperty (0);
    private final SimpleIntegerProperty superHours = new SimpleIntegerProperty (0);
            
        
public  PersonTimer() {    
        this(0,"","",0,0,0,0,0,0,0,0,0); /* Null Constructor */
        }
    
        public PersonTimer (int id, String firstName, String lastName, int workDays ,
                int absentDays, int occupiedDays, int sickDaysLess3, int sickDaysMore3,
                 int vacationDays, double cutHours, int superjobHours, int superHours ){
                setId(id);
                setFirstName(firstName);
                setLastName(lastName);
                setWorkDays(workDays);
                setAbsentDays(absentDays);
                setOccupiedDays(occupiedDays);
                setSickDaysLess3(sickDaysLess3);
                setSickDaysMore3(sickDaysMore3);
                setVacationDays(vacationDays);
                setCutHours(cutHours);
                setSuperjobHours(superjobHours);
                setSuperHours(superHours); /*General Constructor */
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
        public int getWorkDays () {
            return workDays.get();
        }
        public void setWorkDays(int workDays) {
            this.workDays.set(workDays);
        }
        public int getAbsentDays () {
            return absentDays.get();
        }
        public void setAbsentDays(int absentDays) {
            this.absentDays.set(absentDays);
        }
        public int getOccupiedDays () {
            return occupiedDays.get();
        }
        public void setOccupiedDays(int occupiedDays) {
            this.occupiedDays.set(occupiedDays);
        }
        public int getSickDaysLess3 () {
            return sickDaysLess3.get();
        }
        public void setSickDaysLess3(int sickDaysLess3) {
            this.sickDaysLess3.set(sickDaysLess3);
        }
        public int getSickDaysMore3 () {
            return sickDaysMore3.get();
        }
        public void setSickDaysMore3(int sickDaysMore3) {
            this.sickDaysMore3.set(sickDaysMore3);
        }
        public int getVacationDays () {
            return vacationDays.get();
        }
        public void setVacationDays(int vacationDays) {
            this.vacationDays.set(vacationDays);
        }
        public double getCutHours () {
            return cutHours.get();
        }
        public void setCutHours(double cutHours){
            this.cutHours.set(cutHours);
        }
        public int getSuperjobHours () {
            return superjobHours.get();
        }
        public void setSuperjobHours(int superjobHours){
            this.superjobHours.set(superjobHours);
        }
        public int getSuperHours () {
            return superHours.get();
        }
        public void setSuperHours(int superHours) {
            this.superHours.set(superHours);
        }
                                                            
}

    

