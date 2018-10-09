/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AbsentReport;

import static employeegui.Alerts.showInformationAlert;
import employeegui.EmployeeGUI;
import employeegui.ErrorStage;
import employeegui.TimeIntervalController;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author evgero
 */
public class MazikiDiorthosiController implements Initializable {
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;
    @FXML
    private TableView<ObservableList> sourceTable;
    @FXML
    private TableColumn<ObservableList, String> idCol;
    @FXML
    private TableColumn<ObservableList, String> firstNameCol;
    @FXML
    private TableColumn<ObservableList, String> lastNameCol;
    @FXML
    private TableColumn<ObservableList, String> fatherNameCol;
    @FXML
    private TableColumn<ObservableList, String> codeCol;
    @FXML
    private TableView<ObservableList> targetTable;
    @FXML
    private TableColumn<ObservableList, String> idTargetCol;
    @FXML
    private TableColumn<ObservableList, String> firstNameTargetCol;
    @FXML
    private TableColumn<ObservableList, String> lastNameTargetCol;
    @FXML
    private TableColumn<ObservableList, String> fatherNameTargetCol;
    @FXML
    private Button diorthoseBtn;
    @FXML
    private Button cancelBtn;
    
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private ObservableList<ObservableList> selectedData = FXCollections.observableArrayList();
    private Connection con = employeegui.EmployeeGUI.con;
    private PreparedStatement prStm ;
    private Statement workersStm ;
    private ResultSet workersRs, rs;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate hireDate, apolisiDate;
    private List<LocalDate> holidaysList ;
    private Map<Integer, List<List<LocalDate>>> vacationDaysMap = new HashMap<>();
    private Map<Integer, List<List<LocalDate>>> sickLess3DaysMap = new HashMap<>();
    private Map<Integer, List<List<LocalDate>>> sickMore3DaysMap = new HashMap<>();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fromDate.setConverter(utilities.DateFormatterGreek.converter);
        toDate.setConverter(utilities.DateFormatterGreek.converter);
        fromDate.setPromptText(utilities.DateFormatterGreek.promptText);
        toDate.setPromptText(utilities.DateFormatterGreek.promptText);
        produceHolidayList();
        vacationDaysMap.clear();
        vacationDaysMap.putAll( produceDaysMap("VACATION_DAYS")); 
        sickLess3DaysMap.clear();
        sickLess3DaysMap.putAll(produceDaysMap("SICK_DAYS_LESS_THAN_THREE"));       
        sickMore3DaysMap.clear();
        sickMore3DaysMap.putAll(produceDaysMap("SICK_DAYS_MORE_THAN_THREE"));
        fromDate.setValue(null);
        toDate.setValue(null);
        selectedData.clear();
        populateSelectionTable();      
        toDate.setOnAction(e->{showTable();});          
        fromDate.setOnAction(k->{showTable();});
    }  
    
        public void showTable(){
        try {
          startDate = fromDate.getValue();
          endDate = toDate.getValue();
          if (data!=null)data.clear();
          if(!(startDate == null || startDate.equals("")||endDate == null || endDate.equals(""))){ 
            toDate.setDisable(true);
            fromDate.setDisable(true);
            String query = "SELECT workers.id, workers.first_name, workers.last_name, workers.father_name, "
                    + "workers.code, workers.hire_date,workers.apolisi, workers.diakopi "
                    + "FROM workers ";               
            workersStm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            workersRs = workersStm.executeQuery(query);
            while (workersRs.next()){ 
                
                hireDate = workersRs.getDate(6).toLocalDate();               
                if (workersRs.getInt(7) != 0)apolisiDate = workersRs.getDate(8).toLocalDate();
                        else apolisiDate = LocalDate.now().plusDays(1);   
                if (((hireDate.isBefore(endDate)) || (hireDate.isEqual(endDate)))
                        &&(hireDate.isBefore(apolisiDate)) || (hireDate.isEqual(apolisiDate))){                                                  
              
                        ObservableList<String> row = FXCollections.observableArrayList();                
                        row.add(Integer.toString(workersRs.getInt(1)));
                        row.add(workersRs.getString(2));
                        row.add(workersRs.getString(3));
                        row.add(workersRs.getString(4));
                        row.add(workersRs.getString(5));
                        row.add(workersRs.getDate(6).toString());
                        row.add(Integer.toString(workersRs.getInt(7)));
                        row.add(workersRs.getInt(7) != 0 ? workersRs.getDate(8).toString() : "");                        
            
                        data.add(row);              
                                                  
                }                               
            }
        idCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(0).toString());
        });
        firstNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(1).toString());
        });
        lastNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(2).toString());
        });
        fatherNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(3).toString());
        });
        codeCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(4).toString());
        });                                    
        sourceTable.setItems(data);
            }
        sourceTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        MenuItem cmItem1 = new MenuItem("Πρόσθεσε την επιλογή στον πίνακα δίπλα");
        MenuItem cmItem2 = new MenuItem("Πρόσθεσε όλα τα ονόματα στον πίνακα δίπλα");
        cmItem1.setOnAction(l->{
            selectedData.addAll(sourceTable.getSelectionModel().getSelectedItems());
            data.removeAll(selectedData);
        });
        cmItem2.setOnAction(t->{
            selectedData.addAll(data); 
            data.removeAll(selectedData);
        });
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(cmItem1, cmItem2);
        sourceTable.setContextMenu(menu);
        
        } catch (SQLException ex) {
            Logger.getLogger(MazikiDiorthosiController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
       }
        finally{
            try {
                if(workersRs !=null)workersRs.close();
                if(workersStm != null)workersStm.close();
            } catch (SQLException ex) {
                Logger.getLogger(MazikiDiorthosiController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }
        }
      }  
    
    private void populateSelectionTable(){
        
        idTargetCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(0).toString());
        });
        firstNameTargetCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(1).toString());
        });
        lastNameTargetCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(2).toString());
        });
        fatherNameTargetCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(3).toString());
        });
                                            
        targetTable.setItems(selectedData);
        targetTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        MenuItem cmItem3 = new MenuItem("Αφαίρεσε την επιλογή από τον πίνακα");
        MenuItem cmItem4 = new MenuItem("Αφαίρεσε όλα τα ονόματα από τον πίνακα");
        cmItem3.setOnAction(j->{
            data.addAll(targetTable.getSelectionModel().getSelectedItems());
            selectedData.removeAll(targetTable.getSelectionModel().getSelectedItems());
        });
        cmItem4.setOnAction(o->{
            data.addAll(selectedData); 
            selectedData.clear();
        });
        ContextMenu menu = new ContextMenu();
        menu.getItems().addAll(cmItem3, cmItem4);
        targetTable.setContextMenu(menu);
        
        
    }
    
    @FXML
    private void handleDiorthoseBtn(ActionEvent event) {
        for(ObservableList<String> worker : selectedData){
            int id = Integer.parseInt(worker.get(0));            
            LocalDate counterDate = startDate;
            hireDate = Date.valueOf(worker.get(5)).toLocalDate();            
            if (Integer.parseInt(worker.get(6)) != 0)apolisiDate = Date.valueOf(worker.get(7)).toLocalDate();
                        else apolisiDate = LocalDate.now().plusDays(1);
            while (((counterDate.isBefore(endDate)) || (counterDate.isEqual(endDate)))
                        &&(counterDate.isBefore(apolisiDate)) || (counterDate.isEqual(apolisiDate))){
                    if ((counterDate.getDayOfWeek().getValue()!= 6) &&
                     (counterDate.getDayOfWeek().getValue()!= 7) && (counterDate.isAfter(hireDate))
                            && !holidaysList.contains(counterDate)                           
                            && (vacationDaysMap.get(id) != null ?  allowedDays(id, vacationDaysMap, counterDate) : true)
                            && (sickLess3DaysMap.get(id) != null ?  allowedDays(id, sickLess3DaysMap, counterDate) : true)
                            && (sickMore3DaysMap.get(id) != null ?  allowedDays(id, sickMore3DaysMap, counterDate) : true)
                       ){
                         insertOrUpdate(id, counterDate);
                    }
            counterDate = counterDate.plusDays(1); 
            }
        }        
       showInformationAlert("Η μαζική διόρθωση ολοκληρώθηκε!",null,null);    
        
    }

    @FXML
    private void handleCancelBtn(ActionEvent event) {
        data.clear();
        toDate.setDisable(false);
        fromDate.setDisable(false);
        toDate.setValue(null);
        fromDate.setValue(null);
    }
    
    public void insertOrUpdate(int id, LocalDate date){
        PreparedStatement stm = null, stm1 = null ;
        ResultSet rs5 = null;
        LocalTime morning = LocalTime.of(8, 0);
        LocalTime evening = LocalTime.of(16, 0);
        LocalDateTime morningStart = LocalDateTime.of(date, morning);
        LocalDateTime eveningEnd = LocalDateTime.of(date, evening);
        String code;
        try{
            //first find the code for that id            
            String str = "SELECT DISTINCT (timer.code)" +
                        " FROM timer, workers WHERE timer.code = workers.code AND" +
                        " workers.id = ?";
            stm = EmployeeGUI.con.prepareStatement(str);
            stm.setInt(1, id);
            rs5 = stm.executeQuery();
            rs5.next();
            code = rs5.getString("code");
            if (rs5 != null)rs5.close(); 
            if (stm != null)stm.close();           
          
           /* str = "MERGE INTO timer t "   DOES NOT WORK !!
                + "USING workers w "
                + "ON t.code = ? AND DATE(t.starttime) = ?"
                + "WHEN MATCHED  " +
                " THEN UPDATE SET starttime = ?, endtime = ?, interval_time = 28800000" +
                " WHEN NOT MATCHED " +
                " THEN INSERT (code, starttime, endtime, interval_time, "
                            + "pc_name_in, pc_ip_in, pc_name_out, pc_ip_out) "
                            + "VALUES (?," +
                        " ?, ?, 28800000, null, null, null, null)"; */
            
            str = "SELECT * FROM timer where code = ? and DATE(starttime) = ?";
            stm = EmployeeGUI.con.prepareStatement(str);
            stm.setString(1, code);                                    
            stm.setDate(2, Date.valueOf(date));
            rs5 = stm.executeQuery();
            
            // There might be 0 or more records for given date, so first delete them all
            while (rs5.next()){
                str = "DELETE FROM timer where id = ?";
                stm1 = EmployeeGUI.con.prepareStatement(str);
                stm1.setInt(1, rs5.getInt("ID"));
                stm1.executeUpdate();
            }
            if (rs5 != null)rs5.close(); 
            if (stm != null)stm.close();
            if (stm1 != null)stm1.close();
            
            str="INSERT INTO TIMER (code, starttime, endtime, interval_time)"
                    + "VALUES( ?, ?, ?, 28800000) ";
            stm = EmployeeGUI.con.prepareStatement(str);
            stm.setString(1, code);
            stm.setTimestamp(2, Timestamp.valueOf(morningStart));                            ;
            stm.setTimestamp(3, Timestamp.valueOf(eveningEnd));                 
            int ok2 = stm.executeUpdate();
            
        }catch (SQLException ex) {
            Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
                   
        }finally{
            try {
                if (rs5 != null)rs5.close(); 
                if (stm != null)stm.close();
                if (stm1 != null)stm1.close();
            } catch (SQLException ex) {
                Logger.getLogger(MazikiDiorthosiController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }
        }        
    }
    
    public void produceHolidayList(){
        Statement stm =null;
        ResultSet rs1 =null;
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
            Logger.getLogger(MazikiDiorthosiController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally {
            try {
                if (stm != null)stm.close();
                if (rs1 != null )rs1.close();
            } catch (SQLException ex) {
                Logger.getLogger(MazikiDiorthosiController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }
        }
    }
    
    public Map<Integer, List<List<LocalDate>>> produceDaysMap(String table){
        int id;
        Statement stm =null;
        ResultSet rs1 =null;  
        Map<Integer, List<List<LocalDate>>> map = new HashMap<>();
        map.clear();
        try{
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT * FROM "+table;                                      
            rs1 = stm.executeQuery(sql);
            while(rs1.next()){
                id = rs1.getInt("ID");
                List<LocalDate> intervalList = new ArrayList<>();
                intervalList.add(rs1.getDate("EXIT_DAY").toLocalDate());
                intervalList.add(rs1.getDate("BACK_DAY").toLocalDate());
                if (map.keySet().contains(id)){
                    map.get(id).add(intervalList);
                }else{
                    List<List<LocalDate>> intervalsList = new ArrayList<>();
                    intervalsList.add(intervalList);
                    map.put(id, intervalsList);
                }
            }            
            }catch (SQLException ex) {
            Logger.getLogger(MazikiDiorthosiController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally {
            try {
                if (stm != null)stm.close();
                if (rs1 != null )rs1.close();
            } catch (SQLException ex) {
                Logger.getLogger(MazikiDiorthosiController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }
        }       
        return map;
    }
    
    public boolean allowedDays(int id, Map<Integer, List<List<LocalDate>>> map, LocalDate counterDate){
        
        boolean allowed = true;
        for(List<LocalDate> intervalList : map.get(id)){
            if(!(counterDate.isBefore(intervalList.get(0))
                ||counterDate.isAfter(intervalList.get(1))))
                     allowed = false; 
         }       
        return allowed;
    }
}
