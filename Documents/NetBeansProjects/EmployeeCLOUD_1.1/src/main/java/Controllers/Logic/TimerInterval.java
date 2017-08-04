/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

/**
 *
 * @author evgero
 */
public class TimerInterval {
    private String firstName;
    private String lastName;
    private String startTime;
    private String endTime;
    private Integer interval;
    private Integer timerId;
    
public  TimerInterval() { 
        }

    public TimerInterval(String firstName, String lastName, String startTime, String endTime, Integer interval, Integer timerId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.interval = interval;
        this.timerId = timerId;
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

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getTimerId() {
        return timerId;
    }

    public void setTimerId(Integer timerId) {
        this.timerId = timerId;
    }
    
}
