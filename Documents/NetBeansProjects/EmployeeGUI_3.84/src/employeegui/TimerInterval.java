/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Home
 */
public class TimerInterval {    
    private final SimpleStringProperty firstName= new SimpleStringProperty ("");
    private final SimpleStringProperty lastName = new SimpleStringProperty ("");
    private final SimpleStringProperty startTime = new SimpleStringProperty ("");
    private final SimpleStringProperty endTime= new SimpleStringProperty ("");
    private final SimpleIntegerProperty interval = new SimpleIntegerProperty (0);
    private final SimpleIntegerProperty timerId = new SimpleIntegerProperty (0);
    
public  TimerInterval() {    
        this("","","","",0,0); /* Null Constructor */
        }
    
        public TimerInterval (String firstName, String lastName, String startTime,
                String endTime, int interval, int timerId){                
                setFirstName(firstName);
                setLastName(lastName);
                setStartTime(startTime);
                setEndTime(endTime);
                setInterval(interval);
                setTimerId(timerId);/*General Constructor */
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
        public String getStartTime () {
            return startTime.get();
        } 
        public void setStartTime (String startTime) {
            this.startTime.set(startTime);
        }
        public String getEndTime () {
            return endTime.get();
        } 
        public void setEndTime (String endTime) {
            this.endTime.set(endTime);
        }
        public int getInterval () {
            return interval.get();
        } 
        public void setInterval (int interval) {
            this.interval.set(interval);
        }                                                                            
        public int getTimerId () {
            return timerId.get();
        } 
        public void setTimerId (int timerId) {
            this.timerId.set(timerId);
        }                                                                                    
}

    

