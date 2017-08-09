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
import static java.time.temporal.ChronoUnit.DAYS;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author evgero
 */
public class CreatePashaReport {
    
     /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

    
    private Connection con = null;
    private ResultSet rs = null, rs1 = null ;
    private Statement stm = null ;
    private PreparedStatement prStm = null;
    private int update, previousMonth, currentYear, ensimaDays,  workDaysTotal = 0, apolimenosWorkingDays = 0;
    private Map<Integer, Integer> adikaiologitesMap = new HashMap<>();     
    private String doroPashaTableStr;
    private String[] reportTableStr;
    private int[] workDays = new int[12];
    private LocalDate hireLocalDate, diakopiLocalDate;
    private Map<Integer, List<LocalDate>> apolimenoiMap ;
    private List<LocalDate> holidaysList ;
    private double adjustedSalary, prosafxisiDorou, ensimaHours, isforErgCoef, ergazIsfCoef, metalCoef;
    private int tableYear ;
    
    public void createDBDoroPashaReport (Connection con , int year) throws SQLException{
        
        this.con = con;
        
        currentYear = tableYear =  year == 0 ? LocalDate.now().getYear() : year;
        
        /* find which is the previous month */
        
        previousMonth = year == 0 ? LocalDate.now().minusMonths(1).getMonthValue() : 4;        
        if (previousMonth > 4) previousMonth = 4;        
        
        reportTableStr = new String[4];                                      
        reportTableStr[0] = "REPORT_1_"+Integer.toString(currentYear); 
        reportTableStr[1] = "REPORT_2_"+Integer.toString(currentYear);
        reportTableStr[2] = "REPORT_3_"+Integer.toString(currentYear);
        reportTableStr[3] = previousMonth == 3 ? null : "REPORT_4_"+Integer.toString(currentYear);
        doroPashaTableStr = "DORO_PASHA_REPORT_"+Integer.toString(currentYear);       
            
        /* Check if the table exists and if yes delete it */
        
        List<String> missingTableList = new ArrayList<>();
        boolean reportTableExists = true;
        for (int i=0; i<=3; i++){
            try {            
            DatabaseMetaData databaseMetaData = con.getMetaData();
            rs1 = databaseMetaData.getTables(null, null, reportTableStr[i] , null);
            if (!rs1.next()){
                reportTableExists = false;
                missingTableList.add(reportTableStr[i]);
            }             
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{            
            if(rs1 != null)rs1.close();
        }
        }
        
        if(reportTableExists){
        
           /* Check if the table exists and if yes delete it */  
            
        try{
            DatabaseMetaData databaseMetaData = con.getMetaData();            
            rs1 = databaseMetaData.getTables(null, null, doroPashaTableStr , null);
            if (rs1.next()) {                                               
                stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
                update = stm.executeUpdate("DROP TABLE "+doroPashaTableStr);                     
            } 
        }catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally {
            if (stm != null)stm.close();
            if(rs1 != null){rs1.close();}
        }                      
        
        String query = "CREATE TABLE "+doroPashaTableStr+" (ID SMALLINT, First_Name VARCHAR(20), "
    + "Last_Name VARCHAR(20), father_name VARCHAR(20), AM_IKA VARCHAR(20), salary DOUBLE, "      
    + "TA SMALLINT, ensima SMALLINT, hours_misthou DOUBLE, reason_salary VARCHAR(40), reason_salary_2 VARCHAR(40), "            
    + "adjusted_salary DOUBLE, prosafxisi_dorou DOUBLE, isfores_ergazomenou DOUBLE, ergodotikes_isfores DOUBLE, "  
    + "mee DOUBLE, total DOUBLE, reason_isfores SMALLINT, reason_isfores_text VARCHAR(40), "
    + "tapit_isfores_erg DOUBLE,  "
    + "tapit_ergod_isfores DOUBLE, tapit_mee DOUBLE, tapit_total DOUBLE, tapit SMALLINT,  "
    + "xartosimo DOUBLE, OGA DOUBLE, FMY DOUBLE, isfora_allilegiis DOUBLE, "
    + "kratisis DOUBLE GENERATED ALWAYS AS (isfores_ergazomenou+tapit_isfores_erg+fmy+isfora_allilegiis), "
    + "kathara DOUBLE GENERATED ALWAYS AS (adjusted_salary+prosafxisi_dorou-(isfores_ergazomenou+tapit_isfores_erg+fmy+isfora_allilegiis)), "
    + "kostos DOUBLE GENERATED ALWAYS AS (adjusted_salary+prosafxisi_dorou+ergodotikes_isfores), prokatavoli DOUBLE, relation SMALLINT, "
    + "pliroteo DOUBLE GENERATED ALWAYS AS (adjusted_salary+prosafxisi_dorou-(isfores_ergazomenou+tapit_isfores_erg+fmy+isfora_allilegiis)-prokatavoli))";                      
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
        try{
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
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
            if (stm != null)stm.close();
            if (rs1 != null )rs1.close();
        }
        try { 
         stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
         if (previousMonth != 4) workDays[3]= computeAprilWorkingDays ();
         for (int i = 0 ; i < previousMonth ; i++){
           if (reportTableStr[i] != null){            
            query = "SELECT t1.id, t1.work_days, t2.hire_date, t2.diakopi, "
                + "t1.absent_days - t1.sick_days_less_3 - t1.sick_days_more_3 - t1.vacation_days ,"
                + "t2.apolisi "                
                + "FROM "+reportTableStr[i]+" AS t1, workers AS t2 "
                + "WHERE t1.id = t2.id ";                                        
            rs = stm.executeQuery(query);           
            
            rs.first();
            workDays[i] = rs.getInt(2);
            rs.beforeFirst();
            while(rs.next()){ 
                hireLocalDate = rs.getDate(3).toLocalDate();
                diakopiLocalDate = rs.getInt(6) != 0 ? 
                        rs.getDate(4).toLocalDate() : null; 
                if (diakopiLocalDate == null || (diakopiLocalDate.getMonthValue() > i+1 && 
                        diakopiLocalDate.getYear() == tableYear)){
                    //check the adikaiologites if he was hired during the 4 month period                  
                    if (hireLocalDate.getMonthValue() == i+1 && hireLocalDate.getYear() == tableYear){
                        LocalDate counterDate = hireLocalDate;
                        LocalDate endOfMonth = hireLocalDate.with(TemporalAdjusters.lastDayOfMonth());
                        while (counterDate.isBefore(endOfMonth) || counterDate.isEqual(endOfMonth)) {
                            if ((counterDate.getDayOfWeek().getValue()!= 6) &&
                                (counterDate.getDayOfWeek().getValue()!= 7) &&
                                (!holidaysList.contains(counterDate))){
                                    apolimenosWorkingDays++;                              
                            }
                        counterDate = counterDate.plusDays(1);                        
                        }
                        int apolimenosAbsentDays =   rs.getInt(5) + workDays[i]-apolimenosWorkingDays;
                        apolimenosWorkingDays = 0;
                        if (adikaiologitesMap.containsKey(rs.getInt(1))){
                            int newVal = adikaiologitesMap.get(rs.getInt(1))+apolimenosAbsentDays;
                            adikaiologitesMap.put(rs.getInt(1), newVal) ;
                        }else 
                            adikaiologitesMap.put(rs.getInt(1), apolimenosAbsentDays) ;                
                    }else{                    
                        if (adikaiologitesMap.containsKey(rs.getInt(1))){
                        int newVal = adikaiologitesMap.get(rs.getInt(1))+rs.getInt(5);
                        adikaiologitesMap.put(rs.getInt(1), newVal) ;
                        }else 
                        adikaiologitesMap.put(rs.getInt(1), rs.getInt(5)) ;
                    }
                }else if (diakopiLocalDate.getMonthValue() == i+1 && diakopiLocalDate.getYear() == tableYear){                   
                
                    //compute real adikaiologites if apolimenos
                    LocalDate firstDayOfMonth = (hireLocalDate.getMonthValue() == i+1 && hireLocalDate.getYear() == tableYear) ?
                            hireLocalDate : LocalDate.of(tableYear, i+1, 1);/*We compute the first day of the month
                     and check if he was hired and fired in the same month*/          
                    LocalDate counterDate = firstDayOfMonth;
                    while (counterDate.isBefore(diakopiLocalDate) || counterDate.isEqual(diakopiLocalDate)) {
                        if ((counterDate.getDayOfWeek().getValue()!= 6) &&
                            (counterDate.getDayOfWeek().getValue()!= 7) &&
                            (!holidaysList.contains(counterDate))){
                                apolimenosWorkingDays++;                              
                        }
                        counterDate = counterDate.plusDays(1);
                    }
                    int apolimenosAbsentDays =   rs.getInt(5) + workDays[i] - apolimenosWorkingDays;
                    apolimenosWorkingDays = 0;
                    if (adikaiologitesMap.containsKey(rs.getInt(1))){
                        int newVal = adikaiologitesMap.get(rs.getInt(1))+apolimenosAbsentDays;
                        adikaiologitesMap.put(rs.getInt(1), newVal) ;
                    }else 
                    adikaiologitesMap.put(rs.getInt(1), apolimenosAbsentDays) ;
                }  
            }
        if (rs != null) rs.close();
        //if (stm!=null)stm.close();
        }
        } 
         
         
        //Now use adikaiologitesMap , workDays[i] to compute Doro Pasha
        
         query = "SELECT * FROM workers WHERE ((workers.apolisi = 0 OR (workers.apolisi <> 0  "
             +" AND ((YEAR(workers.diakopi) > " +Integer.toString(currentYear)+ ") OR "
                 + "(YEAR(workers.diakopi) = " +Integer.toString(currentYear)+" AND MONTH(workers.diakopi) < 5 ))))"
                 + "AND ((MONTH(workers.hire_date) < 5 AND YEAR(workers.hire_date) = "
                 + Integer.toString(currentYear)+") OR YEAR(workers.hire_date) < "+ Integer.toString(currentYear)+"))";
         
         rs = stm.executeQuery(query);
         workDaysTotal = 0;
         for (int i = 0; i < 4; i++)workDaysTotal += workDays[i];
         while (rs.next()){
          int id = rs.getInt("id");
          if ((rs.getInt("apolisi") == 0) || (rs.getInt("apolisi") != 0 &&
                (rs.getDate("diakopi").toLocalDate().isAfter(LocalDate.of(tableYear, 4, 30))))){
            
            hireLocalDate = rs.getDate("hire_date").toLocalDate();             
            if (hireLocalDate.isBefore(LocalDate.of(currentYear, 1, 1))){ //normal case
              switch (rs.getInt("relation")){
                case 0 : {      //misthotos
                adjustedSalary = rs.getDouble("salary")*0.5*
                        (1 - (double)adikaiologitesMap.get(id)/(double)workDaysTotal); //adikaiologites Coeff. 
                ensimaHours = 83.3 * 
                            (1 - (double)adikaiologitesMap.get(id)/(double)workDaysTotal); //adikaiologites Coeff.;
                break;
                }
                case 1 : {      //imeromisthios
                adjustedSalary = rs.getDouble("salary")*15*
                        (1 - (double)adikaiologitesMap.get(id)/(double)workDaysTotal); //adikaiologites Coeff.; 
                ensimaHours = 100 * 
                            (1 - (double)adikaiologitesMap.get(id)/(double)workDaysTotal); //adikaiologites Coeff.;
                break;
                }
              }
            }
          }else{
                LocalDate startDate = hireLocalDate.isBefore(LocalDate.of(tableYear, 1, 1)) ?
                   LocalDate.of(tableYear, 1, 1) :  hireLocalDate;                 
                LocalDate endDate = rs.getInt("apolisi") == 0 || (rs.getInt("apolisi") != 0 
                   && (rs.getDate("diakopi").toLocalDate().isAfter(LocalDate.of(tableYear, 4, 30)))) ?
                   LocalDate.of(tableYear, 4, 30) :  rs.getDate("diakopi").toLocalDate();
                //Compute the working days
                int apolimenosWorkDays = 0;
                LocalDate counter = startDate;
                while (counter.isBefore(endDate) || counter.isEqual(endDate)) {
                            if ((counter.getDayOfWeek().getValue()!= 6) &&
                                (counter.getDayOfWeek().getValue()!= 7) &&
                                (!holidaysList.contains(counter))){
                                    apolimenosWorkDays++;                              
                        }
                    counter = counter.plusDays(1);                        
                 }
                switch (rs.getInt("relation")){
                case 0 : {      //misthotos
                adjustedSalary = (rs.getDouble("salary") /240.0)*(double)startDate.until(endDate, DAYS)  *
                        (1 - (double)(adikaiologitesMap.get(id)/(double)apolimenosWorkDays)); //adikaiologites Coeff. 
                ensimaHours = (83.3/(double)workDaysTotal)* (double)apolimenosWorkDays * 
                            (1 - (double)adikaiologitesMap.get(id)/(double)apolimenosWorkDays); //adikaiologites Coeff.;
                break;
                }
                case 1 : {      //imeromisthios
                adjustedSalary = (rs.getDouble("salary")*((double)(apolimenosWorkDays - adikaiologitesMap.get(id)))/6.5); 
                ensimaHours = (100 /(double)workDaysTotal) * (double)apolimenosWorkDays * 
                            (1 - (double)adikaiologitesMap.get(id)/(double)apolimenosWorkDays); //adikaiologites Coeff.;
                break;
                }
                }
             }
            
            prosafxisiDorou = adjustedSalary*0.0416666;
            ensimaDays = (int)(Math.round(ensimaHours)/6.6666);
            String strQry = "INSERT INTO "+doroPashaTableStr+"(ID , First_Name , "
                    + "Last_Name, father_name , AM_IKA , salary , "      
                    + "TA , ensima, hours_misthou, reason_salary , reason_salary_2, "            
                    + "adjusted_salary , prosafxisi_dorou, isfores_ergazomenou, ergodotikes_isfores, "  
                    + "mee , total , reason_isfores , reason_isfores_text , "
                    + "tapit_isfores_erg,  "
                    + "tapit_ergod_isfores, tapit_mee, tapit_total, tapit,  "
                    + "xartosimo , OGA, FMY, isfora_allilegiis, "     
                    + " prokatavoli , relation) "   
                    + "VALUES (?, ?, ?, ?, ?, ?, 3 , ?, ?, 'ΒΑΣΙΚΕΣ ΑΠΟΔΟΧΕΣ', "
                    + "'ΠΡΟΣΑΥΞΗΣΗ ΔΩΡΟΥ', ?, ?, ?, ?, 0, ?, ?, ?, ?, 0, 0, ?, ?, 0, 0, ?, ?, 0, ? )";
                    
            prStm = con.prepareStatement(strQry);
            prStm.setInt(1, id);
            prStm.setString(2, rs.getString("first_name"));
            prStm.setString(3, rs.getString("last_name"));
            prStm.setString(4, rs.getString("father_name"));            
            prStm.setString(5, rs.getString("am_ika"));
            prStm.setDouble(6, rs.getDouble("salary"));            
            prStm.setDouble(7, ensimaDays);
            prStm.setDouble(8, ensimaHours);
            prStm.setDouble(9, adjustedSalary);
            prStm.setDouble(10, prosafxisiDorou);
            switch (rs.getInt("reason_isfores")){
                    case 101 : {isforErgCoef = 15.5; ergazIsfCoef = 24.56;
                        prStm.setString(15, "IKA MIKTA TEAM"); break;}
                    case 102 : {isforErgCoef = 15.5; ergazIsfCoef = 25.56;
                        prStm.setString(15, "IKA MIKTA TEAM + Ε.Κ."); break;}                       
                    case 106 : {isforErgCoef = 18.95; ergazIsfCoef = 27.71;
                        prStm.setString(15, "ΒΑΡΕΑ TEAM"); break;}                         
                } 
            prStm.setDouble(11, (adjustedSalary+prosafxisiDorou)*isforErgCoef/100);
            prStm.setDouble(12, (adjustedSalary+prosafxisiDorou)*ergazIsfCoef/100 );
            prStm.setDouble(13, (adjustedSalary+prosafxisiDorou)*(isforErgCoef+ergazIsfCoef)/100);
            prStm.setInt(14, rs.getInt("reason_isfores"));
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
            
            prStm.setDouble(16, (adjustedSalary+prosafxisiDorou)*metalCoef );
            prStm.setDouble(17, (adjustedSalary+prosafxisiDorou)*metalCoef );
            prStm.setInt(18, rs.getInt("tapit"));
            //Compute FMY
            switch (rs.getInt("relation")){
                case 0 : {      //misthotos
                    CreateSalaryReport createSalaryReport = new CreateSalaryReport();
                    prStm.setDouble(19, createSalaryReport.computeFMYOfYear(con,rs.getInt(1),rs.getDouble("salary") ,
                            isforErgCoef, metalCoef)*
                        ((adjustedSalary+prosafxisiDorou)/(rs.getDouble("salary"))/2) );//add a weight coef.
                    prStm.setDouble(20, createSalaryReport.getIsforaAlilegiis()*
                        ((adjustedSalary+prosafxisiDorou)/(rs.getDouble("salary"))/2) );//add a weight coef.    
                    break;        
                }
                case 1 : {      //imeromisthios
                    CreateSalaryReport createSalaryReport = new CreateSalaryReport();
                    prStm.setDouble(19, createSalaryReport.computeFMYOfYear(con, rs.getInt(1), rs.getDouble("salary")*26, isforErgCoef, metalCoef)*                            
                        ((adjustedSalary+prosafxisiDorou)/(rs.getDouble("salary")*13)));//add a weight coef.
                    prStm.setDouble(20, createSalaryReport.getIsforaAlilegiis()*
                        ((adjustedSalary+prosafxisiDorou)/(rs.getDouble("salary")*13)));//add a weight coef.
                            
                    break;        
                }
            }           
            prStm.setInt(21, rs.getInt("relation"));      
            
            prStm.executeUpdate();
         }                          
        JsfUtil.addSuccessMessage("Το Report σας για το Δώρο Πάσχα ολοκληρώθηκε επιτυχώς");
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally {
            if (stm != null)stm.close();
            if (rs != null )rs.close();
            if (prStm != null)prStm.close();
        }
        }else{
            String allTables = "";
            for(String p: missingTableList){
                allTables += p + ", ";            
            }                                           //remove last comma            
            JsfUtil.addErrorMessage("Οι πίνακες "+allTables.trim().substring(0, allTables.length()-2)
                    +" δεν έχουν δημιουργηθεί ακόμη. Παρακαλώ "
                    + "δημιουργείστε τα αντίστοιχα timer Report " 
                    + "και προσπαθείστε ξανά");            
        }         
    }
    public int computeAprilWorkingDays (){
        int aprWorkingDays = 0;
        LocalDate firstDayOfMonth = LocalDate.of(tableYear, 12, 1);
        LocalDate lastDayOfMonth = LocalDate.of(tableYear, 12, 31);
        LocalDate counterDate = firstDayOfMonth;
        while (counterDate.isBefore(lastDayOfMonth) || counterDate.isEqual(lastDayOfMonth)) {
           if ((counterDate.getDayOfWeek().getValue()!= 6) &&
                     (counterDate.getDayOfWeek().getValue()!= 7) &&
                     (!holidaysList.contains(counterDate))
              )
                       {     aprWorkingDays++;                              
                        }
           counterDate = counterDate.plusDays(1);
        }
        
        return aprWorkingDays;
    }
    
}
    

