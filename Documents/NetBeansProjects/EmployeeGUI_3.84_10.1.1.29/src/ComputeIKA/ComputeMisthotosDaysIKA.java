/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ComputeIKA;

import AbsentReport.AbsentReportController;
import Enter_Data_vacation_Days.HolidaysController;
import employeegui.ErrorStage;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author Evangelos Gerogiannis
 */

public class ComputeMisthotosDaysIKA {
    private String query;   
    private ResultSet rs;
    private Connection con;
    ObservableList<ObservableList> data = FXCollections.observableArrayList();    
    private PreparedStatement prStm;
    private Statement workersStm ;
    private ResultSet workersRs; 
    private LocalDate startDate;
    private LocalDate endDate;    
    private int justify = 0;     //0 Αδικαιολόγητη, 1 Άδεια, 2 Ασθένεια < 3, 3 Ασθένεια > 3   
    public Map<Integer, Integer> MisthotosDaysIKA(Connection con,  int previousMonth, int tableYear) throws SQLException{
                        
        Map<Integer, Integer> misthotosIKAMap = new HashMap<>();
        startDate = LocalDate.of(tableYear, previousMonth, 1);        
        endDate = startDate.plusMonths(1).minusDays(1);       
        try{            
                        
            this.con = con;
            query = "SELECT workers.id FROM workers WHERE relation = 0";               
            workersStm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            workersRs = workersStm.executeQuery(query);           
            while (workersRs.next()){ 
                int daysIKA = 25;                                
                LocalDate counterDate = startDate;
                int absentcounter = 0;
                while ((counterDate.isBefore(endDate)) || (counterDate.isEqual(endDate))){
                    if ((counterDate.getDayOfWeek().getValue()!= 7)){ 
                        if(absent(counterDate, workersRs.getInt(1))&&counterDate.getDayOfWeek().getValue()!= 6){ 
                            if (justify == 0) absentcounter++;}
                    }else{      //check every week the absent days 
                            switch (absentcounter){                                
                                case 1 : {daysIKA = daysIKA - 1; break;}
                                case 2 : {daysIKA = daysIKA - 2; break;}
                                case 3 : {daysIKA = daysIKA - 4; break;}
                                case 4 : {daysIKA = daysIKA - 5; break;}  
                                case 5 : {daysIKA = daysIKA - 6; break;}
                            }
                            absentcounter = 0;
                        }                     
                counterDate = counterDate.plusDays(1);                    
                }
                misthotosIKAMap.put(workersRs.getInt(1), daysIKA);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(HolidaysController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }
        finally{            
                if (workersRs != null)workersRs.close();
                if (workersStm != null)workersStm.close();               
        }
        return misthotosIKAMap;
    }
    
    private boolean absent (LocalDate date, int id) throws SQLException{
        boolean holiday = false, vacation = false, sickLess3 = false, sickMore3 = false;
        boolean here = false;
        justify = 0;   //Αδικαιολόγητη
       String qry = "SELECT workers.id FROM workers, timer "              
               + "WHERE ((workers.id = ?) AND(timer.code = workers.code) AND "
               + "(DATE(timer.starttime) = ?)) ";
       
        try {
          prStm = con.prepareStatement(qry);
          prStm.setInt(1, id);
          prStm.setDate(2, Date.valueOf(date));          
          rs = prStm.executeQuery();
          if (rs.next()){here = true;} else {here = false;}
        } catch (SQLException ex) {
            Logger.getLogger(AbsentReportController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally {
            if (rs != null) rs.close();
            if (prStm != null)prStm.close();
        }
        if (!here){            
            qry = "SELECT holidays_column FROM holidays WHERE holidays_column = ?";
            try {
                prStm = con.prepareStatement(qry);                
                prStm.setDate(1, Date.valueOf(date));          
                rs = prStm.executeQuery();
                if (rs.next()){ holiday = true;} else {holiday = false;}
                here = holiday;
            } catch (SQLException ex) {
                Logger.getLogger(AbsentReportController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }finally {
                 if (rs != null) rs.close();
                 if (prStm != null)prStm.close();
            }
            if(!holiday){
                qry = "SELECT id FROM vacation_days "
                    + "WHERE (id  = ?) AND (? BETWEEN exit_day AND back_day)";
            try {
                prStm = con.prepareStatement(qry); 
                prStm.setInt(1, id);
                prStm.setDate(2, Date.valueOf(date));          
                rs = prStm.executeQuery(); 
                if (rs.next()) {vacation = true;} else {vacation = false;}
            } catch (SQLException ex) {
                Logger.getLogger(AbsentReportController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }finally {
                if (rs != null) rs.close();
                if (prStm != null)prStm.close();
            }
            if (vacation){justify = 1;} else {      // Άδεια
                qry = "SELECT id FROM sick_days_less_than_three "
                    + "WHERE (id  = ?) AND (? BETWEEN exit_day AND back_day)";
            try {
                prStm = con.prepareStatement(qry); 
                prStm.setInt(1, id);
                prStm.setDate(2, Date.valueOf(date));          
                rs = prStm.executeQuery();
                if (rs.next()) sickLess3 = true;
            } catch (SQLException ex) {
                Logger.getLogger(AbsentReportController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }finally {
                if (rs != null) rs.close();
                if (prStm != null)prStm.close();
            }
            if (sickLess3){justify = 2;} else {  //Ασθένεια < 3
                qry = "SELECT id FROM sick_days_more_than_three "
                    + "WHERE (id  = ?) AND (? BETWEEN exit_day AND back_day)";
            try {
                prStm = con.prepareStatement(qry); 
                prStm.setInt(1, id);
                prStm.setDate(2, Date.valueOf(date));          
                rs = prStm.executeQuery();
                if (rs.next()) sickMore3 = true;
            } catch (SQLException ex) {
                Logger.getLogger(AbsentReportController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }finally {
                if (rs != null) rs.close();
                if (prStm != null)prStm.close();
            }
            if (sickMore3){justify = 3;}  //Ασθένεια >3
            }  
            }
            
            }
        }
        
      return  !here;  
    } 

}
          
    
