/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

import Controllers.util.DropIfExists;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;;;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author Home
 */
public class CreateVacationReport {
    
    Connection con = null;
    ResultSet rs = null, rs1 = null;
    Statement stm = null ;
    PreparedStatement prStm = null;
    Stage popStage;
    String query1 = "";
    int previousMonth, currentYear, update;
    private Map<Integer, Integer> consumedMap = new HashMap<>();
    private List<LocalDate> holidaysList  = new ArrayList<>();
    private final FacesContext facesContext = FacesContext.getCurrentInstance();    
    
    //Default Constructor
    public CreateVacationReport(){      
    }
    //Non Default Constructor
    public CreateVacationReport(Connection con){
        this.con =  con;
    }
    
    public void CreateVacationDBTable(Connection con) throws SQLException{
        
        try {
            this.con = con;                      
            
            /* find which is the previous month */
            
            previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
            int tableYear = LocalDate.now().minusMonths(1).getYear(); 
            currentYear = LocalDate.now().getYear();
                                              
            String tableStr = "REPORT_"+Integer.toString(previousMonth)
                    +"_"+Integer.toString(tableYear);
            
            
            DropIfExists.exec(con, "VIEW", "TEMP2");
            DropIfExists.exec(con, "VIEW", "TEMP1");
            DropIfExists.exec(con, "TABLE", "TEMP");
            
             /*TEMP1 is used to add any new employees in the list and remove the fired of last year*/ 
            
            
            String query = "CREATE VIEW TEMP1 (ID, firstname, lastname) AS "                   
                    + "SELECT ID, first_name, last_name FROM workers "                    
                    + "WHERE (workers.apolisi = 0 OR (workers.apolisi <> 0 "
                    + "AND (YEAR(workers.diakopi) = " +Integer.toString(tableYear)+ ")"
                    + "AND (MONTH(workers.diakopi) > " +Integer.toString(previousMonth - 1)+ ")))";
            
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            int update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            
            
            query = "CREATE TABLE TEMP ( " +
                            "ID smallint, " +
                            "firstname varchar(20), " +
                            "lastname varchar(20), " +
                            "ENTITLED_DAYS smallint DEFAULT 0, " +
                            "LASTYEAR_DAYS smallint DEFAULT 0, " +
                            "CONSUMED_DAYS smallint DEFAULT 0, " +
                            "REMAINING_DAYS smallint DEFAULT 0)";            
            
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm!= null){stm.close();} 
            
            
            
            query = "CREATE VIEW TEMP2 (ID, firstname, lastname, entitled_days, lastyear_days, " +
                    "consumed_days, remaining_days) AS " +
            "SELECT TEMP1.ID, TEMP1.firstname, TEMP1.lastname, T1.entitled_days, " +
            "T1.lastyear_days, T1.consumed_days, " +
            "T1.remaining_days " +
            "FROM TEMP1 " +
            "LEFT JOIN (EMPLOYEE_VACATION "  
                    + " AS T1) "
    //        + " JOIN workers "
    //        + "ON T1.id = workers.id "
    //                + "AND (workers.apolisi = 0 OR (workers.apolisi <> 0 "
    //                + "AND (YEAR(workers.diakopi) = " +Integer.toString(tableYear)+ "))) "                            
            +"ON TEMP1.ID = T1.ID ";
            
            
            
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
             /*Compute (IN JAVA !!) the consumed days from VACATION_DAYS table*/        
                
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        String sql1 = "SELECT * FROM holidays";
        rs1 = stm.executeQuery(sql1);
        /* Put rs1 in a List */        
        while (rs1.next()){            
            holidaysList.add(rs1.getDate(1).toLocalDate());
        }
        if (stm != null) { stm.close();}
        if (rs1 != null) {rs1.close();}
        
        computeConsumedDays(currentYear) ;       
                       
    query = "INSERT INTO TEMP (ID, firstname, lastname, entitled_days, lastyear_days " +
             ", consumed_days, remaining_days " +
    ") SELECT t.ID, t.firstname, t.lastname, t.ENTITLED_DAYS, t.LASTYEAR_DAYS " +
             ", 0, 0 " +    
    "FROM TEMP2 t " ;
        
    stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
    update = stm.executeUpdate(query);
    if (stm != null){stm.close();}
    
         /* Set to Zero any null values in TEMP */ 
        
    query = "UPDATE temp set lastyear_days = 0 where lastyear_days IS NULL";
    stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
    update = stm.executeUpdate(query);
    if (stm != null){stm.close();}                        
    query = "UPDATE temp set consumed_days = 0 where consumed_days IS NULL";
    stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
    update = stm.executeUpdate(query);
    if (stm != null){stm.close();}
    
        /*Now update the consumed days in TEMP*/
    
    for(Map.Entry<Integer, Integer> entry : consumedMap.entrySet())
    {        
    String str = "UPDATE temp SET consumed_days = ? WHERE id = ? ";        
    prStm = con.prepareStatement(str);    
    prStm.setInt(1, entry.getValue());
    prStm.setInt(2, entry.getKey());
    int updatedRows = prStm.executeUpdate();    
    if (prStm != null) {prStm.close();}
    }
    
     /* Update lastYear_days if currentmonth = JAN and user clicks button  */    
    
    if ( LocalDate.now().getMonth() == Month.JANUARY){ 
        
        popStage = new Stage(StageStyle.DECORATED);
        popStage.setTitle("Μεταφορά ημερών αδείας από προηγούμενο έτος");
        Text text = new Text("Εφόσον δεν έχετε κάνει μεταφορά των ημερών αδείας "+
                "από το προηγούμενο έτος παρακαλώ πατείστε ΝΑΙ");
        text.setWrappingWidth(300);        
        Button btn = new Button("ΝΑΙ");
        btn.setFocusTraversable(false);
        Button btn2 = new Button("Το έχω ήδη κάνει");
        btn2.setFocusTraversable(true);        
        btn.setOnAction((ActionEvent event) -> {
            Statement ssss = null;
            ResultSet rsss = null;
            try {
                createLastYearDayReport(currentYear-1, popStage);
                String query2 = "SELECT id, remaining_days FROM VACATION_REPORT_" +Integer.toString(currentYear-1);
                ssss = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                rsss = ssss.executeQuery(query2);
                while (rsss.next()){
                    query1 = "UPDATE temp SET lastyear_days = "+Integer.toString(rsss.getInt("remaining_days"))+
                            " WHERE temp.id = "+Integer.toString(rsss.getInt("id"));
                    stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                    int update1 = stm.executeUpdate(query1);
                    if(stm != null)stm.close();
                }                
                popStage.close();                
            }catch (SQLException ex) {
                Logger.getLogger(CreateVacationReport.class.getName()).log(Level.SEVERE, null, ex);                
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
            } finally {
                {try {
                    if (stm != null)stm.close();
                    if (rsss != null)rsss.close();
                    if (ssss != null)ssss.close();
                } catch (SQLException ex) {
                    Logger.getLogger(CreateVacationReport.class.getName()).log(Level.SEVERE, null, ex);
                   facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
                }
                }
            }
        });
        btn2.setOnAction((ActionEvent event) -> {
            popStage.close();            
        });
        BorderPane border = new BorderPane();
        border.setPadding(new Insets(25, 25, 25, 25));
        BorderPane.setAlignment(text, Pos.CENTER);
        border.setTop(text);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(120);
        hBox.getChildren().addAll(btn, btn2);
        border.setBottom(hBox);
        Scene scene = new Scene(border, 500, 250);        
        popStage.setScene(scene);
        popStage.showAndWait();
        
        }           
        /* Now compute the remaining days correctly */
    
        query = "UPDATE temp SET remaining_days = entitled_days + lastyear_days - consumed_days ";                            
                           
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
        update = stm.executeUpdate(query); 
    
          facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Η δημιουργία του report αδειών ολοκληρώθηκε!", 
                  "ΟΛΟΚΛΗΡΩΘΗΚΕ"));         
        
        } catch (SQLException ex) {
            Logger.getLogger(CreateVacationReport.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
        }
        finally{
            
            /* Perform Cleanup*/
    
        String query = "DROP VIEW TEMP2" ;
        
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                   ResultSet.CONCUR_UPDATABLE);                      
        int update = stm.executeUpdate(query);
        if (stm != null){stm.close();}
        
        query = "DROP VIEW TEMP1" ;
        
       stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
       update = stm.executeUpdate(query);
       if (stm!= null) {stm.close();}
        
        query = "DROP TABLE EMPLOYEE_VACATION" ;
        
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
        update = stm.executeUpdate(query);
        if (stm != null){stm.close();}

        query = "RENAME TABLE TEMP TO EMPLOYEE_VACATION";

        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
                      
        update = stm.executeUpdate(query);
        if (stm != null){stm.close();}
         
        }
        
    }    
    
    public void computeConsumedDays(int year){
        try{
            /*Put consumed days in a Map with ID | consumed Days */
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        String sql = "SELECT * FROM vacation_days";
        rs = stm.executeQuery(sql);
        consumedMap.clear();
        while (rs.next()){              
            int id = rs.getInt(1);
            Date exitDay = rs.getDate(2);
            Date backDay = rs.getDate(3);
            int counter = 0;
            LocalDate exitLocalDate = exitDay.toLocalDate();
            LocalDate backLocalDate = backDay.toLocalDate();            
            LocalDate localDate = exitLocalDate;
            while (localDate.isBefore(backLocalDate) || localDate.isEqual(backLocalDate)) {                
                if ((localDate.getDayOfWeek().getValue()!= 6) &&
                     (localDate.getDayOfWeek().getValue()!= 7) &&
                     (!holidaysList.contains(localDate)) 
                      && (localDate.getYear() == year)) {                     
                            counter++;                              
                }
                localDate = localDate.plusDays(1);
            }
            if (!consumedMap.containsKey(id)){
                consumedMap.put(id,counter);
            }else {
                counter += consumedMap.get(id);
                consumedMap.replace(id, counter);
            }           
           } 
        if (rs != null) {rs.close();}
        if (stm != null) {stm.close();} 
            
        } catch (SQLException ex) {
            Logger.getLogger(CreateVacationReport.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
        } finally{
            {try {
                if (rs != null)rs.close() ; 
                if (stm != null) stm.close();
                } catch (SQLException ex) {
                    Logger.getLogger(CreateVacationReport.class.getName()).log(Level.SEVERE, null, ex);
                    facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
                }
            }            
        }
    }
    
    
    public Map<Integer, Integer> computeEntitledDays (LocalDate date) {  
        LocalDate today = date;
        ResultSet rs12 = null;
        Statement statmnt = null;
        int entitledDays = 0;
        Map<Integer, Integer> entitledDaysMap = new HashMap<>();
        try {            
            statmnt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT ID, HIRE_DATE, YEARS_BEFORE, ENTITLED_DAYS , apolisi, diakopi FROM WORKERS";
            rs12 = statmnt.executeQuery(query);                      
            while (rs12.next()) {
                int id = rs12.getInt("ID");
                LocalDate apolisiDate;
                if (rs12.getInt("apolisi") != 0)apolisiDate = rs12.getDate("diakopi").toLocalDate();
                        else apolisiDate = (today.getMonthValue() == 12 && today.getDayOfMonth() == 31) ? today :  today.plusDays(1);
                if (today.getYear()<=apolisiDate.getYear()){
                    LocalDate hireLocalDate = rs12.getDate("HIRE_DATE").toLocalDate();                 
                    int yearsBefore = rs12.getInt("YEARS_BEFORE");                    
                    
                    if (((hireLocalDate.until(apolisiDate)).getYears()+yearsBefore) >= 25) {
                        entitledDays = 26;
                    }
                    else if ((((hireLocalDate.until(apolisiDate)).getYears()+yearsBefore) >= 12) ||
                         ((hireLocalDate.until(apolisiDate)).getYears() >= 10)){
                        entitledDays = 25;                       
                    }
                    else if ((hireLocalDate.until(apolisiDate)).getYears() >=2 ){
                        entitledDays = 22;                    
                    }
                
                    else if ((hireLocalDate.until(apolisiDate).getYears()) >= 1  ) {
                        switch (apolisiDate.getYear() - hireLocalDate.getYear()){ 
                            case 1 : entitledDays = (int)Math.round((ChronoUnit.DAYS.between(LocalDate.of(apolisiDate.getYear(), 1, 1), apolisiDate)*21D)/
                                        (LocalDate.of(apolisiDate.getYear(), 12, 31).getDayOfYear()*1D));
                                    break;
                            case 2 : entitledDays = 21;
                                    break;
                        }
                    }                    
                    else { switch (apolisiDate.getYear() - hireLocalDate.getYear()){                      
                        case 0 : entitledDays = (int)Math.round((ChronoUnit.DAYS.between(hireLocalDate, apolisiDate)*20D)/
                                    (LocalDate.of(apolisiDate.getYear(), 12, 31).getDayOfYear()*1D));                                    
                                break;
                        case 1 : entitledDays = (int)Math.round((ChronoUnit.DAYS.between(LocalDate.of(apolisiDate.getYear(), 1, 1), apolisiDate)*20D)/
                                        (LocalDate.of(apolisiDate.getYear(), 12, 31).getDayOfYear()*1D));
                                break;
                        }
                    }                    
                    entitledDaysMap.put(id, entitledDays);
                
                }else if (apolisiDate.getYear() < today.getYear()){
                    entitledDays = 0;                    
                    entitledDaysMap.put(id, entitledDays);
                }
            }
      } catch (SQLException ex) {
            Logger.getLogger(ComputeEntitledDays.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
        }finally{
            try {
                rs12.close();
                statmnt.close();
            } catch (SQLException ex) {
                Logger.getLogger(CreateVacationReport.class.getName()).log(Level.SEVERE, null, ex);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
            }
        }
        return entitledDaysMap;
}
    
    public void createLastYearDayReport(int refYear, Stage stage){
       
            //Run entitled days with 31/12 , watch for fired
            
            //Use lastyeardays of the current report
            
            //Compute for every employee present in last year the consumed days
            
            //Construct a 31/12 report and save it to disk          
            
            
            computeConsumedDays(refYear);
            
            Statement statem = null;
            ResultSet resul = null;
            String tablString = "VACATION_REPORT_" +Integer.toString(refYear);           
            Map<Integer, Integer> lastYearDaysMap = new HashMap<>();
            Map<Integer, Integer> entitledDaysMap = computeEntitledDays(LocalDate.of(refYear, Month.DECEMBER, 31));
            
            String query = null;
            
        try { 
            /**
             * Check if such a table exists for last year and if it exists use it. 
             * Normally after the second year such a table should exist.
             */
            DatabaseMetaData databaseMetaData = con.getMetaData();
            resul = databaseMetaData.getTables(null, null, "VACATION_REPORT_" +Integer.toString(refYear-1) , null);
            if (resul.next()) query = "SELECT ID, REMAINING_DAYS FROM VACATION_REPORT_" +Integer.toString(refYear-1);
            else {
                if(resul != null)resul.close();
                ButtonType ok = Alerts.showDialogOK_CANCEL("Δεν υπάρχουν ασφαλή στοιχεία για υπόλοιπα αδειών έτους "+Integer.toString(refYear-1)
                        +". Να συνεχίσω?", "", "");
                if (ok == ButtonType.OK)
                        query = "SELECT ID, LASTYEAR_DAYS FROM EMPLOYEE_VACATION";
                else stage.close();
            }
            if(resul != null)resul.close();
            
            statem = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);            
            resul = statem.executeQuery(query);
            while (resul.next()) lastYearDaysMap.put(resul.getInt(1), resul.getInt(2));
            if(resul != null)resul.close();
            if (statem!= null)statem.close();
            
            DropIfExists.exec(con, "TABLE", tablString);
            
            query = "CREATE TABLE "+tablString+" ( " +
                    "ID smallint, " +
                    "firstname varchar(20), " +
                    "lastname varchar(20), " +
                    "ENTITLED_DAYS smallint DEFAULT 0, " +
                    "LASTYEAR_DAYS smallint DEFAULT 0, " +
                    "CONSUMED_DAYS smallint DEFAULT 0, " +
                    "REMAINING_DAYS smallint GENERATED ALWAYS AS (ENTITLED_DAYS + LASTYEAR_DAYS - CONSUMED_DAYS) )";            
            
            statem = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);                                          
            update = statem.executeUpdate(query);
            if (statem!= null)statem.close();            
            
            query = "INSERT INTO "+tablString+" ( ID, firstname, lastname)"
                    + "SELECT T1.ID, T1.first_name, T1.last_name FROM "                   
                    + "WORKERS AS T1 WHERE YEAR(T1.HIRE_DATE) <= "+Integer.toString(refYear)
                    +" AND (T1.apolisi = 0 OR (T1.apolisi <> 0 "
                    +" AND (YEAR(T1.diakopi) > " +Integer.toString(refYear-1)+ ")))";
                    
            statem = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);                                          
            update = statem.executeUpdate(query);
            if (statem!= null)statem.close(); 
            
            //Computations and Updates
            for(Entry<Integer, Integer> e:lastYearDaysMap.entrySet()){
                query = "UPDATE "+tablString+" SET lastyear_days =  "+Integer.toString(e.getValue())+
                        " WHERE ID = "+Integer.toString(e.getKey());                          
                statem = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                update = statem.executeUpdate(query); 
                if (statem!= null)statem.close();
            } 
            for(Entry<Integer, Integer> e:entitledDaysMap.entrySet()){
                query = "UPDATE "+tablString+" SET entitled_days =  "+Integer.toString(e.getValue())+
                        " WHERE ID = "+Integer.toString(e.getKey());                          
                statem = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                update = statem.executeUpdate(query); 
                if (statem!= null)statem.close();
            }
            for(Entry<Integer, Integer> e:consumedMap.entrySet()){
                query = "UPDATE "+tablString+" SET consumed_days =  "+Integer.toString(e.getValue())+
                        " WHERE ID = "+Integer.toString(e.getKey());                          
                statem = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
                update = statem.executeUpdate(query); 
                if (statem!= null)statem.close();
            }
         
         facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"Η δημιουργία του report αδειών 31/12/"
                 +Integer.toString(refYear)+" ολοκληρώθηκε!", "ΟΛΟΚΛΗΡΩΘΗΚΕ"));           
            
        } catch (SQLException ex) {
            Logger.getLogger(CreateVacationReport.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
        } finally{
            if (statem!= null)try {
                statem.close();
            } catch (SQLException ex) {
                Logger.getLogger(CreateVacationReport.class.getName()).log(Level.SEVERE, null, ex);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
            }
        }
        
        
    }
    
}   

