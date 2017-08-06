/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

/**
 *
 * @author evgero
 
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
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
@Named("goyaController1")
@SessionScoped
public class GoyaController1 implements Serializable {
    
    @Inject
    private EmplAdminsController emplAdminsController;
    private java.util.Date startDate = null;
    private java.util.Date endDate = null;
    private Workers selected;
    private short option;
    private PreparedStatement stm;
    private ResultSet rs;
    private LocalTime morning = LocalTime.of(8, 0);;
    private LocalTime evening = LocalTime.of(16, 0);
    private LocalDateTime morningStart;
    private LocalDateTime eveningEnd;    
    private int id;
    private Statement stm1;
    private ResultSet rs1;
    private List<Timer> data = new ArrayList<>();
    private Timer selectedTimer;
    private int selectedRow;

    public GoyaController1() {
        
        //    morningStart = LocalDateTime.of(date, morning);
       //     eveningEnd = LocalDateTime.of(date, evening); 
    }

    public EmplAdminsController getEmplAdminsController() {
        return emplAdminsController;
    }

    public void setEmplAdminsController(EmplAdminsController emplAdminsController) {
        this.emplAdminsController = emplAdminsController;
    }

    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
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

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }
        
    public int getSelectedRow() {        
        return selectedRow;
    }
    
    
               
    public void handleShowTable(){        
         
        LocalDate beginLocalDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        java.sql.Date beginSQLDate = null;
        java.sql.Date endSQLDate = null;        
        
        if (data!=null)data.clear();
        try {
        if(beginLocalDate != null)beginSQLDate =  java.sql.Date.valueOf(beginLocalDate); //Convert LocalDate to java.sql.Date            
        if(endLocalDate != null)endSQLDate = java.sql.Date.valueOf(endLocalDate);
        id = this.selected.getId();
        
        String str = "SELECT  timer.code, timer.starttime, timer.endtime, timer.interval_time, timer.id as timer_id ,"+
                    "timer.pc_name_in, timer.pc_ip_in, timer.pc_name_out, timer.pc_ip_out, "+
                    " workers.first_name, workers.last_name, workers.father_name "+
                    "FROM  timer JOIN workers on timer.code = workers.code "+                    
                    "WHERE (DATE(timer.starttime) BETWEEN ? AND ?) AND (workers.id = ?) ";        
            if (this.option == 1) 
                str = str + "and date(starttime) <> date(endtime) ";      
            if (this.option == 2) 
                str = str + "and date(starttime) in "+
                "(select sdate from "+
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
             
        //render the TableView rs.getInt(5)/60000)-480,           
                       //  String code, Date starttime, Date endtime, BigInteger intervalTime, 
       // Long id, String pcNameIn, String pcIpIn, String pcNameOut, String pcIpOut, String firstName, String lastName, String fatherName
        while(rs.next()){
            data.add(new Timer(rs.getString(1), java.util.Date.from(rs.getTimestamp(2).toLocalDateTime().toInstant(ZoneOffset.ofHours(3)))
                    , java.util.Date.from(rs.getTimestamp(3).toLocalDateTime().toInstant(ZoneOffset.ofHours(3))), BigInteger.valueOf(rs.getLong(4)), rs.getLong(5),
             rs.getString(6), rs.getString(7), rs.getString(8),rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12)));
                        }
        //Collections.sort(data, DATE_ORDER);
        if(data.isEmpty()){
            JsfUtil.addSuccessMessage("Δεν βρέθηκαν στοιχεία !");
            return;
        }
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
    
    public void eightEntry(){   /*Διόρθωσε σε 8ωρο βάσει ώρας Εισόδου*/        
        try {
            String str ="update timer set ENDTIME= {fn TIMESTAMPADD(SQL_TSI_HOUR,8,STARTTIME)},"+
                    "INTERVAL_TIME = {fn TIMESTAMPDIFF(SQL_TSI_FRAC_SECOND,STARTTIME,{fn TIMESTAMPADD(SQL_TSI_HOUR,8,STARTTIME)})}*0.000001"+
                    "where timer.id="+this.selectedTimer.getId().toString();
            stm = this.emplAdminsController.getCon().prepareStatement(str);
            int ok2 = stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
            try {                            
                if (stm != null)stm.close();                            
            } catch (SQLException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }  
        
    }
    
    public void eightExit(){        /*Διόρθωσε σε 8ωρο βάσει ώρας Εξόδου*/
        try {
            String str ="update timer set STARTTIME= {fn TIMESTAMPADD(SQL_TSI_HOUR,-8,ENDTIME)},"+
                    "INTERVAL_TIME = {fn TIMESTAMPDIFF(SQL_TSI_FRAC_SECOND,{fn TIMESTAMPADD(SQL_TSI_HOUR,-8,ENDTIME)},ENDTIME)}*0.000001"+
                    "where timer.id="+this.selectedTimer.getId().toString();
            stm = this.emplAdminsController.getCon().prepareStatement(str);
            int ok2 = stm.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
            try {                            
                if (stm != null)stm.close();                            
            } catch (SQLException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }  
        
    }
    
    public void eight(){
        System.out.println("this.getSelectedRow()"+this.getSelectedRow());
        LocalDate date = LocalDateTime.ofInstant(this.getSelectedTimer().
                        getStarttime().toInstant(), ZoneOffset.ofHours(3)).toLocalDate();
        morningStart = LocalDateTime.of(date, morning);
        eveningEnd = LocalDateTime.of(date, evening);
        System.out.println(morningStart);
        try{                                //Διόρθωσε σε 8ωρο 08:00-16:00
                    String str = "UPDATE timer SET starttime = ?, endtime = ?, "
                      + "interval_time = 28800000 WHERE id = ? ";                                              
                    stm = this.emplAdminsController.getCon().prepareStatement(str);                    
                    stm.setTimestamp(1, Timestamp.valueOf(morningStart));                            ;
                    stm.setTimestamp(2, Timestamp.valueOf(eveningEnd));
                    stm.setLong(3, this.selectedTimer.getId());
                   
                    int ok2 = stm.executeUpdate(); 
                    // show in table automatically
                    
                    this.setSelectedRow(data.indexOf(this.selectedTimer));
                    data.get(this.getSelectedRow()).setStarttime(java.util.Date.from(morningStart.toInstant(ZoneOffset.ofHours(3))));
                    data.get(this.getSelectedRow()).setEndtime(java.util.Date.from(eveningEnd.toInstant(ZoneOffset.ofHours(3))));
                    data.get(this.getSelectedRow()).setIntervalTime(BigInteger.valueOf(28800000)); 
                    
                    } catch (SQLException ex) {
                        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                        JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                    }
                    finally{
                        try {                            
                            if (stm != null)stm.close();                            
                        } catch (SQLException ex) {
                            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                        }
                    } 
    }
    
    public void recalculation(){
        
    }
}

