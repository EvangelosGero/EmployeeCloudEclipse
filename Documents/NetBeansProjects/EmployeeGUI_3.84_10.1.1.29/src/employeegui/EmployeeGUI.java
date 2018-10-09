/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import static employeegui.Alerts.showErrorAlert;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 *
 * @author Home
 */
public class EmployeeGUI extends Application {
    
    public static Connection con;
    public static Stage selectIdStage; 
        
    @Override
    public void start(Stage stage) throws Exception {
        
        setUserAgentStylesheet(STYLESHEET_MODENA);       
        Handler handler = new FileHandler("ApplicationLog.log");
        Logger.getLogger("").addHandler(handler);        
        stage.getIcons().add(new Image("file:resources/images/employeeList.png"));
        GridPane gp = getPassword(stage);
        Scene sc = new Scene(gp, 350, 375);
        sc.getStylesheets().add(EmployeeGUI.class.getResource("empl.css").toExternalForm());
        stage.setScene(sc);
        stage.show();  
          
            }               
        
          
    
    public GridPane getPassword(Stage stage) throws SQLException{
        
            //Connection con = (new EstablishConnection()).EstablishDBConnection();
            Statement stm = null;
            ResultSet rs = null;             
            
            final Label message = new Label("");
            GridPane gp = new GridPane();
            gp.setAlignment(Pos.TOP_CENTER);
            gp.setPadding(new Insets(25,25,25,25));
            gp.setHgap(10);
            gp.setVgap(10);
            Text sceneTitle = new Text("EmployeeGUI");
            sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            gp.add(sceneTitle, 0, 0, 2, 1);
            Label usernmLabel = new Label("Username");
            gp.add(usernmLabel, 0, 1);
            TextField usernmTextField = new TextField();
            usernmTextField.setText("Administrator");
            gp.add(usernmTextField, 1, 1);
            Label passwLabel = new Label("Password");
            gp.add(passwLabel, 0, 2);
            PasswordField passwordField = new PasswordField();
            passwordField.setPromptText("Your Password");
            gp.add(passwordField, 1, 2);
            Label chgPasw = new Label("Change Password");
            chgPasw.setUnderline(true);
            chgPasw.setTooltip(new Tooltip("Κάνε click για αλλαγή password"));
            gp.add(chgPasw, 1, 20);
            gp.add(message, 0, 21, 2, 1);
            gp.getStyleClass().add("loginGrid");
            
            chgPasw.setOnMouseClicked((MouseEvent e)->{                
                Stage stage1 = new Stage(StageStyle.DECORATED);
                stage1.getIcons().add(new Image("file:resources/images/employeeList.png"));
                stage1.setTitle("ΑΛΛΑΓΗ PASSWORD");
                VBox vBox = new VBox(200);
                vBox.getStyleClass().add("pane");
                vBox.setSpacing(30);
                vBox.setAlignment(Pos.CENTER);
                vBox.setPadding(new Insets(30, 30, 30, 30));
                HBox hBox1 = new HBox();
                hBox1.setSpacing(20);
                hBox1.setAlignment(Pos.BASELINE_LEFT);
                Label label1 = new Label("Πληκτρολογείστε το Username");
                TextField textField1 = new TextField(usernmTextField.getText());
                hBox1.getChildren().addAll(label1, textField1);
                HBox hBox2 = new HBox();
                hBox2.setSpacing(22);
                hBox2.setAlignment(Pos.BASELINE_LEFT);
                Label label2 = new Label("Πληκτρολογείστε το Password");
                PasswordField pass1 = new PasswordField();
                hBox2.getChildren().addAll(label2, pass1);
                HBox hBox3 = new HBox();
                hBox3.setSpacing(45);
                hBox3.setAlignment(Pos.BASELINE_LEFT);
                Label label3 = new Label("Επαναλάβετε το Password");
                PasswordField pass2 = new PasswordField();
                hBox3.getChildren().addAll(label3, pass2);
                vBox.getChildren().addAll(hBox1, hBox2, hBox3);               
                Scene scene =  new Scene(vBox, 500, 250);
                scene.getStylesheets().add(EmployeeGUI.class.getResource("empl.css").toExternalForm());
                stage1.setScene(scene);
                stage1.show();
            });
            
            passwordField.setOnAction(new EventHandler<ActionEvent>(){
                @Override public void handle(ActionEvent e){                    
                /*Boolean flag = false;                
                
                
                //final String qry = "SELECT * FROM EMPL_ADMINS";                
                    try {                        
                        Statement stm= con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                        ResultSet rs= stm.executeQuery(qry);
                        while(rs.next()){
                            if(passwordField.getText().equals(rs.getString("PASSWORD"))&&
                                    usernmTextField.getText().equals(rs.getString("USERNAME"))){
                            flag = true;
                            break;
                            }     
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(EmployeeGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }finally{
                        try {                        
                        if(rs != null)rs.close();            
                        if(stm != null)stm.close();                        
                        }
                        catch (SQLException ex) {
                            Logger.getLogger(EmployeeGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                
                    if (!flag){
                        message.setText("Your Username and/or Password is incorrect");
                        message.setTextFill(Color.rgb(210, 39, 30));
                    } else {
                        if(con != null)try {
                            con.close();
                        } catch (SQLException ex) {
                            Logger.getLogger(EmployeeGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                     */   
                
                    try {    
                     con = (new EstablishConnection()).EstablishDBConnection(con);   
                    }
                    catch (Exception ex) {
                          showErrorAlert(ex.getMessage(),null,"DB connection error");
                          System.exit(1);
                    }   
                    
                    try {
                    final String qry = "SELECT * FROM EMPL_ADMINS where USERNAME='"+usernmTextField.getText()+"' and PASSWORD='"+passwordField.getText()+"'";                        
                    Statement stm= con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    ResultSet rs= stm.executeQuery(qry);         
                    
                    if (rs.next()== false){
                     showErrorAlert("Λανθασμένος κωδικός χρήστη ή Password. Παρακαλώ προσπαθείστε ξανά",null,"Error");                    
                     passwordField.clear();
                     usernmTextField.clear();
                     usernmTextField.requestFocus();
                     return;
                    }
                    } catch (Exception ex) {
                          showErrorAlert(ex.getMessage(),null,null);
                    }                                                
                        Timeline fiveSecondsWonder = new Timeline(
                                new KeyFrame(Duration.ZERO, new EventHandler<ActionEvent>() {
                                    @Override
                                    public void handle(ActionEvent event) {
                                        message.setText("Your Password has been confirmed");
                                        message.setTextFill(Color.rgb(21, 117, 84));
                                        FadeTransition ft = new FadeTransition(Duration.millis(3000), message);
                                        ft.setFromValue(1.0);
                                        ft.setToValue(0.1);
                                        ft.setCycleCount(2);
                                        ft.setAutoReverse(true);
                                        ft.play();
                                    }
                                }) , new KeyFrame(Duration.seconds(6), new EventHandler<ActionEvent>(){
                                    @Override
                                    public void handle(ActionEvent event) {
                                        
                                        Parent root = null;
                                        try {
                                            root = FXMLLoader.load(getClass().getResource("EmployeeGUI.fxml"));
                                        } catch (IOException ex) {
                                            Logger.getLogger(EmployeeGUI.class.getName()).log(Level.SEVERE, null, ex);
                                            ErrorStage.showErrorStage(ex.getMessage());
                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.setTitle("ΣΦΑΛΜΑ");
                                            alert.setHeaderText("");
                                            alert.setContentText(ex.getMessage());
                                            alert.show();
                                        }
                                        
                                        Scene scene = new Scene(root);
                                        scene.getStylesheets().add(EmployeeGUI.class.getResource("empl.css").toExternalForm());
                                        stage.setScene(scene);
                                        stage.centerOnScreen();
                                        stage.show();
                                    }}
                                ));
                        fiveSecondsWonder.setCycleCount(1);
                        fiveSecondsWonder.play();                                                            
                    }
                    //passwordField.clear();
                    
               // }
            });        
            return gp;
                   
    }        
                
        /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

        
}
