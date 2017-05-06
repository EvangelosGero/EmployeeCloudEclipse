/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Home
 */
public class ComputeSickMore3 {
    Connection con = null;
    Statement stm = null, stm2 = null;
    ResultSet rs = null, rs1= null, rs2 = null;
    PreparedStatement prstm = null;
    int currentYear;
    int previousMonth;
    int monthWorkingDays = 0;
    List<LocalDate> holidaysList ;
    
    // Wrong ! You gotta return all small intervals with start - end 
    
    Map<Integer, Integer> sickMore3DaysMap;  //Returns real days (working days with more than 3 sickness    
    Map<Integer, SickPOJO> startupSickLess3DaysMap; //Returns up to three days and only once per workYear where the sickness started 
    Map<Integer, LocalDate> hireDateMap;   
    private LocalDate startDay;
    private LocalDate endDay;
    private LocalDate workYearEnd = null;
    private int remainingDays = 0;    
    private Map<Integer, Integer> relationMap;
    public Map<Integer, Integer> IKAtotalMap;
    public Map<Integer, Integer> sickMore3DaysMapWithoutPayment;
    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    
        
    public Map<Integer, Integer> sickMore3Days (Connection con, int previousMonth, int tableYear) throws SQLException{
        
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
            if (stm != null) { stm.close();}  
            if (rs1 != null) {rs1.close();}
        }
        
        /* Create hireDateMap */
        
        try {
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        
            String sql2 = "SELECT workers.id, workers.relation, workers.hire_date FROM workers";
            rs1 = stm.executeQuery(sql2);        
        
            relationMap = new HashMap<>();
            hireDateMap  = new HashMap<>();        
            while (rs1.next()){ 
                relationMap.put(rs1.getInt(1), rs1.getInt(2));            
                hireDateMap.put(rs1.getInt(1), rs1.getDate(3).toLocalDate());            
            }
        } catch (SQLException ex) {
            Logger.getLogger(ComputeWorkingDays.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", ex.getMessage()));
        }finally{
        if (stm != null) { stm.close();} 
        if (rs1 != null) {rs1.close();}
        }  
        
        //first perform cleanup
            
            stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT * FROM generated_less_three where (MONTH(start_day) = "+Integer.toString(previousMonth)
             + ") AND (YEAR(start_day) = "+Integer.toString(tableYear)+")";                    
            rs2 =  stm2.executeQuery(query);
            
            while(rs2.next()){
                query = "DELETE FROM sick_days_less_than_three WHERE id = ? AND exit_day = ? AND back_day = ?";
                prstm = con.prepareStatement(query);
                prstm.setInt(1, rs2.getInt("worker_id"));
                prstm.setDate(2, rs2.getDate("start_day"));
                prstm.setDate(3, rs2.getDate("end_day"));
                prstm.executeUpdate();
            }
            if (rs2 != null) rs2.close();
            if (stm2 != null) stm2.close();
            if(prstm != null) prstm.close();
            
            query = "DELETE FROM generated_less_three WHERE (MONTH(start_day) = ?) AND (YEAR(start_day) = ?)";
             
            prstm = con.prepareStatement(query);
            prstm.setInt(1, previousMonth);
            prstm.setInt(2, currentYear);
            prstm.executeUpdate();
            if(prstm != null) prstm.close();
        
        /*Commence with sick More 3  days calculation */
        
        try{
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        String sql = "SELECT * FROM sick_days_more_than_three where (MONTH(exit_day) = "+Integer.toString(previousMonth)
             + ") AND (YEAR(exit_day) = "+Integer.toString(tableYear)+") ORDER BY id, exit_day";                                      
        rs = stm.executeQuery(sql);
        
        sickMore3DaysMap = new HashMap<>();
        sickMore3DaysMapWithoutPayment = new HashMap<>();
        IKAtotalMap = new HashMap<>();  
        
        int flag = 0;
        boolean changed = false;      
        
        while (rs.next()){           
            
            int less3Counter = 0;
            LocalDate less3EndDate = null;
            changed = false;
            int id = rs.getInt(1); 
            if (id != flag){
                flag = id;
                changed = true;
            }
            
            if (changed){
                
                
                
                //Clear generated_more_three_table for this month
        
                stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                sql = "DELETE FROM generated_more_three WHERE worker_id = "
                    +Integer.toString(id)+" AND MONTH(start_day) = "+Integer.toString(this.previousMonth)
                    +" AND YEAR(start_day) = "+Integer.toString(currentYear);
                stm2.executeUpdate(sql);                
                if (stm2 != null) stm2.close();
                
                stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                sql = "SELECT * FROM generated_more_three WHERE worker_id = "
                    +Integer.toString(id)+" ORDER BY start_day";
                rs2 =  stm2.executeQuery(sql);            
                if(rs2.last()){
                    workYearEnd = (rs2.getDate("work_year")).toLocalDate();
                    remainingDays = rs2.getInt("remaining_days");
                }
            }            
                        
            LocalDate exitDate = startDay = rs.getDate(2).toLocalDate();            
            LocalDate backDate = endDay = rs.getDate(3).toLocalDate();
            if(workYearEnd == null || workYearEnd.isBefore(exitDate.minusYears(1))){ //first time sick or more than a year has passed                
                workYearEnd = exitDate.plusYears(1);
                remainingDays = hireDateMap.get(id).plusDays(10).isAfter(exitDate) ? 0 :
                        hireDateMap.get(id).plusYears(1).isAfter(exitDate) ? 13 : relationMap.get(id) == 0 ? 25 : 26 ;
            }
            LocalDate counterDate = exitDate; 
            int realCounter = 0;
            int counterIKA = 0;
            int counterOfUnpaid = 0;
            int counterOfUnpaidIKA = 0;
            while (counterDate.isBefore(backDate) || counterDate.isEqual(backDate) ){                
                
                if  ((counterDate.getDayOfWeek().getValue()!= 7) &&                     
                     (counterDate.getMonthValue() == previousMonth)&&(counterDate.getYear() == currentYear) && 
                        counterDate.isBefore(workYearEnd.plusDays(1))){
                    if(remainingDays == 0)counterOfUnpaid++;
                    if(counterDate.getDayOfWeek().getValue() != 6 && !holidaysList.contains(counterDate)) {
                        if (remainingDays != 0)realCounter++;
                        if (remainingDays == 0)counterOfUnpaidIKA++;
                        if (remainingDays - relationMap.get(id) < 26 && remainingDays - relationMap.get(id) - less3Counter >22){                           
                            less3Counter++;
                            less3EndDate = counterDate;                            
                        }
                    }
                    if (remainingDays > 0){               
                      
                            remainingDays--;
                            counterIKA++; 
                            if(remainingDays == 0)endDay = counterDate;                        
                    }
                }else if (counterDate.isAfter(workYearEnd)){  //Change of Year
                        remainingDays = relationMap.get(id) == 0 ? 25 : 26 ;
                        workYearEnd = counterDate.plusYears(1);
                        remainingDays--;
                        less3Counter++;
                        counterIKA++;                        
                    }
                if (counterDate == counterDate.with(TemporalAdjusters.lastDayOfMonth()) && remainingDays != 0){
                   endDay = counterDate;
                   break; 
                }
                
                counterDate = counterDate.plusDays(1);
                
            }
            
            //persist in generated_more_three or/and generated_less_three
            
            if (less3Counter != 0){
                realCounter -= less3Counter; 
                counterIKA -= less3Counter; //if there is a saturday or holiday in <3 it should be counted in overall >3
                startDay = less3EndDate.plusDays(1).getDayOfWeek().getValue() != 7 ? less3EndDate.plusDays(1) : less3EndDate.plusDays(2);               
                
                //persist in sick_days_less_than_three
                
                if (rs2 != null) rs2.close();
                if (stm2 != null) stm2.close();
            
                stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);            
                rs2 =  stm2.executeQuery("SELECT * FROM sick_days_less_than_three");
                rs2.moveToInsertRow();
                rs2.updateInt("id", id);
                rs2.updateDate("exit_day", Date.valueOf(exitDate));
                rs2.updateDate("back_day", Date.valueOf(less3EndDate));
                rs2.insertRow();
                rs2.moveToCurrentRow();
            }
            
            if (rs2 != null) rs2.close();
            if (stm2 != null) stm2.close();
            
            stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);            
            rs2 =  stm2.executeQuery("SELECT * FROM generated_more_three");
            rs2.moveToInsertRow();
            rs2.updateInt("worker_id", id);
            
            //Check for the case where in this month there are only 2 or 3 <3 days out of the >3 days e.g. 30/5, 31/5.
            
            if(startDay.isAfter(endDay)){
                startDay = endDay;
                realCounter = 0;
                counterIKA = 0;
            }            
            
            rs2.updateDate("start_day", Date.valueOf(startDay));
            rs2.updateDate("end_day", Date.valueOf(endDay));
            rs2.updateInt("real_days", realCounter);
            rs2.updateInt("ika_days", counterIKA);
            rs2.updateDate("work_year", Date.valueOf(workYearEnd));
            rs2.updateInt("remaining_days", remainingDays);
            rs2.insertRow();
            rs2.moveToCurrentRow();
            
         /*Now enter data in  a HashMap with id | monthlySickMore3Days */            
            
            if (!sickMore3DaysMap.containsKey(id)){                
                sickMore3DaysMap.put(id,realCounter+counterOfUnpaidIKA);
            }else {
                realCounter += sickMore3DaysMap.get(id);
                sickMore3DaysMap.replace(id, realCounter+counterOfUnpaidIKA);
            } 
            if (!IKAtotalMap.containsKey(id)){                
                IKAtotalMap.put(id, counterIKA);
            }else {
                counterIKA += IKAtotalMap.get(id);
                IKAtotalMap.replace(id, counterIKA);
            }
            if((counterOfUnpaid != 0)){
                if (!sickMore3DaysMapWithoutPayment.containsKey(id)){                
                    sickMore3DaysMapWithoutPayment.put(id, counterOfUnpaid);
                }else {
                counterOfUnpaid += sickMore3DaysMapWithoutPayment.get(id);
                    sickMore3DaysMapWithoutPayment.replace(id, counterOfUnpaid);
                }
            }
            if (rs2 != null) rs2.close();
            if (stm2 != null) stm2.close();
            
                      
       }
        //persist sickMore3DaysMapWithoutPayment in generated_sick_more_three_unpaid
        
            //but first perform clean-up
            
            stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            sql = "DELETE FROM generated_sick_more_three_unpaid WHERE sick_month = "+Integer.toString(this.previousMonth )
                  +" AND sick_year = "+Integer.toString(currentYear);
            stm2.executeUpdate(sql);                
            if (stm2 != null) stm2.close();
            
            for (Entry<Integer, Integer> entry : sickMore3DaysMapWithoutPayment.entrySet()){
                query = "INSERT INTO generated_sick_more_three_unpaid VALUES(?, ?, ?, ?)";
                prstm = con.prepareStatement(query);
                prstm.setInt(1, entry.getKey());
                prstm.setInt(2, previousMonth);
                prstm.setInt(3, currentYear);
                prstm.setInt(4, entry.getValue());
                prstm.executeUpdate();
                if(prstm != null)prstm.close();
            } 
                                                                                                                                      
        }catch (SQLException ex) {
            Logger.getLogger(ComputeWorkingDays.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", ex.getMessage()));
        }finally{
        if (rs1 != null) rs.close();
        if (rs != null) rs.close();
        if (stm != null) stm.close(); 
        if(prstm != null) prstm.close();
        if (rs2 != null) rs2.close();
        if (stm2 != null) stm2.close();
        }
        return sickMore3DaysMap;           
    }
        
}
