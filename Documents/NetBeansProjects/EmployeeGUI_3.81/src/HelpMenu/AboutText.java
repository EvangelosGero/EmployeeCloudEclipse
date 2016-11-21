/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelpMenu;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author egdyn_000
 */
public class AboutText {
    public static void About (){
        VBox vBox = new VBox();
        vBox.setPrefWidth(300);
        vBox.setPadding(new Insets(15, 15, 15, 15));
        Text text = new Text();
        text.setLineSpacing(2);
        text.setText("Dyna.HR \n."+
                "Version Number : 3.79 \n"+ 
                "Το πρόγραμμα Dyna.HR δημιουργήθηκε το 2015-2016. \n"+
                "Author : Ευάγγελος Γερογιάννης. \n"+
                "Εταιρία Dynamotors Software. \n"+
                "Ελληνικό Software.\n"+
                "Protected under Copyright Law");
        vBox.getChildren().add(text);
        Scene root = new Scene(vBox);
        Stage stage = new Stage (StageStyle.DECORATED);
        stage.setScene(root);
        stage.show();
        
    }
    
}
