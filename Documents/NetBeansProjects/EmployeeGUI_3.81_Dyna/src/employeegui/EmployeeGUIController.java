/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import HelpMenu.GetUpdatesWithNio;
import HelpMenu.HelpText;
import static employeegui.Alerts.showDialogYES_NO;
import static employeegui.EmployeeGUI.con;
import historyMenu.ReportEnum;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;
import utilities.ProgressInd;
 

/**
 *
 * @author Home
 */
public class EmployeeGUIController implements Initializable {
    
    
    //private Connection con;
    public static Person worker = null; 
    public static int worker_id = 0;
    private final Desktop desktop = Desktop.getDesktop();
    private final FileChooser fileChooser = new FileChooser(); 
    private JRViewer jrViewer;
    private final SwingNode swingNode = new SwingNode();
    private boolean active = false;
    
    @FXML
    private MenuItem showTable;
    @FXML
    private MenuItem closeMenu;
    @FXML
    private MenuItem enterData;
    @FXML
    private StackPane centerStack;
    @FXML
    private MenuItem opentimer;
    @FXML
    private MenuItem monthlyReport;
    @FXML
    private MenuItem showMonthlyreport;
    @FXML
    private MenuItem vacationReport;
    @FXML
    private MenuItem createVacationReport;
    
    private String salaryTableStr = null;
    private int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
    @FXML
    private MenuItem openPDFMenu;
    @FXML
    private MenuItem openExcelMenu;
    @FXML
    private MenuItem OpenBrowser;    
    private final DateTimeFormatter formatter = 
                        DateTimeFormatter.ofPattern("M_yyyy");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //con = (new EstablishConnection()).EstablishDBConnection();
        
        /* Compute Entitled Days, because something might have changed*/
        
    ComputeDays.ComputeEntitledDays entitledDays = new ComputeDays.ComputeEntitledDays();
    entitledDays.EntitledDays(EmployeeGUI.con);  
    }    

    @FXML
    private void handleShowTable(ActionEvent event) throws SQLException {        
        
        EnterDataController.MultiRecord = "NO";
        employeegui.DBConnect.searchOnly = false;

        Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = new DBConnect().ProduceTable(EmployeeGUI.con);              
        Scene scene = new Scene(root, 1300, 700);         
        stage.setScene(scene); 
        stage.show();   
    }

    @FXML
    private void handleCloseMenu(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleEnterData(ActionEvent event) throws IOException {
              
        EnterDataController.MultiRecord = "YES";            

        centerStack.getChildren().clear();        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EnterData.fxml"));        
        Parent root = (Parent)fxmlLoader.load();
        root.getStylesheets().add(EmployeeGUI.class.getResource("empl.css").toExternalForm());
        EnterDataController controller = fxmlLoader.<EnterDataController>getController();           
        //centerStack.getChildren().addAll(root); 
        
        Stage stage = new Stage(StageStyle.DECORATED);
        Scene scene = new Scene(root, 1050, 800);         
        stage.setScene(scene); 
        stage.show();
                
        
     //   controller.buttonPressed.addListener(e->{
     //       centerStack.getChildren().clear();
     //   });
    }

    @FXML
    private void handleHelp(ActionEvent event) throws SQLException {
      new  HelpMenu.HelpText().Help(con);
    }
    
    @FXML
    private void handleActiveEmployees(ActionEvent event) throws SQLException {
        active = true;
        handleEmployeesReport(event);
        active = false;
    }
    
    @FXML
    private void handleEmployeesReport(ActionEvent event) throws SQLException {    
    
  
    try {
        // Load template from any Source (File, String path, InputStream, byte array, etc.)        
        InputStream is2 = (InputStream)EmployeeGUIController.class.getResourceAsStream("/JasperReports/Workers_1.jasper");
        
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
        if(active){
            qry = "SELECT c.NAME AS c_name,s.NAME AS s_name,f.NAME AS isfores_name,r.NAME AS relation_name,t.NAME AS tapit_name,w.* "
                    + "FROM workers w"
                +" LEFT JOIN company_information c ON c.id = w.company"
                +" LEFT JOIN subsidiaries s ON s.ID=w.SUBSIDIARY"
                +" LEFT JOIN EISFORES_INFO f ON f.ID=w.REASON_ISFORES"        
                +" LEFT JOIN RELATION_INFO r ON r.ID=w.RELATION"                        
                +" LEFT JOIN TAPIT_INFO t ON t.ID=w.TAPIT"                                        
                +" WHERE HIRE_DATE IS NOT NULL AND apolisi = 0 AND COMPANY >0 ORDER BY COMPANY,SUBSIDIARY,LAST_NAME";
            active = false;
        }else{
            qry = "SELECT c.NAME AS c_name,s.NAME AS s_name,f.NAME AS isfores_name,r.NAME AS relation_name,t.NAME AS tapit_name,w.* FROM workers w"
                +" LEFT JOIN company_information c ON c.id = w.company"
                +" LEFT JOIN subsidiaries s ON s.ID=w.SUBSIDIARY"
                +" LEFT JOIN EISFORES_INFO f ON f.ID=w.REASON_ISFORES"        
                +" LEFT JOIN RELATION_INFO r ON r.ID=w.RELATION"                        
                +" LEFT JOIN TAPIT_INFO t ON t.ID=w.TAPIT"                                        
                +" WHERE HIRE_DATE IS NOT NULL AND COMPANY >0 ORDER BY COMPANY,SUBSIDIARY,LAST_NAME";
        }
        ResultSet rs = statement.executeQuery(qry);
        JRResultSetDataSource jrDataSource = new JRResultSetDataSource(rs);
        //if(rs != null)rs.close();
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

    @FXML
    private void handleOpenTimer(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(
              "/timer/Timer.fxml"));
        Scene scene = new Scene(root); 
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.show();  
    }

    @FXML
    private void createMonthlyReport(ActionEvent event) {
        CreateReport monthlyTable = new CreateReport();
        monthlyTable.CreateMonthlyDBTable(EmployeeGUI.con, null);       
        //monthlyReport.setDisable(true);
    }

    @FXML
    private void handleShowMonthlyReport(ActionEvent event) throws SQLException {
        centerStack.getChildren().clear();
        VBox vb1 = new ShowReport().ProduceReportTable(EmployeeGUI.con, null);
        //centerStack.getChildren().addAll(vb1);
        Stage stage = new Stage(StageStyle.DECORATED);
        Scene scene = new Scene(vb1, 1050, 800);         
        stage.setScene(scene); 
        stage.show();
        
    }

    @FXML
    private void handleVacationReport(ActionEvent event) throws IOException, SQLException {           
        Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = new VacationReport().ProduceVacationTable(EmployeeGUI.con, "EMPLOYEE_VACATION");               
        Scene scene = new Scene(root);         
        stage.setScene(scene); 
        stage.show();   
    }

    @FXML
    private void handleCreateVacationReport(ActionEvent event) throws SQLException {
        CreateVacationReport vacationTable = new CreateVacationReport();
        vacationTable.CreateVacationDBTable(EmployeeGUI.con);       
        //createVacationReport.setDisable(true);
        
    }

    @FXML
    private void createTimerInterval(ActionEvent event) throws IOException {        
        Parent root = FXMLLoader.load(getClass().getResource("TimeInterval.fxml"));
        Scene scene = new Scene(root); 
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleVacationRequest(ActionEvent event) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource(
              "/Enter_Data_vacation_Days/Enter_Data_Vacation.fxml"));
      Scene scene = new Scene(root); 
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setScene(scene);    

      stage.show();  
    }

    @FXML
    private void handleSickLess3(ActionEvent event) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource(
              "/Enter_Data_Sick_Days_Less_3/Sick_Days_Less_3.fxml"));
      Scene scene = new Scene(root); 
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setScene(scene);    

      stage.show();  
    }

    @FXML
    private void handleSickMore3(ActionEvent event) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource(
              "/Enter_Data_Sick_Days_More_3/Sick_Days_More_3.fxml"));
      Scene scene = new Scene(root); 
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setScene(scene);    

      stage.show();  
    }

    @FXML
    private void handleHolidays(ActionEvent event) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource(
              "/Enter_Data_vacation_Days/Holidays.fxml"));
      Scene scene = new Scene(root); 
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setScene(scene);    

      stage.show();  
    }       

    @FXML
    private void handleVacationCard(ActionEvent event)throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource(
              "/Enter_Data_vacation_Days/VacationCard.fxml"));
      Scene scene = new Scene(root); 
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setScene(scene);    

      stage.show();  
    }        
    

    @FXML
    private void handleSickLess3Card(ActionEvent event)throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource(
              "/Enter_Data_Sick_Days_Less_3/SickLess3Card.fxml"));
      Scene scene = new Scene(root); 
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setScene(scene);    

      stage.show();  
    }        
    

    @FXML
    private void handleSickMore3Card(ActionEvent event) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource(
              "/Enter_Data_Sick_Days_More_3/SickMore3Card.fxml"));
      Scene scene = new Scene(root); 
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setScene(scene);    

      stage.show();  
    }        

    @FXML
    private void createAbsentReport(ActionEvent event) throws IOException {
      Parent root = FXMLLoader.load(getClass().getResource(
              "/AbsentReport/AbsentReport.fxml"));
      Scene scene = new Scene(root); 
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setScene(scene);    

      stage.show();  
    }

    @FXML
    private void handleAbout(ActionEvent event)  {     
      HelpMenu.AboutText.About();
    }

    @FXML
    private void handleSalaryReport(ActionEvent event) throws SQLException, Throwable {
       //salaryTableStr = (new SalaryReport.CreateSalaryReport()).CreateDBSalaryReport(EmployeeGUI.con, null); 
       
       if (showDialogYES_NO("Θα δημιουργηθεί μισθοδοσία για τον μήνα "
           +Integer.toString(LocalDate.now().minusMonths(1).getMonthValue())+"/"
           +Integer.toString(LocalDate.now().minusMonths(1).getYear())
           +". Θέλετε να συνεχίσετε?",null,null)!= ButtonType.YES) return; 
        
       Task task = new Task<Void>() {
            @Override 
                public Void call() throws SQLException, InterruptedException, ExecutionException {               
                    updateProgress(0, 100);
                    utilities.ProgressInd.threadProgress.addListener(new ChangeListener<Number>() {
                        @Override
                        public void changed(ObservableValue<? extends Number> ov,
                        Number old_val, Number new_val) {                             
                                
                                updateProgress(new_val.intValue(), 100);                                           
                    }
                });
                salaryTableStr = (new SalaryReport.CreateSalaryReport()).CreateDBSalaryReport(EmployeeGUI.con, null); 
                
                
                return null;    
            }
        };    
        ProgressInd progressInd = new utilities.ProgressInd();
        progressInd.showProgressIndicator();
        progressInd.pi.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {        
                progressInd.progressStage.close();                
                Alerts.showInformationAlert("Η διαδικασία τελείωσε επιτυχώς !",null,null);   
   
            }
        });
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            // We will have to catch here the events of the Exceptions of the thread
            @Override
            public void handle(WorkerStateEvent t) {        
                progressInd.progressStage.close();
                String err = task.exceptionProperty().toString();
                Alerts.showErrorAlert(err, null, null);
                Alerts.showInformationAlert("Συνέβησαν προβλήματα ! Η διαδικασία θα διακοπεί !",null,null);   
   
            }
        });
        
        new Thread(task).start();
       
    }

    @FXML
    private void handleShowMisthodotiki(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = previousMonth == 4 || previousMonth == 7 || previousMonth == 12 ?
                new SalaryReport.ShowMisthodotikiMerger().showMisthodotikiMerger(EmployeeGUI.con,  -1, -1, null) :                        
                new SalaryReport.ShowMisthodotiki().showMisthodotiki(EmployeeGUI.con, null, null);                                        
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowArthro(ActionEvent event)  throws SQLException {
        Stage stage = new Stage(StageStyle.DECORATED);
        VBox root = previousMonth == 4 || previousMonth == 7 || previousMonth == 12 ?
                new SalaryReport.ShowArthroMerger().showArthroMerger(EmployeeGUI.con, -1, -1, null) : 
                new SalaryReport.ShowArthro().showArthro(EmployeeGUI.con, null, null);              
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }
    @FXML
    private void handleShowIKA(ActionEvent event) throws SQLException {
        Stage stage = new Stage(StageStyle.DECORATED); 
        VBox root = previousMonth == 4 || previousMonth == 7 || previousMonth == 12 ?
                new SalaryReport.ShowIKAMerger().showIKAMerger(EmployeeGUI.con, -1, -1, null) : 
                new SalaryReport.ShowIKA().showIKA(EmployeeGUI.con, null, null);              
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowPliromi(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowPliromi()).showPliromi(EmployeeGUI.con, null);               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
        
    }

    @FXML
    private void handleShowAnalitikiAPD(ActionEvent event) throws SQLException {
        new SalaryReport.ApdPdf().apdPdf(EmployeeGUI.con, null);
    }


    @FXML
    private void handleApodixis(ActionEvent event) throws SQLException {
        new SalaryReport.ApodixisPDF().apodixisPDF(EmployeeGUI.con, null);
    }

    @FXML
    private void handleCSL01(ActionEvent event) throws SQLException, IOException {
        new SalaryReport.CSL01().csl01(EmployeeGUI.con, null);
    }

    @FXML
    private void handleSubsidiaries(ActionEvent event) throws SQLException {
        new Diafora.Subsidiaries().subsidiaries(EmployeeGUI.con);        
    }

    @FXML
    private void handleProkatavoles(ActionEvent event) {
    }

    @FXML
    private void handleLoans(ActionEvent event) {
    }

    @FXML
    private void handleStoixeiaEpihirisis(ActionEvent event) throws SQLException {
        new Diafora.StoixiaEpihirisis().stoixiaEpihirisis(EmployeeGUI.con);
    }
    @FXML
    private void handleDoroPashaReport(ActionEvent event) throws SQLException {
       new SalaryReport.CreateDoroPashaReport().createDBDoroPashaReport(EmployeeGUI.con, 0);              
    }
    @FXML
    private void handleEpidomaAdeiasReport(ActionEvent event) throws SQLException {
       new SalaryReport.CreateEpidomaAdeiasReport().createDBEpidomaAdeiasReport(EmployeeGUI.con, 0);              
    }
    @FXML
    private void handleDoroXmasReport(ActionEvent event) throws SQLException {
       new SalaryReport.CreateDoroXmasReport().createDBDoroXmasReport(EmployeeGUI.con, 0);              
    }

    @FXML
    private void handleShowMisthodotikiDoroPasha(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowMisthodotiki()).showMisthodotiki(EmployeeGUI.con, 
                "DORO_PASHA_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowMisthodotikiEpidomaAdeias(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowMisthodotiki()).showMisthodotiki(EmployeeGUI.con, 
                "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowMisthodotikiDoroXmas(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowMisthodotiki()).showMisthodotiki(EmployeeGUI.con, 
                "DORO_XMAS_REPORT_"+Integer.toString(LocalDate.now().minusMonths(1).getYear()), null);               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowArthroDoroPasha(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowArthro()).showArthro(EmployeeGUI.con, 
                "DORO_PASHA_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowArthroEpidomaAdeias(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowArthro()).showArthro(EmployeeGUI.con, 
                "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(LocalDate.now().getYear()),null);               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowArthroDoroXmas(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowArthro()).showArthro(EmployeeGUI.con, 
                "DORO_XMAS_REPORT_"+Integer.toString(LocalDate.now().minusMonths(1).getYear()), null);               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowIkaDoroPasha(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowIKA()).showIKA(EmployeeGUI.con, 
                "DORO_PASHA_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowIkaEpidomaAdeias(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowIKA()).showIKA(EmployeeGUI.con, 
                "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowIkaDoroXmas(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowIKA()).showIKA(EmployeeGUI.con, 
                "DORO_XMAS_REPORT_"+Integer.toString(LocalDate.now().minusMonths(1).getYear()), null);               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowPliromiDoroPasha(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowPliromi()).showPliromi(EmployeeGUI.con, 
                "DORO_PASHA_REPORT_"+Integer.toString(LocalDate.now().getYear()));               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowPliromiEpidomaAdeias(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowPliromi()).showPliromi(EmployeeGUI.con, 
                "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(LocalDate.now().getYear()));               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleShowPliromiDoroXmas(ActionEvent event) throws IOException, SQLException {
      Stage stage = new Stage(StageStyle.DECORATED);        
        VBox root = (new SalaryReport.ShowPliromi()).showPliromi(EmployeeGUI.con, 
                "DORO_XMAS_REPORT_"+Integer.toString(LocalDate.now().minusMonths(1).getYear()));               
        Scene scene = new Scene(root, 1200, 700);          
        stage.setScene(scene); 
        stage.setFullScreen(true);
        stage.show();   
    }

    @FXML
    private void handleParaitisi(ActionEvent event) throws IOException {
        centerStack.getChildren().clear();        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/diakopiErgasiakisShesis/Paraitisi.fxml"));        
        Parent root = (Parent)fxmlLoader.load();          
        diakopiErgasiakisShesis.ParaitisiController controller = fxmlLoader.<diakopiErgasiakisShesis.ParaitisiController>getController();
        controller.initConn(EmployeeGUI.con);        
        centerStack.getChildren().addAll(root);                 
    }
    
    @FXML
    private void handleApolisi(ActionEvent event) throws IOException {
        centerStack.getChildren().clear();        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/diakopiErgasiakisShesis/Apolisi.fxml"));        
        Parent root = (Parent)fxmlLoader.load();          
        diakopiErgasiakisShesis.ApolisiController controller = fxmlLoader.<diakopiErgasiakisShesis.ApolisiController>getController();
        controller.initConn(EmployeeGUI.con);        
        centerStack.getChildren().addAll(root);                 
    
    }

    @FXML
    private void handleSintaxiodotisi(ActionEvent event) throws IOException {
        centerStack.getChildren().clear();        
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/diakopiErgasiakisShesis/Sintaxiodotisi.fxml"));        
        Parent root = (Parent)fxmlLoader.load();          
        diakopiErgasiakisShesis.SintaxiodotisiController controller = fxmlLoader.<diakopiErgasiakisShesis.SintaxiodotisiController>getController();
        controller.initConn(EmployeeGUI.con);        
        centerStack.getChildren().addAll(root);                 
    }

    @FXML
    private void handleApodixisDoroPasha(ActionEvent event) throws SQLException {
        new SalaryReport.ApodixisPDF().apodixisPDF(EmployeeGUI.con, 
                "DORO_PASHA_REPORT_"+Integer.toString(LocalDate.now().getYear()));
    }


    @FXML
    private void handleApodixisEpidomaAdeias(ActionEvent event) throws SQLException {
        new SalaryReport.ApodixisPDF().apodixisPDF(EmployeeGUI.con, 
                "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(LocalDate.now().getYear()));
    }


    @FXML
    private void handleApodixisDoroXmas(ActionEvent event) throws SQLException {
        new SalaryReport.ApodixisPDF().apodixisPDF(EmployeeGUI.con, 
                "DORO_XMAS_REPORT_"+Integer.toString(LocalDate.now().minusMonths(1).getYear()));
    }

    @FXML
    private void handleOpenPDF(ActionEvent event) {
        configureFileChooser(fileChooser);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF(*.pdf)", "*.pdf"));
        Stage fileChooserStage = new Stage(StageStyle.DECORATED);
        List<File> files = fileChooser.showOpenMultipleDialog(fileChooserStage);
        if (files != null)
            for(File f : files)openFile(f);       
    }

    @FXML
    private void handleOpenExcel(ActionEvent event) {
        configureFileChooser(fileChooser);
        fileChooser.getExtensionFilters().clear();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XLS(*.xls)", "*.xls"));
        Stage fileChooserStage = new Stage(StageStyle.DECORATED);
        List<File> files = fileChooser.showOpenMultipleDialog(fileChooserStage);
        if (files != null)
            for(File f : files)openFile(f);
    }
    
    private void openFile(File file){
        try {
            desktop.open(file);
        }catch (IOException ex){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Μήνυμα Σφάλματος");
            alert.setHeaderText("Σφάλμα ανοίγματος φακέλου");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
            Logger.getLogger(EmployeeGUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("Διάλεξε αρχεία");
        fileChooser.setInitialDirectory(new File("C:\\EmployeeGUI\\EmployeeGUIOutput\\"));
    }

    @FXML
    private void openBrowser(ActionEvent event) {
        Stage browserStage = new Stage(StageStyle.DECORATED);
        browserStage.getIcons().add(new Image("file:resources/images/employeeList.png"));
        browserStage.setTitle("EmployeeGUI Browser");
        TabPane tabPane = new TabPane();
        Tab tabDyna = new Tab("dynamotors Home");
        Tab tabIKA = new Tab("IKA home");
        tabPane.getTabs().addAll(tabDyna, tabIKA);
        WebView dynaBrowser = new WebView();
        WebEngine dynaWebEngine = dynaBrowser.getEngine();
        dynaWebEngine.load("http://www.dynamotors.net");
        tabDyna.setContent(dynaBrowser);
        WebView ikaBrowser = new WebView();
        WebEngine ikaWebEngine = ikaBrowser.getEngine();
        ikaWebEngine.load("http://www.ika.gr");
        tabIKA.setContent(ikaBrowser);
        Scene browserScene = new Scene(tabPane, 1200, 600, Color.web("#666970"));
        browserStage.setScene(browserScene);
        browserStage.show();
    }

    @FXML
    private void handleGetUpdates(ActionEvent event) throws SQLException {
        try {
            GetUpdatesWithNio.getUpdates(EmployeeGUI.con);
        } catch (IOException ex) {
            Logger.getLogger(EmployeeGUIController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleOldTimerReports(ActionEvent event) throws SQLException {
      new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.TIMER);
    }

    @FXML
    private void handleOldVacationReports(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.VACATION);
    }

    @FXML
    private void handleShowMisthodotikiHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.MISTHODOTIKH);
    }

    @FXML
    private void handleShowMisthodotikiDoroPashaHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.MISTHODOTIKHDP);
    }

    @FXML
    private void handleShowMisthodotikiEpidomaAdeiasHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.MISTHODOTIKHEA);
    }

    @FXML
    private void handleShowMisthodotikiDoroXmasHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.MISTHODOTIKHXMAS);
    }

    @FXML
    private void handleShowArthroHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.ARTHRO);
    }

    @FXML
    private void handleShowArthroDoroPashaHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.ARTHRODP);
    }

    @FXML
    private void handleShowArthroEpidomaAdeiasHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.ARTHROEA);
    }

    @FXML
    private void handleShowArthroDoroXmasHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.ARTHROXMAS);
    }

    @FXML
    private void handleShowIKAHistory(ActionEvent event) throws SQLException{
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.IKA);
    }

    @FXML
    private void handleShowIkaDoroPashaHistory(ActionEvent event) throws SQLException{
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.IKADP);
    }

    @FXML
    private void handleShowIkaEpidomaAdeiasHistory(ActionEvent event) throws SQLException{
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.IKAEA);
    }

    @FXML
    private void handleShowIkaDoroXmasHistory(ActionEvent event) throws SQLException{
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.IKAXMAS);
    }

    @FXML
    private void handleShowPliromiHistory(ActionEvent event) throws SQLException{
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.PLIROMI);
    }

    @FXML
    private void handleShowPliromiDoroPashaHistory(ActionEvent event) throws SQLException{
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.PLIROMIDP);
    }

    @FXML
    private void handleShowPliromiEpidomaAdeiasHistory(ActionEvent event) throws SQLException{
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.PLIROMIEA);
    }

    @FXML
    private void handleShowPliromiDoroXmasHistory(ActionEvent event) throws SQLException{
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.PLIROMIXMAS);
    }

    @FXML
    private void handleMazikiDiorthosi(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(
              "/AbsentReport/MazikiDiorthosi.fxml"));
      Scene scene = new Scene(root); 
      Stage stage = new Stage(StageStyle.DECORATED);
      stage.setScene(scene);    

      stage.show();
    }
    
    @FXML
    private void handleCreateOldTimerReports(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/historyMenu/CreateOldReports.fxml"));        
        Parent root = (Parent)fxmlLoader.load();      
        historyMenu.CreateOldReportsController controller = fxmlLoader.<historyMenu.CreateOldReportsController>getController();        
        Scene scene = new Scene(root); 
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);    
        controller.stage = stage; 
        stage.show();
    }

    @FXML
    private void handleCreateOldSalaryReports(ActionEvent event) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/historyMenu/CreateOldSalaryReports.fxml"));        
        Parent root = (Parent)fxmlLoader.load();      
        historyMenu.CreateOldSalaryReportsController controller = fxmlLoader
                .<historyMenu.CreateOldSalaryReportsController>getController();        
        Scene scene = new Scene(root); 
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setScene(scene);    
        controller.stage = stage; 
        stage.show();
    }    

    @FXML
    private void handleCreateOldDPReport(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/historyMenu/CreateOldDoroPashaReport.fxml"));
            Parent root = (Parent)fxmlLoader.load();
            historyMenu.CreateOldDPReportsController controller = fxmlLoader
                    .<historyMenu.CreateOldDPReportsController>getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(scene);
            controller.stage = stage;
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(EmployeeGUIController.class.getName()).log(Level.SEVERE, null, ex);
            Alerts.showErrorAlert(ex.getMessage(), null, null);
        }
    }

    @FXML
    private void handleCreateOldEAReport(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/historyMenu/CreateOldEpidomaAdeiasReport.fxml"));
            Parent root = (Parent)fxmlLoader.load();
            historyMenu.CreateOldEAReportsController controller = fxmlLoader
                    .<historyMenu.CreateOldEAReportsController>getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(scene);
            controller.stage = stage;
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(EmployeeGUIController.class.getName()).log(Level.SEVERE, null, ex);
            Alerts.showErrorAlert(ex.getMessage(), null, null);
        }        
    }

    @FXML
    private void handleCreateOldDXReport(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/historyMenu/CreateOldDoroXmasReport.fxml"));
            Parent root = (Parent)fxmlLoader.load();
            historyMenu.CreateOldDXReportsController controller = fxmlLoader
                    .<historyMenu.CreateOldDXReportsController>getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(scene);
            controller.stage = stage;
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(EmployeeGUIController.class.getName()).log(Level.SEVERE, null, ex);
            Alerts.showErrorAlert(ex.getMessage(), null, null);
        }
    }

    @FXML
    private void handleCreateOldVacationReport(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/historyMenu/CreateOldVacationReport.fxml"));
            Parent root = (Parent)fxmlLoader.load();
            historyMenu.CreateOldVacationReportController controller = fxmlLoader
                    .<historyMenu.CreateOldVacationReportController>getController();
            Scene scene = new Scene(root);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setScene(scene);
            controller.stage = stage;
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(EmployeeGUIController.class.getName()).log(Level.SEVERE, null, ex);
            Alerts.showErrorAlert(ex.getMessage(), null, null);
        }
    }

    @FXML
    private void handleShowApodixisHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.APODIXIS);
    }

    @FXML
    private void handleShowApodixisDoroPashaHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.APODIXISDP);
    }

    @FXML
    private void handleShowApodixisEpidomaAdeiasHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.APODIXISEA);
    }

    @FXML
    private void handleShowApodixisDoroXmasHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.APODIXISXMAS);
    }

    @FXML
    private void handleShowAPDHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.APD);
    }

    @FXML
    private void handleShowCLSHistory(ActionEvent event) throws SQLException {
        new historyMenu.OldReports().oldReportsList(EmployeeGUI.con, ReportEnum.CSL01);
    }
}
