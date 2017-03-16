/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import diakopiErgasiakisShesis.ApolisiController;
import diakopiErgasiakisShesis.ParaitisiController;
import employeegui.ErrorStage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;



/**
 *
 * @author evgero
 */
public class ChoiceBoxRoller {
    
    private final Map<Integer, List<String>> idMap  = new HashMap<>();
    private  ObservableList<Integer> idItems;
        
    public void fillChoiceBox(ChoiceBox idChoiceBox, TextField nameTextField, 
            TextField fatherNameTextField, TextField lastNameTextField){
    
        Statement stm1 = null;
        ResultSet rs1 = null;
       try {
            stm1 = employeegui.EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT id, first_name, last_name, father_name FROM workers";
            rs1 = stm1.executeQuery(sql);
            while (rs1.next()) {
                List <String> idList = new ArrayList<>();
                idList.add(rs1.getString("first_name"));
                idList.add(rs1.getString("father_name"));
                idList.add(rs1.getString("last_name"));
                idMap.put(rs1.getInt("id"),idList);
            }
            } catch (SQLException ex) {
         Logger.getLogger(ParaitisiController.class.getName()).log(Level.SEVERE, null, ex);  
         ErrorStage.showErrorStage(ex.getMessage());
        }finally{try {
            if (rs1 != null)rs1.close() ;
            if (stm1 != null)stm1.close();   
            //if (con != null) con.close();   
            } catch (SQLException ex) {
                Logger.getLogger(ApolisiController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }            
        }
        idItems = FXCollections.observableArrayList(idMap.keySet());
        idChoiceBox.setItems(idItems);
        idChoiceBox.setValue(idItems.get(0));
        nameTextField.setText(idMap.get((Integer)idMap.keySet().toArray()[0]).get(0));
        fatherNameTextField.setText(idMap.get((Integer)idMap.keySet().toArray()[0]).get(1));
        lastNameTextField.setText(idMap.get((Integer)idMap.keySet().toArray()[0]).get(2));
        idChoiceBox.valueProperty().addListener(new ChangeListener <Integer>(){
                
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer oldValue, Integer newValue) {
            nameTextField.setText(idMap.get(newValue).get(0));
            fatherNameTextField.setText(idMap.get(newValue).get(1));
            lastNameTextField.setText(idMap.get(newValue).get(2));
            }
        });
    }    
}
