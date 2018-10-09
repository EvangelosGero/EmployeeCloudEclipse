/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Enter_Data_vacation_Days;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import employeegui.Alerts;
import employeegui.ErrorStage;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Home
 */
public class Enter_Data_VacationController implements Initializable {
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;    
    @FXML
    private Button saveBtn;
    //private Connection con;
    private ResultSet rs;
    private PreparedStatement stm;
    @FXML
    private Label resultLabel;
    @FXML
    private ChoiceBox<Integer> idChoiceBox;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField fatherNameTextField;  
    @FXML
    private Button SrcButton;
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        new utilities.ChoiceBoxRoller().fillChoiceBox(idChoiceBox, nameTextField, fatherNameTextField, lastNameTextField);
        fromDatePicker.setConverter(utilities.DateFormatterGreek.converter);
        toDatePicker.setConverter(utilities.DateFormatterGreek.converter);
        fromDatePicker.setPromptText(utilities.DateFormatterGreek.promptText);
        toDatePicker.setPromptText(utilities.DateFormatterGreek.promptText);
        
    }    

    @FXML
    private void handleSaveBtn(ActionEvent event) throws SQLException {
        if (fromDatePicker.getValue() != null && toDatePicker.getValue() != null){
        try {
            resultLabel.setText(null);
            LocalDate beginLocalDate =  fromDatePicker.getValue();
            LocalDate endLocalDate = toDatePicker.getValue();
            Date beginDate =  Date.valueOf(beginLocalDate); /*Convert LocalDate to java.sql.Date*/
            Date endDate = Date.valueOf(endLocalDate);
            int id = idChoiceBox.getValue();
            //con = new EstablishConnection().EstablishDBConnection();
            String query = "INSERT INTO VACATION_DAYS (ID, EXIT_DAY, BACK_DAY) "+
                    "VALUES ( ?, ?, ?)";
            stm = employeegui.EmployeeGUI.con.prepareStatement(query);
            stm.setInt(1, id);
            stm.setDate(2, beginDate);
            stm.setDate(3, endDate);
            
            int rows  = stm.executeUpdate();
            
            if (rows != 0){
                //resultLabel.setText("Database updated");
                Alerts.showInformationAlert("Η εγγραφή καταχωρίστηκε επιτυχώς !",null,null);   
            }
            else {
                resultLabel.setText("Operation was not successful");
            }            
        } catch (SQLException ex) {
            Logger.getLogger(Enter_Data_VacationController.class.getName()).log(Level.SEVERE, null, ex);
            resultLabel.setText(ex.getMessage());
            ErrorStage.showErrorStage(ex.getMessage());
        }
        finally{
            if (stm != null){stm.close();}
            //if (con != null) {con.close();}
        }
        }else{
            Alerts.showErrorAlert("Παρακαλώ συμπληρώστε σωστά τα πεδία ημερομηνίας"
                    , "ΣΦΑΛΜΑ", "Σφάλμα κενών πεδίων ημερομηνίας");
        }
    }
    
}
