/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SalaryReport;

import employeegui.ErrorStage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
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
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author egdyn_000
 */
public class ShowArthro {
    
    private Connection con = null;
    private Statement stm = null;
    private ResultSet rs = null;
    private VBox vBox;
    private TableView table = new TableView();
    private DecimalFormatSymbols symbols;
    private DecimalFormat numFormat;
    private String tableString, reasonSalary;
    private final Map<Integer, String> subsidiariesMap = new HashMap<>();
    private final Map<Integer, List<String>> companyMap = new HashMap<>();
    public ObservableList<ObservableList> data;
    private int company = -1, subsidiary = -1, ta = -1, firstCompany;
    private int previousMonth, tableYear, currentYear;   
    private double logar600000 = 0, logar600100 = 0, logar600005 = 0, logar600105 = 0, logar600300 = 0, logar600400 = 0,
            logar550000IKA = 0 , logar550000IKAEK = 0, logar550300tapitOld = 0, logar550300tapitNew = 0,
            logar550000IKAVarea = 0, logar540300imeromisth = 0, logar540300misth = 0, 
            isfAllilmisth = 0, isfAllilimeromisth = 0, logar530000misth = 0, logar530000imeromisth = 0,
            logar600107 = 0, logar600007 = 0, logar600003 = 0, logar600103 = 0,
            logar600500 = 0, logar600501 = 0, logar600108misth = 0, logar600108imeromisth = 0,
            logar600006 = 0, logar600106 = 0;            
    private double hreosi = 0, pistosi = 0, totalHreosi = 0, totalPistosi = 0;
    private Label messageLabel = new Label();
    private FileOutputStream out = null;
    private FileInputStream in = null;
            
    public VBox showArthro (Connection con, String tableString, ObservableList<ObservableList> data) throws SQLException{
         //initialize the Maps
        try {
            this.con = con;
            this.data = data;
            this.tableString = tableString;
            
            symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            numFormat = new DecimalFormat(".##", symbols);
            
                 // find which is the previous month 
            previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
            tableYear = LocalDate.now().minusMonths(1).getYear();
            currentYear = LocalDate.now().getYear();
            String reportTableStr = "REPORT_"+Integer.toString(previousMonth)
                    +"_"+Integer.toString(tableYear);
            if (tableString == null)this.tableString = "SALARY_"+reportTableStr;
            
           if (data == null){
            this.data = FXCollections.observableArrayList();        
       
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery("SELECT * FROM subsidiaries");
            while (rs.next())subsidiariesMap.put(rs.getInt("id"), rs.getString("name"));
            if (rs != null) rs.close();
            rs = stm.executeQuery("SELECT * FROM company_information");
            while (rs.next()){
                List<String> value = new ArrayList<>();
                Collections.addAll(value, rs.getString("name"), rs.getString("idos_epihirisis"));
                companyMap.put(rs.getInt("id"), value);
            }
            if (rs != null) rs.close();
            
            firstCompany = new TreeSet<>(companyMap.keySet()).first(); //TreeSet is a value ordered Set

            //start building the report with SQL

            rs = stm.executeQuery("SELECT t1.*, t2.subsidiary, t2.company "
                   + "FROM "+this.tableString+" AS t1, workers AS t2 "
                   + "WHERE t1.id = t2.id "
                   + "ORDER BY company, subsidiary, ta ");
            
            ObservableList<String> heading = FXCollections.observableArrayList(
                        previousMonth+"/"+tableYear, " ", " ", " ");
            this.data.add(heading); 
            while(rs.next()){
              if((rs.getInt("ta") != 1) || (rs.getInt("ta") == 1 && (rs.getString("astheneia_text").equals("A-TOTAL")
                        || rs.getString("astheneia_text").equals("") || rs.getString("astheneia_text") == null))){
                if((rs.getInt("company") != company) || (rs.getInt("subsidiary") != subsidiary) 
                        || (rs.getInt("ta") != ta)){           
                    
                    // add the rows
                    
                    if (this.data.size() != 1)createRows();  
                    
                    if((rs.getInt("company") != firstCompany)){
                        ObservableList<String> grandTotalRow = FXCollections.observableArrayList("Γενικό Σύνολο", " ", 
                            numFormat.format(totalHreosi), numFormat.format(totalPistosi));
                    this.data.add(grandTotalRow);
                    totalHreosi = 0;
                    totalPistosi = 0;
                }        
                    
                                      
                    //initialize and add the headings
                    
                    logar600000 = 0; logar600100 = 0; logar600005 = 0; logar600105 = 0; logar600300 = 0; logar600400 = 0;
            logar550000IKA = 0 ; logar550000IKAEK = 0; logar550300tapitOld = 0; logar550300tapitNew = 0;
            logar550000IKAVarea = 0; logar540300imeromisth = 0; logar540300misth = 0; isfAllilmisth = 0;
            isfAllilimeromisth = 0; logar530000misth = 0; logar530000imeromisth = 0;
            logar600107 = 0; logar600007 = 0; logar600003 = 0; logar600103 = 0;
            logar600500 = 0; logar600501 = 0; logar600108misth = 0; logar600108imeromisth = 0;
            logar600006 = 0; logar600106 = 0;
            hreosi = 0; pistosi = 0;
                    if(rs.getInt("company") != company)company = rs.getInt("company");
                    if(rs.getInt("subsidiary") != subsidiary)subsidiary = rs.getInt("subsidiary");
                    if(rs.getInt("ta") != ta)ta = rs.getInt("ta");                  
                    reasonSalary = rs.getInt("ta") == 1 ? "Τακτικές" : rs.getInt("ta") == 2 ?"Άδεια Ληφθείσα" :
                            rs.getInt("ta") == 3 ? "Δώρο Πάσχα" : rs.getInt("ta") == 4 ? "Επίδομα Αδείας" :
                            rs.getInt("ta") == 5 ?"Δώρο Χριστουγέννων" : rs.getInt("ta") == 6 ? 
                            "Αποζημίωση Απόλυσης" : "Μη Ληφθείσα Αδεια" ;
                    ObservableList<String> heading0 = FXCollections.observableArrayList(
                        companyMap.get(rs.getInt("company")).get(0)+" "+ companyMap.get(rs.getInt("company")).get(1)
                        , " ", " ", " "); 
                    ObservableList<String> heading1 = FXCollections.observableArrayList(
                        subsidiariesMap.get(rs.getInt("subsidiary")) , " ", " ", " ");
                    ObservableList<String> heading2 = FXCollections.observableArrayList(
                        reasonSalary, " ", "ΧΡΕΩΣΗ", "ΠΙΣΤΩΣΗ");
                    this.data.addAll(heading0, heading1, heading2);
                }
                
                //add data to the variables
                   
                switch (rs.getInt("relation")){
                    case 0 :{
                        if(rs.getInt("ta") == 1){      
                            logar600000+=rs.getDouble("adjusted_salary");
                            if(rs.getString("astheneia_text").equals("A-TOTAL")){                                            
                                logar600105+=(rs.getDouble("adjusted_salary")-rs.getDouble("epidotisi_astheneias"));
                                logar600000-=(rs.getDouble("adjusted_salary"));
                            }
                        }
                        else if (rs.getInt("ta") == 2)logar600006+=rs.getDouble("adjusted_salary");
                        else if ((rs.getInt("ta") == 3)||(rs.getInt("ta") == 5))
                            logar600003+=rs.getDouble("adjusted_salary")+rs.getDouble("prosafxisi_dorou");
                        else if (rs.getInt("ta") == 4)logar600007+=rs.getDouble("adjusted_salary");
                        else if (rs.getInt("ta") == 6)logar600500+=rs.getDouble("adjusted_salary");
                        else if (rs.getInt("ta") == 7)logar600108misth+=rs.getDouble("adjusted_salary");
                        logar600300+=rs.getDouble("ergodotikes_isfores");
                        logar540300misth+=rs.getDouble("FMY");
                        isfAllilmisth+=rs.getDouble("isfora_allilegiis");
                        logar530000misth+=rs.getDouble("kathara");
                        break;}
                    case 1 :{  
                        if(rs.getInt("ta") == 1){      
                            logar600100+=rs.getDouble("adjusted_salary");
                            if(rs.getString("astheneia_text").equals("A-TOTAL")){                                            
                                logar600105+=(rs.getDouble("adjusted_salary")-rs.getDouble("epidotisi_astheneias"));
                                logar600100-=(rs.getDouble("adjusted_salary"));
                            }
                        }
                        else if (rs.getInt("ta") == 2)logar600106+=rs.getDouble("adjusted_salary");
                        else if ((rs.getInt("ta") == 3)||(rs.getInt("ta") == 5))
                            logar600103+=rs.getDouble("adjusted_salary")+rs.getDouble("prosafxisi_dorou");
                        else if (rs.getInt("ta") == 4)logar600107+=rs.getDouble("adjusted_salary");
                        else if (rs.getInt("ta") == 6)logar600501+=rs.getDouble("adjusted_salary");
                        else if (rs.getInt("ta") == 7)logar600108imeromisth+=rs.getDouble("adjusted_salary");
                        logar600400+=rs.getDouble("ergodotikes_isfores");
                        logar540300imeromisth+=rs.getDouble("FMY");
                        isfAllilimeromisth+=rs.getDouble("isfora_allilegiis");
                        logar530000imeromisth+=rs.getDouble("kathara");
                        break;}
                    }                    
                    switch (rs.getInt("reason_isfores")){
                        case 101 : {
                            logar550000IKA+=rs.getDouble("total");
                            break;
                        }
                        case 102 :{
                            logar550000IKAEK+=rs.getDouble("total");
                            break;
                        }
                        case 106 :{
                            logar550000IKAVarea+=rs.getDouble("total");
                            break; 
                        }
                    }                   
                    switch(rs.getInt("tapit")){
                        case 1 :{logar550300tapitOld+=rs.getDouble("tapit_total");break;} 
                        case 2 :{logar550300tapitNew+=rs.getDouble("tapit_total");break;}
                    } 
                }    
            }             
        

    //deal with the last block
        rs.first();
        createRows();
        ObservableList<String> grandTotalRow = FXCollections.observableArrayList("Γενικό Σύνολο", " ", 
                numFormat.format(totalHreosi), numFormat.format(totalPistosi));
        this.data.add(grandTotalRow);
       } 
        } catch (SQLException ex) {
            Logger.getLogger(ShowIKA.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
            if (rs != null) rs.close();
            if (stm != null) stm.close();
        }
        
     //prepare the tableView to show the data list
    
    TableColumn<ObservableList, String> arthroCol = new TableColumn<>();
    arthroCol.setMinWidth(400);
    arthroCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(0).toString());
    }); 
    TableColumn<ObservableList, String> arthroNumCol = new TableColumn<>();
    arthroNumCol.setMinWidth(150);
    arthroNumCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(1).toString());
    });  
    TableColumn<ObservableList, String> hreosiCol = new TableColumn<>();
    hreosiCol.setMinWidth(150);
    hreosiCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(2).toString());
    }); 
    TableColumn<ObservableList, String> pistosiCol = new TableColumn<>();
    pistosiCol.setMinWidth(150);
    pistosiCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(3).toString());
    }); 
         
    table.setItems(this.data);
    table.getColumns().addAll(arthroCol, arthroNumCol, hreosiCol, pistosiCol);
    
    table.setPrefHeight(650);    
    HBox hBox = new HBox(50);
    hBox.setAlignment(Pos.CENTER);
    Button excelBtn = new Button("EXCEL");
    excelBtn.setOnAction(e->{
        try {
            showXL();
        } catch (IOException ex) {
            Logger.getLogger(ShowPliromi.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }
    });
    hBox.getChildren().addAll(excelBtn, messageLabel);
    vBox = new VBox();
    vBox.setSpacing(5);
    vBox.setPadding(new Insets(10, 0, 0, 10));
    vBox.setAlignment(Pos.CENTER);
    vBox.getChildren().addAll(table, hBox);
    return vBox;
    }
    public void createRows() throws SQLException{        
        if(logar600000 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Εμμισθου Προσ/κού", "600000", numFormat.format(logar600000), " ");
            this.data.add(row);
        }
        if(logar600006 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Εμμισθου Προσ/κού", "600006", numFormat.format(logar600006), " ");
            this.data.add(row);
        }
        if(logar600003 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Εμμισθου Προσ/κού", "600003", numFormat.format(logar600003), " ");
            this.data.add(row);
        }
        if(logar600007 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Εμμισθου Προσ/κού", "600007", numFormat.format(logar600007), " ");
            this.data.add(row);
        }
        if(logar600500 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Εμμισθου Προσ/κού", "600500", numFormat.format(logar600500), " ");
            this.data.add(row);
        }
        if(logar600108misth != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Εμμισθου Προσ/κού", "600108", numFormat.format(logar600108misth), " ");
            this.data.add(row);
        }               
        if(logar600100 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600100", numFormat.format(logar600100), " ");
            this.data.add(row);
        }
        if(logar600106 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600106", numFormat.format(logar600106), " ");
            this.data.add(row);
        }
        if(logar600103 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600103", numFormat.format(logar600103), " ");
            this.data.add(row);
        }
        if(logar600107 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600107", numFormat.format(logar600107), " ");
            this.data.add(row);
        }
        if(logar600501 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600501", numFormat.format(logar600501), " ");
            this.data.add(row);
        }
        if(logar600108imeromisth != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600108", numFormat.format(logar600108imeromisth), " ");
            this.data.add(row);
        }  
        if(logar600005 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Εμμισθου Προσ/κού Ασθένεια", "600005", numFormat.format(logar600005), " ");
            this.data.add(row);
        }
        if(logar600105 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Αμοιβές Ημ/σθιου Προσ/κού Ασθένεια", "600105", numFormat.format(logar600105), " ");
            this.data.add(row);
        }
        if(logar600300 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Εργοδ.Εισφ.Εμμισθου Προσ/κου ΙΚΑ", "600300", numFormat.format(logar600300), " ");
            this.data.add(row);
        }
        if(logar600400 != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Εργοδ.Εισφ.Ημ/σθιου Προσ/κου ΙΚΑ", "600400", numFormat.format(logar600400), " ");
            this.data.add(row);
        }
        if(logar550000IKA != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "ΙΚΑ ΜΙΚΤΑ-ΤΕΑΜ", "550000-ΙΚΑ", " ", numFormat.format(logar550000IKA));
            this.data.add(row);
        }
        if(logar550000IKAEK != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "ΙΚΑ ΜΙΚΤΑ-ΤΕΑΜ εκ", "550000-ΙΚΑ", " ", numFormat.format(logar550000IKAEK));
            this.data.add(row);
        }
        if(logar550000IKAVarea != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "ΙΚΑ ΜΙΚΤΑ ΒΑΡΕΑ-ΤΕΑΜ", "550000-ΙΚΑ", " ", numFormat.format(logar550000IKAVarea));
            this.data.add(row);
        }
        if(logar550300tapitOld != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΠΑΛ)", "550000-ΤΑΠΙΤ", " ", numFormat.format(logar550300tapitOld));
            this.data.add(row);
        }
        if(logar550300tapitNew != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΝΕΟΙ)", "550000-ΤΑΠΙΤ", " ", numFormat.format(logar550300tapitNew));
            this.data.add(row);
        }                    
        if(logar540300misth != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Φόρος Έμμισθου Προσ.", "540300", " ", numFormat.format(logar540300misth));
                        this.data.add(row);
                    }
        if(logar540300imeromisth != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Φόρος Ημ/μίσθιου Προσ.", "540300", " ", numFormat.format(logar540300imeromisth));
                        this.data.add(row);
                    }
        if(isfAllilmisth != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Εισφορά Αλληλεγγύης Έμμισθου Προσ.", "540301", " ", numFormat.format(isfAllilmisth));
                        this.data.add(row);
                    }
        if(isfAllilimeromisth != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Εισφορά Αλληλεγγύης Ημ/μίσθιου Προσ.", "540301", " ", numFormat.format(isfAllilimeromisth));
                        this.data.add(row);
                    }
        if(logar530000misth != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Δικαιούχοι Αμοιβών Έμμισθου Προσ.", "530000", " ", numFormat.format(logar530000misth));
                        this.data.add(row);
                    }
        if(logar530000imeromisth != 0){
            ObservableList<String> row = FXCollections.observableArrayList(
                "Δικαιούχοι Αμοιβών Ημ/μίσθιου Προσ.", "530000", " ", numFormat.format(logar530000imeromisth));
                        this.data.add(row);
                    }
        hreosi = logar600000 + logar600100 + logar600005 + logar600105 + logar600300 + logar600400 +
                logar600107 + logar600007 + logar600003 + logar600103 + 
                logar600500 + logar600501 + logar600108misth + logar600108imeromisth +
                logar600006 + logar600106 ;                
        pistosi = logar550000IKA + logar550000IKAEK + logar550000IKAVarea + logar550300tapitOld +
                logar550300tapitNew + logar540300misth + logar540300imeromisth + isfAllilmisth +
                isfAllilimeromisth + logar530000misth + logar530000imeromisth;
        totalHreosi += hreosi;
        totalPistosi += pistosi;
        ObservableList<String> totalsRow = FXCollections.observableArrayList("Σύνολα ", " ", 
                numFormat.format(hreosi), numFormat.format(pistosi));
        this.data.add(totalsRow);
    }
    public void showXL() throws IOException{
        HSSFWorkbook workbook = null;
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
        if (workbook.getSheet("Άρθρο") != null)           
           workbook.removeSheetAt(workbook.getSheetIndex("Άρθρο"));
        HSSFSheet arthroSheet = workbook.createSheet("Άρθρο");            
        int rowCounter = 0;
        int cellCounter = 0;
        for (ObservableList<String> r : this.data){
            cellCounter = 0;
            HSSFRow dataRow = arthroSheet.createRow(rowCounter);
            rowCounter++;
            for (String str : r){
                dataRow.createCell(cellCounter).setCellValue(str);
                cellCounter++;
             }
        }
            for (int columnIndex = 0; columnIndex < 4; columnIndex++)
                arthroSheet.autoSizeColumn(columnIndex);     
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
}
