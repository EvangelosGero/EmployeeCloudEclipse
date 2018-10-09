/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import static employeegui.Alerts.showDialogYES_NO;
import static employeegui.Alerts.showErrorAlert;
import static employeegui.TimerIntervalComparator.DATE_ORDER;
import java.net.URL;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Home
 */
public class TimeIntervalController implements Initializable {
    
    @FXML
    private Button ShowTable;
    @FXML
    private DatePicker FromDatePicker;
    @FXML
    private DatePicker ToDatePicker;    
   
    private PreparedStatement stm;
    private ResultSet rs;
    private LocalTime morning;
    private LocalTime evening;
    private LocalDateTime morningStart;
    private LocalDateTime eveningEnd;
    //private int timerid;
    int id;
    @FXML
    private ChoiceBox<Integer> idChoiceBox;
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private TextField fatherNameTextField;        
    @FXML
    private CheckBox discrepCheckBox;
    
    private TextField timer_idLabel = null;           
    @FXML
    private CheckBox duplicatesCheckBox;
    protected static ListProperty<String> listProperty = new SimpleListProperty<>();
   
    List<TimerInterval> list = new ArrayList();
        ObservableList<TimerInterval> data  = FXCollections.observableList(list); 
    
    private Statement stm1;
    private ResultSet rs1;

            
    /**
     * Initializes the controller class.
     */
   @Override
    public void initialize(URL url, ResourceBundle rb) {
        new utilities.ChoiceBoxRoller().fillChoiceBox(idChoiceBox, nameTextField, fatherNameTextField, lastNameTextField);
        FromDatePicker.setConverter(utilities.DateFormatterGreek.converter);
        ToDatePicker.setConverter(utilities.DateFormatterGreek.converter);
        FromDatePicker.setPromptText(utilities.DateFormatterGreek.promptText);
        FromDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
          if (data!=null)data.clear(); 
        });
        ToDatePicker.setPromptText(utilities.DateFormatterGreek.promptText);
        ToDatePicker.valueProperty().addListener((ov, oldValue, newValue) -> {
          if (data!=null)data.clear();  
        });   
    } 
    
    @FXML
    private void handleShowTable(ActionEvent event) {
        TableView<TimerInterval> table = new TableView<>();
        
      //  List<TimerInterval> list = new ArrayList();
      //  ObservableList<TimerInterval> data  = FXCollections.observableList(list); 
        if (data!=null)data.clear(); 
        try {
            LocalDate beginLocalDate = null;
            LocalDate endLocalDate = null;
            Date beginDate = null;
            Date endDate = null;
            beginLocalDate =  FromDatePicker.getValue();
            endLocalDate = ToDatePicker.getValue(); 
        
                    
            /*if ((beginLocalDate == null) || (endLocalDate == null)){
             Alerts.showInformationAlert("Παρακαλώ συμπληρώστε απο - εως ημερομηνία!", null, "ΕΙΔΟΠΟΙΗΣΗ");
             return;
            } */  
            
            if (discrepCheckBox.isSelected() && duplicatesCheckBox.isSelected()){
             Alerts.showInformationAlert("Παρακαλώ επιλέξτε μόνο ενα απο τα δυο φίλτρα ημερομηνιών!", null, "ΕΙΔΟΠΟΙΗΣΗ");
             return;
            }      
            if(beginLocalDate != null)beginDate =  Date.valueOf(beginLocalDate); /*Convert LocalDate to java.sql.Date*/            
            if(endLocalDate != null)endDate = Date.valueOf(endLocalDate);
            id = idChoiceBox.getValue();
                       
            String str = "SELECT workers.first_name, workers.last_name, timer.starttime, "+
                    "timer.endtime, timer.interval_time, timer.id as timer_id "+
                    "FROM  timer JOIN workers on timer.code = workers.code "+                    
                    "WHERE (DATE(timer.starttime) BETWEEN ? AND ?) AND (workers.id = ?)";     
            if (discrepCheckBox.isSelected()) 
                str = str + "and date(starttime) <> date(endtime)";      
            if (duplicatesCheckBox.isSelected()) 
                str = str + "and date(starttime) in "+
                "(select sdate from"+
                "(select a.code,a.sdate,count(a.sdate) as cnt from "+
                "(select timer.code,date(starttime) as sdate from timer join workers on timer.code = workers.code "+
                "where date(starttime) between '"+beginDate+"' and '"+endDate+"' and workers.id="+Integer.toString(id)+" "+
                "order by timer.code,date(starttime)) a "+ 
                "group by a.code,a.sdate) b "+ 
                "where b.cnt>1)";
            
            stm = EmployeeGUI.con.prepareStatement(str);
            stm.setDate(1, beginDate);
            stm.setDate(2, endDate);
            stm.setInt(3, id);
            rs = stm.executeQuery();
             
        /* render the TableView */            
                         
        while(rs.next()){
            data.add(new TimerInterval(rs.getString(1), rs.getString(2), rs.getString(3), 
            rs.getString(4), (rs.getInt(5)/60000)-480, rs.getInt(6)));
                        }
        Collections.sort(data, DATE_ORDER);
            } catch (SQLException ex) {
            Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }
        finally{
            try {
                if(rs != null)rs.close();
                if(stm != null)stm.close();                
            } catch (SQLException ex) {
                Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }
        }
        final Label label = new Label("ΔΙΑΣΤΗΜΑ ΠΑΡΟΥΣΙΑΣ ΕΡΓΑΖΟΜΕΝΟΥ");
        label.setFont(new Font("Arial",20));
       if(!data.isEmpty()){
        table.setEditable(true);    
           
        TableColumn firstNameCol = new TableColumn("Ονομα");
        firstNameCol.setMinWidth(150);
        firstNameCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,String>("firstName"));
    
        TableColumn lastNameCol = new TableColumn("Επώνυμο");
        lastNameCol.setMinWidth(150);
        lastNameCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,String>("lastName"));
    
        TableColumn startTimeCol = new TableColumn("Είσοδος");
        startTimeCol.setMinWidth(150);
        startTimeCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,String>("startTime"));
    
        TableColumn endTimeCol = new TableColumn("Έξοδος");
        endTimeCol.setMinWidth(150);
        endTimeCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,String>("endTime"));
    
        TableColumn intervalCol = new TableColumn("Περικοπές ή Υπερωρίες (λεπτά)");
        intervalCol.setMinWidth(200);
        intervalCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,Integer>("interval"));        
        
        TableColumn timerIdCol = new TableColumn("Timer ID)");
        timerIdCol.setMinWidth(50);
        timerIdCol.setCellValueFactory(
            new PropertyValueFactory<PersonTimer,String>("timerId"));                
                    
    table.setItems(data);    
    table.setEditable(true);
    table.getColumns().addAll(firstNameCol, lastNameCol, startTimeCol, endTimeCol, 
            intervalCol,timerIdCol);
    table.setTooltip(new Tooltip("Διπλό click για επεξεργασία"));
    table.setOnMouseClicked(new EventHandler<MouseEvent>() {
        
        public void handle(MouseEvent e) {           
          if (e.getClickCount() == 2) {
            LocalDate date = LocalDate.parse(table.getSelectionModel().getSelectedItem().
                        getStartTime().substring(0, 10));
            morning = LocalTime.of(8, 0);
            evening = LocalTime.of(16, 0);
            morningStart = LocalDateTime.of(date, morning);
            eveningEnd = LocalDateTime.of(date, evening);                                                       
            Stage popup = new Stage(StageStyle.DECORATED);
            Button btn1 = new Button ("ΚΑΤΑΧΩΡΙΣΗ");
            final ToggleGroup toggleGroup = new ToggleGroup();
            RadioButton rb4 = new RadioButton("Διόρθωση σε 8ωρο βάσει ώρας Εισόδου");
            RadioButton rb5 = new RadioButton("Διόρθωση σε 8ωρο βάσει ώρας Εξόδου");
            RadioButton rb6 = new RadioButton("Επανυπολογισμός Περικοπών/Υπερωριών");
            RadioButton rb1 = new RadioButton("Διόρθωση σε 8ωρο 08:00-16:00");
            RadioButton rb2 = new RadioButton("Νέα εγγραφή");
            RadioButton rb3 = new RadioButton("Διαγραφή");
            rb1.setToggleGroup(toggleGroup);
            //rb1.setSelected(true);
            rb2.setToggleGroup(toggleGroup);
            rb3.setToggleGroup(toggleGroup);
            rb4.setToggleGroup(toggleGroup);
            rb5.setToggleGroup(toggleGroup);
            rb6.setToggleGroup(toggleGroup);
            btn1.setOnAction(b1 ->{
                if (toggleGroup.getSelectedToggle() == null) {
                 Alerts.showErrorAlert("Παρακαλώ κάνετε μια επιλογή !",null,null); 
                 return;
                }else if (toggleGroup.getSelectedToggle() == rb1){
                    try{                                /*Διόρθωσε σε 8ωρο 08:00-16:00*/
                    String str = "UPDATE timer SET starttime = ?, endtime = ?, "
                      + "interval_time = 28800000 WHERE code = (SELECT DISTINCT (timer.code)" +
                        " FROM timer, workers WHERE timer.code = workers.code AND" +
                        " workers.id = ?) AND starttime = ?  ";                                              
                    stm = EmployeeGUI.con.prepareStatement(str);                    
                    stm.setTimestamp(1, Timestamp.valueOf(morningStart));                            ;
                    stm.setTimestamp(2, Timestamp.valueOf(eveningEnd));
                    stm.setInt(3, id); 
                    stm.setTimestamp(4, Timestamp.valueOf
                        (table.getSelectionModel().getSelectedItem().getStartTime()));
                   
                    int ok2 = stm.executeUpdate(); 
                    /* show in table automatically*/
                    data.set(table.getSelectionModel().getSelectedIndex(), 
                            new TimerInterval(table.getSelectionModel().getSelectedItem().getFirstName(), 
                            table.getSelectionModel().getSelectedItem().getLastName(), 
                            Timestamp.valueOf(morningStart).toString(),
                            Timestamp.valueOf(eveningEnd).toString(),
                            0,0));                  
                                    
                    } catch (SQLException ex) {
                    Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorStage.showErrorStage(ex.getMessage());
                    }
                    finally{
                        try {
                            if (rs != null)rs.close();
                            if (stm != null)stm.close();                            
                            } catch (SQLException ex) {
                                Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                                ErrorStage.showErrorStage(ex.getMessage());
                        }
                    }                        
                }else if (toggleGroup.getSelectedToggle() == rb2){
                    try{                              /*Νέα εγγραφή */
                    String str = "INSERT INTO timer(code, starttime, endtime, interval_time, "
                            + "pc_name_in, pc_ip_in, pc_name_out, pc_ip_out) "
                            + "VALUES ((SELECT DISTINCT (timer.code)" +
                        " FROM timer, workers WHERE timer.code = workers.code AND" +
                        " workers.id = ?), ?, ?, ?, null, null, null, null) ";                                          
                    stm = EmployeeGUI.con.prepareStatement(str);
                    stm.setInt(1, id);
                    stm.setTimestamp(2, Timestamp.valueOf(morningStart));                            ;
                    stm.setTimestamp(3, Timestamp.valueOf(eveningEnd));
                    long timeBetween = Duration.between(morningStart, eveningEnd).toMillis();
                    stm.setLong(4, timeBetween);
                    int ok1 = stm.executeUpdate();
                    TimerInterval newItem = new TimerInterval();
                    newItem.setFirstName(table.getSelectionModel().getSelectedItem().getFirstName());
                    newItem.setLastName(table.getSelectionModel().getSelectedItem().getLastName());
                    newItem.setStartTime(Timestamp.valueOf(morningStart).toString());
                    newItem.setEndTime(Timestamp.valueOf(eveningEnd).toString());
                    newItem.setInterval(0);
                    data.add(newItem);
                    Collections.sort(data, DATE_ORDER);
                    
                    } catch (SQLException ex) {
                    Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorStage.showErrorStage(ex.getMessage());
                    }
                    finally{
                        try {
                            if (rs != null)rs.close();
                            if (stm != null)stm.close();                           
                            } catch (SQLException ex) {
                                Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                                ErrorStage.showErrorStage(ex.getMessage());
                        }
                    }        
                   
                }else if (toggleGroup.getSelectedToggle() == rb3){
                    try{                                            /*Διαγραφή*/
                        
                    if (showDialogYES_NO("Θα γίνει διαγραφή της συγκεκριμένης εγγραφής timer. Θέλετε να συνεχίσετε?",null,null)!= ButtonType.YES) return;     
                    
                    String str = "DELETE FROM timer WHERE starttime = ?" +
                        " AND endtime = ? AND code = (SELECT DISTINCT (timer.code)" +
                        " FROM timer, workers WHERE timer.code = workers.code AND" +
                        " workers.id = ?)";                                         
                    stm = EmployeeGUI.con.prepareStatement(str);
                    stm.setTimestamp(1, Timestamp.valueOf
                        (table.getSelectionModel().getSelectedItem().getStartTime()));
                    stm.setTimestamp(2, Timestamp.valueOf
                        (table.getSelectionModel().getSelectedItem().getEndTime()));
                    stm.setInt(3, id);
                    int ok = stm.executeUpdate(); 
                    data.remove(table.getSelectionModel().getSelectedItem());
                    } catch (SQLException ex) {
                    Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorStage.showErrorStage(ex.getMessage());
                    }
                    finally{
                        try {
                            if (rs != null)rs.close();
                            if (stm != null)stm.close();                            
                            } catch (SQLException ex) {
                                Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                                ErrorStage.showErrorStage(ex.getMessage());
                        }
                    }        
                } else if ((toggleGroup.getSelectedToggle() == rb4) || (toggleGroup.getSelectedToggle() == rb5)){
                    try{         
                       if (toggleGroup.getSelectedToggle() == rb4)
                       {                                            /*Διόρθωσε σε 8ωρο βάσει ώρας Εισόδου*/
                        if (showDialogYES_NO("Θα γίνει διόρθωση σε 8ωρο βάσει ώρας Εισόδου. Θέλετε να συνεχίσετε?",null,"Ερώτηση")!= ButtonType.YES) return; 
              
                        String str ="update timer set ENDTIME= {fn TIMESTAMPADD(SQL_TSI_HOUR,8,STARTTIME)},"+
                        "INTERVAL_TIME = {fn TIMESTAMPDIFF(SQL_TSI_FRAC_SECOND,STARTTIME,{fn TIMESTAMPADD(SQL_TSI_HOUR,8,STARTTIME)})}*0.000001"+
                        "where timer.id="+timer_idLabel.getText();                           
                        stm = EmployeeGUI.con.prepareStatement(str);                                      
                       }else if (toggleGroup.getSelectedToggle() == rb5)
                       {                                                /*Διόρθωσε σε 8ωρο βάσει ώρας Εξόδου*/
                        if (showDialogYES_NO("Θα γίνει διόρθωση σε 8ωρο βάσει ώρας Εξόδου. Θέλετε να συνεχίσετε?",null,"Ερώτηση")!= ButtonType.YES) return; 
              
                        String str ="update timer set STARTTIME= {fn TIMESTAMPADD(SQL_TSI_HOUR,-8,ENDTIME)},"+
                        "INTERVAL_TIME = {fn TIMESTAMPDIFF(SQL_TSI_FRAC_SECOND,{fn TIMESTAMPADD(SQL_TSI_HOUR,-8,ENDTIME)},ENDTIME)}*0.000001"+
                        "where timer.id="+timer_idLabel.getText();                                                   
                        stm = EmployeeGUI.con.prepareStatement(str);                                                              
                       }
                    int ok2 = stm.executeUpdate();
                    
                   try{
                    String query1 = "SELECT STARTTIME,ENDTIME,INTERVAL_TIME from timer where timer.id="+timer_idLabel.getText();
                    stm1 = EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    rs1 = stm1.executeQuery(query1);           
                    if (!rs1.next()){
                        showErrorAlert("Πρόβλημα ανάκτησης εγγραφής timer με ID="+timer_idLabel.getText(),null,null);
                    }            
                    } catch (SQLException ex) {
                       Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                       ErrorStage.showErrorStage(ex.getMessage());
                    }
                    // show in table automatically
                                       
                    data.set(table.getSelectionModel().getSelectedIndex(), 
                            new TimerInterval(table.getSelectionModel().getSelectedItem().getFirstName(), 
                            table.getSelectionModel().getSelectedItem().getLastName(), 
                            rs1.getString("STARTTIME"),
                            rs1.getString("ENDTIME"),
                            (rs1.getInt("INTERVAL_TIME")/60000)-480,
                            table.getSelectionModel().getSelectedItem().getTimerId()));                                                      
                    } catch (SQLException ex) {
                    Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorStage.showErrorStage(ex.getMessage());
                    }
                    finally{
                        try {
                            if (rs != null)rs.close();
                            if (rs1 != null)rs1.close();
                            if (stm != null)stm.close();                            
                            if (stm1 != null)stm1.close();                            
                            } catch (SQLException ex) {
                                Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                                ErrorStage.showErrorStage(ex.getMessage());
                        }
                    }                        
                }
                else if (toggleGroup.getSelectedToggle() == rb6){
                    try{         
                       if (toggleGroup.getSelectedToggle() == rb6)
                       {                                            /*Επανυπολογισμός Περικοπών/Υπερωριών*/
                        if (showDialogYES_NO("Θα γίνει επανυπολογισμός Περικοπών/Υπερωριών για τη συγκεκριμένη μέρα. Θέλετε να συνεχίσετε?",null,"Ερώτηση")!= ButtonType.YES) return; 
              
                        String str ="update timer set INTERVAL_TIME = {fn TIMESTAMPDIFF(SQL_TSI_FRAC_SECOND,STARTTIME,ENDTIME)}*.000001 "+
                        "where timer.id="+timer_idLabel.getText();                           
                        stm = EmployeeGUI.con.prepareStatement(str);                                      
                       }
                    int ok2 = stm.executeUpdate();

                   try{
                    String query1 = "SELECT INTERVAL_TIME from timer where timer.id="+timer_idLabel.getText();
                    stm1 = EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    rs1 = stm1.executeQuery(query1);           
                    if (!rs1.next()){
                      showErrorAlert("Πρόβλημα ανάκτησης εγγραφής timer με ID="+timer_idLabel.getText(),null,null);
                    }            
                    } catch (SQLException ex) {
                       Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                       ErrorStage.showErrorStage(ex.getMessage());
                    }
                    // show in table automatically
                    data.set(table.getSelectionModel().getSelectedIndex(), 
                            new TimerInterval(table.getSelectionModel().getSelectedItem().getFirstName(), 
                            table.getSelectionModel().getSelectedItem().getLastName(), 
                            table.getSelectionModel().getSelectedItem().getStartTime(),
                            table.getSelectionModel().getSelectedItem().getEndTime(),
                            (rs1.getInt("INTERVAL_TIME")/60000)-480,        
                            //table.getSelectionModel().getSelectedItem().getInterval(),
                            table.getSelectionModel().getSelectedItem().getTimerId()));                  
                                    
                    } catch (SQLException ex) {
                    Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorStage.showErrorStage(ex.getMessage());
                    }
                    finally{
                        try {
                            if (rs != null)rs.close();
                            if (rs1 != null)rs1.close();                            
                            if (stm != null)stm.close();                            
                            if (stm1 != null)stm1.close();                                                        
                            } catch (SQLException ex) {
                                Logger.getLogger(TimeIntervalController.class.getName()).log(Level.SEVERE, null, ex);
                                ErrorStage.showErrorStage(ex.getMessage());
                        }
                    }                        
                }
                
            popup.close();
                });
            Button btn2 = new Button ("ΑΚΥΡΩΣΗ");
            btn2.setOnAction(b2 ->{
                popup.close();
                });
            HBox hBox = new HBox(450, btn1, btn2);
            TextField idLabel = new TextField(Integer.toString(id));
            idLabel.setEditable(false);

            DatePicker datePicker = new DatePicker(date);
            datePicker.setDisable(true);

            TextField startLabel = new TextField();
            if (toggleGroup.getSelectedToggle() == rb2)
             startLabel.setText(Timestamp.valueOf(morningStart).toString());
            else
             startLabel.setText(table.getSelectionModel().getSelectedItem().getStartTime());
            startLabel.setEditable(false);            

            TextField endLabel = new TextField();
            if (toggleGroup.getSelectedToggle() == rb2) 
                endLabel.setText(Timestamp.valueOf(eveningEnd).toString());
            else
             endLabel.setText(table.getSelectionModel().getSelectedItem().getEndTime());
            endLabel.setEditable(false);                        
            
            timer_idLabel = new TextField(Integer.toString(table.getSelectionModel().getSelectedItem().getTimerId()));
            timer_idLabel.setEditable(false);
            
            HBox hBoxLabels = new HBox(10, idLabel, datePicker, startLabel, endLabel, timer_idLabel); 
            VBox vBox = new VBox(20, new Label("ΔΥΝΑΤΟΤΗΤΑ ΑΛΛΑΓΗΣ ΡΟΛΟΓΙΟΥ"), rb4, rb5, rb6, rb1, rb2, rb3, hBoxLabels,
                        hBox);
            vBox.setPadding(new Insets(10, 10, 10, 10));
            vBox.setAlignment(Pos.TOP_LEFT);
            toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
                public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle,
                            Toggle new_toggle){
                    if (toggleGroup.getSelectedToggle() != null) {
                        if (toggleGroup.getSelectedToggle() == rb1){
                            datePicker.setValue(date);
                            datePicker.setDisable(true);     
                            morning = LocalTime.of(8, 0);
                            evening = LocalTime.of(16, 0);
                            morningStart = LocalDateTime.of(date, morning);
                            eveningEnd = LocalDateTime.of(date, evening);
                            startLabel.setText(Timestamp.valueOf(morningStart).toString());
                            endLabel.setText(Timestamp.valueOf(eveningEnd).toString());
                        }else if (toggleGroup.getSelectedToggle() == rb2){
                            datePicker.setDisable(false);
                            datePicker.valueProperty().addListener(new ChangeListener<LocalDate>(){
                                public void changed (ObservableValue<? extends LocalDate> ov, 
                                   LocalDate oldDate, LocalDate newDate) {                                    
                                    morning = LocalTime.of(8, 0);
                                    evening = LocalTime.of(16, 0);
                                    morningStart = LocalDateTime.of(newDate, morning);
                                    eveningEnd = LocalDateTime.of(newDate, evening);
                                    startLabel.setText(Timestamp.valueOf(morningStart).toString());
                                    endLabel.setText(Timestamp.valueOf(eveningEnd).toString());
                                }
                            });
                            
                        }else if (toggleGroup.getSelectedToggle() == rb3){
                            datePicker.setValue(date);
                            datePicker.setDisable(true);
                            startLabel.setText(table.getSelectionModel().getSelectedItem().getStartTime());
                            startLabel.setEditable(false);
                            endLabel.setText(table.getSelectionModel().getSelectedItem().getEndTime());
                            endLabel.setEditable(false);                        
                         }
                        else if (toggleGroup.getSelectedToggle() == rb4){
                            //datePicker.setValue(date);
                            //datePicker.setDisable(true);     
                            //startLabel.setDisable(false);     
                            //endLabel.setDisable(false);     
                            //startLabel.setText(Timestamp.valueOf(morningStart).toString());
                            //endLabel.setText(Timestamp.valueOf(eveningEnd).toString());
                        }
                    }   
                    }
                });
                Scene scene = new Scene(vBox, 870, 350);                
                popup.setScene(scene);
                popup.showAndWait();
            }
         }
        });
    
    
    final ScrollPane sp = new ScrollPane();
    sp.setPrefSize(800, 650);    
    sp.setContent(table);
    
    final VBox vbox = new VBox();
    vbox.setSpacing(5);
    vbox.setPadding(new Insets(10, 10, 10, 10));
    vbox.setAlignment(Pos.CENTER);
    vbox.getChildren().addAll(label,sp);
               
    Scene scene = new Scene(vbox); 
    Stage stage = new Stage(StageStyle.DECORATED);
    stage.setScene(scene);    

    stage.show();   
            
       
    }else{
         //Alerts.showInformationAlert("Παρακαλώ τοποθετείστε έγκυρες παραμέτρους", null, "ΕΙΔΟΠΟΙΗΣΗ");
         Alerts.showInformationAlert("Δεν βρέθηκαν στοιχεία !", null, "ΕΙΔΟΠΟΙΗΣΗ");
        } 
    }
}
