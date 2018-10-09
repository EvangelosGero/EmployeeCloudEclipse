/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package diakopiErgasiakisShesis;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import employeegui.Alerts;
import employeegui.ErrorStage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author egdyn_000
 */
public class ApolisiController implements Initializable {
    
    @FXML
    private TextField nameTextField;
    @FXML
    private TextField fatherNameTextField;
    @FXML
    private TextField lastNameTextField;
    @FXML
    private DatePicker paretisiDatePicker;
    @FXML
    private TextField ipolipesAdeiesTextField;
    @FXML
    private TextField dailyAdeiaCostTextField;
    @FXML
    private TextField totalAdeiesCostTextField;
    @FXML
    private TextField totalCostTextField;
    
    //private Connection con = null;
    private Statement stm = null;
    private PreparedStatement prs = null;
    private ResultSet rs = null, rs1 = null;
    private LocalDate hireDate, apolisiDate;
    private int currentYear, remainingDays, relation;
    private double salary;
    private int proMonths = 0;   
    @FXML
    private RadioButton yesRadioBtn;
    @FXML
    private RadioButton noRadioBtn;
    @FXML
    private TextField apolisiApozimiosiTextField;
    @FXML
    private TextField proTextField;
    @FXML
    private ToggleGroup myToggleGroup;
    @FXML
    private AnchorPane root;
    @FXML
    private Button oristikopoiisiBtn;
    @FXML
    private Button excelBtn;
    @FXML
    private Button PDFButton;
    @FXML
    private ChoiceBox<Integer> idChoiceBox;
    private  ObservableList<Integer> idItems;
    private final Map<Integer, List<String>> idMap  = new HashMap<>();
    private FileOutputStream out;
    private FileInputStream in = null;
    @FXML
    private Label messageLabel;
    private DecimalFormatSymbols symbols;
    private DecimalFormat numFormat;
    private final Map<Integer, String> subsidiariesMap = new HashMap<>();
    private final Map<Integer, List<String>> companyMap = new HashMap<>();
    int simpliromena = 0, imeromisthia =0;
    double misthoi = 0, proMisthoi = 0; 
    private int subsidiary, company, katAsfalisis;
    private String jobTitle, afm, kentroKostous, amEpikourikou, amIka, reportTableStr, salaryTableStr;
    private double apozimiosiMiLifthisasAdeias, apozimiosi, imeresErgasias, epidomaAdeias,
            pliroteoEpidomaAdeias;
    private final DecimalFormat decimalFormat = new DecimalFormat(".##");
    @FXML
    private TextField misthodosiaTextField;
    @FXML
    private TextField epidomaAdeiasTextField;
    @FXML
    private TextField adeiaLifthisaTextField;
    private String temporaryTableStr;
    @FXML
    private TextField pliroteoTotal;
    @FXML
    private TextField pliroteoEpidomaAdeiasTextField;
    @FXML
    private TextField pliroteoMisthodosiaTextField;
    private double pliroteoMisthodosia, pliroteoLiftheisa, misthodosia, adeiaLifthisa;
    @FXML
    private TextField pliroteoLiftheisaTextField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paretisiDatePicker.setConverter(utilities.DateFormatterGreek.converter);
        paretisiDatePicker.setPromptText(utilities.DateFormatterGreek.promptText);        
        symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        numFormat = new DecimalFormat(".00", symbols);
        
        oristikopoiisiBtn.setDisable(true);
        excelBtn.setDisable(true);
        PDFButton.setDisable(true);
        try {
            stm = employeegui.EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "SELECT id, first_name, last_name, father_name FROM workers";
            rs = stm.executeQuery(sql);
            while (rs.next()) {
                List <String> idList = new ArrayList<>();
                idList.add(rs.getString("first_name"));
                idList.add(rs.getString("father_name"));
                idList.add(rs.getString("last_name"));
                idMap.put(rs.getInt("id"), idList);
            }
            if (rs != null)rs.close();
            rs = stm.executeQuery("SELECT * FROM subsidiaries");
            while (rs.next())subsidiariesMap.put(rs.getInt("id"), rs.getString("name"));
            if (rs != null) rs.close();
            rs = stm.executeQuery("SELECT * FROM company_information");
            while (rs.next()){
                List<String> value = new ArrayList<>();
                Collections.addAll(value, rs.getString("name"), rs.getString("idos_epihirisis"), rs.getString("phone_number"),
                        rs.getString("ar_mitr_erg"), rs.getString("ipefthinos"), rs.getString("address"),
                        rs.getString("town"), rs.getString("edra"), rs.getString("afm"));
                companyMap.put(rs.getInt("id"), value);
            }
            
        } catch (SQLException ex) {
         Logger.getLogger(ParaitisiController.class.getName()).log(Level.SEVERE, null, ex);  
         ErrorStage.showErrorStage(ex.getMessage());
        }finally{try {
            if (rs != null)rs.close() ;
            if (stm != null)stm.close();   
            //if (con != null) con.close();   
            } catch (SQLException ex) {
                Logger.getLogger(ApolisiController.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage());
            }            
        }
        idItems = FXCollections.observableArrayList(idMap.keySet());
        idChoiceBox.setItems(idItems);
        idChoiceBox.setValue(idItems.get(0));
        nameTextField.setText(idMap.get((Integer)idMap.keySet().toArray()[0]).get(0));
        fatherNameTextField.setText(idMap.get((Integer)idMap.keySet().toArray()[0]).get(1));
        lastNameTextField.setText(idMap.get((Integer)idMap.keySet().toArray()[0]).get(2));
        idChoiceBox.valueProperty().addListener(new ChangeListener <Integer>(){
                
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer oldValue, Integer newValue) {
            nameTextField.setText(idMap.get(newValue).get(0));
            fatherNameTextField.setText(idMap.get(newValue).get(1));
            lastNameTextField.setText(idMap.get(newValue).get(2));
            }
        });
        
    }    

    @FXML
    private void handleOristikopoiisiBtn(ActionEvent event) throws SQLException {
        String id = Integer.toString(idChoiceBox.getValue());
        Date diakopiDate = Date.valueOf(paretisiDatePicker.getValue());
        try {
            stm = employeegui.EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "select * from workers WHERE id = "+id;
            rs = stm.executeQuery(sql);
            rs.first();
            rs.updateInt("apolisi", 1);
            rs.updateDate("diakopi", diakopiDate);
            
            rs.updateRow();
            if (rs != null)rs.close();
            
            sql = "SELECT * FROM apozimioseis WHERE id = "+id;
            rs = stm.executeQuery(sql);
            if (rs.first() == false){
              rs.close();              
              sql = "INSERT INTO apozimioseis VALUES (?, ?, ?, ?, ? )";
              prs = employeegui.EmployeeGUI.con.prepareStatement(sql);
              prs.setInt(1, idChoiceBox.getValue());
              prs.setDouble(2, apozimiosi);
              prs.setDouble(3, apozimiosiMiLifthisasAdeias);
              prs.setInt(4, remainingDays);
              prs.setInt(5, (int)imeresErgasias);
              prs.executeUpdate();                 
            }else{
              rs.updateInt("id", idChoiceBox.getValue());
              rs.updateDouble("apozimiosi", apozimiosi);
              rs.updateDouble("mi_lifthises_adeies", apozimiosiMiLifthisasAdeias);
              rs.updateInt("imeres_adeias", remainingDays);
              rs.updateInt("imeres_apozimiosis", (int)imeresErgasias);
              rs.updateRow();
            }
                        
        } catch (SQLException ex) {
         Logger.getLogger(ParaitisiController.class.getName()).log(Level.SEVERE, null, ex);
         ErrorStage.showErrorStage(ex.getMessage());
        }finally{
            if (rs != null)rs.close();
            if (stm != null)stm.close();
            if (prs != null)prs.close();
        }
            }

    @FXML
    private void handleExcelBtn(ActionEvent event) throws IOException{
         
        HSSFWorkbook workbook = null;
        String excelPath = "C:\\EmployeeGUI\\EmployeeGUIOutput\\"
                   +lastNameTextField.getText()+"_"+nameTextField.getText()+"_apolisi.xls"; 
        try {
        File excelFile = new File(excelPath);
        if (excelFile.exists()){
            in = new FileInputStream(excelFile); 
            workbook = new HSSFWorkbook(in);
        }else{            
            workbook = new HSSFWorkbook();  
        }
        if (workbook.getSheet("Απόλυση") != null)           
           workbook.removeSheetAt(workbook.getSheetIndex("Απόλυση"));
        HSSFSheet pliromiSheet = workbook.createSheet("Απόλυση");
        HSSFRow heading = pliromiSheet.createRow(0);
        List<String> headings = new ArrayList<>();
        headings.add("Όνομα");
        headings.add("Επώνυμο");
        headings.add("Πατρώνυμο");
        headings.add("Αποζημίωση Απόλυσης");
        headings.add("Προειδοπ.(μήνες)");
        headings.add("Μη Ληφθείσα Άδεια");
        headings.add("Τακτικές Αποδοχές κόστος");
        headings.add("Αδεια Ληφθείσα κόστος");
        headings.add("Επίδομα Αδείας κόστος");
        headings.add("Συνολικό κόστος");
        headings.add("Τακτικές Αποδοχές πληρωτέο");
        headings.add("Αδεια Ληφθείσα πληρωτέο");
        headings.add("Επίδομα Αδείας πληρωτέο");
        headings.add("Συνολικό πληρωτέο");
        int counter = 0;
        for(String s : headings){
        heading.createCell(counter).setCellValue(s);
        counter++;
        }       
        HSSFRow dataRow = pliromiSheet.createRow(1);
        dataRow.createCell(0).setCellValue(nameTextField.getText());
        dataRow.createCell(1).setCellValue(lastNameTextField.getText());
        dataRow.createCell(2).setCellValue(fatherNameTextField.getText());
        dataRow.createCell(3).setCellValue(apolisiApozimiosiTextField.getText());
        dataRow.createCell(4).setCellValue(proTextField.getText());
        dataRow.createCell(5).setCellValue(totalAdeiesCostTextField.getText());
        dataRow.createCell(6).setCellValue(misthodosiaTextField.getText());
        dataRow.createCell(7).setCellValue(adeiaLifthisaTextField.getText());
        dataRow.createCell(8).setCellValue(epidomaAdeiasTextField.getText());
        dataRow.createCell(9).setCellValue(totalCostTextField.getText());
        dataRow.createCell(10).setCellValue(pliroteoMisthodosiaTextField.getText());
        dataRow.createCell(11).setCellValue(pliroteoLiftheisaTextField.getText());
        dataRow.createCell(12).setCellValue(pliroteoEpidomaAdeiasTextField.getText());
        dataRow.createCell(13).setCellValue(pliroteoTotal.getText());
               
        for (int columnIndex = 0; columnIndex < 14; columnIndex++)
                pliromiSheet.autoSizeColumn(columnIndex);        
            out = new FileOutputStream(excelFile);
            workbook.write(out);           
            messageLabel.setText("Excel written successfully..");
             
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage());
            ErrorStage.showErrorStage(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText(e.getMessage());
            ErrorStage.showErrorStage(e.getMessage());
        }finally{
           if(out != null)out.close();
           if(in != null)in.close();
       }
  }
    

    @FXML
    private void handleCancelBtn(ActionEvent event) {
       // Stage stage = (Stage)root.getScene().getWindow();
       root.getChildren().clear();
    }

    @FXML    
    private void handleIpologismosBtn(ActionEvent event) throws SQLException, InterruptedException, ExecutionException {
        String query;
        numFormat = new DecimalFormat(".00");
        
        /* Create a recent employee_vacation table to capture the latest vacation days */
        
        new employeegui.CreateVacationReport().CreateVacationDBTable(employeegui.EmployeeGUI.con);
        
        
        
        /* find which is the current Year */        
        
        currentYear = paretisiDatePicker.getValue().getYear();   
        
        epidomaAdeias = 0;
        pliroteoEpidomaAdeias = 0;
            
        if (idChoiceBox.getValue() != null && idChoiceBox.getValue() != 0 && paretisiDatePicker.getValue() != null){
        try {
            stm = employeegui.EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            
            // Now compute epidoma adeias
        
            if (paretisiDatePicker.getValue().isBefore(LocalDate.of(currentYear, 8, 1))){
                computeEpidomaAdeias(idChoiceBox.getValue());            
                query = "SELECT kostos, pliroteo FROM "+temporaryTableStr+" WHERE id = "+Integer.toString(idChoiceBox.getValue());
                rs = stm.executeQuery(query);
                rs.first();
                epidomaAdeias = rs.getDouble(1);
                pliroteoEpidomaAdeias = rs.getDouble(2);                
                if (rs != null)rs.close(); 
            }
            epidomaAdeiasTextField.setText(numFormat.format(epidomaAdeias));
            pliroteoEpidomaAdeiasTextField.setText(numFormat.format(pliroteoEpidomaAdeias));
            
            //Compute the salary 
            
            computeSalary(idChoiceBox.getValue());
            misthodosiaTextField.setText(numFormat.format(misthodosia));
            pliroteoMisthodosiaTextField.setText(numFormat.format(pliroteoMisthodosia));
            adeiaLifthisaTextField.setText(numFormat.format(adeiaLifthisa));
            pliroteoLiftheisaTextField.setText(numFormat.format(pliroteoLiftheisa));
           
            //Now compute the rest
            
            query = "SELECT t1.first_name, t1.father_name, t1.last_name, t1.hire_date, "
                    + " t2.remaining_days, t1.salary, t1.relation, t1.subsidiary, t1.company, "
                    + "t1.kat_asfalisis, t1.job_title, "
                   + "t1.afm, t1.kentro_kostous, t1.am_epikourikou , t1.am_ika"
                    + " FROM workers AS t1, employee_vacation AS t2 WHERE t1.id = "
                    +Integer.toString(idChoiceBox.getValue())
                    +" AND t1.id = t2.id";
              
            rs = stm.executeQuery(query);
            rs.first();
            nameTextField.setText(rs.getString(1));
            fatherNameTextField.setText(rs.getString(2));
            lastNameTextField.setText(rs.getString(3));
            hireDate = rs.getDate(4).toLocalDate();
            apolisiDate = paretisiDatePicker.getValue();
            remainingDays = rs.getInt(5);
            salary = rs.getDouble(6);
            relation = rs.getInt(7);
            subsidiary = rs.getInt(8);
            company = rs.getInt(9);
            katAsfalisis = rs.getInt(10);
            jobTitle = rs.getString(11);
            afm = rs.getString(12);
            kentroKostous = rs.getString(13);
            amEpikourikou = rs.getString(14);
            amIka = rs.getString(15);
            
            ipolipesAdeiesTextField.setText(Integer.toString(remainingDays));
            dailyAdeiaCostTextField.setText(numFormat.format(relation == 0 ? salary/25 : salary));
            apozimiosiMiLifthisasAdeias = computeApozimiosiMiLifthisasAdeias(remainingDays, salary, relation);            
            apozimiosi = computeApozimiosi(hireDate, apolisiDate, salary, relation, yesRadioBtn.isSelected());
            imeresErgasias = imeromisthia == 0 ? misthoi * 25 :
                    imeromisthia;
            
            apolisiApozimiosiTextField.setText(numFormat.format(apozimiosi));
            totalAdeiesCostTextField.setText(numFormat.format(apozimiosiMiLifthisasAdeias));
            totalCostTextField.setText(numFormat.format(apozimiosiMiLifthisasAdeias + apozimiosi+
                    misthodosia+adeiaLifthisa+epidomaAdeias));
            pliroteoTotal.setText(numFormat.format(apozimiosiMiLifthisasAdeias + apozimiosi+
                pliroteoMisthodosia+ pliroteoLiftheisa+pliroteoEpidomaAdeias));   
            
            if (relation == 0){
                if (yesRadioBtn.isSelected()){
            proTextField.setText(Integer.toString(proMonths));            
        }else{
            proTextField.setText("0");            
        }
       }else {
            proTextField.setText("0");            
       }
            oristikopoiisiBtn.setDisable(false);
            excelBtn.setDisable(false);
            PDFButton.setDisable(false);
        } catch (SQLException ex) {
            Logger.getLogger(ParaitisiController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
            if (rs != null)rs.close();
        }
        }else Alerts.showErrorAlert("Παρακαλώ συμπληρώστε σωστά τo πεδίo ημερομηνίας και ID"
                    , "ΣΦΑΛΜΑ", "Σφάλμα κενών πεδίων ημερομηνίας");
    }
    
    public double computeApozimiosi(LocalDate hireDate, LocalDate apolisiDate, double salary, int relation, boolean yesRadioBtn){
       apozimiosi = 0;
       imeromisthia = 0;
       misthoi = 0;
       
       
       simpliromena = hireDate.until(apolisiDate.plusDays(1)).getYears();
       if (relation == 0){  //μισθωτός
            switch(simpliromena){
                   case 0 : {
                       proMonths = 0;                       
                       proMisthoi = 0;
                       misthoi = 0;
                       break;
                   }
                   case 1 : {
                      proMonths = 1;
                      proMisthoi = 1;
                      misthoi = 2;
                      break; 
                   }
                   case 2 : {
                      proMonths = 2;
                      proMisthoi = 1;
                      misthoi = 2;
                      break; 
                   }
                   case 3 : {
                      proMonths = 2;
                      proMisthoi = 1;
                      misthoi = 2;
                      break; 
                   }
                   case 4 : {
                      proMonths = 2;
                      proMisthoi = 1.5;
                      misthoi = 3;
                      break; 
                   }
                   case 5 : {
                      proMonths = 3;
                      proMisthoi = 1.5;
                      misthoi = 3;
                      break; 
                   }
                   case 6 : {
                      proMonths = 3;
                      proMisthoi = 2;
                      misthoi = 4;
                      break; 
                   }
                   case 7 : {
                      proMonths = 3;
                      proMisthoi = 2;
                      misthoi = 4;
                      break; 
                   }
                   case 8 : {
                      proMonths = 3;
                      proMisthoi = 2.5;
                      misthoi = 5;
                      break; 
                   }
                   case 9 : {
                      proMonths = 3;
                      proMisthoi = 2.5;
                      misthoi = 5;
                      break; 
                   }
                   case 10 : {
                      proMonths = 4;
                      proMisthoi = 3;
                      misthoi = 6;
                      break; 
                   }
                    case 11 : {
                      proMonths = 4;
                      proMisthoi = 3.5;
                      misthoi = 7;
                      break; 
                   }
                    case 12 : {
                      proMonths = 4;
                      proMisthoi = 4;
                      misthoi = 8;
                      break; 
                   }
                    case 13 : {
                      proMonths = 4;
                      proMisthoi = 4.5;
                      misthoi = 9;
                      break; 
                   }
                   case 14 : {
                      proMonths = 4;
                      proMisthoi = 5;
                      misthoi = 10;
                      break; 
                   }
                   case 15 : {
                      proMonths = 4;
                      proMisthoi = 5.5;
                      misthoi = 11;
                      break; 
                   }
                   case 16 : {
                      proMonths = 4;
                      proMisthoi = 6;
                      misthoi = 12;
                      break; 
                   }
                   default:{
                       int oldEmployees = hireDate.until(LocalDate.of(2012, 11, 2)).getYears();
                       if (oldEmployees >=17){
                           switch (oldEmployees) {
                               case 17:{
                                proMonths = 4;
                                proMisthoi = 6.5;
                                misthoi = 13;
                                break; 
                               }
                               case 18:{
                                proMonths = 4;
                                proMisthoi = 7;
                                misthoi = 14;
                                break; 
                               }
                               case 19:{
                                proMonths = 4;
                                proMisthoi = 7.5;
                                misthoi = 15;
                                break; 
                               }
                               case 20:{
                                proMonths = 4;
                                proMisthoi = 8;
                                misthoi = 16;
                                break; 
                               }
                               case 21:{
                                proMonths = 4;
                                proMisthoi = 8.5;
                                misthoi = 17;
                                break; 
                               }
                               case 22:{
                                proMonths = 4;
                                proMisthoi = 9;
                                misthoi = 18;
                                break; 
                               }
                               case 23:{
                                proMonths = 4;
                                proMisthoi = 9.5;
                                misthoi = 19;
                                break; 
                               }
                               case 24:{
                                proMonths = 4;
                                proMisthoi = 10;
                                misthoi = 20;
                                break; 
                               }
                               case 25:{
                                proMonths = 4;
                                proMisthoi = 10.5;
                                misthoi = 21;
                                break; 
                               }
                               case 26:{
                                proMonths = 4;
                                proMisthoi = 11;
                                misthoi = 22;
                                break; 
                               }
                               case 27:{
                                proMonths = 4;
                                proMisthoi = 11.5;
                                misthoi = 23;
                                break; 
                               }
                               default :{
                                proMonths = 4;
                                proMisthoi = 12;
                                misthoi = 24;
                                break; 
                               }
                           }
                       }else 
                        {proMonths = 4;
                        proMisthoi = 6;
                        misthoi = 12;
                        break;
                        }
                   }
                       
               }
       }else{  //ημερομίσθιος
           if (simpliromena == 0)imeromisthia = 0;
           else if (simpliromena == 1)imeromisthia = 7;
           else if (simpliromena >= 2 && simpliromena <5)imeromisthia = 15;
           else if (simpliromena >= 5 && simpliromena <10)imeromisthia = 30;
           else if (simpliromena >= 10 && simpliromena <15)imeromisthia = 60;
           else if (simpliromena >= 15 && simpliromena <20)imeromisthia = 100;
           else if (simpliromena >= 20 && simpliromena <25)imeromisthia = 120;
           else if (simpliromena >= 25 && simpliromena <30)imeromisthia = 145;
           else if (simpliromena >= 30)imeromisthia = 165;
       }
       if (relation == 0){
        if (yesRadioBtn){            
            apozimiosi = proMisthoi * salary * 1.166666666666;
        }else{            
            apozimiosi = misthoi * salary * 1.166666666666;
        }
       }else {            
            apozimiosi = imeromisthia * salary * 1.16666666666;
       }
       return apozimiosi;
    }
   
    
    public void showMessage(String str){
        Stage stage = new Stage(StageStyle.DECORATED);
        Label label = new Label(str);
        VBox vBox = new VBox(label);
        vBox.setPadding(new Insets(5, 5, 5, 5));
        Button btn = new Button("OK");
        btn.setOnAction(ev ->{
          stage.close();
        });         
        Scene scene = new Scene(vBox, 100, 100);
        stage.setScene(scene);
        stage.showAndWait();
        
    }
    public double computeApozimiosiMiLifthisasAdeias(int remainingDays, double salary, int relation){
        
        double salaryFinal;
        salaryFinal = relation == 0 ? salary/25 : salary;
        apozimiosi = remainingDays * 1.2 * salaryFinal; 
        return apozimiosi;
    }
    public void initConn(Connection con){
        //this.con = con;
    }  

    @FXML
    private void handlePdf(ActionEvent event) throws FileNotFoundException, DocumentException, IOException, SQLException {
        String fileString = nameTextField.getText()+"_"+lastNameTextField.getText()+"_apodixi_apolisis.pdf";
        File newFile = new File("C:\\EmployeeGUI\\EmployeeGUIOutput\\"+fileString);
        Document document = new Document();
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(newFile));
        document.open();
        String [] param = new String[69];
        Arrays.fill(param, " ");
        param[0] = companyMap.get(company).get(0);
        param[1] = companyMap.get(company).get(1);
        param[2] = companyMap.get(company).get(5);
        param[3] = Integer.toString(LocalDate.now().getMonthValue())+"/"
                +Integer.toString(LocalDate.now().getYear());
        param[4] = companyMap.get(company).get(7);
        param[5] = "Αποζημίωση Απόλυσης";
        param[6] = companyMap.get(company).get(8);
        param[7] = lastNameTextField.getText();
        param[8] = jobTitle;
        param[9] = nameTextField.getText();
        param[10] = subsidiariesMap.get(subsidiary);
        param[11] = fatherNameTextField.getText();
        param[12] = kentroKostous;
        param[13] = afm;
        param[14] = (relation==0 ? "ΥΠΑΛΛΗΛΟΣ" : "ΗΜΕΡΟΜΙΣΘΙΟΣ");
        param[15] = amIka;
        param[16] = amEpikourikou;
        param[17] = numFormat.format(salary);
        param[23] = numFormat.format(imeresErgasias);
        param[24] = decimalFormat.format(apozimiosi/1.16666666666);
        param[29] = "Προσαύξ. Δώρου";
        param[31] = decimalFormat.format((apozimiosi/1.16666666666)*0.1666666666);
        param[64] = decimalFormat.format(apozimiosi);
        param[65] = decimalFormat.format(0);
        param[66] = decimalFormat.format(0);
        param[67] = param[64];
        param[68] = param[64];
        
        SalaryReport.ApodixisPDF apodixisPDF = new SalaryReport.ApodixisPDF();
        apodixisPDF.createPDF(param, document);
        
        //Create the second apodixi of the page.
        
        param[5] = "Μη Ληφθείσα Αδεια";
        param[23] = numFormat.format(remainingDays * 1.2);
        param[24] = decimalFormat.format(apozimiosiMiLifthisasAdeias);
        param[29] = " ";
        param[31] = " ";
        param[64] = decimalFormat.format(apozimiosiMiLifthisasAdeias);
        param[67] = param[64];
        param[68] = param[64];     
        
        apodixisPDF.createPDF(param, document);
        document.newPage();
        
        try {
            if(stm.isClosed())stm = employeegui.EmployeeGUI.con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            if (paretisiDatePicker.getValue().isBefore(LocalDate.of(currentYear, 8, 1))){
                rs1 = stm.executeQuery("SELECT * FROM "+temporaryTableStr+" WHERE id = "
                    +Integer.toString(idChoiceBox.getValue()));
                rs1.first();        
        
                param[5] = "Επίδομα Αδείας";
                param[17] = numFormat.format(rs1.getDouble("salary"));
                param[21] = rs1.getString("reason_isfores_text");
                param[22] = decimalFormat.format(rs1.getDouble("isfores_ergazomenou"));
                param[23] = numFormat.format(rs1.getInt("ensima")*1.2);
                param[24] = decimalFormat.format(rs1.getDouble("adjusted_salary"));
                param[27] = (rs1.getInt("tapit")==0?" ":(rs1.getInt("tapit")==1?
                 "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΠΑΛ)":"ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ-ΝΕΟΙ"));
                param[28] = rs1.getInt("tapit")==0?" ":  
                 decimalFormat.format(rs1.getDouble("tapit_isfores_erg"));
                param[34] = (rs1.getDouble("fmy") == 0 ? " " : "Φόρος" );                        
                param[35] = (rs1.getDouble("fmy") == 0 ? " " : 
                        decimalFormat.format(rs1.getDouble("fmy")));
                param[41] = (rs1.getDouble("isfora_allilegiis") == 0 ? " " : "Εισφoρά Αλληλεγγύης" );                        
                param[42] = (rs1.getDouble("isfora_allilegiis") == 0 ? " " : 
                        decimalFormat.format(rs.getDouble("isfora_allilegiis")));
                param[64] = decimalFormat.format(rs1.getDouble("adjusted_salary"));
                param[65] = decimalFormat.format(0);
                param[66] = decimalFormat.format(rs1.getDouble("kratisis"));
                param[67] = param[64];
                param[68] = decimalFormat.format(rs1.getDouble("kathara"));       
        
                apodixisPDF.createPDF(param, document);
        
                if (rs1 != null)rs1.close();
            }    
        Map<Integer, Double> cutHoursMap = new HashMap<>();       
        rs1 = stm.executeQuery("SELECT id, cut_hours FROM "+reportTableStr);
                while (rs1.next())cutHoursMap.put(rs1.getInt("id"), rs1.getDouble("cut_hours"));
        if (rs1 != null) rs1.close();
        
        double isforesErgBuffer = 0;
        double tapitBuffer = 0;
        double fmyBuffer = 0;
        double isforaAllilegiisBuffer = 0;
        double adjSalaryBuffer = 0;
        rs1 = stm.executeQuery("SELECT *  FROM "+salaryTableStr+" WHERE  id = "
                +Integer.toString(idChoiceBox.getValue())
                +" ORDER BY ta, reason_salary");
        while(rs1.next()){
          if (rs1.getInt("ta")==1 || rs1.getInt("ta")==2){
            if (!("ΑΣΘΕΝΕΙΑ".equals(rs1.getString("reason_salary")))){
                param[5] = rs1.getInt("ta")==1 ? "Τακτικές" : rs1.getInt("ta")==2 ? "Άδεια Ληφθείσα" : "" ;                        
                param[17] = numFormat.format(rs1.getDouble("salary"));
                param[21] = rs1.getString("reason_isfores_text");
                param[22] = decimalFormat.format(rs1.getDouble("isfores_ergazomenou")+isforesErgBuffer);
                param[23] = (rs1.getInt("ta")==1 ? numFormat.format(rs1.getDouble("hours_misthou")/6.66666666666666)
                        : numFormat.format(rs1.getInt("ensima")*1.2));
                param[24] = decimalFormat.format(rs1.getDouble("adjusted_salary"));
                param[27] = (rs1.getInt("tapit")==0?" ":(rs1.getInt("tapit")==1?
                    "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΠΑΛ)":"ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ-ΝΕΟΙ"));
                param[28] = (rs1.getInt("tapit")==0?" ":  
                        decimalFormat.format(rs1.getDouble("tapit_isfores_erg")+tapitBuffer));                
                double cutHours = cutHoursMap.get(rs1.getInt("id"));
                if ((cutHours > 0) && (rs1.getInt("ta")==1)){
                    param[29] = "'Ωρες παρ./απουσ.";
                    double cutSalary = -(rs1.getInt("relation") == 0 ? (rs1.getDouble("salary")/25)*(cutHours/6.66666666) 
                            : rs1.getDouble("salary")*(cutHours/6.66666666));
                    param[30] = decimalFormat.format(-cutHours);
                    param[31] = decimalFormat.format(cutSalary);                    
                    }                
                param[34] = (rs1.getDouble("fmy") == 0 ? " " : "Φόρος" );                        
                param[35] = (rs1.getDouble("fmy") == 0 ? " " : 
                        decimalFormat.format(rs.getDouble("fmy")+fmyBuffer));
                param[41] = (rs1.getDouble("isfora_allilegiis") == 0 ? " " : "Εισφoρά Αλληλεγγύης" );                        
                param[42] = (rs1.getDouble("isfora_allilegiis") == 0 ? " " : 
                        decimalFormat.format(rs1.getDouble("isfora_allilegiis")+isforaAllilegiisBuffer));
                param[64] = decimalFormat.format(rs1.getDouble("adjusted_salary")+adjSalaryBuffer);                       
                param[65] = decimalFormat.format(0);
                param[66] = decimalFormat.format(rs1.getDouble("kratisis")+isforesErgBuffer+
                        tapitBuffer+fmyBuffer+isforaAllilegiisBuffer);
                param[67] = param[64];
                param[68] = decimalFormat.format(rs1.getDouble("kathara")+adjSalaryBuffer-isforesErgBuffer
                    -tapitBuffer-fmyBuffer-isforaAllilegiisBuffer); 
                
                if (rs1.getInt("ta")==2)document.newPage();
                apodixisPDF.createPDF(param, document);               
                
            }else {
                    param[29] = "ΑΣΘΕΝΕΙΑ <=3";
                    param[30] = numFormat.format(rs1.getInt("ensima")*0.6);
                    param[31] = numFormat.format(rs1.getDouble("adjusted_salary"));
                    adjSalaryBuffer = rs1.getDouble("adjusted_salary");
                    isforesErgBuffer = rs1.getDouble("isfores_ergazomenou");
                    tapitBuffer = rs1.getDouble("tapit_isfores_erg");
                    fmyBuffer = rs1.getDouble("fmy");
                    isforaAllilegiisBuffer = rs1.getDouble("isfora_allilegiis");
                }  
        }
        }
        } catch (SQLException ex) {
            Logger.getLogger(ApolisiController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{ if (rs1 != null)rs1.close();}
        
        document.close();
        writer.close();  
            
        
    }
    public void computeEpidomaAdeias(int id) throws SQLException{
       
        try {
            String qry = "UPDATE workers SET apolisi = 0 WHERE id = "+Integer.toString(id);//To make epidoma adeias run
            stm.executeUpdate(qry);
            new SalaryReport.CreateEpidomaAdeiasReport().createDBEpidomaAdeiasReport(employeegui.EmployeeGUI.con, 0);
            String epidomaAdeiasTableStr = "EPIDOMA_ADEIAS_REPORT_"+
                Integer.toString(currentYear) ;
            temporaryTableStr = "EPIDOMA_ADEIAS_TEMPORARY_"+Integer.toString(currentYear) ;
            
            //Check if temporaryTable exists and if not create it            
            
            DatabaseMetaData databaseMetaData = employeegui.EmployeeGUI.con.getMetaData();            
            rs1 = databaseMetaData.getTables(null, null, temporaryTableStr , null);
            if (!rs1.next()) {                                                      
                stm.executeUpdate("CREATE TABLE "+temporaryTableStr+" (ID SMALLINT, First_Name VARCHAR(20), "
    + "Last_Name VARCHAR(20), father_name VARCHAR(20), AM_IKA VARCHAR(20), salary DOUBLE, "      
    + "TA SMALLINT, ensima SMALLINT, hours_misthou DOUBLE, reason_salary VARCHAR(40), "            
    + "adjusted_salary DOUBLE, isfores_ergazomenou DOUBLE, ergodotikes_isfores DOUBLE, "  
    + "mee DOUBLE, total DOUBLE, reason_isfores SMALLINT, reason_isfores_text VARCHAR(40), "
    + "tapit_isfores_erg DOUBLE,  "
    + "tapit_ergod_isfores DOUBLE, tapit_mee DOUBLE, tapit_total DOUBLE, tapit SMALLINT,  "
    + "xartosimo DOUBLE, OGA DOUBLE, FMY DOUBLE, isfora_allilegiis DOUBLE, "
    + "kratisis DOUBLE , "
    + "kathara DOUBLE , "
    + "kostos DOUBLE , prokatavoli DOUBLE, relation SMALLINT, "
    + "pliroteo DOUBLE )");                     
            }    
        
        stm.executeUpdate("DELETE FROM "+temporaryTableStr+" WHERE id = "+Integer.toString(id));
        stm.executeUpdate("INSERT INTO "+temporaryTableStr+
                " SELECT * FROM "+epidomaAdeiasTableStr+" WHERE id = "+Integer.toString(id));
        stm.executeUpdate("DROP TABLE "+epidomaAdeiasTableStr);           
                                        
        } catch (SQLException ex) {
            Logger.getLogger(ApolisiController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
           if(rs1 != null)rs1.close();
        }
    }
    
    public void computeSalary(int id) throws SQLException, InterruptedException, ExecutionException{
        try {
            misthodosia = 0;
            pliroteoMisthodosia = 0;
            adeiaLifthisa = 0;
            pliroteoLiftheisa = 0;
            
            reportTableStr = "REPORT_"+Integer.toString(paretisiDatePicker.getValue().getMonthValue())
                    +"_"+Integer.toString(paretisiDatePicker.getValue().getYear());
            salaryTableStr = "SALARY_"+reportTableStr;
            
            //if the tables exist read the data out of them. If they don't exist create them
            
            DatabaseMetaData databaseMetaData = employeegui.EmployeeGUI.con.getMetaData();
            rs1 = databaseMetaData.getTables(null, null, reportTableStr , null);
            if (!rs1.next()){
                new employeegui.CreateReport().CreateMonthlyDBTable(employeegui.EmployeeGUI.con, paretisiDatePicker.getValue() );
            }
            if(rs1 != null)rs1.close();
            rs1 = databaseMetaData.getTables(null, null, salaryTableStr , null);
            if (!rs1.next()){
                new SalaryReport.CreateSalaryReport().CreateDBSalaryReport(employeegui.EmployeeGUI.con, paretisiDatePicker.getValue() );
            }
            if(rs1 != null)rs1.close();
            rs1 = stm.executeQuery("SELECT kostos, pliroteo, ta  FROM "+salaryTableStr+" WHERE id = "+Integer.toString(id));
            while (rs1.next())
                switch (rs1.getInt(3)){
                    case 1 :{ //there might be astheneia, therefore +=
                        misthodosia += rs1.getInt(1);
                        pliroteoMisthodosia += rs1.getInt(2);                        
                        break;
                    } 
                    case 2 :{
                        adeiaLifthisa = rs1.getInt(1);
                        pliroteoLiftheisa = rs1.getInt(2);                        
                        break;
                    }
                }
                } catch (SQLException ex) {
            Logger.getLogger(ApolisiController.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
           if(rs1 != null)rs1.close();
        }
    }
       
}
