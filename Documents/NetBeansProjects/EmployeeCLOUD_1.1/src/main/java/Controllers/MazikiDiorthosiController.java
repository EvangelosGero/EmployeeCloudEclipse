/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.util.JsfUtil;
import com.dynamotors.timer1._rest.Workers;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author evgero
 */
@Named("mazikiDiorthosiController")
@SessionScoped
public class MazikiDiorthosiController implements Serializable {
    
    @Inject
    private EmplAdminsController emplAdminsController;
    private Connection con = null;
    private List<Workers> selectedList = new ArrayList<>();
    private PreparedStatement prStm ;
    private Statement workersStm ;
    private ResultSet workersRs, rs;
    java.util.Date startDate = null;
    java.util.Date endDate = null;
    private LocalDate hireDate, apolisiDate;
    private List<LocalDate> holidaysList ;
    private Map<Integer, List<List<LocalDate>>> vacationDaysMap = new HashMap<>();
    private Map<Integer, List<List<LocalDate>>> sickLess3DaysMap = new HashMap<>();
    private Map<Integer, List<List<LocalDate>>> sickMore3DaysMap = new HashMap<>();
    
    
    public MazikiDiorthosiController(){      
       
    }
    
    @PostConstruct
    public void init() { 
        this.con = emplAdminsController.getCon();
        produceHolidayList();
        vacationDaysMap.clear();
        vacationDaysMap.putAll( produceDaysMap("VACATION_DAYS")); 
        sickLess3DaysMap.clear();
        sickLess3DaysMap.putAll(produceDaysMap("SICK_DAYS_LESS_THAN_THREE"));       
        sickMore3DaysMap.clear();
        sickMore3DaysMap.putAll(produceDaysMap("SICK_DAYS_MORE_THAN_THREE"));
        selectedList.clear();
   }

    public List<Workers> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<Workers> selectedList) {
        this.selectedList = selectedList;
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

    public EmplAdminsController getEmplAdminsController() {
        return emplAdminsController;
    }

    public void setEmplAdminsController(EmplAdminsController emplAdminsController) {
        this.emplAdminsController = emplAdminsController;
    }
    
    
    public void handleDiorthoseBtn(){
       LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
       for(Workers worker : selectedList){
            int id = worker.getId();            
            LocalDate counterDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            hireDate = worker.getHireDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();           
            if (worker.getApolisi() != 0)apolisiDate = worker.getDiakopi().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                        else apolisiDate = LocalDate.now().plusDays(1);
            while (((counterDate.isBefore(endLocalDate)) || (counterDate.isEqual(endLocalDate)))
                        &&(counterDate.isBefore(apolisiDate)) || (counterDate.isEqual(apolisiDate))){
                    if ((counterDate.getDayOfWeek().getValue()!= 6) &&
                     (counterDate.getDayOfWeek().getValue()!= 7) && (counterDate.isAfter(hireDate))
                            && !holidaysList.contains(counterDate)                           
                            && (vacationDaysMap.get(id) != null ?  allowedDays(id, vacationDaysMap, counterDate) : true)
                            && (sickLess3DaysMap.get(id) != null ?  allowedDays(id, sickLess3DaysMap, counterDate) : true)
                            && (sickMore3DaysMap.get(id) != null ?  allowedDays(id, sickMore3DaysMap, counterDate) : true)
                       ){
                         insertOrUpdate(id, counterDate);
                    }
            counterDate = counterDate.plusDays(1); 
            }
        }
        JsfUtil.addSuccessMessage("Η μαζική διόρθωση ολοκληρώθηκε!"); 
    }
    
    public void insertOrUpdate(int id, LocalDate date){
        PreparedStatement stm = null, stm1 = null ;
        ResultSet rs5 = null;
        LocalTime morning = LocalTime.of(8, 0);
        LocalTime evening = LocalTime.of(16, 0);
        LocalDateTime morningStart = LocalDateTime.of(date, morning);
        LocalDateTime eveningEnd = LocalDateTime.of(date, evening);
        String code;
        try{
            //first find the code for that id            
            String str = "SELECT DISTINCT (timer.code)" +
                        " FROM timer, workers WHERE timer.code = workers.code AND" +
                        " workers.id = ?";
            stm = emplAdminsController.getCon().prepareStatement(str);
            stm.setInt(1, id);
            rs5 = stm.executeQuery();
            rs5.next();
            code = rs5.getString("code");
            if (rs5 != null)rs5.close(); 
            if (stm != null)stm.close();           
          
           /* str = "MERGE INTO timer t "   DOES NOT WORK !!
                + "USING workers w "
                + "ON t.code = ? AND DATE(t.starttime) = ?"
                + "WHEN MATCHED  " +
                " THEN UPDATE SET starttime = ?, endtime = ?, interval_time = 28800000" +
                " WHEN NOT MATCHED " +
                " THEN INSERT (code, starttime, endtime, interval_time, "
                            + "pc_name_in, pc_ip_in, pc_name_out, pc_ip_out) "
                            + "VALUES (?," +
                        " ?, ?, 28800000, null, null, null, null)"; */
            
            str = "SELECT * FROM timer where code = ? and DATE(starttime) = ?";
            stm = emplAdminsController.getCon().prepareStatement(str);
            stm.setString(1, code);                                    
            stm.setDate(2, java.sql.Date.valueOf(date));
            rs5 = stm.executeQuery();
            
            // There might be 0 or more records for given date, so first delete them all
            while (rs5.next()){
                str = "DELETE FROM timer where id = ?";
                stm1 = emplAdminsController.getCon().prepareStatement(str);
                stm1.setInt(1, rs5.getInt("ID"));
                stm1.executeUpdate();
            }
            if (rs5 != null)rs5.close(); 
            if (stm != null)stm.close();
            if (stm1 != null)stm1.close();
            
            str="INSERT INTO TIMER (code, starttime, endtime, interval_time)"
                    + "VALUES( ?, ?, ?, 28800000) ";
            stm = emplAdminsController.getCon().prepareStatement(str);
            stm.setString(1, code);
            stm.setTimestamp(2, Timestamp.valueOf(morningStart));                            ;
            stm.setTimestamp(3, Timestamp.valueOf(eveningEnd));                 
            int ok2 = stm.executeUpdate();
            
        }catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                   
        }finally{
            try {
                if (rs5 != null)rs5.close(); 
                if (stm != null)stm.close();
                if (stm1 != null)stm1.close();
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }        
    }
    
    public void produceHolidayList(){
        Statement stm =null;
        ResultSet rs1 =null;
         try{            
            stm = this.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE); 
            String sql1 = "SELECT * FROM holidays";
            rs1 = stm.executeQuery(sql1);
        
            /* Put rs1 in a List */
            holidaysList  = new ArrayList<>();
            while (rs1.next()){            
                holidaysList.add(rs1.getDate(1).toLocalDate());
            }
        }catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally {
            try {
                if (stm != null)stm.close();
                if (rs1 != null )rs1.close();
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }
    
    public Map<Integer, List<List<LocalDate>>> produceDaysMap(String table){
        int id;
        Statement stm =null;
        ResultSet rs1 =null;  
        Map<Integer, List<List<LocalDate>>> map = new HashMap<>();
        map.clear();
        try{
            stm = emplAdminsController.getCon().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM "+table;                                      
            rs1 = stm.executeQuery(sql);
            while(rs1.next()){
                id = rs1.getInt("ID");
                List<LocalDate> intervalList = new ArrayList<>();
                intervalList.add(rs1.getDate("EXIT_DAY").toLocalDate());
                intervalList.add(rs1.getDate("BACK_DAY").toLocalDate());
                if (map.keySet().contains(id)){
                    map.get(id).add(intervalList);
                }else{
                    List<List<LocalDate>> intervalsList = new ArrayList<>();
                    intervalsList.add(intervalList);
                    map.put(id, intervalsList);
                }
            }            
            }catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex); 
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally {
            try {
                if (stm != null)stm.close();
                if (rs1 != null )rs1.close();
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }       
        return map;
    }
    
    public boolean allowedDays(int id, Map<Integer, List<List<LocalDate>>> map, LocalDate counterDate){
        
        boolean allowed = true;
        for(List<LocalDate> intervalList : map.get(id)){
            if(!(counterDate.isBefore(intervalList.get(0))
                ||counterDate.isAfter(intervalList.get(1))))
                     allowed = false; 
         }       
        return allowed;
    }
    
}
