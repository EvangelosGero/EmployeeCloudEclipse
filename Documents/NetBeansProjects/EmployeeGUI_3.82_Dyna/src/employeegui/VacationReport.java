/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import HelpMenu.HelpText;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;

/**
 *
 * @author Home
 */
public class VacationReport {
    private TableView<PersonVacation> table = new TableView<>();
    private List<PersonVacation> list = new ArrayList();
    private ObservableList<PersonVacation> data  = FXCollections.observableList(list);
    private List<PersonVacation> list2 = new ArrayList();
    private ObservableList<PersonVacation> dataA4  = FXCollections.observableList(list2);
    private String tableStr;
    Connection con = null;
    private Statement stm = null ;
    private ResultSet rs = null ;
    private final SwingNode swingNode = new SwingNode();
    private JRViewer jrViewer;
    
    public VBox ProduceVacationTable(Connection con, String tbl) throws SQLException {
      try{  
            
            this.con = con;                    
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql = "select * from "+tbl;    
            rs = stm.executeQuery(sql);
      
      while (rs.next()){
               data.add(new PersonVacation(rs.getInt("ID"), rs.getString(2), rs.getString(3), 
               rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7)));                      
           }
    }
      catch (SQLException err){
        System.out.println(err.getMessage());
        ErrorStage.showErrorStage(err.getMessage());
              }  
      finally{
          if (rs != null){rs.close();}
          if (stm != null) {stm.close();}
      }
   
    final Label label = new Label(tableStr);
    label.setFont(new Font("Arial",20));
    
    final Button btn = new Button("Update Database");
        
    table.setEditable(true);
    TableColumn idCol = new TableColumn("ID");
    idCol.setMinWidth(70);
    idCol.setCellValueFactory(
            new PropertyValueFactory<PersonVacation,Integer>("id"));
    
    TableColumn firstNameCol = new TableColumn("Ονομα");
    firstNameCol.setMinWidth(150);
    firstNameCol.setCellValueFactory(
            new PropertyValueFactory<PersonVacation,String>("firstName"));                      
                 
    TableColumn lastNameCol = new TableColumn("Επώνυμο");
    lastNameCol.setMinWidth(150);
    lastNameCol.setCellValueFactory(
            new PropertyValueFactory<PersonVacation,String>("lastName"));
    
    TableColumn entitledDaysCol = new TableColumn("Δικαιούμενες ημέρες");
    entitledDaysCol.setMinWidth(100);
    entitledDaysCol.setCellValueFactory(
            new PropertyValueFactory<PersonVacation,Integer>("entitledDays"));
    
    entitledDaysCol.setCellFactory(
    new Callback<TableColumn<PersonVacation, Integer>, TableCell<PersonVacation, Integer>>() {
        
        @Override
        public TableCell<PersonVacation, Integer> call(TableColumn<PersonVacation, Integer> l) {
        
        return new TextFieldTableCell(new IntegerStringConverter());
                    }
                }
        );
    entitledDaysCol.setOnEditCommit(
        new EventHandler<CellEditEvent<PersonVacation, Integer>> (){
            @Override
            public void handle(CellEditEvent<PersonVacation, Integer> t){
                ((PersonVacation) t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setEntitledDays(t.getNewValue());
            }
        }
    );
                      
    TableColumn lastyearDaysCol = new TableColumn("Από προηγουμένα έτη");
    lastyearDaysCol.setMinWidth(100);
    lastyearDaysCol.setCellValueFactory(
            new PropertyValueFactory<PersonVacation,Integer>("lastyearDays"));
    
    lastyearDaysCol.setCellFactory(
    new Callback<TableColumn<PersonVacation, Integer>, TableCell<PersonVacation, Integer>>() {
        
        @Override
        public TableCell<PersonVacation, Integer> call(TableColumn<PersonVacation, Integer> l) {
        
        return new TextFieldTableCell(new IntegerStringConverter());
                    }
                }
        );
    lastyearDaysCol.setOnEditCommit(
        new EventHandler<CellEditEvent<PersonVacation, Integer>> (){
            @Override
            public void handle(CellEditEvent<PersonVacation, Integer> t){
                ((PersonVacation) t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setLastyearDays(t.getNewValue());
            }
        }
    );
    TableColumn consumedDaysCol = new TableColumn("φέτος έχει πάρει");
    consumedDaysCol.setMinWidth(100);
    consumedDaysCol.setCellValueFactory(
            new PropertyValueFactory<PersonVacation,Integer>("consumedDays"));
    
    consumedDaysCol.setCellFactory(
    new Callback<TableColumn<PersonVacation, Integer>, TableCell<PersonVacation, Integer>>() {
        
        @Override
        public TableCell<PersonVacation, Integer> call(TableColumn<PersonVacation, Integer> l) {
        
        return new TextFieldTableCell(new IntegerStringConverter());
                    }
                }
        );
    consumedDaysCol.setOnEditCommit(
        new EventHandler<CellEditEvent<PersonVacation, Integer>> (){
            @Override
            public void handle(CellEditEvent<PersonVacation, Integer> t){
                ((PersonVacation) t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setConsumedDays(t.getNewValue());
            }
        }
    );
    
    TableColumn remainingDaysCol = new TableColumn("ΥΠΟΛΟΙΠΟ");
    remainingDaysCol.setMinWidth(100);
    remainingDaysCol.setCellValueFactory(
            new PropertyValueFactory<PersonVacation,Integer>("remainingDays"));
    
    remainingDaysCol.setCellFactory(
    new Callback<TableColumn<PersonVacation, Integer>, TableCell<PersonVacation, Integer>>() {
        
        @Override
        public TableCell<PersonVacation, Integer> call(TableColumn<PersonVacation, Integer> l) {        
        return new TextFieldTableCell(new IntegerStringConverter());
                    }
                }
        );
    remainingDaysCol.setOnEditCommit(
        new EventHandler<CellEditEvent<PersonVacation, Integer>> (){
            @Override
            public void handle(CellEditEvent<PersonVacation, Integer> t){
                ((PersonVacation) t.getTableView().getItems().get(t.getTablePosition().getRow())
                ).setRemainingDays(t.getNewValue());
            }
        }
    );
    
    
    table.setItems(data);
    table.getColumns().addAll(idCol, firstNameCol, lastNameCol, entitledDaysCol, 
            lastyearDaysCol, consumedDaysCol, remainingDaysCol);
    
    MenuItem cmItem1 = new MenuItem("Copy Table");
    MenuItem cmItem2 = new MenuItem("Print");
    MenuItem cmItem3 = new MenuItem("Report");
    /*table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);*/
    cmItem1.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e){
            final ClipboardContent content = new ClipboardContent();
                              
            StringBuilder clipboardString = new StringBuilder();
             
              for  (PersonVacation row : data){

               String cell = Integer.toString(row.getId());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = row.getFirstName();
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = row.getLastName();
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getEntitledDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getLastyearDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getConsumedDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getRemainingDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');                    
                
                clipboardString.append('\n');
                }
                       
            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);
        }
    });
    cmItem2.setOnAction(new EventHandler<ActionEvent>() {
        public void handle(ActionEvent e){            
            Printer printer = Printer.getDefaultPrinter();
                    Stage dialogStage = new Stage(StageStyle.DECORATED); 
                    dialogStage.setTitle("Προσαρμογή Σελίδας για Εκτύπωση");
                    GridPane grid = new GridPane();
                    grid.setAlignment(Pos.CENTER);
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(25, 25, 25, 25));
                    Label scaleX = new Label("Scale X in real number, e.g 0.50 :");
                    grid.add(scaleX, 0, 1);
                    TextField scaleXTextField = new TextField("1.0");
                    grid.add(scaleXTextField, 1, 1);
                    Label scaleY = new Label("Scale Y in real number, e.g. 0.50 :");
                    grid.add(scaleY, 0, 2);
                    TextField scaleYTextField = new TextField("1.0");
                    grid.add(scaleYTextField, 1, 2);
                    Label translateX = new Label("Move X in negative pixels , e.g -300 :");
                    grid.add(translateX, 0, 3);
                    TextField translateXTextField = new TextField("0");
                    grid.add(translateXTextField, 1, 3);
                    Label translateY = new Label("Move Y in negative pixels, e.g. -70 :");
                    grid.add(translateY, 0, 4);
                    TextField translateYTextField = new TextField("0");
                    grid.add(translateYTextField, 1, 4);
                    Button btn1 = new Button("Print");
                    HBox hbBtn = new HBox(10);
                    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                    hbBtn.getChildren().add(btn1);
                    grid.add(hbBtn, 1, 6);
                    Scene scene = new Scene(grid, 500, 275);
                    dialogStage.setScene(scene);
                    dialogStage.show();
                    btn1.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle (ActionEvent e) {                     
                        PageLayout pageLayout = printer.createPageLayout(Paper.A4,PageOrientation.LANDSCAPE,
                                Printer.MarginType.HARDWARE_MINIMUM);
                    for(int i=0; i <= (data.size() / 18); i++){ 
                     PrinterJob job = PrinterJob.createPrinterJob(printer);
                    if (job != null) {   
                        dataA4 = FXCollections.observableArrayList(data.subList(i*18, (i+1)*18 < data.size() ? (i+1)*18 : data.size()));
                        table.setItems(dataA4);
                        table.setScaleX(Double.parseDouble(scaleXTextField.getText()));
                        table.setScaleY(Double.parseDouble(scaleYTextField.getText()));
                        table.setTranslateX(Double.parseDouble(translateXTextField.getText()));
                        table.setTranslateY(Double.parseDouble(translateYTextField.getText()));
                    boolean success = job.printPage(pageLayout,table);
                        if (success) {
                             job.endJob(); 
                        }
                    }
                        
                    }
                    dialogStage.close();
                    table.setItems(data);
                    table.setTranslateX(0);
                    table.setTranslateY(0);               
                    table.setScaleX(1.0);
                    table.setScaleY(1.0); 
                        }                        
                     });                   
                  }                       
            });
    cmItem3.setOnAction(new EventHandler<ActionEvent>() {   //JasperReport
        public void handle(ActionEvent e){
            try {
        // Load template from any Source (File, String path, InputStream, byte array, etc.)        
        InputStream is2 = (InputStream)EmployeeGUIController.class.getResourceAsStream("/JasperReports/VacationReport.jasper");
        
        // Compile jrxml file.      
        //   JasperDesign design = JRXmlLoader.load(is);
        //   JasperReport jasperReport = JasperCompileManager.compileReport(design);
        //Or even better load the compiled file
       JasperReport jasperReport = (JasperReport) JRLoader.loadObject(is2);    
        // Parameters for report
       Map<String, Object> parameters = new HashMap<String, Object>();
       // DataSource
       // This is simple example, no database.
       // then using empty datasource.
       // JRDataSource dataSource = new JREmptyDataSource();       
        Statement statement =  EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        String qry = null;
        qry = "SELECT c.NAME AS c_name, s.NAME AS s_name ,r.NAME AS relation_name, v.* " +
                "FROM  EMPLOYEE_VACATION v " +
                "LEFT JOIN  workers w ON w.id = v.id " +
                "LEFT JOIN company_information c ON c.id = w.company " +
                "LEFT JOIN subsidiaries s ON s.ID = w.SUBSIDIARY " +
                "LEFT JOIN RELATION_INFO r ON r.ID = w.RELATION " +
                "WHERE HIRE_DATE IS NOT NULL AND apolisi = 0 AND COMPANY >0 ORDER BY COMPANY,SUBSIDIARY,LAST_NAME" ;        
        ResultSet resultSet = statement.executeQuery(qry);        
        JRResultSetDataSource jrDataSource = new JRResultSetDataSource(resultSet);
        //if(resultSet != null)rs.close();
        //if(statement != null)statement.close();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,parameters, jrDataSource);
        jrViewer = new JRViewer(jasperPrint);
       
       // Convert the JComponent to Node i.e. from swing to FX, run in another thread 
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(jrViewer);                
            }
        });
       // Make sure the output directory exists.
       // File outDir = new File("C:/jasperoutput");
       // outDir.mkdirs();
       // Export to PDF.       
       JasperExportManager.exportReportToPdfFile(jasperPrint,"C:/EmployeeGUI/EmployeeGUIOutput/Workers_1.pdf");       
   //    showInformationAlert("Done!",null,null);
              
    } catch (Exception ex) {
        Logger.getLogger(HelpText.class.getName()).log(Level.SEVERE, null, ex);
        Alerts.showErrorAlert(ex.getMessage(), null, null);
    }
        
        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);
        Stage stage = new Stage (StageStyle.DECORATED);
        stage.setScene(new Scene(pane, 1200, 800)); 
        stage.setResizable(false);
        stage.show();
        }
       });
    ContextMenu menu = new ContextMenu();
    menu.getItems().addAll(cmItem1, cmItem2, cmItem3);
    table.setContextMenu(menu);

    table.setPrefSize(900, 500);
    final ScrollPane sp = new ScrollPane();
    sp.setPrefSize(900, 500);
    sp.setContent(table);
    
    final VBox vbox = new VBox();
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    vbox.setAlignment(Pos.CENTER);
    vbox.getChildren().addAll(label,sp,btn);
    
    btn.setOnAction(new EventHandler<ActionEvent>() {
       @Override  
       public void handle(ActionEvent e){
           try {               
               stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                       ResultSet.CONCUR_UPDATABLE);
                String sql = "select * from "+tbl;    
                rs = stm.executeQuery(sql);
                /*System.out.println(data.get(0).getEntitledDays());*/
              while (rs.next()) {
                   rs.updateInt(4, data.get(rs.getRow() - 1).getEntitledDays());
                   rs.updateInt(5, data.get(rs.getRow() - 1).getLastyearDays());
                   rs.updateInt(6, data.get(rs.getRow() - 1).getConsumedDays());
                   rs.updateInt(7, data.get(rs.getRow() - 1).getRemainingDays());
                   
                   rs.updateRow();
               }
           } catch (SQLException ex) {
               Logger.getLogger(VacationReport.class.getName()).log(Level.SEVERE, null, ex);
               ErrorStage.showErrorStage(ex.getMessage());
           }finally{
               if (rs != null) {try {
                   rs.close();
                   } catch (SQLException ex) {
                       Logger.getLogger(VacationReport.class.getName()).log(Level.SEVERE, null, ex);
                       ErrorStage.showErrorStage(ex.getMessage());
                   }
}
               if (stm != null) {try {
                   stm.close();
                   } catch (SQLException ex) {
                       Logger.getLogger(VacationReport.class.getName()).log(Level.SEVERE, null, ex);
                       ErrorStage.showErrorStage(ex.getMessage());
                   }
}
           }
       }
       });
    
    
   return vbox ; 
}    
    
    
}
