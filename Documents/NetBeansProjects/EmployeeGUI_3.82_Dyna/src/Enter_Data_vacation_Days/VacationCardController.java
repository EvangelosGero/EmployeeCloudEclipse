/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Enter_Data_vacation_Days;

import employeegui.Alerts;
import employeegui.ErrorStage;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


/**
 * FXML Controller class
 *
 * @author Home
 */
public class VacationCardController implements Initializable {
    @FXML
    private TableView<ObservableList> vacationCardTable;
    @FXML
    private TableColumn<ObservableList, String> idColumn;
    @FXML
    private TableColumn<ObservableList, String> firstNameColumn;
    @FXML
    private TableColumn<ObservableList, String> lastNameColumn;
    @FXML
    private TableColumn<ObservableList, String> fromColumn;
    @FXML
    private TableColumn<ObservableList, String> toColumn;
    ObservableList<ObservableList> data = FXCollections.observableArrayList();
    
    ObservableList<String> rowClicked = FXCollections.observableArrayList();;
    //private Connection con;
    private PreparedStatement stm;
    private Statement statement;
    private ResultSet rs;
    private Date holidaysDate;
    int rowNumber;    
    @FXML
    private DatePicker fromDatePicker;
    @FXML
    private DatePicker toDatePicker;
    @FXML
    private ChoiceBox<Integer> idChoiceBox;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField fatherNameTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

       new utilities.ChoiceBoxRoller().fillChoiceBox(idChoiceBox, nameTextField, fatherNameTextField, lastNameTextField);
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
        
        if (fromDatePicker.getValue() == null || toDatePicker.getValue() == null)       
        {
         Alerts.showInformationAlert("Παρακαλώ επιλέξτε Απο - Εως ημερομηνία ή Μήνα!", null, "ΕΙΔΟΠΟΙΗΣΗ");
         return;                
        }         
        if (fromDatePicker.getValue() != null && toDatePicker.getValue() != null){ 
        try{
            if (data!=null){data.clear();}            
            //con = new EstablishConnection().EstablishDBConnection();
            /*String query = "SELECT vacation_days.id, workers.first_name, workers.last_name, "
                    + "vacation_days.exit_day, vacation_days.back_day  FROM vacation_days, "
                    + "workers WHERE (vacation_days.id = workers.id) AND (vacation_days.id = ? )"
                    + "AND (vacation_days.exit_day BETWEEN ? AND ?)";*/
            
            String query = "SELECT vacation_days.id, workers.first_name, workers.last_name, "
                    + "vacation_days.exit_day, vacation_days.back_day "
                    + "FROM vacation_days join workers on vacation_days.id = workers.id "
                    + "WHERE vacation_days.id = ? "
                    + "AND ((vacation_days.exit_day BETWEEN ? AND ?) OR (vacation_days.back_day BETWEEN ? AND ? ))";            
            stm = employeegui.EmployeeGUI.con.prepareStatement(query);
            stm.setInt(1, idChoiceBox.getValue());
            stm.setDate(2, Date.valueOf(fromDatePicker.getValue()));
            stm.setDate(3, Date.valueOf(toDatePicker.getValue()));
            stm.setDate(4, Date.valueOf(fromDatePicker.getValue()));
            stm.setDate(5, Date.valueOf(toDatePicker.getValue()));            
            rs = stm.executeQuery();           
            while (rs.next()){
                ObservableList<String> row = FXCollections.observableArrayList();                
                row.add(Integer.toString(rs.getInt(1)));
                row.add(rs.getString(2));
                row.add(rs.getString(3));
                row.add(rs.getDate(4).toString());
                row.add(rs.getDate(5).toString());
            
                data.add(row);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(HolidaysController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }
        finally{            
                if (rs != null){rs.close();}
                if (stm != null){stm.close();}
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
        fromColumn.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(3).toString());
        });
        toColumn.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(4).toString());
        });
        
        vacationCardTable.setItems(data);        
        vacationCardTable.setOnMousePressed(e->{
            rowClicked = vacationCardTable.getSelectionModel().getSelectedItem();
            rowNumber = vacationCardTable.getSelectionModel().getSelectedIndex();
            showPopUp();
        });
        }/*else{
            Alerts.showErrorAlert("Παρακαλώ συμπληρώστε σωστά τα πεδία ημερομηνίας"
                    , "ΣΦΑΛΜΑ", "Σφάλμα κενών πεδίων ημερομηνίας");
        }*/
    }
    private void showPopUp(){
        Stage popStage = new Stage(StageStyle.DECORATED);
        popStage.centerOnScreen();
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(10, 10, 10, 10));
        vBox.setSpacing(50);
        Label question = new Label("Θέλετε να διαγράψετε αυτή την εγγραφή ?");
        vBox.setAlignment(Pos.CENTER);
        HBox btnBox = new HBox();
        btnBox.setPadding(new Insets(10, 10, 10, 10));
        Button btn1 = new Button("ΝΑΙ");
        Button btn2 = new Button("ΟΧΙ");
        btnBox.setSpacing(80);
        btnBox.setAlignment(Pos.CENTER);
        btnBox.getChildren().addAll(btn1, btn2);
        vBox.getChildren().addAll(question, btnBox);
        btn1.setOnAction(ev ->{
            try {
            //con = new EstablishConnection().EstablishDBConnection();
            String query = "DELETE FROM vacation_days WHERE id = ? AND "
                    + "exit_day = ? AND back_day = ?";            
            stm = employeegui.EmployeeGUI.con.prepareStatement(query);            
            stm.setInt(1,Integer.parseInt(rowClicked.get(0)));
            stm.setDate(2, Date.valueOf(rowClicked.get(3)));
            stm.setDate(3, Date.valueOf(rowClicked.get(4)));
            int updatedRows = stm.executeUpdate();
            data.remove(rowNumber);
            } catch (SQLException ex) {
                Logger.getLogger(VacationCardController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }finally{
                try {
                if (rs != null){rs.close();}
                if (stm != null){stm.close();}
                //if (con != null){con.close();}   
                } catch (SQLException ex) {
                    Logger.getLogger(VacationCardController.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorStage.showErrorStage(ex.getMessage());
                }                                    
            }
            popStage.close();
        });
        btn2.setOnAction(ev ->{
          popStage.close();
        });
        Scene pop = new Scene(vBox, 280, 150);
        popStage.setScene(pop);
        popStage.show();
    }

    @FXML
    private void handleSrcButton(ActionEvent event) {
    }
}       
