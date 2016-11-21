/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Enter_Data_vacation_Days;

import employeegui.ErrorStage;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;

/**
 * FXML Controller class
 *
 * @author Home
 */
public class HolidaysController implements Initializable {
    @FXML
    private ListView<String> HolidaysList;
    private final ObservableList<String> data = FXCollections.observableArrayList();
    @FXML
    private DatePicker holidaysDatePicker;   
    private PreparedStatement stm;
    private Statement statement;
    private ResultSet rs;
    private Date holidaysDate;
    @FXML
    private Label resultLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        holidaysDatePicker.setConverter(utilities.DateFormatterGreek.converter);
        holidaysDatePicker.setPromptText(utilities.DateFormatterGreek.promptText);        
        HolidaysList.setTooltip(new Tooltip("Χρησιμοποιείστε το πεδίο εισόδου ημερομηνίας για οποιαδήποτε αλλαγή"));
        try {            
            String query = "SELECT * FROM HOLIDAYS";
            statement = employeegui.EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            rs = statement.executeQuery(query);           
            while (rs.next()){
                data.add(rs.getString(1));
            }
            Collections.sort(data);
            HolidaysList.setItems(data);
        } catch (SQLException ex) {
            Logger.getLogger(HolidaysController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }
        finally{
            try {
                rs.close();
                statement.close();
                //con.close();
            } catch (SQLException ex) {
                Logger.getLogger(HolidaysController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }
        }
    }    

    @FXML
    private void handleSubmitBtn(ActionEvent event) throws SQLException {
        
            resultLabel.setText("");
            LocalDate holidaysLocalDate =  holidaysDatePicker.getValue();            
            holidaysDate =  holidaysLocalDate != null ? Date.valueOf(holidaysLocalDate) : null; /*Convert LocalDate to java.sql.Date*/ 
            if(holidaysDate != null){
           try {
            String query = "INSERT INTO HOLIDAYS (HOLIDAYS_COLUMN) "+
                    "VALUES ( ? )";
            stm = employeegui.EmployeeGUI.con.prepareStatement(query);            
            stm.setDate(1, holidaysDate);
                        
            Boolean bad = stm.execute();
            
            if (!bad){
                resultLabel.setText("Ο πίνακας ενημερώθηκε");
            }
            else {
                resultLabel.setText("Η καταχώριση απέτυχε, παρακαλώ προσπαθείστε ξανά");
            }
            data.add(String.valueOf(holidaysLocalDate));
            Collections.sort(data);
        } catch (SQLException ex) {
            Logger.getLogger(Enter_Data_VacationController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
            if (stm != null){stm.close();}            
        }
        }else{
          resultLabel.setText("Παρακαλώ πληκτρολογείστε ημερομηνία");      
        }      
        
    }

    @FXML
    private void handleDeleteBtn(ActionEvent event) throws SQLException {   
        
            resultLabel.setText("");
            LocalDate holidaysLocalDate =  holidaysDatePicker.getValue();
            holidaysDate =  holidaysLocalDate != null ? Date.valueOf(holidaysLocalDate) : null; /*Convert LocalDate to java.sql.Date*/ 
           if(holidaysDate != null){
            try {
            String query = "DELETE FROM HOLIDAYS WHERE HOLIDAYS_COLUMN = ?";
            stm = employeegui.EmployeeGUI.con.prepareStatement(query);
            stm.setDate(1, holidaysDate);
            Boolean bad = stm.execute();
            
            if (!bad){
                resultLabel.setText("Η καταχώριση διαγράφηκε");
            }
            else {
                resultLabel.setText("Η διαγραφή απέτυχε, παρακαλώ προσπαθείστε ξανά");
            }
            data.remove(String.valueOf(holidaysLocalDate));           
        } catch (SQLException ex) {
            Logger.getLogger(HolidaysController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
            if (stm != null){stm.close();}            
        }
     }else{
          resultLabel.setText("Παρακαλώ πληκτρολογείστε ημερομηνία");      
        } 
    }
           
    }
    
    
