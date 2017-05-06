/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

import java.sql.Connection;
import java.sql.Date;
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
public class ComputeSickLess3 {
    Connection con = null;
    Statement stm = null, stm2 = null;    
    ResultSet rs = null, rs1 = null, rs2 = null;
    int currentYear;
    int previousMonth;
    int monthWorkingDays = 0;
    List<LocalDate> holidaysList ;
    Map<Integer, Integer> sickLess3DaysMap;
    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    
        
    public Map<Integer, Integer> sickLess3Days (Connection con, int previousMonth, int tableYear) throws SQLException{
        
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
        String sql = "SELECT * FROM sick_days_less_than_three where (MONTH(exit_day) = "+Integer.toString(previousMonth)
             + ") AND (YEAR(exit_day) = "+Integer.toString(tableYear)+") ORDER BY id, exit_day";                                       
        rs = stm.executeQuery(sql);
        sickLess3DaysMap = new HashMap<>();
        
        while (rs.next()){              
            int id = rs.getInt(1); 
            int counter = 0;             
            LocalDate exitDate = rs.getDate(2).toLocalDate();
            LocalDate backDate = rs.getDate(3).toLocalDate();
            LocalDate counterDate = exitDate; 
            while (counterDate.isBefore(backDate) || counterDate.isEqual(backDate)){
                if ((counterDate.getDayOfWeek().getValue()!= 6) &&
                     (counterDate.getDayOfWeek().getValue()!= 7) &&                     
                     (counterDate.getMonthValue() == previousMonth)&&(counterDate.getYear() == currentYear))                
                       {     
                           if(!holidaysList.contains(counterDate))counter++;                            
                        }
                counterDate = counterDate.plusDays(1);
            }
            
            stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);            
            rs2 =  stm2.executeQuery("SELECT * FROM generated_less_three");
            rs2.moveToInsertRow();
            rs2.updateInt("worker_id", id);
            rs2.updateDate("start_day", Date.valueOf(exitDate));
            rs2.updateDate("end_day", Date.valueOf(backDate));
            rs2.updateInt("real_days", counter);            
            rs2.insertRow();
            rs2.moveToCurrentRow();
            
            if (rs2 != null) rs2.close();
            if (stm2 != null) stm2.close();
            
         /*Now enter data in  a HashMap with id | monthlySickLess3Days */
            
            if (!sickLess3DaysMap.containsKey(id)){                
                sickLess3DaysMap.put(id,counter);
            }else {
                counter += sickLess3DaysMap.get(id);
                sickLess3DaysMap.replace(id, counter);
            }           
        }                                                                                                                                                  
        }catch (SQLException ex) {
            Logger.getLogger(ComputeWorkingDays.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", ex.getMessage()));
        }finally{
        if (rs1 != null) {rs1.close();}
        if (rs != null) {rs.close();}
        if (stm != null) {stm.close();} 
        if (rs2 != null) rs2.close();
        if (stm2 != null) stm2.close();}
        
        return sickLess3DaysMap;           
    }    
    
}
