/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.util.JsfUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author evgero
 */
@Named("showMisthodotiki")
@SessionScoped
public class ShowMisthodotiki implements Serializable{  
    
    private Connection con = null;
    Statement stm = null;
    ResultSet rs = null;
    String tableString = null;
    
    private List<List<String>> data ;
    private DecimalFormatSymbols symbols ;
    private DecimalFormat numFormat ;
    int formerSubsidiary;
    private Map<Integer, List<String>> subtotalsMap = new HashMap<>();
    private final Map<Integer, String> subsidiariesMap = new HashMap<>();
    private Label messageLabel = new Label();
    private FileOutputStream out = null;
    private FileInputStream in = null;

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
        
    public void showMisthodotiki(Connection con, String tableString, List<List<String>> data) throws SQLException{
        
       
       this.con = con;
       this.data = data;
       this.tableString = tableString;
       
       /* find which is the previous month */
       int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
       int tableYear = LocalDate.now().minusMonths(1).getYear(); 
       int currentYear = LocalDate.now().getYear();                                              
       String reportTableStr = "REPORT_"+Integer.toString(previousMonth)
                     +"_"+Integer.toString(tableYear);
       if (tableString == null)this.tableString = "SALARY_"+reportTableStr; 
        
       symbols = new DecimalFormatSymbols();
       symbols.setDecimalSeparator('.');
       numFormat = new DecimalFormat(".##", symbols);
        
       if (data == null){
          this.data = new ArrayList<>(); 
        try {  
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String qry = "SELECT * FROM subsidiaries";
            rs = stm.executeQuery(qry);
            while (rs.next()){
                subsidiariesMap.put(rs.getInt("id"), rs.getString("name"));
            }} catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
            if(rs != null)rs.close();
            if(stm != null)stm.close();
        }     
            
            
        try {  
                       
            String query = "SELECT t1.*, t2.subsidiary FROM "+this.tableString+" AS t1, workers AS t2 "
                    + "WHERE t1.id = t2.id ORDER BY subsidiary, t1.id, ta, reason_salary DESC ";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery(query);
            int counter = 0;            
            while(rs.next()){
                if((rs.getInt("ta") != 1) || (rs.getInt("ta") == 1 && (rs.getString("astheneia_text").equals("A-TOTAL")
                        || rs.getString("astheneia_text").equals("") || rs.getString("astheneia_text") == null))){
                ObservableList<String> row1 = FXCollections.observableArrayList();
                row1.add(Integer.toString(counter++));
                row1.add(Integer.toString(rs.getInt("id")));
                row1.add(rs.getString("last_name"));
                row1.add(" ");
                row1.add(rs.getString("first_name"));
                row1.add(rs.getString("father_name"));
                row1.add(rs.getString("am_ika"));
                row1.add(" ");
                row1.add(" ");
                row1.add(" ");
                row1.add(" ");
                row1.add(" ");
                row1.add(" ");
                row1.add(" ");
                row1.add(" ");
                row1.add(" ");
                row1.add(numFormat.format(rs.getDouble("salary"))); 
                row1.add("-1");
                
                this.data.add(row1); //split the rows so that they fit in the screen.
                
                ObservableList<String> row2 = FXCollections.observableArrayList();
                row2.add((rs.getInt("ta") == 1 ? "ΤΑ" :(rs.getInt("ta") == 2 ? "ΑΛ" :
                        (rs.getInt("ta") == 3 ? "ΔΠ" : (rs.getInt("ta") == 4 ? "ΕΑ" :(rs.getInt("ta") == 5 ? "ΔΧ" : 
                               (rs.getInt("ta") == 6 ? "AA" : "ΜΛ")))))));
                row2.add(Integer.toString(rs.getInt("ensima")));
                row2.add(numFormat.format(rs.getDouble("hours_misthou")));
                row2.add(rs.getString("reason_salary"));
                row2.add(numFormat.format(rs.getInt("ta") == 6 ? rs.getDouble("adjusted_salary")/1.1666666666 :
                        rs.getDouble( "adjusted_salary")));
                row2.add(numFormat.format(rs.getDouble("isfores_ergazomenou")));
                row2.add(numFormat.format(rs.getDouble("ergodotikes_isfores")));
                row2.add(numFormat.format(rs.getInt("MEE")));
                //row2.add(numFormat.format(rs.getDouble("MEE")));
                //row2.add(numFormat.format(rs.getInt("total")));
                row2.add(numFormat.format(rs.getDouble("total")));
                row2.add(rs.getInt("ta") == 6 || rs.getInt("ta") == 7 ? "" : rs.getString("reason_isfores_text"));
                row2.add(" ");
                row2.add(" ");
                row2.add(" ");
                row2.add(" ");
                row2.add(" ");
                row2.add(" ");
                row2.add(" ");  
                row2.add("-2");
                
                this.data.add(row2);
                
                if ((rs.getInt("tapit") != 0)||(rs.getInt("ta") == 3)||(rs.getInt("ta") == 5)
                        ||(rs.getInt("ta") == 6)||(rs.getInt("ta") == 1 && rs.getDouble("epidotisi_astheneias") != 0 )){ //add tapit row or ΔΠ or ΔΧ or AA or ΜΛ                       
                ObservableList<String> row3 = FXCollections.observableArrayList();
                row3.add(" ");
                row3.add(" ");
                row3.add(" ");
                if((rs.getInt("ta") == 3)||(rs.getInt("ta") == 5))row3.add(rs.getString("reason_salary_2"));
                else if (rs.getInt("ta") == 6)row3.add("ΠΡΟΣΑΥΞΗΣΗ ΔΩΡΟΥ");
                else if (rs.getInt("ta") == 1 && rs.getDouble("epidotisi_astheneias") != 0)row3.add("ΕΠΙΔΟΤΗΣΗ ΑΣΘΕΝΕΙΑΣ");
                else row3.add(" ");
                if((rs.getInt("ta") == 3)||(rs.getInt("ta") == 5))
                    row3.add(numFormat.format(rs.getDouble("prosafxisi_dorou")));
                else if (rs.getInt("ta") == 6)row3.add(numFormat.format(rs.getDouble("adjusted_salary")*(0.1666666666/1.1666666666)));
                else if (rs.getInt("ta") == 1 && rs.getDouble("epidotisi_astheneias") != 0)row3.add(numFormat.format(rs.getDouble("epidotisi_astheneias")));
                else row3.add(" ");
                if (rs.getInt("tapit") != 0){
                    row3.add(numFormat.format(rs.getDouble("tapit_isfores_erg")));
                    row3.add(numFormat.format(rs.getDouble("tapit_ergod_isfores")));
                    row3.add(numFormat.format(rs.getInt("tapit_MEE")));
                    //row3.add(numFormat.format(rs.getInt("tapit_total")));
                    row3.add(numFormat.format(rs.getDouble("tapit_total")));
                    row3.add((rs.getInt("tapit")==1?
                        "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ (ΠΑΛ)":"ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ (ΝΕΟΙ)"));
                }else{
                    row3.add(" ");
                    row3.add(" ");
                    row3.add(" ");
                    row3.add(" ");
                    row3.add(" ");
                }
                row3.add(" ");
                row3.add(" ");
                row3.add(" ");
                row3.add(" ");
                row3.add(" ");
                row3.add(" ");
                row3.add(" ");
                row3.add("-3");
                
                this.data.add(row3);  
                }
                
                ObservableList<String> row4 = FXCollections.observableArrayList();
                row4.add(" ");
                row4.add(" ");
                row4.add(" ");
                row4.add(" ");
                row4.add(numFormat.format(rs.getDouble("adjusted_salary")+((rs.getInt("ta") == 3)||(rs.getInt("ta") == 5)?
                    rs.getDouble("prosafxisi_dorou"):0) 
                    - (rs.getInt("ta") == 1 && rs.getDouble("epidotisi_astheneias") != 0 ? rs.getDouble("epidotisi_astheneias") : 0)));
                row4.add(numFormat.format(rs.getDouble("isfores_ergazomenou")+rs.getDouble("tapit_isfores_erg")));
                row4.add(numFormat.format(rs.getDouble("ergodotikes_isfores")+rs.getDouble("tapit_ergod_isfores")));
                //row4.add(numFormat.format(rs.getDouble("MEE")+rs.getDouble("tapit_mee")));
                row4.add(numFormat.format(rs.getInt("MEE")+rs.getDouble("tapit_mee")));
                row4.add(numFormat.format(rs.getDouble("total")+rs.getDouble("tapit_total")));
                //row4.add(numFormat.format(rs.getInt("total")+rs.getDouble("tapit_total")));
                row4.add(numFormat.format(rs.getDouble("xartosimo")));
                row4.add(numFormat.format(rs.getDouble("oga")));
                row4.add(numFormat.format(rs.getDouble("fmy")+rs.getDouble("isfora_allilegiis")));
                row4.add(numFormat.format(rs.getDouble("kratisis")));
                row4.add(numFormat.format(rs.getDouble("kathara")));
                row4.add(numFormat.format(rs.getDouble("kostos")));
                row4.add(numFormat.format(rs.getDouble("prokatavoli")));
                row4.add(numFormat.format(rs.getDouble("pliroteo")));
                row4.add(Integer.toString(rs.getInt("subsidiary")));
                
                this.data.add(row4);
              }   
                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
            if(rs != null)rs.close();
            if(stm != null)stm.close();
        }
     
        //Compute the subtotals and put them in the subtotalsMap
        
    for (List<String> r : this.data ){  
        formerSubsidiary = (Integer.parseInt(r.get(17)) < 0 ? 
                -1 : Integer.parseInt(r.get(17)));
        if (formerSubsidiary >= 0) break;}
     
    int ensimaTotal = 0, currEnsima = 0;
    double hoursTotal = 0, currHours = 0;
    double[] subtotals = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};    
    for (List<String> r : this.data ){
        if (Integer.parseInt(r.get(17)) == -2){   
            currEnsima = Integer.parseInt(r.get(1));
            currHours = Double.valueOf(r.get(2));
            ensimaTotal += currEnsima;
            hoursTotal +=  currHours;             
             }     
        if (Integer.parseInt(r.get(17)) == formerSubsidiary)              
            for (int i = 0; i < 13; i++) subtotals[i] += Double.valueOf(r.get(4+i));
        else if (!(Integer.parseInt(r.get(17)) < 0)){
            int currentPosition = this.data.indexOf(r);
            int insertionPosition = (Integer.parseInt((String)(this.data.get(currentPosition - 1)
                    .get(17))) == -3 ? currentPosition - 3 : currentPosition - 2);
            ObservableList<String> subtotalRow = FXCollections.observableArrayList();                                    
            subtotalRow.add(" ");
            subtotalRow.add(numFormat.format(ensimaTotal - currEnsima));
            subtotalRow.add(numFormat.format(hoursTotal - currHours));
            subtotalRow.add(subsidiariesMap.get(formerSubsidiary));
            for (int i = 0; i < 13; i++) subtotalRow.add(numFormat.format( subtotals[i] ));                                              
            subtotalRow.add("SUBTOTAL");
            subtotalsMap.put(insertionPosition, subtotalRow);             
            ensimaTotal = currEnsima; 
            hoursTotal = currHours;
            for (int i = 0; i < 13; i++) subtotals[i] = Double.valueOf(r.get(4+i));            
            formerSubsidiary = Integer.parseInt(r.get(17));
    }           
    }
          ObservableList<String> subtotalRow = FXCollections.observableArrayList();                                    
            subtotalRow.add(" ");
            subtotalRow.add(numFormat.format(ensimaTotal - currEnsima));
            subtotalRow.add(numFormat.format(hoursTotal - currHours));
            subtotalRow.add(subsidiariesMap.get(formerSubsidiary));
            for (int i = 0; i < 13; i++) subtotalRow.add(numFormat.format( subtotals[i] ));                                              
            subtotalRow.add("SUBTOTAL");
            subtotalsMap.put(this.data.size(), subtotalRow);                
                 
     //add the subtotals rows to the data list
    int counter1 = 0;
    for (Map.Entry<Integer, List<String>> entry : subtotalsMap.entrySet()){
        this.data.add(entry.getKey()+counter1, entry.getValue());        
        counter1++;
    }
     //add at the end the grand total row.
    ObservableList<String> grandTotalRow = FXCollections.observableArrayList();
    double subgrandTotals[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    for (List<String> subRow : subtotalsMap.values()){
        subgrandTotals[0] += Double.valueOf(subRow.get(1));
        subgrandTotals[1] += Double.valueOf(subRow.get(2));
        for (int i = 0; i < 13; i++) subgrandTotals[i+2] += Double.valueOf(subRow.get(4+i));
    }
    grandTotalRow.add(" ");
    grandTotalRow.add(numFormat.format(subgrandTotals[0]));
    grandTotalRow.add(numFormat.format(subgrandTotals[1]));
    grandTotalRow.add("ΓΕΝΙΚΟ ΣΥΝΟΛΟ");
    for (int i = 2; i < 15; i++) grandTotalRow.add(numFormat.format( subgrandTotals[i] ));
    
    this.data.add(grandTotalRow);
   } 
    //prepare the tableView to show the data list
    
     
    TableColumn<ObservableList, String> idCol = new TableColumn<>("Κωδικός\nΗμέρες");
    idCol.setMinWidth(30);
    idCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(1).toString());
    });  
    TableColumn<ObservableList, String> lastNameCol = new TableColumn<>("Επώνυμο\nΩρες");
    lastNameCol.setMinWidth(70);
    lastNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(2).toString());
    }); 
    TableColumn<ObservableList, String> reasonSalaryCol = new TableColumn<>("\nΑποδοχές");
    reasonSalaryCol.setMinWidth(70);
    reasonSalaryCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(3).toString());
    }); 
    TableColumn<ObservableList, String> firstNameCol = new TableColumn<>("Ονομα\nΑξία");
    firstNameCol.setMinWidth(70);
    firstNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(4).toString());
    });  
    TableColumn<ObservableList, String> fatherNameCol = new TableColumn<>("Πατρώνυμο\nΕισφ.Εργαζομ.");
    fatherNameCol.setMinWidth(70);
    fatherNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(5).toString());
    }); 
    TableColumn<ObservableList, String> amIKACol = new TableColumn<>("ΑΜ ΙΚΑ\nΕργοδ.Εισφ.");
    amIKACol.setMinWidth(50);
    amIKACol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(6).toString());
    }); 
    TableColumn<ObservableList, String> meeCol = new TableColumn<>("\nMEE");
    meeCol.setMinWidth(20);
    meeCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(7).toString());             
    }); 
    TableColumn<ObservableList, String> totalCol = new TableColumn<>("\nΣύνολο");
    totalCol.setMinWidth(70);
    totalCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(8).toString());
     }); 
    TableColumn<ObservableList, String> paratirisisCol = new TableColumn<>("Παρατηρήσεις\nΧαρ/μο");
    paratirisisCol.setMinWidth(70);
    paratirisisCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(9).toString());
     }); 
    TableColumn<ObservableList, String> ogaCol = new TableColumn<>("\nΟ.Γ.Α.");
    ogaCol.setMinWidth(30);
    ogaCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(10).toString());
     }); 
    TableColumn<ObservableList, String> fmyCol = new TableColumn<>("\nΦ.Μ.Υ.");
    fmyCol.setMinWidth(30);
    fmyCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(11).toString());
     }); 
    TableColumn<ObservableList, String> kratisisCol = new TableColumn<>("\nΚρατήσεις");
    kratisisCol.setMinWidth(70);
    kratisisCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(12).toString());
     }); 
    TableColumn<ObservableList, String> katharaCol = new TableColumn<>("\nΚαθ.Αποδ.");
    katharaCol.setMinWidth(70);
    katharaCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(13).toString());
     });
    TableColumn<ObservableList, String> kostosCol = new TableColumn<>("Μ.Κ.\nΚόστος");
    kostosCol.setMinWidth(70);
    kostosCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(14).toString());
     }); 
    TableColumn<ObservableList, String> prokatavoliCol = new TableColumn<>("Δάνειο\nΠροκ/λή");
    prokatavoliCol.setMinWidth(30);
    prokatavoliCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(15).toString());
     }); 
    TableColumn<ObservableList, String> miktaCol = new TableColumn<>("Μικτ.Αποδ.\nΠληρωτέο");
    miktaCol.setMinWidth(70);
    miktaCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(16).toString());
     });     
      
    }
    public void showXL() throws IOException{
        HSSFWorkbook workbook = null;
        HSSFSheet misthodotikiSheet = null;
        String excelPath = "C:\\EmployeeGUI\\EmployeeGUIOutput\\"
                   + ""+this.tableString+".xls"; 
        try { 
        File excelFile = new File(excelPath);
        if (excelFile.exists()){
            in = new FileInputStream(excelFile); 
            workbook = new HSSFWorkbook(in);
        }else{            
            workbook = new HSSFWorkbook();  
        }         
        if (workbook.getSheet("Μισθοδοτική Κατάσταση") != null)           
           workbook.removeSheetAt(workbook.getSheetIndex("Μισθοδοτική Κατάσταση"));        
        misthodotikiSheet = workbook.createSheet("Μισθοδοτική Κατάσταση");        
        HSSFRow heading1 = misthodotikiSheet.createRow(0);
        HSSFRow heading2 = misthodotikiSheet.createRow(1);
        int counter = 0;
     //   for (TableColumn<ObservableList, ?> col : table.getColumns()){       
     //   heading1.createCell(counter).setCellValue(col.getText().substring(0, col.getText().indexOf("\n")));
      //  heading2.createCell(counter).setCellValue(col.getText().substring(col.getText().indexOf("\n"),
       //         col.getText().length()));
      //  counter++;
       // } 
        int rowCounter = 2;
        int cellCounter = 0;
        for (List<String> r : this.data){
            cellCounter = 0;
            HSSFRow dataRow = misthodotikiSheet.createRow(rowCounter);
            rowCounter++;
            for (String str : r){
                if (cellCounter < r.size()-1)
                   dataRow.createCell(cellCounter).setCellValue(str);              
                cellCounter++;
             }
        }
        for (int columnIndex = 0; columnIndex < 17; columnIndex++)
                misthodotikiSheet.autoSizeColumn(columnIndex);
        out = new FileOutputStream(excelFile);
        workbook.write(out);           
        messageLabel.setText("Excel written successfully..");
             
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
           if(out != null)out.close();
           if(in!= null)in.close();
       }
  }
}

