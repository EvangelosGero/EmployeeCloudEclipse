/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Diafora;

import employeegui.ErrorStage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author egdyn_000
 */
public class Subsidiaries {
    
    private Connection con = null;
    private ResultSet rs = null;
    Statement stm = null;
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();    
    private TextField idTextField, nameTextField;
    Button insertButton, cancelButton, deleteButton;
    TableView tableView;
    Stage popup;
    String query;
    
    public void subsidiaries (Connection con) throws SQLException{
      try{  
        this.con = con;
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("ΥΠΟΚΑΤΑΣΤΗΜΑΤΑ");
        Label label = new Label("Παρακαλώ εισάγετε ή διαγράψετε κάποιο υποκατάστημα");
       
        HBox hBox = new HBox(70);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setAlignment(Pos.CENTER);       
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE,
                ResultSet.HOLD_CURSORS_OVER_COMMIT);
        query = "SELECT * FROM subsidiaries";
        rs = stm.executeQuery(query);       
        while (rs.next()){
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(Integer.toString(rs.getInt(1)));
            row.add(rs.getString(2));
            data.add(row);
        }
        if (rs != null)rs.close();
        tableView = new TableView();
        TableColumn<ObservableList, String> idCol = new TableColumn<>("Κωδικός");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(0).toString());
        });        
        TableColumn<ObservableList, String> nameCol = new TableColumn("Ονομασία");
        nameCol.setMinWidth(300); 
        nameCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(1).toString());
        });
        
        tableView.setItems(data);
        tableView.getColumns().addAll(idCol, nameCol);        
        
        insertButton = new Button("ΕΙΣΑΓΩΓΗ");
        insertButton.setOnAction(e -> {
            try {
                this.handleInsert();
            } catch (SQLException ex) {
                Logger.getLogger(Subsidiaries.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }
        });
        deleteButton = new Button("ΔΙΑΓΡΑΦΗ");
        deleteButton.setOnAction(e->{
            try {                            
                rs = stm.executeQuery(query);               
                int rowNumb = tableView.getSelectionModel().getSelectedIndex()+1;
                if(rs.absolute(rowNumb)){
                    rs.deleteRow();
                    rs.beforeFirst();                
                    data.remove(rowNumb-1);
                }
            } catch (SQLException ex) {
                Logger.getLogger(Subsidiaries.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }finally{try {
                if (rs != null)  rs.close();                  
                } catch (SQLException ex) {
                    Logger.getLogger(Subsidiaries.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorStage.showErrorStage(ex.getMessage());
                }
            }
        });
        cancelButton = new Button("ΑΚΥΡΩΣΗ");
        cancelButton.setOnAction(e->stage.close()); 
        hBox.getChildren().addAll(insertButton, deleteButton, cancelButton);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        vBox.getChildren().addAll(label, tableView, hBox);
        Scene scene = new Scene(vBox, 400, 400);
        stage.setScene(scene);
        stage.show();
        stage.setOnHidden(e->{
            try {
                if(rs != null)rs.close(); 
                if(stm != null)stm.close();
            } catch (SQLException ex) {
                Logger.getLogger(Subsidiaries.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }
           
        });
    }catch (SQLException ex) {
            Logger.getLogger(Subsidiaries.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
    }
   }    
   
    public void handleInsert()throws SQLException{
        insertButton.setDisable(true);
        deleteButton.setDisable(true);
        cancelButton.setDisable(true);
        popup = new Stage(StageStyle.DECORATED);
        VBox insertVBox = new VBox(20);
        insertVBox.setPadding(new Insets(5, 5, 5, 5));
        HBox hBox = new HBox(5);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        Label idLabel = new Label("Παρακαλώ εισάγετε Κωδικό :");
        idTextField = new TextField();
        Label nameLabel = new Label("Παρακαλώ εισάγετε Ονομασία :");
        nameTextField = new TextField();
        hBox.getChildren().addAll(idLabel, idTextField, nameLabel, nameTextField);
        Button insertionBtn = new Button("ΕΙΣΑΓΩΓΗ");
        insertionBtn.setOnAction(e->{
            try {
                rs = stm.executeQuery(query);
                rs.moveToInsertRow();
                rs.updateInt(1, Integer.parseInt(idTextField.getText()));
                rs.updateString(2, nameTextField.getText());
                rs.insertRow();
                rs.beforeFirst();
                con.commit();
                ObservableList<String> rowIn = FXCollections.observableArrayList();
                rowIn.addAll(idTextField.getText(), nameTextField.getText());
                data.add(rowIn);
                popup.close(); 
            } catch (SQLException ex) {
                Logger.getLogger(Subsidiaries.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }finally {if (rs != null)try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(Subsidiaries.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }}
        });
        Button cancelationBtn = new Button("ΑΚΥΡΩΣΗ");
        cancelationBtn.setOnAction(e->popup.close());
        HBox btnHBox  = new HBox(530);             
        btnHBox.getChildren().addAll(insertionBtn, cancelationBtn);
        insertVBox.getChildren().addAll(hBox, btnHBox);
        popup.setScene(new Scene(insertVBox));
        popup.setOnHidden(e->{insertButton.setDisable(false);
                            deleteButton.setDisable(false);
                            cancelButton.setDisable(false);
                          });
        popup.showAndWait();
        
    
    }   
    
}
