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
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author evgero
 */
public class CreateOldReportsController implements Initializable {
    @FXML
    private DatePicker monthYearDatePicker;
    @FXML
    private AnchorPane root;
    public Stage stage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        monthYearDatePicker.setConverter(utilities.DateFormatterGreek.converter);
        monthYearDatePicker.setPromptText(utilities.DateFormatterGreek.promptText);       
    }    

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
        new employeegui.CreateReport().CreateMonthlyDBTable(EmployeeGUI.con, date);
    }
    else{
            Alerts.showErrorAlert("Παρακαλώ συμπληρώστε σωστά τα πεδία ημερομηνίας"
                    , "ΣΦΑΛΜΑ", "Σφάλμα κενών πεδίων ημερομηνίας");
        }
    }
}
