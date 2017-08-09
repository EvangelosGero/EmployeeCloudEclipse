/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic.Misthodosia;

import Controllers.util.JsfUtil;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ButtonType;

/**
 *
 * @author evgero
 */
public class CreateEAReport { 
    
    private Connection con = null;
    private ResultSet rs = null, rs1 = null ;
    private Statement stm = null ;
    private PreparedStatement prStm = null;
    private int update, currentYear;     
    private String epidomaAdeiasTableStr;      
    private double adjustedSalary, ensimaHours, isforErgCoef, ergazIsfCoef, metalCoef, ensimaDays;    
    
    public void createDBEpidomaAdeiasReport (Connection con, int year) throws SQLException{
        
        this.con = con;             
        
        currentYear = year == 0 ? LocalDate.now().getYear() : year;             
        
        epidomaAdeiasTableStr = "EPIDOMA_ADEIAS_REPORT_"+
                Integer.toString(currentYear) ;
            
        /* Check if the table exists and if yes delete it */
            
        try{
            DatabaseMetaData databaseMetaData = con.getMetaData();            
            rs1 = databaseMetaData.getTables(null, null, epidomaAdeiasTableStr , null);
            if (rs1.next()) {                                               
                stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
                
             update = stm.executeUpdate("DROP TABLE "+epidomaAdeiasTableStr);                     
            } 
        }catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally {
            if (stm != null)stm.close();
            if(rs1 != null){rs1.close();}
        }                      
        
        String query = "CREATE TABLE "+epidomaAdeiasTableStr+" (ID SMALLINT, First_Name VARCHAR(20), "
    + "Last_Name VARCHAR(20), father_name VARCHAR(20), AM_IKA VARCHAR(20), salary DOUBLE, "      
    + "TA SMALLINT, ensima SMALLINT, hours_misthou DOUBLE, reason_salary VARCHAR(40), "            
    + "adjusted_salary DOUBLE, isfores_ergazomenou DOUBLE, ergodotikes_isfores DOUBLE, "  
    + "mee DOUBLE, total DOUBLE, reason_isfores SMALLINT, reason_isfores_text VARCHAR(40), "
    + "tapit_isfores_erg DOUBLE,  "
    + "tapit_ergod_isfores DOUBLE, tapit_mee DOUBLE, tapit_total DOUBLE, tapit SMALLINT,  "
    + "xartosimo DOUBLE, OGA DOUBLE, FMY DOUBLE, isfora_allilegiis DOUBLE, "
    + "kratisis DOUBLE GENERATED ALWAYS AS (isfores_ergazomenou+tapit_isfores_erg+fmy+isfora_allilegiis), "
    + "kathara DOUBLE GENERATED ALWAYS AS (adjusted_salary-(isfores_ergazomenou+tapit_isfores_erg+fmy+isfora_allilegiis)), "
    + "kostos DOUBLE GENERATED ALWAYS AS (adjusted_salary+ergodotikes_isfores), prokatavoli DOUBLE, relation SMALLINT, "
    + "pliroteo DOUBLE GENERATED ALWAYS AS (adjusted_salary-(isfores_ergazomenou+tapit_isfores_erg+fmy+isfora_allilegiis)-prokatavoli))";                      
        try {
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);        
            update = stm.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally {
            if (stm != null)stm.close();            
        }
       
        try { 
         stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);                                            
         query = "SELECT * FROM workers WHERE ((workers.apolisi = 0 OR (workers.apolisi <> 0  "
             +" AND (YEAR(workers.diakopi) >= " +Integer.toString(currentYear)+ ")))"
                 + "AND ((MONTH(workers.hire_date) <= "+Integer.toString(7)+" AND YEAR(workers.hire_date) = "
                 + Integer.toString(currentYear)+") OR YEAR(workers.hire_date) < "+ Integer.toString(currentYear)+") AND COMPANY>0)";
            
         rs = stm.executeQuery(query);        
         while(rs.next()){          
            int entitledDays;  
/*    if (rs.getInt("id") == 23) 
     {
     int xxx=1;
     }
*/
            
            
            entitledDays = rs.getInt("entitled_days");
            switch (rs.getInt("relation")){
                case 0 : {      //misthotos
                double interimSalary = (rs.getDouble("salary")/25)*entitledDays;
                adjustedSalary = interimSalary <= rs.getDouble("salary")/2 ? interimSalary :
                   rs.getDouble("salary")/2;
                ensimaDays = interimSalary <= rs.getDouble("salary")/2 ? entitledDays : 12.5;
                break;
                }
                case 1 : {      //imeromisthios
                double interimSalary = rs.getDouble("salary")*entitledDays;
                 adjustedSalary = interimSalary <= rs.getDouble("salary")*13 ? interimSalary :
                   rs.getDouble("salary")*13;
                 ensimaDays = interimSalary <= rs.getDouble("salary")*13 ? entitledDays : 13;                          
                break;
                 
                }
            }          
                                        
            ensimaHours = ensimaDays*6.6666;
            String strQry = "INSERT INTO "+epidomaAdeiasTableStr+"(ID, First_Name , "
                    + "Last_Name, father_name , AM_IKA, salary,  "      
                    + "TA, ensima, hours_misthou, reason_salary,  "            
                    + "adjusted_salary, isfores_ergazomenou, ergodotikes_isfores, "  
                    + "mee, total, reason_isfores, reason_isfores_text, "
                    + "tapit_isfores_erg,  "
                    + "tapit_ergod_isfores, tapit_mee, tapit_total, tapit,  "
                    + "xartosimo, OGA, FMY, isfora_allilegiis, "   
                    + "prokatavoli, relation) "   
                    +" VALUES (?, ?, ?, ?, ?, ?, 4 , ?, ?, 'ΒΑΣΙΚΕΣ ΑΠΟΔΟΧΕΣ', "
                    + " ?,  ?, ?, 0, ?, ?, ?, ?, 0, 0, ?, ?, 0, 0, ?, ?, 0, ?) ";
                    
            prStm = con.prepareStatement(strQry);
            prStm.setInt(1, rs.getInt("id"));
            prStm.setString(2, rs.getString("first_name"));
            prStm.setString(3, rs.getString("last_name"));
            prStm.setString(4, rs.getString("father_name"));            
            prStm.setString(5, rs.getString("am_ika"));
            prStm.setDouble(6, rs.getDouble("salary"));
            prStm.setDouble(7, ensimaDays);
            prStm.setDouble(8, ensimaHours);
            prStm.setDouble(9, adjustedSalary);           
            switch (rs.getInt("reason_isfores")){
                    case 101 : {isforErgCoef = 16; ergazIsfCoef = 25.06;
                        prStm.setString(14, "IKA MIKTA TEAM"); break;}
                    case 102 : {isforErgCoef = 16; ergazIsfCoef = 26.06;
                        prStm.setString(14, "IKA MIKTA TEAM + Ε.Κ."); break;}                       
                    case 106 : {isforErgCoef = 19.45; ergazIsfCoef = 28.21;
                        prStm.setString(14, "ΒΑΡΕΑ TEAM"); break;}                             
                } 
            prStm.setDouble(10, (adjustedSalary)*isforErgCoef/100);
            prStm.setDouble(11, (adjustedSalary)*ergazIsfCoef/100 );
            prStm.setDouble(12, (adjustedSalary)*(isforErgCoef+ergazIsfCoef)/100);
            prStm.setInt(13, rs.getInt("reason_isfores"));
            // Compute the TAPIT row
          
            switch (rs.getInt("tapit")) {
                case 0:
                    metalCoef = 0;               
                    break;
                case 2:
                    metalCoef = 0.04;               
                    break;
                case 1:
                    metalCoef = 0.01;
                    break;
            }              
            
            prStm.setDouble(15, (adjustedSalary)*metalCoef );
            prStm.setDouble(16, (adjustedSalary)*metalCoef );
            prStm.setInt(17, rs.getInt("tapit"));
            //Compute FMY
            switch (rs.getInt("relation")){
                case 0 : {      //misthotos
                    CreateSalaryReport createSalaryReport = new CreateSalaryReport();
                    prStm.setDouble(18, createSalaryReport.computeFMYOfYear(con,rs.getInt(1), rs.getDouble("salary"),
                            isforErgCoef, metalCoef)*
                        ((adjustedSalary)/(rs.getDouble("salary"))/2));//add a weight coef.);
                    prStm.setDouble(19, createSalaryReport.getIsforaAlilegiis()*
                        (adjustedSalary/(rs.getDouble("salary"))/2));//add a weight coef.
                    break;        
                }
                case 1 : {      //imeromisthios
                    CreateSalaryReport createSalaryReport = new CreateSalaryReport();
                    prStm.setDouble(18, createSalaryReport.computeFMYOfYear(con,rs.getInt(1), rs.getDouble("salary")*26, isforErgCoef, metalCoef)*                            
                        ((adjustedSalary)/(rs.getDouble("salary")*13)));//add a weight coef.
                    prStm.setDouble(19, createSalaryReport.getIsforaAlilegiis()*
                        ((adjustedSalary)/(rs.getDouble("salary")*13)));//add a weight coef.
                    break;        
                }
            }
            
            //
            prStm.setInt(20, rs.getInt("relation"));
                        
            prStm.executeUpdate();            
          } 
         JsfUtil.addSuccessMessage("Η διαδικασία τελείωσε επιτυχώς !");   
         
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally {
            if (stm != null)stm.close();
            if (rs != null )rs.close();
            if (prStm != null)prStm.close();
        }
        
    }
    
}
     

    
