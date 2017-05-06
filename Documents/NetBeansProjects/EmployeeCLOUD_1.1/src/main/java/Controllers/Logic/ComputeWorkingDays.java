/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Home
 */
public class ComputeWorkingDays {
    
    Connection con = null;
    Statement stm = null;
    ResultSet rs = null, rs1 = null;
    String sql;
    int currentYear;
    int previousMonth;
    int monthWorkingDays = 0;
    List<LocalDate> holidaysList ;
    Map<Integer, Integer> employeeWorkDaysMap;
    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    
    public int getMonthWorkingDays(){
        return monthWorkingDays;
    }
    
    public Map<Integer, Integer> WorkingDays (Connection con, int previousMonth, int tableYear) throws SQLException{
        
        this.con = con;
        currentYear = tableYear;        
        this.previousMonth = previousMonth;
        
        try {
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        
        String sql1 = "SELECT * FROM holidays";
        rs1 = stm.executeQuery(sql1);
        
        /* Put rs1 in a List */
        holidaysList  = new ArrayList<>();
        while (rs1.next()){            
            holidaysList.add(rs1.getDate(1).toLocalDate());
        }
        } catch (SQLException ex) {
            Logger.getLogger(ComputeWorkingDays.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", ex.getMessage()));
        }finally{
        if (stm != null) { stm.close();}}       
                      
        /*Commence month working days calculation and then employee work days calculation */
        
        try{        
            sql = "SELECT DISTINCT id, DATE(endtime) FROM temp_id WHERE "+
                    "MONTH(endtime) = "+Integer.toString(previousMonth)+
                    " AND YEAR(endtime) = "+Integer.toString(currentYear);   
       
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);                              
        rs = stm.executeQuery(sql);        
                  
     
        LocalDate findMonthAndYear = LocalDate.of(tableYear, previousMonth, 1);  /*SZ*/
        //LocalDate findMonthAndYear = LocalDate.now().minusMonths(1);/*We compute the first day of the month*/        
        LocalDate firstDayOfMonth = findMonthAndYear.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = findMonthAndYear.with(TemporalAdjusters.lastDayOfMonth());
        LocalDate counterDate = firstDayOfMonth;
        while (counterDate.isBefore(lastDayOfMonth) || counterDate.isEqual(lastDayOfMonth)) {
           if ((counterDate.getDayOfWeek().getValue()!= 6) &&
                     (counterDate.getDayOfWeek().getValue()!= 7) &&
                     (!holidaysList.contains(counterDate))
              )
                       {     monthWorkingDays++;                              
                        }
           counterDate = counterDate.plusDays(1);
        }
        
        /*Now construct a HashMap with id | employeeWorkDays */
        int counter = 1;
        employeeWorkDaysMap = new HashMap<>();
        while (rs.next()){ 
            int id = rs.getInt(1);                
            LocalDate employeeLocalDate = rs.getDate(2).toLocalDate();                                       
            if ((employeeLocalDate.getDayOfWeek().getValue()!= 6) &&
                (employeeLocalDate.getDayOfWeek().getValue()!= 7) &&
                (!holidaysList.contains(employeeLocalDate))) 
                       {                                                                                                                
            if (!employeeWorkDaysMap.containsKey(id)){
                counter = 1;
                employeeWorkDaysMap.put(id,counter);
            }else {
                counter = employeeWorkDaysMap.get(id)+1;
                employeeWorkDaysMap.replace(id, counter);
            }           
           }
        }
        }catch (SQLException ex) {
            Logger.getLogger(ComputeWorkingDays.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", ex.getMessage()));
        }finally{
        if (rs1 != null) {rs1.close();}
        if (rs != null) {rs.close();}
        if (stm != null) {stm.close();} }
        
        return employeeWorkDaysMap;
           
    }
    
}
