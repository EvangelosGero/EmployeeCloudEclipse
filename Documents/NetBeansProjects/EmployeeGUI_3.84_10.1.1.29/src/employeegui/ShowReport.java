/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Home
 */
public class ShowReport {
    
    private TableView<PersonTimer> table = new TableView<>();
    private List<PersonTimer> list = new ArrayList();
    private ObservableList<PersonTimer> data  = FXCollections.observableList(list); 
    private String tableStr;
    private Statement stm = null ;
    private ResultSet rs = null ;
      
               
    public VBox ProduceReportTable(Connection con, String tableString) throws SQLException {
      try{  
            this.tableStr = tableString;
            int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
            int tableYear = LocalDate.now().minusMonths(1).getYear();
            if (tableString == null)tableStr = "REPORT_"+Integer.toString(previousMonth)
            +"_"+Integer.toString(tableYear);
            if (rs != null){rs.close();}
            if (stm != null){stm.close();}
                                 
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql = "select * from "+tableStr;    
            rs = stm.executeQuery(sql);
      
      while (rs.next()){
               data.add(new PersonTimer(rs.getInt("ID"), rs.getString(2), rs.getString(3), 
               rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), 
               rs.getInt(9), rs.getDouble(10), rs.getInt(11), rs.getInt(12)));                      
           }
    }
      catch (SQLException err){
        System.out.println(err.getMessage());
        ErrorStage.showErrorStage(err.getMessage());
       }  finally{
       if (rs != null){rs.close();}
       if (stm != null){stm.close();}
      }             
   
    final Label label = new Label(tableStr);
    label.setFont(new Font("Arial",20));
        
    table.setEditable(true);
    
    TableColumn idCol = new TableColumn("ID");
    idCol.setMinWidth(100);
    idCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,Integer>("id"));
    
    TableColumn firstNameCol = new TableColumn("Ονομα");
    firstNameCol.setMinWidth(100);
    firstNameCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,String>("firstName"));
    
    TableColumn lastNameCol = new TableColumn("Επώνυμο");
    lastNameCol.setMinWidth(100);
    lastNameCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,String>("lastName"));
    
    TableColumn workDayCol = new TableColumn("Εργάσιμες");
    workDayCol.setMinWidth(100);
    workDayCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,Integer>("workDays"));
    
    TableColumn absentDaysCol = new TableColumn("Ημέρες Απουσίας");
    absentDaysCol.setMinWidth(100);
    absentDaysCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,Integer>("absentDays"));
    
    TableColumn occupiedDaysCol = new TableColumn("Ημέρες Απασχόλησης");
    occupiedDaysCol.setMinWidth(100);
    occupiedDaysCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,Integer>("occupiedDays"));
    
    TableColumn sickDaysLess3Col = new TableColumn("Ασθένεια <3");
    sickDaysLess3Col.setMinWidth(100);
    sickDaysLess3Col.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,Integer>("sickDaysLess3"));
    
    TableColumn sickDaysMore3Col = new TableColumn("Ασθένεια >3");
    sickDaysMore3Col.setMinWidth(100);
    sickDaysMore3Col.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,Integer>("sickDaysMore3"));
    
    TableColumn vacationDaysCol = new TableColumn("Ημέρες Αδείας");
    vacationDaysCol.setMinWidth(100);
    vacationDaysCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,Integer>("vacationDays"));
    
    TableColumn cutHoursCol = new TableColumn("Περικοπές(ΩΡΕΣ)");
    cutHoursCol.setMinWidth(100);
    cutHoursCol.setCellValueFactory(
            new PropertyValueFactory<>("cutHours"));
    
    TableColumn superjobHoursCol = new TableColumn("Υπερεργασία");
    superjobHoursCol.setMinWidth(100);
    superjobHoursCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,Integer>("superjobHours"));
    
    TableColumn superHoursCol = new TableColumn("Υπερωρία");
    superHoursCol.setMinWidth(100);
    superHoursCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,Integer>("superHours"));
    
    table.setItems(data);
    table.getColumns().addAll(idCol, firstNameCol, lastNameCol, workDayCol, absentDaysCol, 
            occupiedDaysCol, sickDaysLess3Col, sickDaysMore3Col, vacationDaysCol, 
            cutHoursCol, superjobHoursCol, superHoursCol);
        
    MenuItem cmItem1 = new MenuItem("Copy Table");
    MenuItem cmItem2 = new MenuItem("Print");
    table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    cmItem1.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e){
            final ClipboardContent content = new ClipboardContent();
                              
        /*ObservableList<PersonTimer> rowList = (ObservableList<PersonTimer>) table.getSelectionModel().getSelectedItems();*/

            StringBuilder clipboardString = new StringBuilder();
             
              for  (PersonTimer row : data){

               String cell = Integer.toString(row.getId());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = row.getFirstName();
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = row.getLastName();
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getWorkDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getAbsentDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getOccupiedDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getSickDaysLess3());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getSickDaysMore3());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getVacationDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Double.toString(row.getCutHours());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getSuperjobHours());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getSuperHours());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                
                clipboardString.append('\n');
                }
            
            
            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);
        }
    });
        
        cmItem2.setOnAction(new EventHandler<ActionEvent>() {        
        @Override
        public void handle(ActionEvent e){             
            Printer printer = Printer.getDefaultPrinter();            
            PrinterJob job = PrinterJob.createPrinterJob(printer);
                if (job != null) {
                    Stage dialogStage = new Stage(StageStyle.DECORATED);
                    dialogStage.setTitle("Προσαρμογή Σελίδας για Εκτύπωση");
                    GridPane grid = new GridPane();
                    grid.setAlignment(Pos.CENTER);
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(25, 25, 25, 25));
                    Label scaleX = new Label("Scale X in real number, e.g 0.50 :");
                    grid.add(scaleX, 0, 1);
                    TextField scaleXTextField = new TextField("0.30");
                    grid.add(scaleXTextField, 1, 1);
                    Label scaleY = new Label("Scale Y in real number, e.g. 0.50 :");
                    grid.add(scaleY, 0, 2);
                    TextField scaleYTextField = new TextField("0.30");
                    grid.add(scaleYTextField, 1, 2);
                    Label translateX = new Label("Move X in negative pixels , e.g -300 :");
                    grid.add(translateX, 0, 3);
                    TextField translateXTextField = new TextField("-10");
                    grid.add(translateXTextField, 1, 3);
                    Label translateY = new Label("Move Y in negative pixels, e.g. -70 :");
                    grid.add(translateY, 0, 4);
                    TextField translateYTextField = new TextField("-120");
                    grid.add(translateYTextField, 1, 4);
                    Button btn = new Button("Print");
                    HBox hbBtn = new HBox(10);
                    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                    hbBtn.getChildren().add(btn);
                    grid.add(hbBtn, 1, 6);
                    Scene scene = new Scene(grid, 500, 275);
                    dialogStage.setScene(scene);
                    dialogStage.show();                    
                    btn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle (ActionEvent e) {                    
                    PageLayout pageLayout = printer.createPageLayout(Paper.A4,PageOrientation.LANDSCAPE,
                                Printer.MarginType.HARDWARE_MINIMUM);                                   
                        table.setScaleX(Double.parseDouble(scaleXTextField.getText()));
                        table.setScaleY(Double.parseDouble(scaleYTextField.getText()));
                        table.setTranslateX(Double.parseDouble(translateXTextField.getText()));
                        table.setTranslateY(Double.parseDouble(translateYTextField.getText()));
                    boolean success = job.printPage(pageLayout,table);
                        if (success) {
                             job.endJob(); 
                        } 
                        table.setTranslateX(0);
                        table.setTranslateY(0);               
                        table.setScaleX(1.0);
                        table.setScaleY(1.0); 
                        dialogStage.close();
                                }
                     
                        });
                    }
                }       
            });
    ContextMenu menu = new ContextMenu();
    menu.getItems().addAll(cmItem1, cmItem2);
    table.setContextMenu(menu);
    table.setPrefSize(1300, 1300);

                  
    
    final ScrollPane sp = new ScrollPane();
    sp.setPrefSize(1400, 1400);
    sp.setContent(table);
    
    final VBox vbox = new VBox();
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    vbox.setAlignment(Pos.CENTER);
    vbox.getChildren().addAll(label,sp);
    
    
   return vbox ; 
}    
    
}
