/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package employeegui;

import ValidationWithBindings.Validation;
import static employeegui.Alerts.showDialogOK_CANCEL;
import static employeegui.Alerts.showErrorAlert;
import static employeegui.Alerts.showInformationAlert;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Home
 */
public class EnterDataController implements Initializable {
    
    //public Connection con = null;
    public static String MultiRecord;
    private Statement stm, stm1 = null;
    private ResultSet rs, rs1 = null;
    private int curRow = 0;  
    private final Text msg = new Text();
    private final Text msg2 = new Text();
    private String strQry ;
    private PreparedStatement prs;
    private final Map<Integer, String> subsidiariesMap = new HashMap<>();
    private final Map<Integer, String> companiesMap = new HashMap<>();
    public BooleanProperty buttonPressed = new SimpleBooleanProperty();
    public Person changedPerson;
    
    @FXML
    private TextField identif;
    @FXML
    private TextField fn;
    @FXML
    private TextField ln;
    @FXML
    private TextField job;
    @FXML
    private DatePicker hire;
    @FXML
    private TextField sal;
    @FXML
    private Button firstBtn;
    @FXML
    private Button previousBtn;
    @FXML
    private Button nextBtn;
    @FXML
    private Button lastBtn;
    @FXML
    private Button updateBtn;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button newBtn;
    @FXML
    private Button saveBtn;
    @FXML
    private Button cancelBtn;
    
    @FXML
    private GridPane gp;
    @FXML
    private HBox msgHBox;
    @FXML
    private TextField codeTextField;
    @FXML
    private TextField Phone1Textfield;
    @FXML
    private ChoiceBox<String> RelationChoiceBox;
    @FXML
    private TextField Phone2TextField;
    @FXML
    private TextField StreetTextField;
    @FXML
    private TextField ExperienceTextField;
    @FXML
    private TextField ADTTextField;
    @FXML
    private TextField StreetNumberTextField;
    @FXML
    private TextField AMKATextField;
    @FXML
    private TextField DemosTextField;
    @FXML
    private TextField TKTextField;
    @FXML
    private TextField AFMTextField;
    @FXML
    private TextField ChildrenTextfield;
    @FXML
    private ChoiceBox<String> education;
    @FXML
    private ChoiceBox<String> marriage;
    @FXML
    private TextField amIkaTextField;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private DatePicker firstEverHireDatePicker;
    @FXML
    private TextField fatherName;
    @FXML
    private ChoiceBox<String> packageIKAChoiceBox;
    @FXML
    private ChoiceBox<String> tapitChoiceBox;
    @FXML
    private ChoiceBox<String> subsidiaryChoiceBox;
    @FXML
    private ChoiceBox<String> companyChoiceBox;
    @FXML
    private TextField katAsfalTextField;
    @FXML
    private TextField amEpikourikouTextField;
    @FXML
    private TextField kentroKostousTextField;
    @FXML
    private TextField motherNameTextField;
    @FXML
    private TextField arParartTextField;
    @FXML
    private TextField kadTextField;
    @FXML
    private TextField kodikosIdikotitasTextField;
    @FXML
    private TextField idikiPeriptosiAsfalisisTextField;
    @FXML
    private ChoiceBox<String> katastasiChoiceBox;
    @FXML
    private DatePicker diakopiDatePicker;
    @FXML
    private TextField emailTextField;
    
    private BooleanProperty updateBtnDisable = new SimpleBooleanProperty(Boolean.FALSE);
    private BooleanProperty saveBtnDisable = new SimpleBooleanProperty(Boolean.TRUE);
    private StringProperty  stringObservable = new SimpleStringProperty("");
    
    private final String pattern = "dd-MM-uuuu";
    private final String promptText = "dd-MM-yyyy";
    
    
    
    private final StringConverter converter = new StringConverter<LocalDate>() {
                DateTimeFormatter dateFormatter = 
                    DateTimeFormatter.ofPattern(pattern);
                @Override
                public String toString(LocalDate date) {
                    if (date != null) {
                        return dateFormatter.format(date);
                    } else {
                        return "";
                    }
                }
                @Override
                public LocalDate fromString(String string) {
                    if (string != null && !string.isEmpty()) {
                        return LocalDate.parse(string, dateFormatter);
                    } else {
                        return null;
                    }
                }
            };         
    
     
    @Override
    public void initialize(URL url, ResourceBundle rb)  {
        try{ 
            identif.setDisable(true);
            // Add Validation using Bindings            
            BooleanBinding firstNameInvalid = Validation.emptyTextFieldBinding(fn, "Συμπληρώστε όνομα");
            BooleanBinding lastNameInvalid = Validation.emptyTextFieldBinding(ln, "Συμπληρώστε όνομα");
            BooleanBinding jobInvalid = Validation.emptyTextFieldBinding(job, "Συμπληρώστε ειδικότητα");
            Pattern salaryPattern = Pattern.compile("\\d{1,6}\\.?\\d{0,2}");
            BooleanBinding salInvalid = Validation.patternTextFieldBinding(sal, salaryPattern, 
                    "Συμπληρώστε μισθό έως 6 κύρια και έως 2 δεκαδικά με διαχωριστικό τελεία");
            Pattern codePattern = Pattern.compile("\\w{5}");
            BooleanBinding codeInvalid = Validation.patternTextFieldBinding(codeTextField, codePattern, 
                    "Συμπληρώστε κωδικό πρόσβασης με ακριβώς 5 αλφαριθμητικά ψηφία");
            BooleanBinding adtInvalid = Validation.emptyTextFieldBinding(ADTTextField, "Συμπληρώστε Αριθμό Ταυτότητας");
            Pattern numericAndNonemptyPattern = Pattern.compile("\\d+");
            BooleanBinding amkaInvalid = Validation.patternTextFieldBinding(AMKATextField, numericAndNonemptyPattern, 
                    "Συμπληρώστε ΑΜΚΑ με αριθμητικά ψηφία");
            BooleanBinding afmInvalid = Validation.patternTextFieldBinding(AFMTextField, numericAndNonemptyPattern, 
                    "Συμπληρώστε ΑΦΜ με αριθμητικά ψηφία");
            BooleanBinding streetInvalid = Validation.emptyTextFieldBinding(StreetTextField, "Συμπληρώστε οδό");
            Pattern oneOrTwoDigitsPattern = Pattern.compile("\\d{1,2}");
            BooleanBinding experienceInvalid = Validation.patternTextFieldBinding(ExperienceTextField, oneOrTwoDigitsPattern, 
                    "Συμπληρώστε προυπηρεσία με έως 2 αριθμητικά ψηφία ή το μηδέν");
            BooleanBinding childrenInvalid = Validation.patternTextFieldBinding(ChildrenTextfield, oneOrTwoDigitsPattern, 
                    "Συμπληρώστε αριθμό τέκνων με έως 2 αριθμητικά ψηφία ή το μηδέν");
            BooleanBinding amIKAInvalid = Validation.patternTextFieldBinding(amIkaTextField, numericAndNonemptyPattern, 
                    "Συμπληρώστε Α.Μ. ΙΚΑ με αριθμητικά ψηφία");
            BooleanBinding kodEidikInvalid = Validation.patternTextFieldBinding(kodikosIdikotitasTextField, numericAndNonemptyPattern, 
                    "Συμπληρώστε κωδικό ειδικότητας με αριθμητικά ψηφία");
            BooleanBinding fatherNameInvalid = Validation.emptyTextFieldBinding(fatherName, "Συμπληρώστε όνομα πατρός");
            BooleanBinding motherNameInvalid = Validation.emptyTextFieldBinding(motherNameTextField, "Συμπληρώστε όνομα μητρός");
            BooleanBinding hireInvalid = Validation.emptyDatePickerBinding(hire, "Συμπληρώστε ημερομηνία");
            BooleanBinding birthDatePickerInvalid = Validation.emptyDatePickerBinding(birthDatePicker, "Συμπληρώστε ημερομηνία");
            BooleanBinding firstEverHireDatePickerInvalid = Validation.emptyDatePickerBinding(firstEverHireDatePicker, "Συμπληρώστε ημερομηνία");
            Pattern emailPattern = Pattern.compile("[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}", Pattern.CASE_INSENSITIVE);      
            BooleanBinding emailInvalid = Validation.patternTextFieldBinding(emailTextField, emailPattern, "Συμπληρώστε έγκυρο E-mail");
            Pattern twoOrZeroDigitsPattern = Pattern.compile("^$|\\d{2}");
            BooleanBinding idikiPeriptosiAsfalisisInvalid = Validation.patternTextFieldBinding(idikiPeriptosiAsfalisisTextField, twoOrZeroDigitsPattern, 
                    "Συμπληρώστε ειδική περίπτωση ασφάλισης  με 2 αριθμητικά ψηφία ή αφείστε την κενή");
                    
            msg2.setFill(Color.FIREBRICK);
             msg2.setText("Παρακαλώ συμπληρώστε το κάθε υποχρεωτικό πεδίο.\nΤοποθετήστε τον κέρσορα πάνω στο πεδίο,για περισσότερες πληροφορίες");
            msgHBox.setSpacing(20);
            msgHBox.getChildren().addAll(msg, msg2);
            msg2.visibleProperty().bind(firstNameInvalid.or(lastNameInvalid).or(jobInvalid).or(salInvalid).
                    or(codeInvalid).or(adtInvalid).or(amkaInvalid).or(afmInvalid).or(streetInvalid).or(experienceInvalid).
                    or(childrenInvalid).or(amIKAInvalid).or(kodEidikInvalid).or(fatherNameInvalid).or(motherNameInvalid)
                    .or(hireInvalid).or(birthDatePickerInvalid).or(firstEverHireDatePickerInvalid).or(emailInvalid).
                    or(idikiPeriptosiAsfalisisInvalid)); 
            msg.setFill(Color.FIREBRICK);
            msg.textProperty().bind(stringObservable);
            
            updateBtn.disableProperty().bind(firstNameInvalid.or(updateBtnDisable).or(lastNameInvalid).or(jobInvalid).
                    or(salInvalid).or(codeInvalid).or(adtInvalid).or(amkaInvalid).or(afmInvalid).or(streetInvalid).
                    or(experienceInvalid).or(childrenInvalid).or(amIKAInvalid).or(kodEidikInvalid).
                    or(fatherNameInvalid).or(motherNameInvalid).or(hireInvalid).or(birthDatePickerInvalid)
                    .or(firstEverHireDatePickerInvalid).or(emailInvalid).or(idikiPeriptosiAsfalisisInvalid));
            
            saveBtn.disableProperty().bind(firstNameInvalid.or(saveBtnDisable).or(lastNameInvalid).or(jobInvalid).
                    or(salInvalid).or(codeInvalid).or(adtInvalid).or(amkaInvalid).or(afmInvalid).or(streetInvalid).
                    or(experienceInvalid).or(childrenInvalid).or(amIKAInvalid).or(kodEidikInvalid).
                    or(fatherNameInvalid).or(motherNameInvalid).or(hireInvalid).or(birthDatePickerInvalid)
                    .or(firstEverHireDatePickerInvalid).or(emailInvalid).or(idikiPeriptosiAsfalisisInvalid));
            
            
            //Add DatePicker converters and promptText
            
            hire.setConverter(converter);
            birthDatePicker.setConverter(converter);
            firstEverHireDatePicker.setConverter(converter);
            diakopiDatePicker.setConverter(converter);
            hire.setPromptText(promptText);
            birthDatePicker.setPromptText(promptText);
            firstEverHireDatePicker.setPromptText(promptText);
            diakopiDatePicker.setPromptText(promptText);
            
            //con = (new EstablishConnection()).EstablishDBConnection();            
            stm = EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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
         } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());           
            
        }finally{
             try {
                 if (rs != null) rs.close();
                 if (stm != null) stm.close();                 
             } catch (SQLException ex) {
                 Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
                 ErrorStage.showErrorStage(ex.getMessage());
             }
            }            
            ObservableList<String> subsidiaryItems = FXCollections.observableArrayList(subsidiariesMap.values());
            subsidiaryChoiceBox.setItems(subsidiaryItems);
            ObservableList<String> companyItems = FXCollections.observableArrayList(companiesMap.values());
            companyChoiceBox.setItems(companyItems);
           
        try {            
            
         try 
         {
            String sql = null;     
            
            stm = EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            if (MultiRecord.equals("YES")) {
             sql = "select * from workers order by id";              
            } 
            else {                         
             sql = "select * from workers where id="+ EmployeeGUIController.worker.getId();             
            } 
            rs = stm.executeQuery(sql);                       
          } catch (Exception ex) {
              showErrorAlert(ex.getMessage(),null,"DB connection error");            
            }       
            
            rs.first();              
            RenderResultSet();             
            if (MultiRecord.equals("YES")) {
                firstBtn.setDisable(false);
                nextBtn.setDisable(false);
                previousBtn.setDisable(false);
                lastBtn.setDisable(false);
                updateBtnDisable.setValue(Boolean.FALSE);
                deleteBtn.setDisable(false);
                newBtn.setDisable(false);            
                saveBtnDisable.setValue(Boolean.TRUE);
                cancelBtn.setDisable(true);                
                curRow = rs.getRow();                   
            }   
            else {
                rs.next();
                firstBtn.setDisable(true);
                nextBtn.setDisable(true);
                previousBtn.setDisable(true);
                lastBtn.setDisable(true);
                updateBtnDisable.setValue(Boolean.FALSE);
                deleteBtn.setDisable(false);
                newBtn.setDisable(true);            
                saveBtnDisable.setValue(Boolean.TRUE);
                cancelBtn.setDisable(true);                 
                curRow = 0;
            }                
                //curRow = rs.getRow();   
        } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());                     
                    
  /*   try {            
            CreateResultSet ();
            rs.first();
            RenderResultSet();             
            nextBtn.setDisable(false);
            previousBtn.setDisable(false);
            lastBtn.setDisable(false);
            updateBtnDisable.setValue(Boolean.FALSE);
            deleteBtn.setDisable(false);
            newBtn.setDisable(false);            
            saveBtnDisable.setValue(Boolean.TRUE);
            cancelBtn.setDisable(true);
            curRow = rs.getRow();    
        } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());           
*/                     
        }finally{
             try {
                 if (rs != null) rs.close();
                 if (stm != null) stm.close();
                 //if (con!= null) con.close();
             } catch (SQLException ex) {
                 Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
                 ErrorStage.showErrorStage(ex.getMessage());
             }
            }
                
    }  
    
    public boolean isButtonPressed(){
        return buttonPressed.get();
    }
    public void setButtonPressed(boolean buttonPressed){
         this.buttonPressed.set(buttonPressed);
    }
       
    @FXML
    private void handleFirstBtn(ActionEvent event) throws SQLException  {
        
        stringObservable.setValue("");
        
        try {
            CreateResultSet ();
            rs.first();
            RenderResultSet();             
            nextBtn.setDisable(false);
            previousBtn.setDisable(false);
            lastBtn.setDisable(false);            
            updateBtnDisable.setValue(Boolean.FALSE);
            deleteBtn.setDisable(false);
            newBtn.setDisable(false);  
            saveBtnDisable.setValue(Boolean.TRUE);            
            cancelBtn.setDisable(true);
            curRow = rs.getRow();    
        } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());            
        }finally{
                if (rs != null) rs.close();
                if (stm != null) stm.close();
            }
                
    }

    @FXML
    private void handlePreviousBtn(ActionEvent event) throws SQLException {
        
        stringObservable.setValue("");
         
        try {
            CreateResultSet ();
            rs.absolute(curRow);
            RenderResultSet ();
            if (rs.previous()){        
                RenderResultSet (); 
            }
        else {
            rs.next();
            stringObservable.setValue("Start of File");           
        }
        curRow = rs.getRow();  
        } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());           
        }finally{
                if (rs != null) rs.close();
                if (stm != null) stm.close();
            }
    }
  

    @FXML
    private void handleNextBtn(ActionEvent event) throws SQLException {
        
        
        stringObservable.setValue(""); 
        
        
        try {
            CreateResultSet ();
            rs.absolute(curRow);
        //    RenderResultSet ();
      
        if (rs.next()){
           
        RenderResultSet ();
        }
        else {
            rs.previous();
            stringObservable.setValue("End of File");                        
           }
        curRow = rs.getRow();
                } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());                       
        }finally{
                if (rs != null) rs.close();
                if (stm != null) stm.close();
            }
    }

    @FXML
    private void handleLastBtn(ActionEvent event) throws SQLException {
        
         stringObservable.setValue(""); 
        
        try {
            
            CreateResultSet();
            rs.last();            
            RenderResultSet();
            curRow = rs.getRow();
        } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());           
            
        }finally{
                if (rs != null) rs.close();
                if (stm != null) stm.close();
            }
    }

    @FXML
    private void handleUpdateBtn(ActionEvent event) throws SQLException {
        
        stringObservable.setValue(""); 
            int id = Integer.parseInt(identif.getText());
            String firstName = fn.getText();            
            String lastName = ln.getText();
            String jb = job.getText();
            Date hireDate =Date.valueOf(hire.getValue());
            Date birthDate = Date.valueOf(birthDatePicker.getValue());
            Date firstEverHireDate = Date.valueOf(firstEverHireDatePicker.getValue());
            Date diakopiDate = diakopiDatePicker.getValue() != null ? Date.valueOf(diakopiDatePicker.getValue()) : null;
            double salary = Double.parseDouble(sal.getText());
            String code = codeTextField.getText();            
            try {
                int curId = 0;
                String lastNm = null;
                String firstNm = null;
                if(MultiRecord.equals("YES")){
                    CreateResultSet();
                    rs.absolute(curRow);
                    curId = rs.getInt("id");
                    lastNm = rs.getString("last_name");
                    firstNm = rs.getString("last_name");
                    if (rs != null) rs.close();
                    if (stm != null) stm.close();                                
                } 
                stm1 = EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                String sql = "select * from workers ";                                                 
                rs1 = stm1.executeQuery(sql); 
            if (showDialogOK_CANCEL("Θα αποθηκευτούν οι αλλαγές για τον εργαζόμενο με id: "+(MultiRecord.equals("NO") ? EmployeeGUIController.worker.getId() : curId)
                    +" < "+(MultiRecord.equals("NO") ? EmployeeGUIController.worker.getLastName() : lastNm)+" "
                    +(MultiRecord.equals("NO") ? EmployeeGUIController.worker.getFirstName() : firstNm)+"> ! Συμφωνείτε?",null,null)!= ButtonType.OK)
            {
             showInformationAlert("Η αποθήκευση των αλλαγών ακυρώθηκε απο το χρήστη! ",null,null);      
             return;
            }            
            while(rs1.next())if(rs1.getInt("id") == 
                    (MultiRecord.equals("NO") ? EmployeeGUIController.worker.getId() : curId))break; 
            rs1.updateString(2, firstName);
            rs1.updateString(3, lastName);
            rs1.updateString(4, jb);
            rs1.updateDate(5, hireDate);
            rs1.updateDouble(6, salary);            
            rs1.updateString(8, Phone1Textfield.getText());
            rs1.updateString(9, Phone2TextField.getText());
            rs1.updateString(10, ADTTextField.getText());
            rs1.updateString(11, AMKATextField.getText());
            rs1.updateString(12, AFMTextField.getText());
            rs1.updateString(13, StreetTextField.getText());
            rs1.updateInt(14, Integer.parseInt(StreetNumberTextField.getText() != null && !StreetNumberTextField.getText().equals("")? 
                    StreetNumberTextField.getText() : "0"));
            rs1.updateString(15, DemosTextField.getText());
            rs1.updateString(16, TKTextField.getText());
            rs1.updateInt(17, Integer.parseInt(ExperienceTextField.getText() !=null && !ExperienceTextField.getText().equals("")? 
                    ExperienceTextField.getText() : "0"));
            rs1.updateString(18, education.getValue());
            rs1.updateString(19, marriage.getValue());
            rs1.updateInt(20, Integer.parseInt(ChildrenTextfield.getText() != null && !ChildrenTextfield.getText().equals("")? 
                    ChildrenTextfield.getText() : "0"));
            rs1.updateString(22, amIkaTextField.getText());
            rs1.updateDate("birthdate", birthDate);
            rs1.updateDate("first_hire_date", firstEverHireDate);
            rs1.updateString("father_name", fatherName.getText());
            rs1.updateInt("reason_isfores", Integer.parseInt(packageIKAChoiceBox.getValue() != null && !packageIKAChoiceBox.getValue().equals("")? 
                    packageIKAChoiceBox.getValue() : "101"));
            
            rs1.updateInt("subsidiary", 1);
            for(Map.Entry<Integer, String> entry : subsidiariesMap.entrySet()){
                if (subsidiaryChoiceBox.getValue() != null && subsidiaryChoiceBox.getValue().equals(entry.getValue())){                   
                    rs1.updateInt("subsidiary", entry.getKey());
                    break;
                }
            }
            rs1.updateInt("company", 1);
            for(Map.Entry<Integer, String> entry : companiesMap.entrySet()){
                if (companyChoiceBox.getValue() != null && companyChoiceBox.getValue().equals(entry.getValue())){                   
                    rs1.updateInt("company", entry.getKey());
                    break;
                }
            }
            rs1.updateInt("kat_asfalisis", Integer.parseInt(katAsfalTextField.getText() != null && !katAsfalTextField.getText().equals("") ?
                    katAsfalTextField.getText() : "0"));
            rs1.updateInt("tapit", tapitChoiceBox.getSelectionModel().getSelectedIndex());
            rs1.updateInt("relation", RelationChoiceBox.getSelectionModel().getSelectedIndex());
            rs1.updateString("kentro_kostous", kentroKostousTextField.getText());
            rs1.updateString("am_epikourikou", amEpikourikouTextField.getText());
            rs1.updateString("mother_name", motherNameTextField.getText());
            rs1.updateString("ar_parartimatos", arParartTextField.getText());
            rs1.updateString("kad", kadTextField.getText());
            rs1.updateString("kodikos_idikotitas", kodikosIdikotitasTextField.getText());
            rs1.updateString("idiki_per_asfalisis", idikiPeriptosiAsfalisisTextField.getText());
            rs1.updateInt("apolisi", katastasiChoiceBox.getSelectionModel().getSelectedIndex());
            rs1.updateDate("diakopi", diakopiDate);
            rs1.updateString("email", emailTextField.getText());
            
            rs1.updateRow();
            
            if(rs1.rowUpdated()){                
              changedPerson = new Person(rs1.getInt("ID"), rs1.getString(2), rs1.getString(3), rs1.getString(4),
               rs1.getString(5), rs1.getDouble(6), 
               (rs1.getInt("relation")==0?"Μισθωτός":"Ημερομίσθιος"), rs1.getString(8), 
               rs1.getString(9), rs1.getString(10), rs1.getString(11), rs1.getString(12), 
               rs1.getString(13), rs1.getInt(14), rs1.getString(15), rs1.getString(16), 
               rs1.getInt(17), rs1.getString(18), rs1.getString(19), rs1.getInt(20), 
               rs1.getString(22), rs1.getString(23
               ), rs1.getString("first_hire_date"),
               rs1.getString("father_name"), rs1.getInt("reason_isfores"),
               (rs1.getInt("tapit")==0?"ΟΧΙ":(rs1.getInt("tapit")==1?
                    "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ (ΠΑΛ)":"ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ (ΝΕΟΙ)")), 
               subsidiariesMap.get(rs1.getInt("subsidiary")), companiesMap.get(rs1.getInt("company")),
               rs1.getInt("kat_asfalisis"), rs1.getString("kentro_kostous"), rs1.getString("am_Epikourikou"), 
               rs1.getString("mother_name"),  rs1.getString("ar_parartimatos"), rs1.getString("kad"),
               rs1.getString("kodikos_idikotitas"), rs1.getString("idiki_per_asfalisis"), 
               (rs1.getInt("apolisi") == 0 ? "Ενεργός" : (rs1.getInt("apolisi") == 1 ? "Απόλυση" : 
                   (rs1.getInt("apolisi") == 2 ? "Συνταξιοδότηση" : "Παραίτηση"))), 
               rs1.getString("diakopi"), rs1.getString("email"));
            
              //showInformationAlert("Οι αλλαγές αποθηκεύτηκαν! ",null,null);
              
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle(null);
                alert1.setHeaderText(null);
                alert1.setContentText("Οι αλλαγές αποθηκεύτηκαν.");
                alert1.show();
                alert1.setOnHiding(f->{                
                    buttonPressed.set(f.getEventType() == DialogEvent.DIALOG_HIDING);  
                });
            }else{
                Alerts.showErrorAlert("Δεν έγινε η αλλαγή. Εφόσον το πρόβλημα παραμένει/n"
                        + "επικοινωνείστε με την J-dev ", "ΣΦΑΛΜΑ", jb);
            }
 
            //stringObservable.setValue("Record Updated");                 
            
            } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());          
            }finally{
                if (rs1 != null) rs1.close();
                if (stm1 != null) stm1.close();
                if (rs != null) rs.close();
                if (stm != null) stm.close();
            }            
    }

    @FXML
    private void handleDeleteBtn(ActionEvent event) throws SQLException {
        
       stringObservable.setValue(""); 
         
        Stage errorStage = new Stage();
        HBox hBox = new HBox(200);
        hBox.setPadding(new Insets(5, 5, 5, 5));
        Label label = new Label("ΠΡΟΣΟΧΗ : Μην σβήσετε την εγγραφή \n εφόσον ο Εργαζόμενος έχει απολυθεί φέτος");
        label.setTextFill(Color.FIREBRICK);        
        hBox.getChildren().add(label);
        hBox.setAlignment(Pos.CENTER);
        VBox vBox = new VBox (200);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        Button okBtn = new Button("ΔΙΑΓΡΑΦΗ");
        okBtn.setAlignment(Pos.CENTER_LEFT); 
        okBtn.setFocusTraversable(false);
        Button outBtn = new Button("ΑΚΥΡΟ");
        outBtn.setAlignment(Pos.CENTER_RIGHT);
        outBtn.setFocusTraversable(true);
        outBtn.setOnAction(ev -> {
            errorStage.close();
        });
        errorStage.setOnHiding(f->{                
                    buttonPressed.set(f.getEventType() == WindowEvent.WINDOW_HIDING);  
                });
        
        okBtn.setOnAction(ev -> {
            errorStage.close();
        try { 
          int curId = 0;
          if(MultiRecord.equals("YES")){
            CreateResultSet();
            rs.absolute(curRow);
            curId = rs.getInt(1);
            if(stm != null)stm.close();
            if(rs != null)rs.close(); 
          }
            strQry = "DELETE FROM workers where id = ?";
            prs = EmployeeGUI.con.prepareStatement(strQry);
            prs.setInt(1, MultiRecord.equals("YES") ? curId : EmployeeGUIController.worker.getId());
            prs.executeUpdate();            
            
            } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex); 
            ErrorStage.showErrorStage(ex.getMessage());           
            }finally{
            try {
                if (prs != null) prs.close();
                if (rs != null) rs.close();
                if (stm != null) stm.close();
            } catch (SQLException ex) {
                Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }
            }
            try{
            CreateResultSet();           
            rs.first();
            RenderResultSet ();
            stringObservable.setValue("Record Deleted");                     
            curRow = rs.getRow();
            }catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex); 
            ErrorStage.showErrorStage(ex.getMessage());            
        }finally{
            try {
                if (rs != null) rs.close();
                if (stm != null) stm.close();
            } catch (SQLException ex) {
                Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }
            }
    });
        HBox btnHBox = new HBox (200);
        btnHBox.getChildren().addAll(okBtn, outBtn);
        vBox.getChildren().addAll(hBox, btnHBox);
        vBox.setSpacing(100);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox, 350, 250);
        errorStage.setScene(scene);
        errorStage.showAndWait();
       }

    @FXML
    private void handleNewBtn(ActionEvent event) throws SQLException {
        
       stringObservable.setValue("");
            
        identif.clear();       
        fn.clear();    
        ln.clear();    
        job.clear();
        hire.setValue(null);
        sal.clear();
        codeTextField.clear();        
        Phone1Textfield.clear();
        Phone2TextField.clear();
        ADTTextField.clear();
        AMKATextField.clear();
        AFMTextField.clear();
        StreetTextField.clear();
        StreetNumberTextField.clear();
        DemosTextField.clear();
        TKTextField.clear();
        ExperienceTextField.clear();
        education.setValue(null);
        marriage.setValue(null);
        ChildrenTextfield.clear();
        amIkaTextField.clear();
        birthDatePicker.setValue(null);
        firstEverHireDatePicker.setValue(null);
        fatherName.clear();
        packageIKAChoiceBox.setValue("101");       
        subsidiaryChoiceBox.setValue(null);
        companyChoiceBox.setValue(null);
        katAsfalTextField.clear();
        tapitChoiceBox.setValue(null);
        RelationChoiceBox.setValue(null);
        kentroKostousTextField.clear();
        amEpikourikouTextField.clear();
        motherNameTextField.clear();
        arParartTextField.clear();
        kadTextField.clear();
        kodikosIdikotitasTextField.clear();
        idikiPeriptosiAsfalisisTextField.clear();
        katastasiChoiceBox.getSelectionModel().selectFirst();
        diakopiDatePicker.setValue(null);
        emailTextField.clear();
        
        firstBtn.setDisable(true);
        nextBtn.setDisable(true);
        previousBtn.setDisable(true);
        lastBtn.setDisable(true);        
        updateBtnDisable.setValue(Boolean.TRUE);
        deleteBtn.setDisable(true);
        newBtn.setDisable(true);        
        saveBtnDisable.setValue(Boolean.FALSE);
        cancelBtn.setDisable(false);                                              
    }

    @FXML
    private void handleSaveBtn(ActionEvent event) throws SQLException {
        
            stringObservable.setValue(""); 
        
            //int id = Integer.parseInt(identif.getText());
            String firstName = fn.getText();
            String lastName = ln.getText();
            String jb = job.getText();
            Date hireDate = hire.getValue() != null? Date.valueOf(hire.getValue()) : null;
            Date birthDate = birthDatePicker.getValue() != null? Date.valueOf(birthDatePicker.getValue()) : null;
            Date firstEverHireDate = firstEverHireDatePicker.getValue() != null? Date.valueOf(firstEverHireDatePicker.getValue()) : null;
            Date diakopiDate = diakopiDatePicker.getValue() != null ? Date.valueOf(diakopiDatePicker.getValue()) : null;
            double salary = Double.parseDouble(sal.getText());
            String code = codeTextField.getText();
            
            strQry = "INSERT INTO workers(FIRST_NAME, LAST_NAME, JOB_TITLE, HIRE_DATE, SALARY,"
                    + "CODE, PHONE1, PHONE2, ADT, AMKA, AFM, STREET, STREET_NUMBER, DEMOS, TK, YEARS_BEFORE,"
                    + "EDUCATION, MARRIAGE, CHILDREN, ENTITLED_DAYS, AM_IKA, BIRTHDATE, FIRST_HIRE_DATE, FATHER_NAME,"
                    + "REASON_ISFORES, SUBSIDIARY, COMPANY, KAT_ASFALISIS, TAPIT, RELATION, KENTRO_KOSTOUS, AM_EPIKOURIKOU,"
                    + "MOTHER_NAME, AR_PARARTIMATOS, KAD, KODIKOS_IDIKOTITAS, IDIKI_PER_ASFALISIS, APOLISI, DIAKOPI, EMAIL )"
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, "
                    + "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                    + "?, ?, ?, ?, ?, ?, ?, ?) ";
            prs = EmployeeGUI.con.prepareStatement(strQry);
        //    prs.setInt(1, id);
            prs.setString(1, firstName);
            prs.setString(2, lastName);
            prs.setString(3, jb);
            prs.setDate(4, hireDate);
            prs.setDouble(5, salary);
            prs.setString(6, code);            
            prs.setString(7, Phone1Textfield.getText());
            prs.setString(8, Phone2TextField.getText());
            prs.setString(9, ADTTextField.getText());
            prs.setString(10, AMKATextField.getText());
            prs.setString(11, AFMTextField.getText());
            prs.setString(12, StreetTextField.getText());
            prs.setInt(13, Integer.parseInt(StreetNumberTextField.getText() != null && !StreetNumberTextField.getText().equals("")? 
                    StreetNumberTextField.getText() : "0"));
            prs.setString(14, DemosTextField.getText());
            prs.setString(15, TKTextField.getText());
            prs.setInt(16, Integer.parseInt(ExperienceTextField.getText() !=null && !ExperienceTextField.getText().equals("")? 
                    ExperienceTextField.getText() : "0"));
            prs.setString(17, education.getValue());
            prs.setString(18, marriage.getValue());
            prs.setInt(19, Integer.parseInt(ChildrenTextfield.getText() != null && !ChildrenTextfield.getText().equals("")? 
                    ChildrenTextfield.getText() : "0"));
            prs.setString(20, amIkaTextField.getText());
            prs.setDate(21, birthDate);
            prs.setDate(22, firstEverHireDate);
            prs.setString(23, fatherName.getText());
            prs.setInt(24, Integer.parseInt(packageIKAChoiceBox.getValue() != null && !packageIKAChoiceBox.getValue().equals("")? 
                    packageIKAChoiceBox.getValue() : "101"));
            prs.setInt(25, 1);
            for(Map.Entry<Integer, String> entry : subsidiariesMap.entrySet()){                
                if (subsidiaryChoiceBox.getValue() != null && subsidiaryChoiceBox.getValue().equals(entry.getValue())){
                    prs.setInt(25, entry.getKey()); 
                    break;
                }
            }
            prs.setInt(26, 1);
            for(Map.Entry<Integer, String> entry : companiesMap.entrySet()){
                if (companyChoiceBox.getValue() != null && companyChoiceBox.getValue().equals(entry.getValue())){
                    prs.setInt(26, entry.getKey()); 
                    break;
                }
            }
            prs.setInt(27, Integer.parseInt(katAsfalTextField.getText() != null && !katAsfalTextField.getText().equals("") ?
                    katAsfalTextField.getText() : "0"));
            prs.setInt(28, tapitChoiceBox.getSelectionModel().getSelectedIndex());
            prs.setInt(29, RelationChoiceBox.getSelectionModel().getSelectedIndex());
            prs.setString(30, kentroKostousTextField.getText());
            prs.setString(31, amEpikourikouTextField.getText());
            prs.setString(32, motherNameTextField.getText());
            prs.setString(33, arParartTextField.getText());
            prs.setString(34, kadTextField.getText());
            prs.setString(35, kodikosIdikotitasTextField.getText());
            prs.setString(36, idikiPeriptosiAsfalisisTextField.getText());
            prs.setInt(37, katastasiChoiceBox.getSelectionModel().getSelectedIndex());
            prs.setDate(38, diakopiDate);
            prs.setString(39, emailTextField.getText());
            prs.executeUpdate();                                  
                      
            try{            
            CreateResultSet();  
            rs.last();
            curRow = rs.getRow();            
            RenderResultSet();
            
        firstBtn.setDisable(false);
        nextBtn.setDisable(false);
        previousBtn.setDisable(false);
        lastBtn.setDisable(false);        
        updateBtnDisable.setValue(Boolean.FALSE);
        deleteBtn.setDisable(false);
        newBtn.setDisable(false);
        saveBtnDisable.setValue(Boolean.TRUE);        
        cancelBtn.setDisable(true);
        
        stringObservable.setValue("Record Saved to database");        
                            
        } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());     
        }finally{
                if (rs != null) rs.close();
                if (stm != null) stm.close();
            }
    }

    @FXML
    private void handleCancelBtn(ActionEvent event) throws SQLException {
        
       stringObservable.setValue(""); 
        
        try {
            CreateResultSet(); 
            rs.absolute(curRow);
            RenderResultSet();
            
        firstBtn.setDisable(false);
        nextBtn.setDisable(false);
        previousBtn.setDisable(false);
        lastBtn.setDisable(false);        
        updateBtnDisable.setValue(Boolean.FALSE);
        deleteBtn.setDisable(false);
        newBtn.setDisable(false);
        cancelBtn.setDisable(true);        
        saveBtnDisable.setValue(Boolean.TRUE);
        
            
        } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());         
        }finally{
                if (rs != null) rs.close();
                if (stm != null) stm.close();
            }
    }
    public String Autoincrement() throws SQLException{
        int i = 0;
        try {
            CreateResultSet();
            Set<Integer> rsSet = new HashSet<>();
            rs.beforeFirst();
            while (rs.next())rsSet.add(rs.getInt(1));
            i = Collections.max(rsSet);           
            i++;
            
            
        } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());           
        }finally{
                if (rs != null) rs.close();
                if (stm != null) stm.close();
            }
        
        return Integer.toString(i) ;
        
    }
    public void CreateResultSet (){
        try {
            stm = EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);            
            String sql  = "select * from workers order by id";
            rs = stm.executeQuery(sql);
                        
        } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());           
        }
        
    }
    public void RenderResultSet (){
                    
        try {
            identif.setText(Integer.toString(rs.getInt("ID")));
            fn.setText(rs.getString(2));
            ln.setText(rs.getString(3));
            job.setText(rs.getString(4));
            
            /* Convert from sql.Date(no time) to util.Date to Localdate*/
            
            LocalDate localDate = rs.getDate(5) != null? rs.getDate(5).toLocalDate() : null;
            hire.setValue(localDate);            
            LocalDate localDate1 = rs.getDate(23) != null? rs.getDate(23).toLocalDate() : null;                     
            birthDatePicker.setValue(localDate1);
            LocalDate localDate2 = rs.getDate(23) != null? rs.getDate(24).toLocalDate() : null; 
            LocalDate localDate3 = rs.getDate("diakopi") != null ? rs.getDate("diakopi").toLocalDate() : null; 
            firstEverHireDatePicker.setValue(localDate2);
            diakopiDatePicker.setValue(localDate3);
                       
            sal.setText(Double.toString(rs.getDouble(6)));
            codeTextField.setText(rs.getString(7));            
            Phone1Textfield.setText(rs.getString(8));
            Phone2TextField.setText(rs.getString(9));
            ADTTextField.setText(rs.getString(10));
            AMKATextField.setText(rs.getString(11));
            AFMTextField.setText(rs.getString(12));
            StreetTextField.setText(rs.getString(13));
            StreetNumberTextField.setText(Integer.toString(rs.getInt(14)));
            DemosTextField.setText(rs.getString(15));
            TKTextField.setText(rs.getString(16));
            ExperienceTextField.setText(Integer.toString(rs.getInt(17)));
            education.setValue(rs.getString(18));
            marriage.setValue(rs.getString(19));
            ChildrenTextfield.setText(rs.getString(20));
            amIkaTextField.setText(rs.getString(22));
            fatherName.setText(rs.getString("father_name"));
            packageIKAChoiceBox.setValue(Integer.toString(rs.getInt("reason_isfores")).trim());            
            subsidiaryChoiceBox.setValue(subsidiariesMap.get(rs.getInt("subsidiary")));
            companyChoiceBox.setValue(companiesMap.get(rs.getInt("company")));
            katAsfalTextField.setText(Integer.toString(rs.getInt("kat_asfalisis")));
            tapitChoiceBox.setValue((rs.getInt("tapit")==0?"ΟΧΙ":(rs.getInt("tapit")==1?
                    "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ (ΠΑΛ)":"ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ (ΝΕΟΙ)")));
            RelationChoiceBox.setValue((rs.getInt("relation")==0?"Μισθωτός":"Ημερομίσθιος"));
            kentroKostousTextField.setText(rs.getString("kentro_kostous"));
            amEpikourikouTextField.setText(rs.getString("am_epikourikou"));
            motherNameTextField.setText(rs.getString("mother_name"));
            arParartTextField.setText(rs.getString("ar_parartimatos"));
            kadTextField.setText(rs.getString("kad"));
            kodikosIdikotitasTextField.setText(rs.getString("kodikos_idikotitas"));
            idikiPeriptosiAsfalisisTextField.setText(rs.getString("idiki_per_asfalisis"));
            katastasiChoiceBox.setValue(katastasiChoiceBox.getItems().get(rs.getInt("apolisi")));
            emailTextField.setText(rs.getString("email"));
            
        } catch (SQLException ex) {
            Logger.getLogger(EnterDataController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());          
        }
      
    }
    public void initConn(Connection con){
        //this.con = con;
    }
}
