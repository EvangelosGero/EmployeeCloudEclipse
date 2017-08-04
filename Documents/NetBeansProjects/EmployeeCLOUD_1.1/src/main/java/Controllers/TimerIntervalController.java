/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import com.dynamotors.timer1._rest.Timer;
import com.dynamotors.timer1._rest.Workers;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author evgero
 */
@Named("timerIntervalController")
@SessionScoped
public class TimerIntervalController implements Serializable {
    
    @Inject
    private EmplAdminsController emplAdminsController;
    private java.util.Date startDate = null;
    private java.util.Date endDate = null;
    private Workers selected;
    private short option;
    private PreparedStatement stm;
    private ResultSet rs;
    private LocalTime morning;
    private LocalTime evening;
    private LocalDateTime morningStart;
    private LocalDateTime eveningEnd;
    //private int timerid;
    int id;
    private Statement stm1;
    private ResultSet rs1;
    private List<Timer> data = new ArrayList<>();
    private Timer selectedTimer;

    public TimerIntervalController() {
    }

    public EmplAdminsController getEmplAdminsController() {
        return emplAdminsController;
    }

    public void setEmplAdminsController(EmplAdminsController emplAdminsController) {
        this.emplAdminsController = emplAdminsController;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Workers getSelected() {
        return selected;
    }

    public void setSelected(Workers selected) {
        this.selected = selected;
    }

    public short getOption() {
        return option;
    }

    public void setOption(short option) {
        this.option = option;
    }

    public List<Timer> getData() {
        return data;
    }

    public void setData(List<Timer> data) {
        this.data = data;
    }

    public Timer getSelectedTimer() {
        return selectedTimer;
    }

    public void setSelectedTimer(Timer selectedTimer) {
        this.selectedTimer = selectedTimer;
    }
    
               
    public void handleShowTable(){
        
    }
}
