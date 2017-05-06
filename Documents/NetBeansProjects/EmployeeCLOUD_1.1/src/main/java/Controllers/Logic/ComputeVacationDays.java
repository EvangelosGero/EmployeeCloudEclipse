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
public class ComputeVacationDays {
    Connection con = null;
    Statement stm = null;
    ResultSet rs = null, rs1 = null;
    int currentYear;
    int previousMonth;
    int monthWorkingDays = 0;
    List<LocalDate> holidaysList ;
    Map<Integer, Integer> vacationDaysMap;
    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    
        
    public Map<Integer, Integer> VacationDays (Connection con, int previousMonth, int tableYear) throws SQLException{
        
        this.con = con;
        currentYear = tableYear;        
        this.previousMonth = previousMonth;
        
        try {
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        
        String sql1 = "SELECT * FROM holidays";
        rs1 = stm.executeQuery(sql1);
        
        /* Put holidays in a List */
        holidaysList  = new ArrayList<>();
        while (rs1.next()){            
            holidaysList.add(rs1.getDate(1).toLocalDate());
        }
        } catch (SQLException ex) {
            Logger.getLogger(ComputeWorkingDays.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", ex.getMessage()));
        }finally{
        if (stm != null) { stm.close();}}       
                      
        /*Commence with employee vacation  days calculation */
        
        try{
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        String sql = "SELECT * FROM VACATION_DAYS";                                      
        rs = stm.executeQuery(sql);
        vacationDaysMap = new HashMap<>();
        
        while (rs.next()){              
            int id = rs.getInt(1); 
            int counter = 0;         
            LocalDate exitDate = rs.getDate(2).toLocalDate();
            LocalDate backDate = rs.getDate(3).toLocalDate();
            LocalDate counterDate = exitDate; 
            while (counterDate.isBefore(backDate) || counterDate.isEqual(backDate)){
                if ((counterDate.getDayOfWeek().getValue()!= 6) &&
                     (counterDate.getDayOfWeek().getValue()!= 7) &&
                     (!holidaysList.contains(counterDate)) &&
                     (counterDate.getMonthValue() == previousMonth)&&(counterDate.getYear() == currentYear))                                     
                       {     counter++;                              
                        }
                counterDate = counterDate.plusDays(1);
            }
         /*Now enter data in  a HashMap with id | monthlyVacationDays */
            
            if (!vacationDaysMap.containsKey(id)){                
                vacationDaysMap.put(id,counter);
            }else {
                counter += vacationDaysMap.get(id);
                vacationDaysMap.replace(id, counter);
            }           
        }                                                                                                                                                  
        }catch (SQLException ex) {
            Logger.getLogger(ComputeWorkingDays.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", ex.getMessage()));
        }finally{
        if (rs1 != null) {rs1.close();}
        if (rs != null) {rs.close();}
        if (stm != null) {stm.close();} }       
        return vacationDaysMap;           
    }    
}
