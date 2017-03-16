/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import static employeegui.Alerts.showInformationAlert;
import historyMenu.CreateOldSalaryReportsController;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 *
 * @author sz
 */
public class ProgressInd {
    
public  ProgressBar pb;
public  ProgressIndicator pi;
public  Stage progressStage;
public static IntegerProperty threadProgress = new SimpleIntegerProperty();

public static int getThreadProgress() {
        return threadProgress.get();
}

public static void setThreadProgress(int threadPrg) {
        threadProgress.set(threadPrg);
}


public void showProgressIndicator() { 
    
        //pb = new ProgressBar(0);
        pi = new ProgressIndicator(0);  
        final HBox hb = new HBox();
        hb.setSpacing(50);
        hb.setAlignment(Pos.CENTER);       
        //hb.getChildren().addAll(pb,pi) ;      
        hb.getChildren().addAll(pi) ;      
        Scene scene = new Scene(hb, 200, 50);
        progressStage = new Stage();         
        progressStage.setScene(scene);   
        progressStage.setX(500);
        progressStage.setY(350);
        progressStage.show();
    }   
    
}
