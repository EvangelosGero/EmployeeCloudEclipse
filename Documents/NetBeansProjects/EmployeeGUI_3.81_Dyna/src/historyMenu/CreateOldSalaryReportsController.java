/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package historyMenu;

import employeegui.Alerts;
import static employeegui.Alerts.showDialogYES_NO;
import employeegui.EmployeeGUI;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utilities.ProgressInd;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * FXML Controller class
 *
 * @author evgero
 */
public class CreateOldSalaryReportsController implements Initializable {
    @FXML
    private AnchorPane root;
    @FXML
    private DatePicker monthYearDatePicker;
    public Stage stage;
    //public static IntegerProperty threadProgress = new SimpleIntegerProperty();
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monthYearDatePicker.setConverter(utilities.DateFormatterGreek.converter);
        monthYearDatePicker.setPromptText(utilities.DateFormatterGreek.promptText);
        
    }  

/*    public static int getThreadProgress() {
        return threadProgress.get();
    }

    public static void setThreadProgress(int threadProgress) {
        CreateOldSalaryReportsController.threadProgress.set(threadProgress);
    }*/
    
    

    @FXML
    private void handleCancelButton(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void handleCreateButton(ActionEvent event) {
        LocalDate date = monthYearDatePicker.getValue() != null ? monthYearDatePicker.getValue() : null;
        if(date !=null){
            if (showDialogYES_NO("Έχετε επιλέξει τον μήνα "
                +Integer.toString(monthYearDatePicker.getValue().getMonthValue())
                    +"/"
                +Integer.toString(monthYearDatePicker.getValue().getYear())
                +". Θέλετε να συνεχίσετε?",null,null)!= ButtonType.YES) return;            

        Task task = new Task<Void>() {
            @Override 
                public Void call() throws SQLException, InterruptedException, ExecutionException {               
                    updateProgress(0, 100);
                    utilities.ProgressInd.threadProgress.addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> ov,
                        Number old_val, Number new_val) {                             
                                
                                updateProgress(new_val.intValue(), 100);                                           
                    }
                });
                       
               
                new SalaryReport.CreateSalaryReport().CreateDBSalaryReport(EmployeeGUI.con, date);
                
                
                return null;    
            }
      //       @Override 
       //      protected void succeeded() {
       //         super.succeeded();
       //         updateMessage("Done!");
       //         }
        };    
        ProgressInd progressInd = new utilities.ProgressInd();
        progressInd.showProgressIndicator();
        progressInd.pi.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {        
                progressInd.progressStage.close();                
                Alerts.showInformationAlert("Η διαδικασία τελείωσε επιτυχώς !",null,null);   
   
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
        
        }else{
            Alerts.showErrorAlert("Παρακαλώ συμπληρώστε σωστά τα πεδία ημερομηνίας"
                    , "ΣΦΑΛΜΑ", "Σφάλμα κενών πεδίων ημερομηνίας");
        }
    }
    
}
