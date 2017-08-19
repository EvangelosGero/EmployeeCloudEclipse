package Controllers.Logic.Misthodosia;

import Controllers.EmplAdminsController;
import Controllers.MisthodosiaController;
import Controllers.util.DropIfExists;
import Controllers.util.JsfUtil;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;


/**
 *
 * @author evgero
 */
@Named("createSalaryReport")
@SessionScoped
public class CreateSalaryReport implements Serializable {
    
    @Inject
    private EmplAdminsController emplAdminsController;
    private MisthodosiaController misthodosiaController;
    private Connection con = null, con1 = null;
    private ResultSet rs = null, rs1 = null, rs2 = null, rs3 = null, rs4 = null, rschldrn = null, rs5 = null, rs6 = null;
    private Statement stm = null , stm1 = null, stm2 = null, stm3 = null, stm4 = null,stm5 = null, stm6 = null, stmchldrn = null;                      
    private PreparedStatement prStm = null, prStm1 = null, prStm2 = null, prStm3 = null, prStm4 = null;       
    private int update, previousMonth, currentYear, tableYear, i;
    private Map<Integer, Integer> misthotosDaysIKAMap, imeromisthiosDaysIKAMap;
    private Map<Integer, Double> misthotosDaysMisthouMap, imeromisthiosDaysMisthouMap ;
    private double metalCoef; 
    private String reportTableStr, salaryTableStr;
    private double isforaAlilegiis;    
    private Map<Integer, Integer> generatedSickMore3UnpaidMap; 
    private Map<Integer, List<Double>> epidotisiMap = new HashMap<>(); 
    private boolean firstRun;

    public Map<Integer, List<Double>> getEpidotisiMap() {
        return epidotisiMap;
    }

    public void setEpidotisiMap(Map<Integer, List<Double>> epidotisiMap) {
        this.epidotisiMap = epidotisiMap;
    }

    public String getSalaryTableStr() {
        return salaryTableStr;
    }

    public void setSalaryTableStr(String salaryTableStr) {
        this.salaryTableStr = salaryTableStr;
    }
    
    public boolean isFirstRun() {
        return firstRun;
    }

    public void setFirstRun(boolean firstRun) {
        this.firstRun = firstRun;
    }

    public double getIsforaAlilegiis() {
        return isforaAlilegiis;
    }

    public void setIsforaAlilegiis(double isforaAlilegiis) {
        this.isforaAlilegiis = isforaAlilegiis;
    }

    public EmplAdminsController getEmplAdminsController() {
        return emplAdminsController;
    }

    public void setEmplAdminsController(EmplAdminsController emplAdminsController) {
        this.emplAdminsController = emplAdminsController;
    }  
    
    public void createMisthodosiaReport() {
                  
        try {            
            this.setFirstRun(true);
            CreateDBSalaryReport(this.emplAdminsController.getCon(), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
           
    }
    
    public void saveEpidotisi(){        
        try {
            for(int pk : this.getEpidotisiMap().keySet())
                Collections.replaceAll(this.getEpidotisiMap().get(pk), null, new Double(0));
            this.setFirstRun(false);
            CreateDBSalaryReport(this.emplAdminsController.getCon(), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
       
    }
           
    public void CreateDBSalaryReport(Connection con, LocalDate paretisiDate) throws SQLException{        
        
        this.con = con;                      
        boolean reportTableExists = true;         
        
        /* find which is the previous month */
         
        previousMonth = paretisiDate == null ? LocalDate.now().minusMonths(1).getMonthValue() :
                    paretisiDate.getMonthValue();
        tableYear = paretisiDate == null ? LocalDate.now().minusMonths(1).getYear() :
                    paretisiDate.getYear(); 
        currentYear = LocalDate.now().getYear();
                                              
        reportTableStr = "REPORT_"+Integer.toString(previousMonth)
                    +"_"+Integer.toString(tableYear);        
        
       //Check if ReportTable exists and if not show message and return                   
        
        try {            
            DatabaseMetaData databaseMetaData = con.getMetaData();
            rs1 = databaseMetaData.getTables(null, null, reportTableStr , null);
            if (!rs1.next())reportTableExists = false;            
            
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            
        }finally{            
            if(rs1 != null)rs1.close();
        }        
                                    
        if (reportTableExists){
            salaryTableStr = "SALARY_"+reportTableStr;
            
              /* Check if the table exists and if yes delete it */
            
            DropIfExists.exec(con, "TABLE", salaryTableStr);                      
    
        
    String query = "CREATE TABLE "+salaryTableStr+" (ID SMALLINT, First_Name VARCHAR(20), "
    + "Last_Name VARCHAR(20), father_name VARCHAR(20), AM_IKA VARCHAR(20), salary DOUBLE, "      
    + "TA SMALLINT, ensima SMALLINT, hours_misthou DOUBLE, reason_salary VARCHAR(40), "            
    + "adjusted_salary DOUBLE, isfores_ergazomenou DOUBLE, ergodotikes_isfores DOUBLE, "  
    + "mee DOUBLE, total DOUBLE, reason_isfores SMALLINT, reason_isfores_text VARCHAR(40), "
    + "astheneia_text VARCHAR(40), epidotisi_astheneias DOUBLE, tapit_isfores_erg DOUBLE,  "
    + "tapit_ergod_isfores DOUBLE, tapit_mee DOUBLE, tapit_total DOUBLE, tapit SMALLINT,  "
    + "xartosimo DOUBLE, OGA DOUBLE, FMY DOUBLE, isfora_allilegiis DOUBLE, "
    + "kratisis DOUBLE GENERATED ALWAYS AS (isfores_ergazomenou+tapit_isfores_erg+fmy+isfora_allilegiis), "
    + "kathara DOUBLE GENERATED ALWAYS AS (adjusted_salary-(isfores_ergazomenou+tapit_isfores_erg+fmy+isfora_allilegiis)-epidotisi_astheneias), "
    + "kostos DOUBLE GENERATED ALWAYS AS (adjusted_salary+ergodotikes_isfores-epidotisi_astheneias), prokatavoli DOUBLE, relation SMALLINT, "
    + "pliroteo DOUBLE GENERATED ALWAYS AS (adjusted_salary-(isfores_ergazomenou+tapit_isfores_erg+fmy+isfora_allilegiis)-prokatavoli-epidotisi_astheneias), "
    + "start_date DATE, end_date DATE)";                      
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
        //Compute days misthotou
        misthotosDaysIKAMap = 
                (new ComputeMisthotosDaysIKA()).MisthotosDaysIKA(con, previousMonth, tableYear);       
        misthotosDaysMisthouMap = 
                (new ComputeMisthotosDaysMisthou()).MisthotosDaysMisthou(con, reportTableStr );
        
        /* Insert values first for μισθωτους */ 
        
        query = "INSERT INTO "+salaryTableStr+" (id, first_name, last_name, father_name, AM_IKA, salary, ta,"
                + "ensima, hours_misthou, reason_salary, adjusted_salary, isfores_ergazomenou, "
                + "ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "astheneia_text, epidotisi_astheneias, "
                + "tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, tapit_total, tapit, "
                + "xartosimo, oga, FMY, isfora_allilegiis, prokatavoli, relation) "
                + "SELECT t1.id, t1.first_name, t1.last_name, t1.father_name, t1.AM_IKA, t1.salary, 1, "
                + "0, 0, 'ΒΑΣΙΚΕΣ ΑΠΟΔΟΧΕΣ', 0, 0, "              
                + "0, 0, 0, t1.reason_isfores, '', "
                + "'', 0, "
                + "0, 0, 0, 0, t1.tapit, "
                + "0, 0, 0, 0, 0, t1.relation "                               
                + "FROM workers AS t1, "+reportTableStr+" AS t2 "
                + "WHERE t1.id = t2.id ";                      
        try{
        prStm = con.prepareStatement(query);                           
        int updatedRows = prStm.executeUpdate(); 
        }catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            
        }finally {
            if (prStm != null)prStm.close();
        }                                  

  //Bring the salaryTable for Μισθωτούς into an updatable ResultSet and use the 2 Maps to update the values      
       
        query = "SELECT * FROM "+salaryTableStr+" WHERE  relation = 0";
        try {           
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);        
            rs = stm.executeQuery(query);
            
            
            //i=0;
            while(rs.next()){       
                //i=i++;
                int ensimaDays = misthotosDaysIKAMap.get(rs.getInt(1));
                double misthosDays = misthotosDaysMisthouMap.get(rs.getInt(1));                            
                double isfErgCoef = 16, ergIsfCoef = 25.06;                
                double misthosHrs = computeMisthosHours(rs.getInt(1), misthosDays, reportTableStr );               
                double adjustedSalary = 0;
                if(misthosDays != 0) adjustedSalary = rs.getDouble("salary")*(misthosDays/25)
                    *(misthosHrs/(misthosDays*6.667));
                else adjustedSalary = 0;               
                rs.updateInt("ensima", ensimaDays);
                rs.updateDouble("hours_misthou", misthosHrs);
                rs.updateDouble("adjusted_salary", adjustedSalary);
                
                /* αυτά ίσχυαν μέχρι και 31/05/16 
                    case 101 : {isfErgCoef = 15.5; ergIsfCoef = 24.56; 
                        rs.updateString("reason_isfores_text", "IKA MIKTA TEAM"); break; }
                    case 102 : {isfErgCoef = 15.5; ergIsfCoef = 25.56;
                        rs.updateString("reason_isfores_text", "IKA MIKTA TEAM + E.K."); break;}                       
                    case 106 : {isfErgCoef = 18.95; ergIsfCoef = 27.71;     */
                
                switch (rs.getInt("reason_isfores")){
                    case 101 : {isfErgCoef = 16; ergIsfCoef = 25.06; 
                        rs.updateString("reason_isfores_text", "IKA MIKTA TEAM"); break; }
                    case 102 : {isfErgCoef = 16; ergIsfCoef = 26.06;
                        rs.updateString("reason_isfores_text", "IKA MIKTA TEAM + E.K."); break;}                       
                    case 106 : {isfErgCoef = 19.45; ergIsfCoef = 28.21;
                        rs.updateString("reason_isfores_text", "ΒΑΡΕΑ TEAM"); break;}                         
                }                
                rs.updateDouble("isfores_ergazomenou", adjustedSalary*isfErgCoef/100);
                rs.updateDouble("ergodotikes_isfores", adjustedSalary*ergIsfCoef/100);
                rs.updateDouble("total", adjustedSalary*(isfErgCoef+ergIsfCoef)/100);
                
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
                rs.updateDouble("tapit_isfores_erg",adjustedSalary*metalCoef );
                rs.updateDouble("tapit_total",adjustedSalary*metalCoef );  
                rs.updateDouble("FMY", computeFMYOfYear(con,rs.getInt(1), adjustedSalary, isfErgCoef, metalCoef*100)/14);                        
                rs.updateDouble("isfora_allilegiis", isforaAlilegiis/14 );
              
                rs.updateRow();               
            }
         } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            
        } finally {            
            if (rs != null)rs.close();
            if (stm != null)stm.close();
            if (rs2 != null)rs2.close();
            if (stm1 != null)stm1.close();
            if (rschldrn != null) rschldrn.close();
            if (stmchldrn != null) stmchldrn.close();            
        }                         
             //Compute days imeromisthiou
        imeromisthiosDaysIKAMap = 
                (new ComputeImeromisthiosDaysIKA()).ImeromisthiosDaysIKA(con, previousMonth, tableYear);
        imeromisthiosDaysMisthouMap = 
                (new ComputeImeromisthiosDaysMisthou()).
                        imeromisthiosDaysMisthou(con, reportTableStr, previousMonth, tableYear );                         
       
  //Bring the salaryTable into an updatable ResultSet and use the 2 Maps to update the values for ημερομίσθιους 
               
       
  
        query = "SELECT * FROM "+salaryTableStr+" WHERE relation = 1";
        try {
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);        
            rs = stm.executeQuery(query);
            while(rs.next()){
                int ensimaDays = imeromisthiosDaysIKAMap.get(rs.getInt(1));
                double misthosDays = imeromisthiosDaysMisthouMap.get(rs.getInt(1));
                double isfErgCoef = 16, ergIsfCoef = 25.06;
                double misthosHrs = computeMisthosHours(
                        rs.getInt(1), misthosDays, reportTableStr );
                double adjustedSalary = 0;
                if (misthosDays != 0) adjustedSalary = (rs.getDouble("salary")*misthosDays) *
                        (misthosHrs/(misthosDays*6.667));
                else adjustedSalary = 0;           
                rs.updateInt("ensima", ensimaDays);
                rs.updateDouble("hours_misthou", misthosHrs);
                rs.updateDouble("adjusted_salary", adjustedSalary);
                switch (rs.getInt("reason_isfores")){
                    case 101 : {isfErgCoef = 16; ergIsfCoef = 25.06;
                        rs.updateString("reason_isfores_text", "IKA MIKTA TEAM"); break;}
                    case 102 : {isfErgCoef = 16; ergIsfCoef = 26.06;
                        rs.updateString("reason_isfores_text", "IKA MIKTA TEAM + Ε.Κ."); break;}                       
                    case 106 : {isfErgCoef = 19.45; ergIsfCoef = 28.21;
                        rs.updateString("reason_isfores_text", "ΒΑΡΕΑ ΤΕΑΜ"); break;}                         
                } 
                rs.updateDouble("isfores_ergazomenou", adjustedSalary*isfErgCoef/100 );
                rs.updateDouble("ergodotikes_isfores", adjustedSalary*ergIsfCoef/100 );
                rs.updateDouble("total", adjustedSalary*(isfErgCoef+ergIsfCoef)/100);
                
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
                rs.updateDouble("tapit_isfores_erg",adjustedSalary*metalCoef );
                rs.updateDouble("tapit_total",adjustedSalary*metalCoef );                            
                rs.updateDouble("FMY", computeFMYOfYear(con,rs.getInt(1), adjustedSalary, isfErgCoef, metalCoef*100)/14);                
                rs.updateDouble("isfora_allilegiis", isforaAlilegiis/14);
                                
                rs.updateRow();
            }
            
          } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
        }finally {            
            if (rs != null)rs.close();
            if (stm != null)stm.close();  
            if (rs2 != null)rs2.close();
            if (stm1 != null)stm1.close();
            if (rschldrn != null) rschldrn.close();
            if (stmchldrn != null) stmchldrn.close();   
        } 
                            
                    
        // Iterate over the id's of reportTable and find out which are on vacation 
                
        int vacationDays = 0, sickDaysLess3 = 0, sickDaysMore3 = 0;
        int vacationHours = 0, sickDaysLess3Hours = 0, sickDaysTotal = 0; 
        double sickDaysMore3Hours = 0,  sickDaysTotalHours = 0;
        double adjSalary = 0, adjSalaryLess3 = 0, adjSalaryMore3 = 0; 
        double isforErgCoef = 0  ,ergazIsfCoef = 0;                
        double FMYVacation = 0, FMYSickLess3 = 0, FMYSickMore3 = 0, isforaAlilegiisVacation = 0, 
                isforaAlilegiisSickLess3 = 0, isforaAlilegiisSickMore3 = 0, epidotisiTotal = 0;
        query = "SELECT t1.id, t1.vacation_days, t1.sick_days_less_3, t1.sick_days_more_3, "
               +"t2.relation, t2.first_name, t2.last_name FROM "+reportTableStr+" AS t1, workers AS t2 "
               +"WHERE t1.id = t2.id";
        try {
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);        
            rs = stm.executeQuery(query);
           
            while(rs.next()){                                                                     
            try{               
            
                query = "SELECT * FROM "+salaryTableStr+" WHERE id = "+Integer.toString(rs.getInt("id"));
                stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);                            
                rs3 = stm2.executeQuery(query); 
                rs3.next();              
          
             if((rs.getInt("vacation_days")!=0) ){
                 query = "INSERT INTO "+salaryTableStr+" (id, first_name, last_name, father_name,"
                + "AM_IKA, salary, TA, ensima, hours_misthou, reason_salary, adjusted_salary, "
                + "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, "
                + "tapit_total, tapit, xartosimo, OGA, FMY, isfora_allilegiis,  prokatavoli, relation) "
                + "SELECT id, first_name, last_name, father_name, "
                + "AM_IKA, salary, 2, ?, ?, 'ΒΑΣΙΚΕΣ ΑΠΟΔΟΧΕΣ', ?, "
                + "?, ?, 0, ?, reason_isfores, ?, "
                + "'', 0, ?, 0, 0, ?, tapit, "
                + "0, 0, ?, ?, 0 , relation "               
                + "FROM workers  WHERE  id = ?";                      
        try{
        prStm = con.prepareStatement(query);
        vacationDays = rs.getInt("vacation_days");
        prStm.setInt(1, vacationDays);
        vacationHours = vacationDays*8;
        prStm.setDouble(2, vacationHours);        
        switch (rs.getInt("relation")){
            case 0 :{ adjSalary = (rs3.getDouble("salary")/25)*(vacationDays*1.2);
                      //adjSalary = rs3.getDouble("salary")/25*vacationDays;
                     break;}
            case 1:{ adjSalary = rs3.getDouble("salary")*vacationDays*1.2;
                     break;}
        }       
        prStm.setDouble(3, adjSalary);
       
        switch (rs3.getInt("reason_isfores")){
                    case 101 : {isforErgCoef = 16; ergazIsfCoef = 25.06;
                        prStm.setString(7, "IKA MIKTA TEAM"); break;}
                    case 102 : {isforErgCoef = 16; ergazIsfCoef = 26.06;
                        prStm.setString(7, "IKA MIKTA TEAM + Ε.Κ."); break;}                       
                    case 106 : {isforErgCoef = 19.45; ergazIsfCoef = 28.21;
                        prStm.setString(7, "ΒΑΡΕΑ TEAM"); break;}                         
                } 
        prStm.setDouble(4, adjSalary*isforErgCoef/100);
        prStm.setDouble(5, adjSalary*ergazIsfCoef/100 );
        prStm.setDouble(6, adjSalary*(isforErgCoef+ergazIsfCoef)/100);        
        // Compute the TAPIT row
          
            switch (rs3.getInt("tapit")) {
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
            
        prStm.setDouble(8, adjSalary*metalCoef );
        prStm.setDouble(9, adjSalary*metalCoef );        
        FMYVacation =  rs3.getDouble("FMY")*vacationDays*8/rs3.getDouble("hours_misthou");       
        prStm.setDouble(10, FMYVacation);
        isforaAlilegiisVacation =  rs3.getDouble("isfora_allilegiis")*vacationDays*8/rs3.getDouble("hours_misthou");       
        prStm.setDouble(11, isforaAlilegiisVacation);
        prStm.setInt(12, rs.getInt("id"));                  
        
        int updatedRows = prStm.executeUpdate(); 
                      
       } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
       }finally {
           if (prStm != null)prStm.close(); 
        }
      }
    
    //check for sick less 3  
    
    // We have to create a unique row for each <3 and a total <3. The same for >3, a total for >3and a grand total.
    // They should also have StartDate and EndDate
    
      int ensimaTotalLess3 = 0;
      if((rs.getInt("sick_days_less_3")!=0) ){
          
          
          query = "SELECT * FROM generated_less_three WHERE worker_id = "+Integer.toString(rs.getInt("id"))
                  +"  AND (MONTH(start_day) = "+Integer.toString(previousMonth)
             + ") AND (YEAR(start_day) = "+Integer.toString(tableYear)+")";
          stm5 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);                            
          rs5 = stm5.executeQuery(query);
          while(rs5.next()){
          
                  query = "INSERT INTO "+salaryTableStr+" (id, first_name, last_name, father_name,"
                + "AM_IKA, salary, TA, ensima, hours_misthou, reason_salary, adjusted_salary, "
                + "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, "
                + "tapit_total, tapit, xartosimo, OGA, FMY, isfora_allilegiis, prokatavoli, relation, start_date, end_date) "
                + "SELECT id, first_name, "
                + "last_name, father_name, AM_IKA, salary, "
                + "1, ?, ?, 'ΑΣΘΕΝΕΙΑ', "
                + "?, ?, ?, "
                + "0, ?, reason_isfores, ?, "
                + "?, 0, ?, "
                + "0, 0, ?, tapit, "
                + "0, 0, ?, ?, 0 , relation , ?, ?"               
                + "FROM workers  WHERE  id = ?";                                    
        try{
        prStm2 = con.prepareStatement(query);
        sickDaysLess3 = rs5.getInt("real_days");
        prStm2.setInt(1,  sickDaysLess3);
        sickDaysLess3Hours =  sickDaysLess3*4;
        prStm2.setDouble(2, sickDaysLess3Hours);       
        switch (rs.getInt("relation")){
            case 0 :{ adjSalaryLess3 = (rs3.getDouble("salary")/25)*(sickDaysLess3 /2);
                     break;}
            case 1 :{ adjSalaryLess3 = rs3.getDouble("salary")*sickDaysLess3*0.6;
                     break;}
        }       
        prStm2.setDouble(3, adjSalaryLess3);
       
        switch (rs3.getInt("reason_isfores")){
                    case 101 : {isforErgCoef = 16; ergazIsfCoef = 25.06; 
                     prStm2.setString(7, "IKA MIKTA TEAM"); break;}
                    case 102 : {isforErgCoef = 16; ergazIsfCoef = 26.06; 
                     prStm2.setString(7, "IKA MIKTA TEAM + Ε.Κ."); break;}                       
                    case 106 : {isforErgCoef = 19.45; ergazIsfCoef = 28.21; 
                     prStm2.setString(7, "ΒΑΡΕΑ TEAM"); break;}                         
                } 
        prStm2.setDouble(4, adjSalaryLess3*isforErgCoef/100 );
        prStm2.setDouble(5, adjSalaryLess3*ergazIsfCoef/100 );
        prStm2.setDouble(6, adjSalaryLess3*(isforErgCoef+ergazIsfCoef)/100);
        // Compute the TAPIT row                
             
            switch (rs3.getInt("tapit")) {
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
        prStm2.setString(8, "ΑΣΘΕΝΕΙΑ<3"); 
        prStm2.setDouble(9, adjSalaryLess3*metalCoef );
        prStm2.setDouble(10, adjSalaryLess3*metalCoef );        
        //FMYSickLess3 =  rs3.getDouble("FMY")*sickDaysLess3*4/rs3.getDouble("hours_misthou");       
        prStm2.setDouble(11, 0);
        //isforaAlilegiisSickLess3 =  rs3.getDouble("isfora_allilegiis")*sickDaysLess3*4/rs3.getDouble("hours_misthou");       
        prStm2.setDouble(12, 0);
        prStm2.setDate(13, rs5.getDate("start_day"));
        prStm2.setDate(14, rs5.getDate("end_day"));
        prStm2.setInt(15, rs.getInt("id"));
                  
        int updatedRows = prStm2.executeUpdate(); 
                      
       } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
             if (prStm2 != null)prStm2.close();   
        }
        ensimaTotalLess3 += rs5.getInt("real_days");
      } 
          if(rs5 != null)rs5.close();
          if(stm5 != null)stm5.close();
          
          //Create a sick < 3 Total Row
          
            query = "INSERT INTO "+salaryTableStr+" (id, first_name, last_name, father_name,"
                + "AM_IKA, salary, TA, ensima, hours_misthou, reason_salary, adjusted_salary, "
                + "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, "
                + "tapit_total, tapit, xartosimo, OGA, FMY, isfora_allilegiis, prokatavoli, relation) "
                + "SELECT id, first_name, "
                + "last_name, father_name, AM_IKA, salary, "
                + "1, ?, ?, 'ΑΣΘΕΝΕΙΑ', "
                + "?, ?, ?, "
                + "0, ?, reason_isfores, ?, "
                + "?, 0, ?, "
                + "0, 0, ?, tapit, "
                + "0, 0, ?, ?, 0 , relation "               
                + "FROM workers  WHERE  id = ?";                                    
        try{
        prStm2 = con.prepareStatement(query);
        sickDaysLess3 = ensimaTotalLess3;
        prStm2.setInt(1,  sickDaysLess3);
        sickDaysLess3Hours =  sickDaysLess3*4;
        prStm2.setDouble(2, sickDaysLess3Hours);       
        switch (rs.getInt("relation")){
            case 0 :{ adjSalaryLess3 = (rs3.getDouble("salary")/25)*(sickDaysLess3 /2);
                     break;}
            case 1 :{ adjSalaryLess3 = rs3.getDouble("salary")*sickDaysLess3*0.6;
                     break;}
        }       
        prStm2.setDouble(3, adjSalaryLess3);
       
        switch (rs3.getInt("reason_isfores")){
                    case 101 : {isforErgCoef = 16; ergazIsfCoef = 25.06; 
                     prStm2.setString(7, "IKA MIKTA TEAM"); break;}
                    case 102 : {isforErgCoef = 16; ergazIsfCoef = 26.06; 
                     prStm2.setString(7, "IKA MIKTA TEAM + Ε.Κ."); break;}                       
                    case 106 : {isforErgCoef = 19.45; ergazIsfCoef = 28.21; 
                     prStm2.setString(7, "ΒΑΡΕΑ TEAM"); break;}                         
                } 
        prStm2.setDouble(4, adjSalaryLess3*isforErgCoef/100 );
        prStm2.setDouble(5, adjSalaryLess3*ergazIsfCoef/100 );
        prStm2.setDouble(6, adjSalaryLess3*(isforErgCoef+ergazIsfCoef)/100);
        // Compute the TAPIT row                
             
            switch (rs3.getInt("tapit")) {
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
        prStm2.setString(8, "A<3TOTAL"); 
        prStm2.setDouble(9, adjSalaryLess3*metalCoef );
        prStm2.setDouble(10, adjSalaryLess3*metalCoef );        
        //FMYSickLess3 =  rs3.getDouble("FMY")*sickDaysLess3*4/rs3.getDouble("hours_misthou");       
        prStm2.setDouble(11, 0);
        //isforaAlilegiisSickLess3 =  rs3.getDouble("isfora_allilegiis")*sickDaysLess3*4/rs3.getDouble("hours_misthou");       
        prStm2.setDouble(12, 0);
        prStm2.setInt(13, rs.getInt("id"));
                  
        int updatedRows = prStm2.executeUpdate(); 
                      
       } catch (SQLException ex) {
           Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
           JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
             if (prStm2 != null)prStm2.close();   
        }
    }
      
     
      
      //  Check for sick > 3
      
      int ensimaTotalMore3 = 0;      
        if((rs.getInt("sick_days_more_3")!=0)){
            
            
         if(this.isFirstRun()){  
            query = "SELECT * FROM generated_more_three WHERE worker_id = "+Integer.toString(rs.getInt("id"))
                    +"  AND (MONTH(start_day) = "+Integer.toString(previousMonth)
                    + ") AND (YEAR(start_day) = "+Integer.toString(tableYear)+")";
          stm5 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);                            
          rs5 = stm5.executeQuery(query);
          List<Double> epidotiseis = new ArrayList<>();
          while(rs5.next()){ 
            
            if(!(rs5.getDate("start_day").equals(rs5.getDate("end_day"))) && (rs5.getInt("real_days") > 0)) {  //The case where it is actually <3 i.e. the beginning of >3, e.g. 30/5, 31/5
              
                epidotiseis.add(new Double(0));
            }
            }
          this.epidotisiMap.put(rs.getInt("id"), epidotiseis);
         }else {
             List<Double> epid = this.epidotisiMap.getOrDefault(rs.getInt("id"), new ArrayList<Double>(Arrays.asList(new Double(0))));
             
             for(double epidotisi : epid){                 
                         
                epidotisiTotal += epidotisi;
            
                  query = "INSERT INTO "+salaryTableStr+" (id, first_name, last_name, father_name,"
                + "AM_IKA, salary, TA, ensima, hours_misthou, reason_salary, adjusted_salary, "
                + "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, "
                + "tapit_total, tapit, xartosimo, OGA, FMY, isfora_allilegiis, prokatavoli, relation, start_date, end_date) "
                + "SELECT id, first_name, "
                + "last_name, father_name, AM_IKA, salary, "
                + "1, ?, ?, 'ΑΣΘΕΝΕΙΑ', "
                + "?, ?, ?, "
                + "0, ?, reason_isfores, ?, "
                + "?, ?, ?, "
                + "0, 0, ?, tapit, "
                + "0, 0, ?, ?, 0 , relation, ?, ? "               
                + "FROM workers  WHERE  id = ?";                                    
        try{
        prStm2 = con.prepareStatement(query);
        sickDaysMore3 = rs5.getInt("ika_days");
        prStm2.setInt(1,  sickDaysMore3);
        sickDaysMore3Hours =  sickDaysMore3*6.667;
        prStm2.setDouble(2, sickDaysMore3Hours);       
        switch (rs.getInt("relation")){
            case 0 :{ adjSalaryMore3 = (rs3.getDouble("salary")/25)*sickDaysMore3 ;
                     break;}
            case 1 :{ adjSalaryMore3 = rs3.getDouble("salary")*sickDaysMore3;
                     break;}
        }       
        prStm2.setDouble(3, adjSalaryMore3);
       
        switch (rs3.getInt("reason_isfores")){
                    case 101 : {isforErgCoef = 16; ergazIsfCoef = 25.06; 
                     prStm2.setString(7, "IKA MIKTA TEAM"); break;}
                    case 102 : {isforErgCoef = 16; ergazIsfCoef = 26.06; 
                     prStm2.setString(7, "IKA MIKTA TEAM + Ε.Κ."); break;}                       
                    case 106 : {isforErgCoef = 19.45; ergazIsfCoef = 28.21; 
                     prStm2.setString(7, "ΒΑΡΕΑ TEAM"); break;}                         
                } 
        prStm2.setDouble(4, adjSalaryMore3*isforErgCoef/100 );
        prStm2.setDouble(5, adjSalaryMore3*ergazIsfCoef/100 );
        prStm2.setDouble(6, adjSalaryMore3*(isforErgCoef+ergazIsfCoef)/100);
        // Compute the TAPIT row                
             
            switch (rs3.getInt("tapit")) {
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
        prStm2.setString(8, "ΑΣΘΕΝΕΙΑ>3");
        prStm2.setDouble(9, epidotisi );
        prStm2.setDouble(10, adjSalaryMore3*metalCoef );
        prStm2.setDouble(11, adjSalaryMore3*metalCoef );        
        //FMYSickMore3 =  rs3.getDouble("FMY")*sickDaysMore3*6.667/rs3.getDouble("hours_misthou");       
        prStm2.setDouble(12, 0);
        //isforaAlilegiisSickMore3 =  rs3.getDouble("isfora_allilegiis")*sickDaysMore3*6.667/rs3.getDouble("hours_misthou");       
        prStm2.setDouble(13, 0);
        prStm2.setDate(14, rs5.getDate("start_day"));
        prStm2.setDate(15, rs5.getDate("end_day"));
        prStm2.setInt(16, rs.getInt("id"));
                  
        if(!(rs5.getDate("start_day").equals(rs5.getDate("end_day"))) && (rs5.getInt("real_days") > 0)) {  //The case where it is actually <3 i.e. the beginning of >3, e.g. 30/5, 31/5
            prStm2.executeUpdate(); 
        }              
        }catch (SQLException ex) {
           Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
           JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
             if (prStm2 != null)prStm2.close();   
        } 
        ensimaTotalMore3 += rs5.getInt("ika_days");
       
      }        
      }      
       if(rs5 != null)rs5.close();
       if(stm5 != null)stm5.close();
       
       //Create sick > 3 Total row
       
        query = "INSERT INTO "+salaryTableStr+" (id, first_name, last_name, father_name,"
                + "AM_IKA, salary, TA, ensima, hours_misthou, reason_salary, adjusted_salary, "
                + "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, "
                + "tapit_total, tapit, xartosimo, OGA, FMY, isfora_allilegiis, prokatavoli, relation) "
                + "SELECT id, first_name, "
                + "last_name, father_name, AM_IKA, salary, "
                + "1, ?, ?, 'ΑΣΘΕΝΕΙΑ', "
                + "?, ?, ?, "
                + "0, ?, reason_isfores, ?, "
                + "?, ?, ?, "
                + "0, 0, ?, tapit, "
                + "0, 0, ?, ?, 0 , relation "               
                + "FROM workers  WHERE  id = ?";                                    
        try{
        prStm2 = con.prepareStatement(query);
        sickDaysMore3 = ensimaTotalMore3;
        prStm2.setInt(1,  sickDaysMore3);
        sickDaysMore3Hours =  sickDaysMore3*6.667;
        prStm2.setDouble(2, sickDaysMore3Hours);       
        switch (rs.getInt("relation")){
            case 0 :{ adjSalaryMore3 = (rs3.getDouble("salary")/25)*sickDaysMore3 ;
                     break;}
            case 1 :{ adjSalaryMore3 = rs3.getDouble("salary")*sickDaysMore3;
                     break;}
        }       
        prStm2.setDouble(3, adjSalaryMore3);
       
        switch (rs3.getInt("reason_isfores")){
                    case 101 : {isforErgCoef = 16; ergazIsfCoef = 25.06; 
                     prStm2.setString(7, "IKA MIKTA TEAM"); break;}
                    case 102 : {isforErgCoef = 16; ergazIsfCoef = 26.06; 
                     prStm2.setString(7, "IKA MIKTA TEAM + Ε.Κ."); break;}                       
                    case 106 : {isforErgCoef = 19.45; ergazIsfCoef = 28.21; 
                     prStm2.setString(7, "ΒΑΡΕΑ TEAM"); break;}                         
                } 
        prStm2.setDouble(4, adjSalaryMore3*isforErgCoef/100 );
        prStm2.setDouble(5, adjSalaryMore3*ergazIsfCoef/100 );
        prStm2.setDouble(6, adjSalaryMore3*(isforErgCoef+ergazIsfCoef)/100);
        // Compute the TAPIT row                
             
            switch (rs3.getInt("tapit")) {
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
        prStm2.setString(8, "A>3TOTAL"); 
        prStm2.setDouble(9, epidotisiTotal );
        prStm2.setDouble(10, adjSalaryMore3*metalCoef );
        prStm2.setDouble(11, adjSalaryMore3*metalCoef );        
        //FMYSickMore3 =  rs3.getDouble("FMY")*sickDaysMore3*6.667/rs3.getDouble("hours_misthou");       
        prStm2.setDouble(12, 0);
        //isforaAlilegiisSickMore3 =  rs3.getDouble("isfora_allilegiis")*sickDaysMore3*6.667/rs3.getDouble("hours_misthou");       
        prStm2.setDouble(13, 0);
        prStm2.setInt(14, rs.getInt("id"));
                  
        if(ensimaTotalMore3 > 0)prStm2.executeUpdate(); 
                      
        }catch (SQLException ex) {
           Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
           JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
             if (prStm2 != null)prStm2.close();   
        } 
      }
        
        // Create the Astheneia Total row
        
        if((rs.getInt("sick_days_more_3")!=0) || (rs.getInt("sick_days_less_3")!=0)){
        query = "INSERT INTO "+salaryTableStr+" (id, first_name, last_name, father_name,"
                + "AM_IKA, salary, TA, ensima, hours_misthou, reason_salary, adjusted_salary, "
                + "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, "
                + "tapit_total, tapit, xartosimo, OGA, FMY, isfora_allilegiis, prokatavoli, relation) "
                + "SELECT id, first_name, "
                + "last_name, father_name, AM_IKA, salary, "
                + "1, ?, ?, 'ΑΣΘΕΝΕΙΑ', "
                + "?, ?, ?, "
                + "0, ?, reason_isfores, ?, "
                + "?, ?, ?, "
                + "0, 0, ?, tapit, "
                + "0, 0, ?, ?, 0 , relation "               
                + "FROM workers  WHERE  id = ?";                                    
        try{
        prStm2 = con.prepareStatement(query);
        sickDaysTotal = ensimaTotalMore3+ensimaTotalLess3;
        prStm2.setInt(1,  sickDaysTotal);
        sickDaysTotalHours = ensimaTotalLess3*4 + ensimaTotalMore3*6.667;
        prStm2.setDouble(2, sickDaysTotalHours);      
        prStm2.setDouble(3, adjSalaryMore3 + adjSalaryLess3);
       
        switch (rs3.getInt("reason_isfores")){
                    case 101 : {isforErgCoef = 16; ergazIsfCoef = 25.06; 
                     prStm2.setString(7, "IKA MIKTA TEAM"); break;}
                    case 102 : {isforErgCoef = 16; ergazIsfCoef = 26.06; 
                     prStm2.setString(7, "IKA MIKTA TEAM + Ε.Κ."); break;}                       
                    case 106 : {isforErgCoef = 19.45; ergazIsfCoef = 28.21; 
                     prStm2.setString(7, "ΒΑΡΕΑ TEAM"); break;}                         
                } 
        prStm2.setDouble(4, (adjSalaryLess3+adjSalaryMore3)*isforErgCoef/100 );
        prStm2.setDouble(5, (adjSalaryLess3+adjSalaryMore3)*ergazIsfCoef/100 );
        prStm2.setDouble(6, (adjSalaryLess3+adjSalaryMore3)*(isforErgCoef+ergazIsfCoef)/100);
        // Compute the TAPIT row                
             
            switch (rs3.getInt("tapit")) {
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
        prStm2.setString(8, "A-TOTAL");
        prStm2.setDouble(9, epidotisiTotal );
        prStm2.setDouble(10, (adjSalaryLess3+adjSalaryMore3)*metalCoef );
        prStm2.setDouble(11, (adjSalaryLess3+adjSalaryMore3)*metalCoef );        
        FMYSickMore3 =  rs3.getDouble("FMY")*sickDaysMore3*6.667/rs3.getDouble("hours_misthou");       
        prStm2.setDouble(12, FMYSickLess3);
        isforaAlilegiisSickLess3 =  rs3.getDouble("isfora_allilegiis")*sickDaysLess3*6.667/rs3.getDouble("hours_misthou");       
        prStm2.setDouble(13, isforaAlilegiisSickLess3);
        prStm2.setInt(14, rs.getInt("id"));
                  
        if(sickDaysTotal > 0) prStm2.executeUpdate(); 
                      
        }catch (SQLException ex) {
           Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
           JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
             if (prStm2 != null)prStm2.close();   
        } 
        }
        
        
        
     }finally {
        if (rs3 != null)rs3.close();        
        if (prStm2 != null)prStm2.close();        
    }              
            
                               
    // now update the original record. REOPEN the rs because I have used it to insert rows and it might still be
    // in the insert row.
  
   if((rs.getInt("vacation_days")!=0) || (rs.getInt("sick_days_less_3")!=0) || (rs.getInt("sick_days_more_3")!=0))  {      
    try{
      if(rs.getInt("sick_days_more_3")!=0){
        query = "SELECT * FROM generated_sick_more_three_unpaid where sick_month = "+Integer.toString(previousMonth)
             + " AND sick_year = "+Integer.toString(tableYear);
        stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);      
        rs3 = stm2.executeQuery(query);    
                   
        generatedSickMore3UnpaidMap = new HashMap<>(); 
        while (rs3.next()){ 
            generatedSickMore3UnpaidMap.put(rs3.getInt(1), rs3.getInt("unpaid_days")); 
        }        
        if (rs3 != null)rs3.close();
        if(stm2 != null)stm2.close(); 
      }  
        query = "SELECT * FROM "+salaryTableStr+" WHERE id = "+Integer.toString(rs.getInt("id"))+
                " AND ta = 1 AND reason_salary = 'ΒΑΣΙΚΕΣ ΑΠΟΔΟΧΕΣ'";        
        stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);                            
        rs3 = stm2.executeQuery(query); 
        rs3.first();    
        
        int unpaidSickDays = generatedSickMore3UnpaidMap == null || generatedSickMore3UnpaidMap.get(rs.getInt("id")) == null ? 0 
                :generatedSickMore3UnpaidMap.get(rs.getInt("id")) ;
        double adjSlaryUnpaidSickDays = rs3.getInt("relation") == 0? (rs3.getDouble("salary")/25)*unpaidSickDays :
            rs3.getDouble("salary")*unpaidSickDays;
        int newEnsima = rs3.getInt("ensima")-vacationDays-sickDaysLess3-sickDaysMore3-unpaidSickDays;
        rs3.updateInt("ensima", newEnsima);        
        rs3.updateDouble("hours_misthou", rs3.getDouble("hours_misthou")-vacationHours-sickDaysLess3Hours*2-sickDaysMore3Hours
                -unpaidSickDays*6.66);       
        rs3.updateDouble("adjusted_salary", rs3.getDouble("adjusted_salary")-adjSalary-adjSalaryLess3*2-adjSalaryMore3-adjSlaryUnpaidSickDays);        
        rs3.updateDouble("isfores_ergazomenou",rs3.getDouble("isfores_ergazomenou")-((adjSalary+adjSalaryLess3*2+adjSalaryMore3+adjSlaryUnpaidSickDays)*isforErgCoef/100 ));
        rs3.updateDouble("ergodotikes_isfores", rs3.getDouble("ergodotikes_isfores")-((adjSalary+adjSalaryLess3*2+adjSalaryMore3+adjSlaryUnpaidSickDays)*ergazIsfCoef/100 ));
        rs3.updateDouble("total", rs3.getDouble("total")-((adjSalary+adjSalaryLess3*2+adjSalaryMore3+adjSlaryUnpaidSickDays)*(isforErgCoef+ergazIsfCoef)/100));
        rs3.updateDouble("tapit_isfores_erg", rs3.getDouble("tapit_isfores_erg")-(adjSalary+adjSalaryLess3*2+adjSalaryMore3+adjSlaryUnpaidSickDays)*metalCoef);
        rs3.updateDouble("tapit_total", rs3.getDouble("tapit_total")-(adjSalary+adjSalaryLess3*2+adjSalaryMore3+adjSlaryUnpaidSickDays)*metalCoef);
        
        // Recalculate all FMYs if Sick
        
            double FMYVacationRecalc = 0; 
            double isfAllVacationRecalc = 0; 
            double FMYSickMore3Recalc = 0; 
            double isfAllSickMore3Recalc = 0 ;
        
        if(adjSalaryLess3 != 0 || adjSlaryUnpaidSickDays != 0 ){  //This is the lost revenue (plus epidotisiTotal)
            
            double recalculatedSalary = rs3.getDouble("adjusted_salary") - adjSalaryLess3 - adjSalaryMore3 - adjSlaryUnpaidSickDays - epidotisiTotal;
            double FMYRecalculated = computeFMYOfYear(con, rs.getInt(1), recalculatedSalary, isforErgCoef, metalCoef*100)/14;
            rs3.updateDouble("FMY", FMYRecalculated);                        
            rs3.updateDouble("isfora_allilegiis", isforaAlilegiis/14 );
            
            // Now devide FMYRecalculated among AΛ and ΑΣΘΕΝΕΙΑ -TOTAL
            
            FMYVacationRecalc = FMYRecalculated*(adjSalary/recalculatedSalary);
            isfAllVacationRecalc = (isforaAlilegiis/14)*(adjSalary/recalculatedSalary);
            FMYSickMore3Recalc = FMYRecalculated*((adjSalaryMore3-epidotisiTotal)/recalculatedSalary);
            isfAllSickMore3Recalc = (isforaAlilegiis/14)*((adjSalaryMore3-epidotisiTotal)/recalculatedSalary);
            
           
        }  
        
        if (newEnsima > 0)rs3.updateRow();
        else{ //Delete the row if ensima = 0
            if(rs3 != null)rs3.close();
            if(stm2 != null)stm2.close();
            query = "DELETE FROM " +salaryTableStr+" WHERE id = "+Integer.toString(rs.getInt("id"))
               + " AND ta = 1 AND reason_salary = 'ΒΑΣΙΚΕΣ ΑΠΟΔΟΧΕΣ'";
            stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);      
            stm2.executeUpdate(query);
        }
        
         //update AΛ and ΑΣΘΕΝΕΙΑ -TOTAL rows
        
         if(adjSalaryLess3 != 0 || adjSlaryUnpaidSickDays != 0 ){
             
            query = "SELECT * FROM "+salaryTableStr+" WHERE id = "+Integer.toString(rs.getInt("id"))+
                " AND astheneia_text = 'A-TOTAL'";        
            stm6 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);                            
            rs6 = stm6.executeQuery(query); 
            if(rs6.first()){ 
                rs6.updateDouble("FMY", FMYSickMore3Recalc);                        
                rs6.updateDouble("isfora_allilegiis", isfAllSickMore3Recalc);
                rs6.updateRow();
            }
                if (rs6 != null)rs6.close();        
                if (stm6 != null)stm6.close();
                FMYSickMore3Recalc = 0;
                isfAllSickMore3Recalc = 0;            
            
            if(rs.getInt("vacation_days")!=0){
                query = "SELECT * FROM "+salaryTableStr+" WHERE id = "+Integer.toString(rs.getInt("id"))+
                    " AND ta = 2 ";        
                stm6 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);                            
                rs6 = stm6.executeQuery(query); 
                if(rs6.first()){ 
                    rs6.updateDouble("FMY", FMYVacationRecalc);                        
                    rs6.updateDouble("isfora_allilegiis", isfAllVacationRecalc);
                    rs6.updateRow();
                }
                if (rs6 != null)rs6.close();        
                if (stm6 != null)stm6.close();
                FMYVacationRecalc = 0;
                isfAllVacationRecalc = 0;
            }
         }
        
        
        vacationDays = 0; sickDaysLess3 = 0; sickDaysMore3 = 0; sickDaysTotal = 0; unpaidSickDays = 0;
        vacationHours = 0; sickDaysLess3Hours = 0;sickDaysMore3Hours = 0;  sickDaysTotalHours = 0;
        adjSalary = 0; adjSalaryLess3 = 0; adjSalaryMore3 = 0; adjSlaryUnpaidSickDays = 0;
        isforErgCoef = 0  ;ergazIsfCoef = 0;
        FMYVacation = 0; FMYSickLess3 = 0; FMYSickMore3 = 0;
        isforaAlilegiisVacation = 0; isforaAlilegiisSickLess3 = 0; isforaAlilegiisSickMore3 = 0; 
        epidotisiTotal = 0;
        
        }catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            
}finally { if (rs3 != null)rs3.close();        
        if (prStm2 != null)prStm2.close();  
    }
   }
  }  
}catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           
}finally {
        if (rs != null)rs.close();
        if(stm != null)stm.close();        
        }                         
        
 //Now check for apolisi and insert up to 3 new rows for EA, AA and ML

try{ 
 if (stm2 != null)stm2.close(); 
 stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);

rs4 = stm.executeQuery("SELECT id, apolisi, diakopi FROM workers WHERE workers.apolisi <> 0 "
        + " AND (MONTH(workers.diakopi) = "+Integer.toString(previousMonth)
        + " ) AND (YEAR(workers.diakopi) = "+Integer.toString(currentYear)+")");

while (rs4.next()){
    
    //start with EA
    
    if (LocalDate.now().getMonthValue()<8){  
        stm3 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
        
        stm3.executeUpdate("INSERT INTO "+salaryTableStr+" (id, first_name, last_name, father_name, "
                + "AM_IKA, salary, TA, ensima, hours_misthou, reason_salary, adjusted_salary, "
                + "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, "
                + "tapit_total, tapit, xartosimo, OGA, FMY, isfora_allilegiis, prokatavoli, relation) "
                + "SELECT  id, first_name, last_name, father_name,"
                + "AM_IKA, salary, TA, ensima, hours_misthou, reason_salary, adjusted_salary, "
                + "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "'', 0, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, "
                + "tapit_total, tapit, xartosimo, OGA, FMY, isfora_allilegiis, prokatavoli, relation"          
                + " FROM EPIDOMA_ADEIAS_TEMPORARY_"+Integer.toString(currentYear)
                + " WHERE  id = "+Integer.toString(rs4.getInt("id")));  

    
}
    if(stm3 != null)stm3.close();
    
//Now insert rows for AA and ML using APOZIMIOSEIS table   
    
    stm2 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
    double apozimiosi = 0;
    double miLifthisesAdeies = 0;
    int imeresAdeias = 0;
    int imeresApozimiosis = 0;
    rs3 = stm2.executeQuery("SELECT * FROM APOZIMIOSEIS WHERE ID = "+Integer.toString(rs4.getInt("id")));
    while (rs3.next()){
        apozimiosi = rs3.getDouble("apozimiosi");
        miLifthisesAdeies = rs3.getDouble("mi_lifthises_adeies");
        imeresAdeias = rs3.getInt("imeres_adeias");
        imeresApozimiosis = rs3.getInt("imeres_apozimiosis");
    }
    if (rs3 != null)rs3.close();
    if (stm2 != null)stm2.close();
    if(apozimiosi != 0){
    query = "INSERT INTO "+salaryTableStr+" (id, first_name, last_name, father_name,"
                + "AM_IKA, salary, TA, ensima, hours_misthou, reason_salary, adjusted_salary, "
                + "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, "
                + "tapit_total, tapit, xartosimo, OGA, FMY, isfora_allilegiis,  prokatavoli, relation) "
                + "SELECT id, first_name, last_name, father_name, "
                + "AM_IKA, salary, 6, ?, ?, 'ΒΑΣΙΚΕΣ ΑΠΟΔΟΧΕΣ', ?, "
                + "0, 0, 0, 0, reason_isfores, '', "
                + "'', 0, 0, 0, 0, 0, tapit, "
                + "0, 0, 0, 0, 0 , relation "               
                + "FROM workers  WHERE  id = ?";
    try{
        prStm3 = con.prepareStatement(query);        
        prStm3.setInt(1, imeresApozimiosis);        
        prStm3.setDouble(2, imeresApozimiosis*6.6666666666666);             
        prStm3.setDouble(3, apozimiosi);        
        prStm3.setInt(4, rs4.getInt("id"));  
        prStm3.executeUpdate();
        
    } catch (SQLException ex) {
           Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
           JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           
    }finally{
             if (prStm3 != null)prStm3.close();   
    }
    }
    if( miLifthisesAdeies != 0){
    query = "INSERT INTO "+salaryTableStr+" (id, first_name, last_name, father_name,"
                + "AM_IKA, salary, TA, ensima, hours_misthou, reason_salary, adjusted_salary, "
                + "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "
                + "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, "
                + "tapit_total, tapit, xartosimo, OGA, FMY, isfora_allilegiis,  prokatavoli, relation) "
                + "SELECT id, first_name, last_name, father_name, "
                + "AM_IKA, salary, 7, ?, ?, 'ΒΑΣΙΚΕΣ ΑΠΟΔΟΧΕΣ', ?, "
                + "0, 0, 0, 0, reason_isfores, '', "
                + "'', 0, 0, 0, 0, 0, tapit, "
                + "0, 0, 0, 0, 0 , relation "               
                + "FROM workers  WHERE  id = ?";
    try{        
        prStm4 = con.prepareStatement(query);        
        prStm4.setInt(1, imeresAdeias);        
        prStm4.setDouble(2, imeresAdeias*8);             
        prStm4.setDouble(3, miLifthisesAdeies);        
        prStm4.setInt(4, rs4.getInt("id"));      
       
        prStm4.executeUpdate();
        
    } catch (SQLException ex) {
           Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
           JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           
    }finally{
             if (prStm4 != null)prStm4.close();   
    }
    }
    }
    }catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
    }finally {
       if (rs4 != null)rs4.close();
       if(stm != null)stm.close(); 
       if(stm3 != null)stm3.close(); 
      }
     
      //  this.epidotisiMap.put(1, Arrays.asList(new Double(0),new Double(0),new Double(0)));
    if(!this.epidotisiMap.isEmpty()){
        System.out.println(this.salaryTableStr);
        RequestContext context = RequestContext.getCurrentInstance();        
        context.execute("PF('misthodosiaDlgWV').show();");        
        context.update("misthodosiaGT3:GT3Box");
        
    }else
    JsfUtil.addSuccessMessage("Το Report μισθοδοσίας Ολοκληρώθηκε");    
    
    }
        else{           
            JsfUtil.addErrorMessage("Ο πίνακας "+reportTableStr+" δεν έχει δημιουργηθεί ακόμη. Παρακαλώ "
                    + "δημιουργείστε το timer Report " + Integer.toString(previousMonth)+ "/"
                    + Integer.toString(tableYear) + "και προσπαθείστε ξανά");           
            return ;
        }
}                  
        
    
    public double computeMisthosHours(int id, double misthosDays, String reportTableStr ) 
            throws SQLException{
        double misthosHours = 0 ;
        String query1 ="SELECT id, cut_hours FROM "+reportTableStr
                +" WHERE id = "+Integer.toString(id);
        try{                 
            stm1 = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                                            
            rs2 = stm1.executeQuery(query1);            
            while(rs2.next()){                 
            if(rs2.getDouble(2) < 0) misthosHours = misthosDays*6.667;               
            else misthosHours = misthosDays*6.667-rs2.getDouble(2);               
            }
        }catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            
        }
        
        return misthosHours;
    }
    public double computeFMYOfYear(Connection conF,int id, double salary ,double isforErgCoef, double metalCoef ) throws SQLException  { 

        int children = 0, foroapallagi =1900;
            double katharaOfYear = 0;
        try { 
            stmchldrn = conF.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            rschldrn = stmchldrn.executeQuery("SELECT CHILDREN FROM WORKERS WHERE ID = "+Integer.toString(id));
            if(rschldrn.next())children = rschldrn.getInt(1);
       
            katharaOfYear = salary*14*(1-((isforErgCoef+metalCoef)/100));
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            
        }
            double tax = katharaOfYear * 0.22;
            if (katharaOfYear > 20000)tax += (katharaOfYear - 20000)*0.07;
            if (katharaOfYear > 30000)tax += (katharaOfYear - 30000)*0.08;
            if (katharaOfYear > 40000)tax += (katharaOfYear - 40000)*0.08;
            foroapallagi = children == 0 ? 1900: children == 1 ? 1950 : children == 2 ? 2000 : children >= 3? 2100:0;
            if (tax < foroapallagi)
                tax = 0;
            else if(katharaOfYear <= 20000)
                tax = tax - foroapallagi;
            else if(katharaOfYear > 20000) 
                tax = tax - (foroapallagi - ((katharaOfYear-20000)*0.1));
            
            // Compute also isforaAlilegiis
            
            isforaAlilegiis = computeIsforaAlilegiis(katharaOfYear);
            
            //λόγω προπληρωμής του φόρου υπάρχει έκπτωση 1,5%
            return tax*0.985;  
         
    }
    
    public double computeIsforaAlilegiis(double katharaOfYear){
        double isfora = 0;
        
      /*if (katharaOfYear > 12000)isfora += (katharaOfYear - 11999) * 0.022 ;
        if (katharaOfYear > 20000)isfora += (katharaOfYear - 20000) * 0.028 ;
        if (katharaOfYear > 30000)isfora += (katharaOfYear - 50000) * 0.015 ;
        if (katharaOfYear > 40000)isfora += (katharaOfYear - 100000) * 0.01 ;
        if (katharaOfYear > 65000)isfora += (katharaOfYear - 100000) * 0.015 ;
        if (katharaOfYear > 155000)isfora += (katharaOfYear - 100000) * 0.01 ;*/
        
      if (katharaOfYear > 220000){
          isfora += (katharaOfYear - 220000) * 0.10;
          isfora += (220000 - 65000) * 0.09;
          isfora += (65000 - 40000)  * 0.075;
          isfora += (40000 - 30000)  * 0.065;
          isfora += (30000 - 20000)  * 0.05;
          isfora += (20000 - 12000)  * 0.022;
      }
      else if (katharaOfYear >  65000){
          isfora += (katharaOfYear - 65000) * 0.09;
          isfora += (65000 - 40000)  * 0.075;
          isfora += (40000 - 30000)  * 0.065;
          isfora += (30000 - 20000)  * 0.05;
          isfora += (20000 - 12000)  * 0.022;          
      }
      else if (katharaOfYear >  40000){          
          isfora += (katharaOfYear - 40000)  * 0.075;
          isfora += (40000 - 30000)  * 0.065;
          isfora += (30000 - 20000)  * 0.05;
          isfora += (20000 - 12000)  * 0.022;          
      }
      else if (katharaOfYear >  30000){                    
          isfora += (katharaOfYear - 30000)  * 0.065;
          isfora += (30000 - 20000)  * 0.05;
          isfora += (20000 - 12000)  * 0.022;          
      }
      else if (katharaOfYear >  20000){                    
          isfora += (katharaOfYear - 20000)  * 0.05;
          isfora += (20000 - 12000)  * 0.022;          
      }
      else if (katharaOfYear >  12000){                              
          isfora += (katharaOfYear - 12000)  * 0.022;          
      }      
      else isfora =0;     

      return isfora;
    }    
}
  

