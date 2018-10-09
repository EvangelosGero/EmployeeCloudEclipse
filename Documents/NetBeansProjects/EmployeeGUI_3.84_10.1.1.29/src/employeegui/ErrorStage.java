/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import javafx.scene.control.Alert;

/**
 *
 * @author e
 */
public class ErrorStage {
    
    public static void showErrorStage(String message) {        
        
  Alert alert = new Alert(Alert.AlertType.ERROR);
  alert.setTitle("ΣΦΑΛΜΑ");
  alert.setHeaderText("");
  alert.setContentText(message);
  alert.showAndWait();
 }
    
}
