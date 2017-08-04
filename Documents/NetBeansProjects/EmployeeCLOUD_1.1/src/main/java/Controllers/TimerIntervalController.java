/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import static Controllers.Logic.TimerComparator.DATE_ORDER;
import Controllers.util.JsfUtil;
import com.dynamotors.timer1._rest.Timer;
import com.dynamotors.timer1._rest.Workers;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
         
        LocalDate beginLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        java.sql.Date beginSQLDate = null;
        java.sql.Date endSQLDate = null;        
        
        if (data!=null)data.clear();
        try {
        if(beginLocalDate != null)beginSQLDate =  java.sql.Date.valueOf(beginLocalDate); /*Convert LocalDate to java.sql.Date*/            
        if(endLocalDate != null)endSQLDate = java.sql.Date.valueOf(endLocalDate);
        id = this.selected.getId();
        
        String str = "SELECT  timer.code, timer.starttime, timer.endtime, timer.interval_time, timer.id as timer_id ,"+
                    "timer.pc_name_in, timer.pc_ip_in, timer.pc_name_out, timer.pc_ip_out, "+
                    " workers.first_name, workers.last_name, workers.father_name"+
                    "FROM  timer JOIN workers on timer.code = workers.code "+                    
                    "WHERE (DATE(timer.starttime) BETWEEN ? AND ?) AND (workers.id = ?)";        
            if (this.option == 1) 
                str = str + "and date(starttime) <> date(endtime)";      
            if (this.option == 2) 
                str = str + "and date(starttime) in "+
                "(select sdate from"+
                "(select a.code,a.sdate,count(a.sdate) as cnt from "+
                "(select timer.code,date(starttime) as sdate from timer join workers on timer.code = workers.code "+
                "where date(starttime) between '"+beginSQLDate+"' and '"+endSQLDate+"' and workers.id="+Integer.toString(id)+" "+
                "order by timer.code,date(starttime)) a "+ 
                "group by a.code,a.sdate) b "+ 
                "where b.cnt>1)";
            
            stm = emplAdminsController.getCon().prepareStatement(str);
            stm.setDate(1, beginSQLDate);
            stm.setDate(2, endSQLDate);
            stm.setInt(3, id);
            rs = stm.executeQuery();
             
        /* render the TableView rs.getInt(5)/60000)-480,*/            
                       //  String code, Date starttime, Date endtime, BigInteger intervalTime, 
       // Long id, String pcNameIn, String pcIpIn, String pcNameOut, String pcIpOut, String firstName, String lastName, String fatherName
        while(rs.next()){
            data.add(new Timer(rs.getString(1), rs.getDate(2), rs.getDate(3), BigInteger.valueOf(rs.getLong(4)), rs.getLong(5),
             rs.getString(6), rs.getString(7), rs.getString(8),rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
                        }
        Collections.sort(data, DATE_ORDER);
            } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
        finally{
            try {
                if(rs != null)rs.close();
                if(stm != null)stm.close();                
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }
}
