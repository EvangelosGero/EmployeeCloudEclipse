/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package historyMenu;

import employeegui.Alerts;
import employeegui.EmployeeGUI;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author evgero
 */
public class CreateOldEAReportsController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private ChoiceBox<Integer> yearChoiceBox;
    public Stage stage;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<Integer> yearList = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 20 ; i < currentYear + 21; i++)yearList.add(i);
        ObservableList<Integer> items = FXCollections.observableArrayList(yearList);
        yearChoiceBox.setItems(items);
        yearChoiceBox.setValue(currentYear);
    }   

    @FXML
    private void handleCancelButton(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void handleCreateButton(ActionEvent event) {
        try {
            int year = yearChoiceBox.getValue();
            new SalaryReport.CreateEpidomaAdeiasReport().createDBEpidomaAdeiasReport(EmployeeGUI.con, year);
        } catch (SQLException ex) {
            Logger.getLogger(CreateOldEAReportsController.class.getName()).log(Level.SEVERE, null, ex);
            Alerts.showErrorAlert(ex.getMessage(), null, null);
        }
    }
    
}
