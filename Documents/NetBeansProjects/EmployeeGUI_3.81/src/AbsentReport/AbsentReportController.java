/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AbsentReport;

import Enter_Data_vacation_Days.HolidaysController;
import employeegui.Alerts;
import employeegui.ErrorStage;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import utilities.ProgressInd;

/**
 * FXML Controller class
 *
 * @author Home
 */
public class AbsentReportController implements Initializable {
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private TableView<ObservableList> absentTable;
    @FXML
    private TableColumn<ObservableList, String> idColumn;
    @FXML
    private TableColumn<ObservableList, String> firstNameColumn;
    @FXML
    private TableColumn<ObservableList, String> lastNameColumn;
    @FXML
    private TableColumn<ObservableList, String> dateColumn;
    @FXML
    private TableColumn<ObservableList, String> reasonColumn;
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private PreparedStatement prStm;
    private Statement workersStm ;
    private ResultSet workersRs, rs; 
    LocalDate startDate;
    LocalDate endDate;
    LocalDate hireDate, apolisiDate;
    String justify = "ΑΔΙΚΑΙΟΛΟΓΗΤΗ";
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { 
        fromDatePicker.setConverter(utilities.DateFormatterGreek.converter);
        toDatePicker.setConverter(utilities.DateFormatterGreek.converter);
        fromDatePicker.setPromptText(utilities.DateFormatterGreek.promptText);        
        fromDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
          if (data!=null)data.clear();  
        });
        toDatePicker.setPromptText(utilities.DateFormatterGreek.promptText);        
        toDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
          if (data!=null)data.clear(); 
        });
        
    }    

    @FXML
    private void handleShowCard(ActionEvent event) throws SQLException {
        startDate = fromDatePicker.getValue()!=null ? fromDatePicker.getValue() : null;
        endDate = toDatePicker.getValue() != null ? toDatePicker.getValue() : null;
        
        
//******************** start of task
        Task task = new Task<Void>() {
        @Override 
            public Void call() throws SQLException {               
                updateProgress(0, 100);
                utilities.ProgressInd.threadProgress.addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> ov,
                Number old_val, Number new_val) {                             
                              
                 updateProgress(new_val.intValue(), 100);                                           
                }
                });
                    
        utilities.ProgressInd.setThreadProgress(2);

        if(startDate != null && endDate != null){
        try{            
            if (data!=null)data.clear();          
            //con = new EstablishConnection().EstablishDBConnection();
            String query = "SELECT workers.id, workers.first_name, workers.last_name, workers.hire_date, "
                    + "workers.apolisi, workers.diakopi "
                    + "FROM workers ";               
            workersStm = employeegui.EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            workersRs = workersStm.executeQuery(query);   
            
            int cnt=0;
            workersRs.beforeFirst();
            while (workersRs.next()){ 
             cnt++;   
            }                       
            
            double i=0;
            workersRs.beforeFirst();
            while (workersRs.next()){            
                i=i+1;
                utilities.ProgressInd.setThreadProgress((int)Math.floor((i/cnt)*100));
                                
                hireDate = workersRs.getDate(4).toLocalDate();                
                LocalDate counterDate = startDate;
                if (workersRs.getInt(5) != 0)apolisiDate = workersRs.getDate(6).toLocalDate();
                        else apolisiDate = LocalDate.now().plusDays(1);   
                while (((counterDate.isBefore(endDate)) || (counterDate.isEqual(endDate)))
                        &&(counterDate.isBefore(apolisiDate)) || (counterDate.isEqual(apolisiDate))){
                    if ((counterDate.getDayOfWeek().getValue()!= 6) &&
                     (counterDate.getDayOfWeek().getValue()!= 7) && (counterDate.isAfter(hireDate))){ 
                        if(absent(counterDate, workersRs.getInt(1))){                            
              
                        ObservableList<String> row = FXCollections.observableArrayList();                
                        row.add(Integer.toString(workersRs.getInt(1)));
                        row.add(workersRs.getString(2));
                        row.add(workersRs.getString(3));
                        row.add(counterDate.toString());
                        row.add(justify);
            
                        data.add(row);
                     } 
                    }
                counterDate = counterDate.plusDays(1);                    
                }                               
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(HolidaysController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }
        finally{            
                if (workersRs != null){workersRs.close();}
                if (workersStm != null){workersStm.close();}
                //if (con != null){con.close();}
        }
        idColumn.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(0).toString());
        });
        firstNameColumn.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(1).toString());
        });
        lastNameColumn.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(2).toString());
        });
        dateColumn.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(3).toString());
        });
        reasonColumn.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(4).toString());
        });                            
        absentTable.setItems(data);
        }
        /*else{
            Alerts.showErrorAlert("Παρακαλώ συμπληρώστε σωστά τα πεδία ημερομηνίας"
                    , "ΣΦΑΛΜΑ", "Σφάλμα κενών πεδίων ημερομηνίας");
        }*/
        
        utilities.ProgressInd.setThreadProgress(100);
        try{
             Thread.sleep(400);   
           } catch (Exception ex) {
        };
    
        return null;  
            }
        };    
        ProgressInd progressInd = new utilities.ProgressInd();
        progressInd.showProgressIndicator();
        progressInd.pi.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {        
                progressInd.progressStage.close();                
//                Alerts.showInformationAlert("Η διαδικασία τελείωσε επιτυχώς !",null,null);   
   
            }
        });
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            // We will have to catch here the events of the Exceptions of the thread
            @Override
            public void handle(WorkerStateEvent t) {        
                progressInd.progressStage.close();
                String err = task.exceptionProperty().toString();
                Alerts.showErrorAlert(err, null, null);
                Alerts.showInformationAlert("Συνέβησαν προβλήματα ! Η διαδικασία θα διακοπεί !",null,null);   
   
            }
        });
        
        new Thread(task).start();              
    }
        
    
    private boolean absent (LocalDate date, int id) throws SQLException{
        boolean holiday = false, vacation = false, sickLess3 = false, sickMore3 = false;
        boolean here = false;
        justify = "ΑΔΙΚΑΙΟΛΟΓΗΤΗ";
       String qry = "SELECT workers.id FROM workers join timer on timer.code = workers.code "              
               + "WHERE ((workers.id = ?) AND (DATE(timer.starttime) = ?))";
       /*String qry = "SELECT workers.id FROM workers, timer "              
               + "WHERE ((workers.id = ?) AND(timer.code = workers.code) AND "
               + "(DATE(timer.starttime) = ?)) ";       */
       
        try {
          prStm = employeegui.EmployeeGUI.con.prepareStatement(qry);
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
                prStm = employeegui.EmployeeGUI.con.prepareStatement(qry);                
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
                prStm = employeegui.EmployeeGUI.con.prepareStatement(qry); 
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
            if (vacation){justify = "ΑΔΕΙΑ";} else {
                qry = "SELECT id FROM sick_days_less_than_three "
                    + "WHERE (id  = ?) AND (? BETWEEN exit_day AND back_day)";
            try {
                prStm = employeegui.EmployeeGUI.con.prepareStatement(qry); 
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
            if (sickLess3){justify = "ΑΣΘΕΝΕΙΑ<3";} else {
                qry = "SELECT id FROM sick_days_more_than_three "
                    + "WHERE (id  = ?) AND (? BETWEEN exit_day AND back_day)";
            try {
                prStm = employeegui.EmployeeGUI.con.prepareStatement(qry); 
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
            if (sickMore3){justify = "ΑΣΘΕΝΕΙΑ>3";}
            }  
            }
            
            }
        }
        
      return  !here;  
    }
}
