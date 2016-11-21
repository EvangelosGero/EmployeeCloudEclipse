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
public class StoixiaEpihirisis {
    
    private Connection con = null;
    private ResultSet rs = null;
    Statement stm = null;
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();    
    private TextField idTextField, nameTextField, idosTextField, phoneTextField,
            arMitrErgTextField, ipefthinosTextField, addressTextField, townTextField,
            edraTextField, afmTextField, ipokatastimaIKATextField, arithmosTextField,
            patronymoTextField, TKTextField;
    Button insertButton, cancelButton, deleteButton;
    TableView tableView;
    Stage popup;
    String query;
    
    public void stoixiaEpihirisis (Connection con) throws SQLException{
      try{  
        this.con = con;
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("ΣΤΟΙΧΕΙΑ ΕΠΙΧΕΙΡΗΣΗΣ");
        Label label = new Label("Παρακαλώ εισάγετε ή διαγράψετε κάποια εταιρία");
       
        HBox hBox = new HBox(70);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        hBox.setAlignment(Pos.CENTER);       
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE,
                ResultSet.HOLD_CURSORS_OVER_COMMIT);
        query = "SELECT * FROM company_information";
        rs = stm.executeQuery(query);       
        while (rs.next()){
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(Integer.toString(rs.getInt(1)) != null ? Integer.toString(rs.getInt(1)) : "");
            row.add(rs.getString(2) != null ? rs.getString(2) : "");
            row.add(rs.getString(3) != null ? rs.getString(3) : "");
            row.add(rs.getString(4) != null ? rs.getString(4) : "");
            row.add(rs.getString(5) != null ? rs.getString(5) : "");
            row.add(rs.getString(6) != null ? rs.getString(6) : "");
            row.add(rs.getString(7) != null ? rs.getString(7) : "");
            row.add(rs.getString(8) != null ? rs.getString(8) : "");
            row.add(rs.getString(9) != null ? rs.getString(9) : "");
            row.add(rs.getString(10) != null ? rs.getString(10) : "");
            row.add(rs.getString(11) != null ? rs.getString(11) : "");
            row.add(rs.getString(12) != null ? rs.getString(12) : "");
            row.add(rs.getString(13) != null ? rs.getString(13) : "");
            row.add(rs.getString(14) != null ? rs.getString(14) : "");
            data.add(row);
        }
        if (rs != null)rs.close();
        tableView = new TableView();
        TableColumn<ObservableList, String> idCol = new TableColumn<>("Κωδικός");
        idCol.setMinWidth(30);
        idCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(0).toString());
        });        
        TableColumn<ObservableList, String> nameCol = new TableColumn("Ονομασία");
        nameCol.setMinWidth(100); 
        nameCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(1).toString());
        });
        TableColumn<ObservableList, String> idosEpihirisisCol = new TableColumn("Είδος Επιχ.");
        idosEpihirisisCol.setMinWidth(30); 
        idosEpihirisisCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(2).toString());
        });
        TableColumn<ObservableList, String> phoneCol = new TableColumn("Τηλέφωνο");
        phoneCol.setMinWidth(70); 
        phoneCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(3).toString());
        });
        TableColumn<ObservableList, String> arMitrCol = new TableColumn("Αρ.Μητρ.Εργ.");
        arMitrCol.setMinWidth(70); 
        arMitrCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(4).toString());
        });
        TableColumn<ObservableList, String> ipefthinosCol = new TableColumn("Υπεύθυνος");
        ipefthinosCol.setMinWidth(70); 
        ipefthinosCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(5).toString());
        });
        TableColumn<ObservableList, String> addressCol = new TableColumn("Διεύθυνση");
        addressCol.setMinWidth(70); 
        addressCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(6).toString());
        });
        TableColumn<ObservableList, String> townCol = new TableColumn("Πόλη");
        townCol.setMinWidth(70); 
        townCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(7).toString());
        });
        TableColumn<ObservableList, String> edraCol = new TableColumn("'Εδρα");
        edraCol.setMinWidth(70); 
        edraCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(8).toString());
        });
        TableColumn<ObservableList, String> afmCol = new TableColumn("Α.Φ.Μ.");
        afmCol.setMinWidth(70); 
        afmCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(9).toString());
        });
        TableColumn<ObservableList, String> ipokatastimaIKACol = new TableColumn("Υποκατάστημα ΙΚΑ Υποβολής");
        ipokatastimaIKACol.setMinWidth(120); 
        ipokatastimaIKACol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(10).toString());
        });
        TableColumn<ObservableList, String> patronymoCol = new TableColumn("Ονομα Πατρός");
        patronymoCol.setMinWidth(120); 
        patronymoCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(11).toString());
        });
        TableColumn<ObservableList, String> arithmosOdouCol = new TableColumn("Αριθμός Οδού");
        arithmosOdouCol.setMinWidth(80); 
        arithmosOdouCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(12).toString());
        });
        TableColumn<ObservableList, String> TKCol = new TableColumn("TK");
        TKCol.setMinWidth(80); 
        TKCol.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().get(13).toString());
        });
        
        
        tableView.setItems(data);
        tableView.getColumns().addAll(idCol, nameCol,  idosEpihirisisCol, phoneCol,
                arMitrCol, ipefthinosCol, patronymoCol, addressCol, arithmosOdouCol, TKCol, 
                townCol, edraCol, afmCol, ipokatastimaIKACol);                        
        
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
        cancelButton = new Button("ΚΛΕΙΣΙΜΟ");
        cancelButton.setOnAction(e->stage.close()); 
        hBox.getChildren().addAll(insertButton, deleteButton, cancelButton);
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        vBox.getChildren().addAll(label, tableView, hBox);
        Scene scene = new Scene(vBox, 1250, 400);
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
        HBox hBox1 = new HBox(5);
        hBox1.setPadding(new Insets(5, 5, 5, 5));
        Label idLabel = new Label  ("Παρακαλώ εισάγετε Κωδικό Επιχ.:");
        idTextField = new TextField();
        Label nameLabel = new Label("Παρακαλώ εισάγετε Ονομασία Επιχ.:");
        nameTextField = new TextField();
        hBox1.getChildren().addAll(idLabel, idTextField, nameLabel, nameTextField);
        HBox hBox2 = new HBox(5);
        hBox2.setPadding(new Insets(5, 5, 5, 5));
        Label idosLabel = new Label ("Παρακαλώ εισάγετε Είδος Επιχ.   :");
        idosTextField = new TextField();
        Label phoneLabel = new Label("Παρακαλώ εισάγετε τηλέφωνο Επιχ.:");
        phoneTextField = new TextField();
        hBox2.getChildren().addAll(idosLabel, idosTextField, phoneLabel, phoneTextField);
        HBox hBox3 = new HBox(5);
        hBox3.setPadding(new Insets(5, 5, 5, 5));
        Label arMitrErgLabel = new Label("Παρακαλώ Αρ. Μητρώου Εργοδότη:");
        arMitrErgTextField = new TextField();
        Label ipefthinosLabel = new Label("Παρακαλώ εισάγετε Υπεύθυνο Επ.:");
        ipefthinosTextField = new TextField();        
        hBox3.getChildren().addAll(arMitrErgLabel, arMitrErgTextField, ipefthinosLabel, ipefthinosTextField);                
        HBox hBox8 = new HBox(5);
        hBox8.setPadding(new Insets(5, 5, 5, 5));
        Label patronymoLabel = new Label("Παρακαλώ εισάγετε όνομα πατρός υπευθύνου:");
        patronymoTextField = new TextField();
        hBox8.getChildren().addAll(patronymoLabel, patronymoTextField);
        HBox hBox4 = new HBox(5);
        hBox4.setPadding(new Insets(5, 5, 5, 5));
        Label addressLabel = new Label("Παρακαλώ εισάγετε Διεύθυνση Επιχ.    :");
        addressTextField = new TextField();
        Label arithmosLabel = new Label("Παρακαλώ εισάγετε αριθμό οδού       :");
        arithmosTextField = new TextField();
        hBox4.getChildren().addAll(addressLabel, addressTextField, arithmosLabel, arithmosTextField);                
        HBox hBox7 = new HBox(5);
        hBox7.setPadding(new Insets(5, 5, 5, 5));
        Label TKLabel = new Label("Παρακαλώ εισάγετε Τ.Κ.       :");
        TKTextField = new TextField();
        Label townLabel = new Label("Παρακαλώ εισάγετε Πόλη Επιχ.:");
        townTextField = new TextField();        
        hBox7.getChildren().addAll( TKLabel, TKTextField, townLabel, townTextField);        
        HBox hBox5 = new HBox(5);
        hBox5.setPadding(new Insets(5, 5, 5, 5));
        Label edraLabel = new Label("Παρακαλώ εισάγετε Έδρα Επιχ.       :");
        edraTextField = new TextField();
        Label afmLabel = new Label("Παρακαλώ εισάγετε Α.Φ.Μ. Επιχ.:");
        afmTextField = new TextField();
        hBox5.getChildren().addAll(edraLabel, edraTextField, afmLabel, afmTextField);
        HBox hBox6 = new HBox(5);
        hBox6.setPadding(new Insets(5, 5, 5, 5));
        Label ipokatastimaIKALabel = new Label("Παρακαλώ εισάγετε Υποκατάστημα ΙΚΑ Υποβολής:");
        ipokatastimaIKATextField = new TextField();
        hBox6.getChildren().addAll(ipokatastimaIKALabel, ipokatastimaIKATextField );
        Button insertionBtn = new Button("ΕΙΣΑΓΩΓΗ");
        insertionBtn.setOnAction(e->{
            try {
                rs = stm.executeQuery(query);
                rs.moveToInsertRow();
                rs.updateInt(1, Integer.parseInt(idTextField.getText()));
                rs.updateString(2, nameTextField.getText());
                rs.updateString(3, idosTextField.getText());
                rs.updateString(4, phoneTextField.getText());
                rs.updateString(5, arMitrErgTextField.getText());
                rs.updateString(6, ipefthinosTextField.getText());
                rs.updateString(7, addressTextField.getText());
                rs.updateString(8, townTextField.getText());
                rs.updateString(9, edraTextField.getText());
                rs.updateString(10, afmTextField.getText());
                rs.updateString(11, ipokatastimaIKATextField.getText());
                rs.updateString(12, patronymoTextField.getText());
                rs.updateString(13, arithmosTextField.getText());
                rs.updateString(14, TKTextField.getText());
                rs.insertRow();
                rs.beforeFirst();                
                ObservableList<String> rowIn = FXCollections.observableArrayList();
                rowIn.addAll(idTextField.getText(), nameTextField.getText(), idosTextField.getText(),
                        phoneTextField.getText(), arMitrErgTextField.getText(), ipefthinosTextField.getText(),
                        patronymoTextField.getText(), addressTextField.getText(), arithmosTextField.getText(), 
                        townTextField.getText(), edraTextField.getText(), TKTextField.getText(),
                        afmTextField.getText(), ipokatastimaIKATextField.getText());
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
        HBox btnHBox  = new HBox(700);             
        btnHBox.getChildren().addAll(insertionBtn, cancelationBtn);
        insertVBox.getChildren().addAll(hBox1, hBox2, hBox3,hBox8, hBox4, hBox7, hBox5, hBox6, btnHBox);
        popup.setScene(new Scene(insertVBox));
        popup.setOnHidden(e->{insertButton.setDisable(false);
                            deleteButton.setDisable(false);
                            cancelButton.setDisable(false);
                          });
        popup.showAndWait();
        
    
    }   
    
    
}
