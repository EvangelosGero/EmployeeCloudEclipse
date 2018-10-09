/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package employeegui;

import static employeegui.Alerts.showErrorAlert;
import java.sql.Connection;
import javafx.scene.text.Font;
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
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Home
 */
public class DBConnect {
    
    private TableView<Person> table = new TableView<>();
    private List<Person> list = new ArrayList<>();
    private ObservableList<Person> data  = FXCollections.observableList(list); 
    private ResultSet rs;
    private Statement stm;
    private final Map<Integer, String> subsidiariesMap = new HashMap<>();
    private final Map<Integer, String> companiesMap = new HashMap<>();
    private Button updateButton;
    public static boolean searchOnly = false;
         
    public VBox ProduceTable(Connection con) throws SQLException {
        try{  
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String qry = "SELECT * FROM subsidiaries";
            rs = stm.executeQuery(qry);
            while (rs.next()){
                subsidiariesMap.put(rs.getInt("id"), rs.getString("name"));
            } 
            if (rs != null)rs.close(); 
            qry = "SELECT * FROM company_information";
            rs = stm.executeQuery(qry);
            while (rs.next()){
                companiesMap.put(rs.getInt("id"), rs.getString("name"));
            }  
            if (rs != null)rs.close(); 
            String sql = "SELECT * FROM workers ORDER BY id";    
            rs = stm.executeQuery(sql);                    
      while (rs.next()){
               data.add(new Person(rs.getInt("ID"), rs.getString(2), rs.getString(3), rs.getString(4),
               rs.getString(5), rs.getDouble(6), 
               (rs.getInt("relation")==0?"Μισθωτός":"Ημερομίσθιος"), rs.getString(8), 
               rs.getString(9), rs.getString(10), rs.getString(11), rs.getString(12), 
               rs.getString(13), rs.getInt(14), rs.getString(15), rs.getString(16), 
               rs.getInt(17), rs.getString(18), rs.getString(19), rs.getInt(20), 
               rs.getString(22), rs.getString(23
               ), rs.getString("first_hire_date"),
               rs.getString("father_name"), rs.getInt("reason_isfores"),
               (rs.getInt("tapit")==0?"ΟΧΙ":(rs.getInt("tapit")==1?
                    "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ (ΠΑΛ)":"ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ (ΝΕΟΙ)")), 
               subsidiariesMap.get(rs.getInt("subsidiary")), companiesMap.get(rs.getInt("company")),
               rs.getInt("kat_asfalisis"), rs.getString("kentro_kostous"), rs.getString("am_Epikourikou"), 
               rs.getString("mother_name"),  rs.getString("ar_parartimatos"), rs.getString("kad"),
               rs.getString("kodikos_idikotitas"), rs.getString("idiki_per_asfalisis"), 
               (rs.getInt("apolisi") == 0 ? "Ενεργός" : (rs.getInt("apolisi") == 1 ? "Απόλυση" : 
                   (rs.getInt("apolisi") == 2 ? "Συνταξιοδότηση" : "Παραίτηση"))), 
               rs.getString("diakopi"), rs.getString("email")));                     
           }
    }catch (SQLException err){
        System.out.println(err.getMessage());
        ErrorStage.showErrorStage(err.getMessage());
              }
     finally{
          if(rs != null)rs.close();
          if(stm != null)stm.close();
      }
       
    final Label label = new Label(searchOnly==false ? "Στοιχεία Εργαζομένων" : "Επιλογή Εργαζομένου");
    label.setFont(new Font("Arial",20));
        
    table.setEditable(true);
    
    TableColumn idCol = new TableColumn("ID");
    idCol.setMinWidth(100);
    idCol.setCellValueFactory(
            new PropertyValueFactory<>("id"));
    
    TableColumn firstNameCol = new TableColumn("First Name");
    firstNameCol.setMinWidth(100);
    firstNameCol.setCellValueFactory(
            new PropertyValueFactory<>("firstName"));
    
    TableColumn lastNameCol = new TableColumn("Last Name");
    lastNameCol.setMinWidth(100);
    lastNameCol.setCellValueFactory(
            new PropertyValueFactory<>("lastName"));
    
    TableColumn jobTitleCol = new TableColumn("Job Title");
    jobTitleCol.setMinWidth(100);
    jobTitleCol.setCellValueFactory(
            new PropertyValueFactory<>("jobTitle"));
    
    TableColumn hireDateCol = new TableColumn("Hire Date");
    hireDateCol.setMinWidth(100);
    hireDateCol.setCellValueFactory(
            new PropertyValueFactory<>("hireDate"));
    
    TableColumn salaryCol = new TableColumn("Salary");
    salaryCol.setMinWidth(100);
    salaryCol.setCellValueFactory(
            new PropertyValueFactory<>("salary"));
    TableColumn relationCol = new TableColumn("Σχέση Εργασίας");
    relationCol.setMinWidth(100);
    relationCol.setCellValueFactory(
            new PropertyValueFactory<>("relation"));
    TableColumn phone1Col = new TableColumn("Τηλέφωνο 1");
    phone1Col.setMinWidth(100);
    phone1Col.setCellValueFactory(
            new PropertyValueFactory<>("phone1"));
    TableColumn phone2Col = new TableColumn("Τηλέφωνο 2");
    phone2Col.setMinWidth(100);
    phone2Col.setCellValueFactory(
            new PropertyValueFactory<>("phone2"));
    TableColumn adtCol = new TableColumn("ΑΔΤ");
    adtCol.setMinWidth(100);
    adtCol.setCellValueFactory(
            new PropertyValueFactory<>("adt"));
    TableColumn amkaCol = new TableColumn("AMKA");
    amkaCol.setMinWidth(100);
    amkaCol.setCellValueFactory(
            new PropertyValueFactory<>("amka"));
    TableColumn afmCol = new TableColumn("ΑΦΜ");
    afmCol.setMinWidth(100);
    afmCol.setCellValueFactory(
            new PropertyValueFactory<>("afm"));
    TableColumn streetCol = new TableColumn("Οδός");
    streetCol.setMinWidth(100);
    streetCol.setCellValueFactory(
            new PropertyValueFactory<>("street"));
    TableColumn streetNumberCol = new TableColumn("Αριθμός");
    streetNumberCol.setMinWidth(100);
    streetNumberCol.setCellValueFactory(
            new PropertyValueFactory<>("streetNumber"));
    TableColumn demosCol = new TableColumn("Δήμος");
    demosCol.setMinWidth(100);
    demosCol.setCellValueFactory(
            new PropertyValueFactory<>("demos"));
    TableColumn tkCol = new TableColumn("T.K.");
    tkCol.setMinWidth(100);
    tkCol.setCellValueFactory(
            new PropertyValueFactory<>("tk"));
    TableColumn yearsBeforeCol = new TableColumn("Έτη υπηρεσίας σε άλλο εργοδότη");
    yearsBeforeCol.setMinWidth(100);
    yearsBeforeCol.setCellValueFactory(
            new PropertyValueFactory<>("yearsBefore"));
    TableColumn educationCol = new TableColumn("Εκπαίδευση");
    educationCol.setMinWidth(100);
    educationCol.setCellValueFactory(
            new PropertyValueFactory<>("education"));
    TableColumn marriageCol = new TableColumn("Γάμος");
    marriageCol.setMinWidth(100);
    marriageCol.setCellValueFactory(
            new PropertyValueFactory<>("marriage"));
    TableColumn childrenCol = new TableColumn("Αριθμός τέκνων");
    childrenCol.setMinWidth(100);
    childrenCol.setCellValueFactory(
            new PropertyValueFactory<>("children"));
    TableColumn amIkaCol = new TableColumn("A.M. IKA");
    amIkaCol.setMinWidth(100);
    amIkaCol.setCellValueFactory(
            new PropertyValueFactory<>("amIka"));
    TableColumn birthDateCol = new TableColumn("Ημερ. Γέννησης");
    birthDateCol.setMinWidth(100);
    birthDateCol.setCellValueFactory(
            new PropertyValueFactory<>("birthDate"));
    TableColumn firstHireDateCol = new TableColumn("Ημερ. πρώτης πρόσληψης");
    firstHireDateCol.setMinWidth(100);
    firstHireDateCol.setCellValueFactory(
            new PropertyValueFactory<>("firstHireDate"));
    TableColumn fatherNameCol = new TableColumn("Ονομα Πατρός");
    fatherNameCol.setMinWidth(100);
    fatherNameCol.setCellValueFactory(
            new PropertyValueFactory<>("fatherName"));
    TableColumn reasonIsforesCol = new TableColumn("Πακέτο Εισφορών ΙΚΑ");
    reasonIsforesCol.setMinWidth(100);
    reasonIsforesCol.setCellValueFactory(
            new PropertyValueFactory<>("reasonIsfores"));
    TableColumn tapitCol = new TableColumn("Πρόνοια Εργ/λων Μετάλλου");
    tapitCol.setMinWidth(100);
    tapitCol.setCellValueFactory(
            new PropertyValueFactory<>("tapit"));
    TableColumn subsidiaryCol = new TableColumn("Υποκατάστημα");
    subsidiaryCol.setMinWidth(100);
    subsidiaryCol.setCellValueFactory(
            new PropertyValueFactory<>("subsidiary"));
    TableColumn companyCol = new TableColumn("Εταιρία");
    companyCol.setMinWidth(100);
    companyCol.setCellValueFactory(
            new PropertyValueFactory<>("company"));
    TableColumn katAsfalisisCol = new TableColumn("Κατ.Ασφ.");
    katAsfalisisCol.setMinWidth(50);
    katAsfalisisCol.setCellValueFactory(
            new PropertyValueFactory<>("katAsfalisis"));
    TableColumn kentroKostousCol = new TableColumn("Κέντρο Κόστους");
    kentroKostousCol.setMinWidth(70);
    kentroKostousCol.setCellValueFactory(
            new PropertyValueFactory<>("kentroKostous"));
    TableColumn amEpikourikouCol = new TableColumn("Α.Μ. Επικουρικού");
    amEpikourikouCol.setMinWidth(70);
    amEpikourikouCol.setCellValueFactory(
            new PropertyValueFactory<>("amEpikourikou"));   
    TableColumn motherNameCol = new TableColumn("Όνομα Μητρός");
    motherNameCol.setMinWidth(100);
    motherNameCol.setCellValueFactory(
            new PropertyValueFactory<>("motherName"));
    TableColumn arParartCol = new TableColumn("ΑΡ.ΠΑΡΑΡΤ.");
    arParartCol.setMinWidth(30);
    arParartCol.setCellValueFactory(
            new PropertyValueFactory<>("arParart"));
    TableColumn kadCol = new TableColumn("ΚΑΔ");
    kadCol.setMinWidth(30);
    kadCol.setCellValueFactory(
            new PropertyValueFactory<>("kad"));
    TableColumn kodikosIdikotitasCol = new TableColumn("Κωδικός Ειδικότητας");
    kodikosIdikotitasCol.setMinWidth(30);
    kodikosIdikotitasCol.setCellValueFactory(
            new PropertyValueFactory<>("kodikosIdikotitas"));
    TableColumn idikiPeriptosiAsfalisisCol = new TableColumn("Ειδική περ. ασφ.");
    idikiPeriptosiAsfalisisCol.setMinWidth(30);
    idikiPeriptosiAsfalisisCol.setCellValueFactory(
            new PropertyValueFactory<>("idikiPeriptosiAsfalisis"));
    TableColumn apolisiCol = new TableColumn("Κατάστση εργαζ.");
    apolisiCol.setMinWidth(100);
    apolisiCol.setCellValueFactory(
            new PropertyValueFactory<>("apolisi"));
    TableColumn diakopiCol = new TableColumn("Ημερομηνία διακοπής");
    diakopiCol.setMinWidth(100);
    diakopiCol.setCellValueFactory(
            new PropertyValueFactory<>("diakopi"));
    TableColumn emailCol = new TableColumn("E-mail");
    emailCol.setMinWidth(200);
    emailCol.setCellValueFactory(
            new PropertyValueFactory<>("email"));
    
    table.setItems(data);
    table.getColumns().addAll(idCol, firstNameCol, lastNameCol, jobTitleCol,
            hireDateCol, salaryCol, relationCol, phone1Col, phone2Col, adtCol,
            amkaCol, afmCol, streetCol, streetNumberCol, demosCol, tkCol, 
            yearsBeforeCol, educationCol, marriageCol, childrenCol, amIkaCol, birthDateCol,
            firstHireDateCol, fatherNameCol, reasonIsforesCol, tapitCol, subsidiaryCol, companyCol,
            katAsfalisisCol, kentroKostousCol, amEpikourikouCol, motherNameCol, arParartCol, 
            kadCol, kodikosIdikotitasCol, idikiPeriptosiAsfalisisCol, apolisiCol, diakopiCol, emailCol);
       
    if (searchOnly==true)
     table.setTooltip(new Tooltip("Διπλό click για επιλογή"));        
    else
     table.setTooltip(new Tooltip("Διπλό click για επεξεργασία εργαζομένου"));
   
    table.setOnMouseClicked(new EventHandler<MouseEvent>() {
      
      @Override
      public void handle(MouseEvent e) {           
         if (e.getClickCount() == 2) {
          EmployeeGUIController.worker = table.getSelectionModel().getSelectedItem();
          if (searchOnly==true)
          { 
              employeegui.EmployeeGUI.selectIdStage.close();             
              return;          
          }
        try {
            /* this is the code of handleEnterData */
            EnterDataController.MultiRecord = "NO";     
            Stage enterDataStage = new Stage(StageStyle.DECORATED);                       
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EnterData.fxml"));        
            Parent root = (Parent)fxmlLoader.load();
            root.getStylesheets().clear();
            root.getStylesheets().add(EmployeeGUI.class.getResource("empl.css").toExternalForm());
            EnterDataController controller = fxmlLoader.<EnterDataController>getController();
            Scene scene = new Scene(root, 1050, 800);         
            enterDataStage.setScene(scene); 
            enterDataStage.show(); 
            
            controller.buttonPressed.addListener(k->{
                enterDataStage.close();
                data.set(table.getSelectionModel().getSelectedIndex(), controller.changedPerson);        
            });            
            
            } catch (Throwable ex) {
                showErrorAlert(ex.getMessage(),null,null);
                Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
            }              
          }      
        }
  });
 
 /*
    updateButton = new Button("Επεξεργασία Εργαζομένου");    
    updateButton.setOnAction(e->{
        //show_Information_Alert("worker id is "+table.getSelectionModel().getSelectedItem().getId(),null,null);   
        EmployeeGUIController.worker_id = table.getSelectionModel().getSelectedItem().getId();
        try {
            // this is the code of handleEnterData 
            EnterDataController.MultiRecord = "NO";     
            Stage enterDataStage = new Stage(StageStyle.DECORATED);                       
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("EnterData.fxml"));        
            Parent root = (Parent)fxmlLoader.load();
            root.getStylesheets().clear();
            root.getStylesheets().add(EmployeeGUI.class.getResource("empl.css").toExternalForm());
            EnterDataController controller = fxmlLoader.<EnterDataController>getController();
            Scene scene = new Scene(root, 1050, 800);         
            enterDataStage.setScene(scene); 
            enterDataStage.show(); 
            
            controller.buttonPressed.addListener(k->{
            enterDataStage.close();
            data.set(table.getSelectionModel().getSelectedIndex(), controller.changedPerson);        
            });            
            
            } catch (Throwable ex) {
                showErrorAlert(ex.getMessage(),null,null);
                Logger.getLogger(DBConnect.class.getName()).log(Level.SEVERE, null, ex);
            }
    });    
    */
                    
    table.setPrefSize(4000, 500);
    final ScrollPane sp = new ScrollPane();
    sp.setPrefSize(4000, 500);
    sp.setContent(table);
    
    final VBox vbox = new VBox();
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 0, 0, 10));
    vbox.setAlignment(Pos.CENTER);
    //vbox.getChildren().addAll(label,sp,updateButton);
    vbox.getChildren().addAll(label,sp);
    
   return vbox ; 
}
   
}
