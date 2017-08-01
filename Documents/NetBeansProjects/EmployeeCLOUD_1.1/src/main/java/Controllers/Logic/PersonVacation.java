/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Home
 */
public class PersonVacation {
     private final SimpleIntegerProperty id = new SimpleIntegerProperty (0);
    private final SimpleStringProperty firstName= new SimpleStringProperty ("");
    private final SimpleStringProperty lastName = new SimpleStringProperty ("");
    private final SimpleIntegerProperty entitledDays = new SimpleIntegerProperty (0);
    private final SimpleIntegerProperty lastyearDays = new SimpleIntegerProperty (0);
    private final SimpleIntegerProperty consumedDays = new SimpleIntegerProperty (0);
    private final SimpleIntegerProperty remainingDays = new SimpleIntegerProperty (0);
    
public  PersonVacation() {    
        this(0,"","",0,0,0,0); /* Null Constructor */
        }
    
        public PersonVacation (int id, String firstName, String lastName, int entitledDays, 
                int lastyearDays, int consumedDays, int remainingDays){
                setId(id);
                setFirstName(firstName);
                setLastName(lastName);
                setEntitledDays(entitledDays);
                setLastyearDays(lastyearDays);
                setConsumedDays(consumedDays);  
                setRemainingDays(remainingDays);/*General Constructor */
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
        public int getEntitledDays () {
            return entitledDays.get();
        } 
        public void setEntitledDays (int entitledDays) {
            this.entitledDays.set(entitledDays);
        }
        public int getLastyearDays () {
            return lastyearDays.get();
        } 
        public void setLastyearDays (int lastyearDays) {
            this.lastyearDays.set(lastyearDays);
        }
        public int getConsumedDays () {
            return consumedDays.get();
        } 
        public void setConsumedDays (int consumedDays) {
            this.consumedDays.set(consumedDays);
        } 
         public int getRemainingDays () {
            return remainingDays.get();
        } 
        public void setRemainingDays (int remainingDays) {
            this.remainingDays.set(remainingDays);
        } 
    
}
