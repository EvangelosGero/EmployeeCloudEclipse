/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

/**
 *
 * @author sz
 */
public class Alerts {
      
public static void showInformationAlert(String M, String H, String T)
 {
 Alert alert = new Alert(Alert.AlertType.INFORMATION);
 alert.setTitle(T);
 alert.setHeaderText(H);
 alert.setContentText(M);
 alert.showAndWait();    
 }    

public static void showErrorAlert(String M, String H, String T)
 {
  Alert alert = new Alert(Alert.AlertType.ERROR);
  alert.setTitle(T);
  alert.setHeaderText(H);
  alert.setContentText(M);
  alert.showAndWait();
 }    

public static ButtonType showDialogOK_CANCEL(String M,String Q,String T)
{        
 Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
 alert.setTitle(T);
 alert.setHeaderText(M);
 alert.setContentText(Q);

 Optional<ButtonType> result = alert.showAndWait();
 
 return result.get();
}

public static ButtonType showDialogYES_NO(String M,String Q,String T)
{           
 Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
 alert.setTitle(T);
 alert.setHeaderText(M);
 alert.setContentText(Q);

 ButtonType buttonTypeYes = new ButtonType("Yes", ButtonData.YES);
 ButtonType buttonTypeNo  = new ButtonType("No", ButtonData.NO);

 alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
    
 Optional<ButtonType> result = alert.showAndWait();
 
 if (result.get() == buttonTypeYes)
   return ButtonType.YES;
 else
   return ButtonType.NO;  
 
}

}

