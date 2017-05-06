/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.print.attribute.standard.Severity;
import Controllers.util.DropIfExists;

/**
 *
 * @author Home
 
 */
public class CreateReport {
    
    Connection con = null;
    ResultSet rs = null, rs1 = null;
    Statement stm = null;
    private int  previousMonth, monthWorkingDays, tableYear;
    private int update;
    Map<Integer, Integer> employeeWorkDaysMap;
    Map<Integer, Integer> vacationDaysMap;
    Map<Integer, Integer> sickLess3DaysMap;
    Map<Integer, Integer> sickMore3DaysMap;
    PreparedStatement prStm = null;
    private final FacesContext facesContext = FacesContext.getCurrentInstance();
    
    public void CreateMonthlyDBTable(Connection con, LocalDate paretisiDate){
        
        try {
            
            this.con = con;
            
            //String fetchTimerData = "SELECT * FROM Timer";
            //stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            //rs = stm.executeQuery(fetchTimerData);
            
            /* find which is the previous month */        
            
            previousMonth = paretisiDate == null ? LocalDate.now().minusMonths(1).getMonthValue() :
                    paretisiDate.getMonthValue();
            tableYear = paretisiDate == null ? LocalDate.now().minusMonths(1).getYear() :
                    paretisiDate.getYear();       
            String tableStr = "REPORT_"+Integer.toString(previousMonth)
            +"_"+Integer.toString(tableYear);
            //if (rs != null){rs.close();}
            //if (stm != null){stm.close();}
            
            /* Check if the table exists and delete it */
            
            DropIfExists.exec(con, "TABLE", tableStr);
            
            /*create Database Table*/
            
            String query = "CREATE TABLE "+tableStr+"(ID SMALLINT, First_Name VARCHAR(20), "
            + "Last_Name VARCHAR(20), work_days SMALLINT, absent_days SMALLINT, "
            + "occupied_days SMALLINT, Sick_days_less_3 SMALLINT, "
            + "Sick_days_more_3 SMALLINT, Vacation_days SMALLINT, "
            + "Cut_hours DECIMAL(5,2), Superjob_hours SMALLINT, super_hours SMALLINT)";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}            
            
            /* Perform Initialization of Database */
            
            DropIfExists.exec(con, "TABLE", "VACATION_TOTAL");                    
            DropIfExists.exec(con, "TABLE", "SICKLESS3_TOTAL");
            DropIfExists.exec(con, "TABLE", "SICKMORE3_TOTAL");
            DropIfExists.exec(con, "VIEW", "REPORT1");
            DropIfExists.exec(con, "TABLE", "REPORT20");
            DropIfExists.exec(con, "VIEW", "MONTH_WORK_DAYS");
            DropIfExists.exec(con, "TABLE", "EMPLOYEE_WORK_DAYS");
            DropIfExists.exec(con, "VIEW", "HOURS_WORKED");
            DropIfExists.exec(con, "VIEW", "TEMP_ID");
            
                        /*populate Report Table with data*/
            query = "CREATE VIEW TEMP_ID  AS "+
            "SELECT workers.ID, workers.code, timer.endtime, timer.interval_time "+
            "FROM workers, timer where ((workers.code = timer.code) "
             + " AND (MONTH(timer.endtime) = "+Integer.toString(previousMonth)
             + ") AND (YEAR(timer.endtime) = "+Integer.toString(tableYear)
             + ") AND ((YEAR(workers.hire_date) < "+Integer.toString(tableYear)
             + ") OR (( YEAR(workers.hire_date) = "+Integer.toString(tableYear)
             + ") AND (MONTH(workers.hire_date) <= "+Integer.toString(previousMonth)
             + "))) AND (workers.apolisi = 0 OR (workers.apolisi <> 0 AND (MONTH(workers.diakopi) >= "
             +Integer.toString(previousMonth)+" ) AND (YEAR(workers.diakopi) >= "
             +Integer.toString(tableYear)+ "))))";
                
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}     
            
                               
            query = "CREATE VIEW HOURS_WORKED (Hours, ID) AS "+
            "SELECT SUM(interval_time), ID "+
            "FROM Temp_ID "+
            " GROUP BY ID ";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
  
            /* Incorporate manual calculation of workdays */           
                        
            ComputeWorkingDays computeWorkingDays = new ComputeWorkingDays();
            employeeWorkDaysMap = computeWorkingDays.WorkingDays(con, previousMonth, tableYear);
            monthWorkingDays = computeWorkingDays.getMonthWorkingDays();
            
            vacationDaysMap =(new ComputeVacationDays()).VacationDays(con, previousMonth, tableYear);
            sickMore3DaysMap =(new ComputeSickMore3()).sickMore3Days(con, previousMonth, tableYear); 
            sickLess3DaysMap =(new ComputeSickLess3()).sickLess3Days(con, previousMonth, tableYear);
            
            
            
                 
            query = "CREATE TABLE Employee_Work_Days( id SMALLINT, workdays SMALLINT)";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            for(Map.Entry<Integer, Integer> entry : employeeWorkDaysMap.entrySet())
            {        
                String str = "INSERT INTO EMPLOYEE_WORK_DAYS VALUES (?, ?) ";        
                prStm = con.prepareStatement(str); 
                prStm.setInt(1, entry.getKey());   
                prStm.setInt(2, entry.getValue());                
                int updatedRows = prStm.executeUpdate();    
                if (prStm != null) {prStm.close();}
            }
            
            
            
            query = "CREATE VIEW month_work_days (month, workdays) AS "+
            "VALUES ("+Integer.toString(previousMonth)+ ", "+
            Integer.toString(monthWorkingDays)+")";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            
                                   
            query = "CREATE TABLE vacation_total ( id SMALLINT, totaldays SMALLINT)";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            for(Map.Entry<Integer, Integer> entry : vacationDaysMap.entrySet())
            {        
                String str = "INSERT INTO vacation_total VALUES (?, ?) ";        
                prStm = con.prepareStatement(str); 
                prStm.setInt(1, entry.getKey());   
                prStm.setInt(2, entry.getValue());                
                int updatedRows = prStm.executeUpdate();    
                if (prStm != null) {prStm.close();}
            }
            
            
                        
            query = "CREATE TABLE sickLess3_total ( id SMALLINT, totaldays SMALLINT)";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            for(Map.Entry<Integer, Integer> entry : sickLess3DaysMap.entrySet())
            {        
                String str = "INSERT INTO sickLess3_total VALUES (?, ?) ";        
                prStm = con.prepareStatement(str); 
                prStm.setInt(1, entry.getKey());   
                prStm.setInt(2, entry.getValue());                
                int updatedRows = prStm.executeUpdate();    
                if (prStm != null) {prStm.close();}
            } 
            
            
                       
            query = "CREATE TABLE sickMore3_total ( id SMALLINT, totaldays SMALLINT)";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            for(Map.Entry<Integer, Integer> entry : sickMore3DaysMap.entrySet())
            {        
                String str = "INSERT INTO sickMore3_total VALUES (?, ?) ";        
                prStm = con.prepareStatement(str);
                prStm.setInt(1, entry.getKey());    
                prStm.setInt(2, entry.getValue());                
                int updatedRows = prStm.executeUpdate();    
                if (prStm != null) {prStm.close();}
            }
            
            
            
            query = "CREATE VIEW REPORT1 (ID, first_name, last_name, work_days) AS "+
"SELECT WORKERS.ID, WORKERS.FIRST_NAME, WORKERS.LAST_NAME, MONTH_WORK_DAYS.WORKDAYS "+ 
                    "FROM WORKERS "
                    + "CROSS JOIN MONTH_WORK_DAYS "
                    + "WHERE ((YEAR(workers.hire_date) < "+Integer.toString(tableYear)
             + ") OR (( YEAR(workers.hire_date) = "+Integer.toString(tableYear)
             + ") AND (MONTH(workers.hire_date) <= "+Integer.toString(previousMonth)
             + "))) AND (workers.apolisi = 0 OR (workers.apolisi <> 0 AND (MONTH(workers.diakopi) >= "
             +Integer.toString(previousMonth)+" ) AND (YEAR(workers.diakopi) >= "
             +Integer.toString(tableYear)+ ")))";
                     
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            
            
            query = "CREATE table REPORT20 (ID smallint, first_name varchar(20), "+ 
"last_name varchar(20), work_days integer, occupied_days DOUBLE, sick_days_less_3 integer, "+ 
"sick_days_more_3 integer, vacation_days integer, hours_worked BIGINT) ";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            query = "INSERT INTO report20 "+
"SELECT report1.ID, report1.FIRST_NAME, report1.LAST_NAME, report1.WORK_DAYS, "+ 
 "employee_work_days.workdays, sickLess3_total.totaldays, sickmore3_total.totaldays, "+
    "VACATION_TOTAL.totaldays, hours_worked.hours "+ 
            "FROM REPORT1 "+ 
            "LEFT JOIN EMPLOYEE_WORK_DAYS "+
            "ON report1.ID = employee_work_days.ID "+
            "LEFT JOIN sickLess3_total "+
            "ON report1.ID = sickLess3_total.ID "+
            "LEFT JOIN sickmore3_total "+
            "ON report1.ID = sickmore3_total.ID "+
            "LEFT JOIN VACATION_TOTAL "+
            "ON report1.ID = VACATION_TOTAL.ID "+
            "LEFT JOIN hours_worked "+
            "ON report1.ID = hours_worked.ID ";            
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            /* change the null values to zeroes because you cannot subtract nulls */
            query = "update report20 set occupied_days = 0 where occupied_days IS NULL";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            query = "update report20 set sick_days_less_3 = 0 where sick_days_less_3 IS NULL";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            query = "update report20 set sick_days_more_3 = 0 where sick_days_more_3 IS NULL";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            query = "update report20 set vacation_days = 0 where vacation_days IS NULL";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            query = "update report20 set hours_worked = 0 where hours_worked IS NULL";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            query = "INSERT INTO "+tableStr+" "+ 
                    "SELECT ID, first_name, last_name, work_days, "+ 
                    "(work_days - occupied_days), occupied_days, "+ 
                    "sick_days_Less_3, sick_days_more_3, vacation_days, "+ 
                    "(occupied_days * 8.0  ) - (hours_worked / 3600000.1) , 0, 0 "+   
                    "FROM REPORT20 " ;            
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            /* Perform Cleanup */
            query = "DROP TABLE vacation_total";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}            
            query = "DROP TABLE sickLess3_total";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}            
            query = "DROP TABLE sickMore3_total";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}            
            query = "DROP VIEW REPORT1";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            query = "DROP TABLE REPORT20";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}           
            query = "DROP VIEW month_work_days" ;        
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}        
            query = "DROP TABLE Employee_Work_Days" ;        
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}        
            query = "DROP VIEW HOURS_WORKED" ;        
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            query = "DROP VIEW TEMP_ID";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            
            try {
                stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                String sql1 = "SELECT * FROM "+tableStr+
                        " WHERE (occupied_days + sick_days_Less_3 +sick_days_more_3 + vacation_days > work_days)"+
                        " OR (sick_days_Less_3 +sick_days_more_3 + vacation_days > absent_days)";
                rs1 = stm.executeQuery(sql1);
                
                while (rs1.next()){            
                 facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, rs1.getString("id")+" - "+rs1.getString("Last_Name")+" - "+rs1.getString("First_Name"),
                         "Υπάρχει ασυμφωνία ημερών εργασίας/απουσίας !"));                    
                 }
                } catch (SQLException ex) {
                Logger.getLogger(ComputeWorkingDays.class.getName()).log(Level.SEVERE, null, ex);                
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
                
                }finally{
                if (stm != null) { stm.close();}}       
            
         facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Η δημιουργία του μηνιαίου report ολοκληρώθηκε!", "ΟΛΟΚΛΗΡΩΘΗΚΕ"));    
            
        } catch (SQLException ex) {
            Logger.getLogger(CreateReport.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
         }
    }
    
}
