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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ShowIKA {

    private Connection con = null;
    private Statement stm = null;
    private ResultSet rs = null;
    private VBox vBox;
    private TableView table = new TableView();
    private DecimalFormat numFormat ;
    private DecimalFormatSymbols symbols ;
    private String tableString, reasonSalary;
    private final Map<Integer, String> subsidiariesMap = new HashMap<>();
    private final Map<Integer, List<String>> companyMap = new HashMap<>();
    public ObservableList<ObservableList> data; 
    private int company = -1, subsidiary = -1, ta = -1, counter =0;
    private int previousMonth, tableYear, currentYear;
    private double totals[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double ika = 0, ikaEK = 0, ikaVarea = 0, tapitOld = 0, tapitNew = 0;
    private Label messageLabel = new Label();
    private FileOutputStream out = null;
    private FileInputStream in = null;
    

    public VBox showIKA (Connection con, String tableString, ObservableList<ObservableList> data) throws SQLException{
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
                Collections.addAll(value, rs.getString("name"), rs.getString("idos_epihirisis"), rs.getString("phone_number"),
                        rs.getString("ar_mitr_erg"), rs.getString("ipefthinos"), rs.getString("address"),
                        rs.getString("town"), rs.getString("edra"), rs.getString("afm"));
                companyMap.put(rs.getInt("id"), value);
            }
            if (rs != null) rs.close();

            //start building the report with SQL

           rs = stm.executeQuery("SELECT t1.*, t2.subsidiary, t2.company, t2.kat_asfalisis, t2.job_title"
                   + " FROM "+this.tableString+" AS t1, workers AS t2 "
                   + "WHERE t1.id = t2.id "
                   + "ORDER BY company, subsidiary, ta ,reason_salary DESC, t1.last_name, t1.first_name ");

           while(rs.next()){
              if (rs.getInt("ta") != 6 && rs.getInt("ta") != 7 && (rs.getInt("ta") != 1) 
                        || (rs.getInt("ta") == 1 && (rs.getString("astheneia_text").equals("A-TOTAL")
                        || rs.getString("astheneia_text").equals("") || rs.getString("astheneia_text") == null))){
                if((rs.getInt("company") != company) || (rs.getInt("subsidiary") != subsidiary) 
                        || (rs.getInt("ta") != ta)){
                    
                            // Add the totals Row
                    
                   if (!this.data.isEmpty()){
                    ObservableList<String> totalRow = FXCollections.observableArrayList();
                    for (int i =0 ; i<5; i++) totalRow.add(i, " ");
                    totalRow.add(numFormat.format(totals[0]));
                    totalRow.add(" ");
                    for (int i = 1; i<9; i++)totalRow.add(6+i, numFormat.format(totals[i]));
                    totalRow.add(" ");
                    totalRow.add("-1");
                    totalRow.add("-1");
                    this.data.add(totalRow);
                    Arrays.fill(totals, 0);
                    
                        //Add the IKA category totals
                    ObservableList<String> headingIKA = FXCollections.observableArrayList();
                    headingIKA.addAll(" ", " ", "Σύνολα ανά Κατηγορία");
                    for(int i = 3; i < 18; i++)headingIKA.add(i, " ");
                    this.data.add(headingIKA);
                    
                    if(ika != 0){
                    ObservableList<String> rowIKA = FXCollections.observableArrayList();
                    rowIKA.addAll(" ", " ", "IKA ΜΙΚΤΑ-ΤΕΑΜ", numFormat.format(ika));
                    for(int i = 4; i < 18; i++)rowIKA.add(i, " ");
                    this.data.add(rowIKA);  
                    }
                    if(ikaEK != 0){
                    ObservableList<String> rowIKAek = FXCollections.observableArrayList();
                    rowIKAek.addAll(" ", " ", "IKA ΜΙΚΤΑ-ΤΕΑΜ εκ", numFormat.format(ikaEK));
                    for(int i = 4; i < 18; i++)rowIKAek.add(i, " ");
                    this.data.add(rowIKAek);  
                    }
                    if(ikaVarea != 0){
                    ObservableList<String> rowIKAVarea = FXCollections.observableArrayList();
                    rowIKAVarea.addAll(" ", " ", "IKA ΜΙΚΤΑ ΒΑΡΕΑ-ΤΕΑΜ", numFormat.format(ikaVarea));
                    for(int i = 4; i < 18; i++)rowIKAVarea.add(i, " ");
                    this.data.add(rowIKAVarea);  
                    }
                    if(tapitOld != 0){
                    ObservableList<String> rowTapitOld = FXCollections.observableArrayList();
                    rowTapitOld.addAll(" ", " ", "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΠΑΛ)", numFormat.format(tapitOld));
                    for(int i = 4; i < 18; i++)rowTapitOld.add(i, " ");
                    this.data.add(rowTapitOld);  
                    }
                    if(tapitNew != 0){
                    ObservableList<String> rowTapitNew = FXCollections.observableArrayList();
                    rowTapitNew.addAll(" ", " ", "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΝΕΟΙ)", numFormat.format(tapitNew));
                    for(int i = 4; i < 18; i++)rowTapitNew.add(i, " ");
                    this.data.add(rowTapitNew); 
                    }
                    ObservableList<String> totalIKARow = FXCollections.observableArrayList();
                    totalIKARow.addAll(" ", " ", " ", numFormat.format(ika + ikaEK + ikaVarea + tapitOld + tapitNew));
                    for(int i = 4; i < 18; i++)totalIKARow.add(i, " ");
                    this.data.add(totalIKARow);  
                    ika = 0;
                    ikaEK = 0;
                    ikaVarea = 0;
                    tapitOld = 0;
                    tapitNew = 0;
                   }
                    counter = 0;
                    if(rs.getInt("company") != company)company = rs.getInt("company");
                    if(rs.getInt("subsidiary") != subsidiary)subsidiary = rs.getInt("subsidiary");
                    if(rs.getInt("ta") != ta)ta = rs.getInt("ta");                  
                    reasonSalary = (rs.getInt("ta") == 1 ? "Τακτικές" : (rs.getInt("ta") == 2 ?"Άδεια Ληφθείσα" :
                            (rs.getInt("ta") == 3 ? "Δώρο Πάσχα" : (rs.getInt("ta") == 4 ? "Επίδομα Αδείας" :
                            "Δώρο Χριστουγέννων"))));
                    ObservableList<String> heading = FXCollections.observableArrayList(" ", " ", " ",
                            " "," "," "," ","Κατάσταση Ασφάλισης Προσωπικού "+Integer.toString(tableYear)
                    +"/"+Integer.toString(previousMonth)+" "+reasonSalary, " ", " ", " ", " ", " ", " ", " ", " ", "-1", "-1");
                    this.data.add(heading);                  
                    ObservableList<String> subheading1 = FXCollections.observableArrayList(
                    "Επων.Επιχ.:", companyMap.get(company).get(0), " ", "Είδος Επιχ.:", 
                    companyMap.get(company).get(1), " ", "Τηλ.:", companyMap.get(company).get(2), " ",
                    "Αρ.Μητρ.Εργ.:", companyMap.get(company).get(3), " ", " ", " ", " ", " ", "-1", "-1");
                    this.data.add(subheading1);
                    ObservableList<String> subheading2 = FXCollections.observableArrayList(
                    "Υπεύθυνος:", companyMap.get(company).get(4), " ", "Διεύθυνση:", 
                    subsidiariesMap.get(subsidiary), " ", "Πόλη:", companyMap.get(company).get(6), " ",
                    "Πόλη:", companyMap.get(company).get(7), " ", " ", " ", " ", " ", "-1", "-1");
                    this.data.add(subheading2);
                    ObservableList<String> subheading3 = FXCollections.observableArrayList(
                    "ΑΦΜ:", companyMap.get(company).get(8), " ", " ", " ", " ", " ", " ", " ", " ", " ",
                            " ", " ", " ", " ", " ", "-1", "-1");
                    this.data.add(subheading3);
                     ObservableList<String> subheading4 = FXCollections.observableArrayList(
                    " ", " ", "Στοιχεία", "Εργαζομένου ", " ", "Ημέρες ", "Αποδοχές ", " ", " ", "Αποδοχές ", "Εισφορές ",
                            " ", " ", "Επιδοτ. ", " ", "Κατ. ", "-1", "-1");
                    this.data.add(subheading4);
                    ObservableList<String> subheading5 = FXCollections.observableArrayList(
                    "Α/Α", "Α.Μ.", "Επώνυμο", "Ονομα", "Ειδικότητα", "Εργασ.", "Βασικές", "Λοιπές",                          
                       "Σύνολο", "Α.Π.Δ.", "Ασφ/νου", "Σύνολο", "Ε.Ε.Ε", "Εισφορ.", "Καταβ/τέο", "Ασφαλ.", "-1", "-1");
                    this.data.add(subheading5);
                }
                    //now create the rows and be careful to add all 'ta' together (ΑΣΘΕΝΕΙΑ)
                    
                    if ("ΑΣΘΕΝΕΙΑ".equals(rs.getString("reason_salary"))){
                        for (ObservableList<String> r : this.data) {
                           if (!r.get(16).equals(" "))
                            if (Integer.parseInt(r.get(16)) == rs.getInt("id")&&(!r.get(17).equals(" ")))
                              if(Integer.parseInt(r.get(17))==1){
                                int ensimaNew = Integer.parseInt(r.get(5)) + rs.getInt("ensima");                            
                                r.set(5, Integer.toString(ensimaNew));
                                totals[0] += rs.getInt("ensima");
                                double salaryNew = Double.parseDouble(r.get(8)) + rs.getDouble("adjusted_salary");
                                r.set(8, numFormat.format(salaryNew));
                                totals[2] += rs.getDouble("adjusted_salary");
                                double apdNew = Double.parseDouble(r.get(9)) + rs.getDouble("adjusted_salary");
                                r.set(9, numFormat.format(apdNew));
                                totals[3] = totals [2];
                                double isforErgNew = Double.parseDouble(r.get(10)) + rs.getDouble("isfores_ergazomenou")+
                                        rs.getDouble("tapit_isfores_erg");                                
                                r.set(10, numFormat.format(isforErgNew));
                                totals[4]+=rs.getDouble("isfores_ergazomenou")+rs.getDouble("tapit_isfores_erg");
                                double isforTotNew = Double.parseDouble(r.get(11)) + rs.getDouble("total")+
                                        rs.getDouble("tapit_total");
                                r.set(11, numFormat.format(isforTotNew));
                                totals[5] += rs.getDouble("total")+rs.getDouble("tapit_total");
                                double katavNew = Double.parseDouble(r.get(14)) + rs.getDouble("total")+
                                        rs.getDouble("tapit_total");
                                r.set(14, numFormat.format(katavNew));
                                totals[8] += rs.getDouble("total")+rs.getDouble("tapit_total");
                                //Compute IKA totals
                                switch(rs.getInt("reason_isfores")){
                                    case 101 : {ika+=rs.getDouble("total"); break;}
                                    case 102 : {ikaEK+=rs.getDouble("total");break;}
                                    case 106 : {ikaVarea+=rs.getDouble("total");break;}
                                }
                                switch(rs.getInt("tapit")){
                                    case 1 :{tapitOld+=rs.getDouble("tapit_total");break;} 
                                    case 2 :{tapitNew+=rs.getDouble("tapit_total");break;}
                                } 
                                
                                break;
                            }
                        }                       
                    }else{
                        counter++;
                        ObservableList<String> row = FXCollections.observableArrayList();
                        row.add(Integer.toString(counter));
                        row.add(rs.getString("AM_IKA"));
                        row.add(rs.getString("last_name"));
                        row.add(rs.getString("first_name"));
                        row.add(rs.getString("job_title"));
                        row.add(Integer.toString(rs.getInt("ensima")));
                        totals[0] += rs.getInt("ensima");
                        row.add(numFormat.format(rs.getDouble("salary")));
                        row.add(numFormat.format(rs.getInt("ta") == 3 || rs.getInt("ta") == 5 ? rs.getDouble("prosafxisi_dorou"):0));
                        row.add(numFormat.format(rs.getDouble("adjusted_salary")+((rs.getInt("ta") == 3)||(rs.getInt("ta") == 5)?
                            rs.getDouble("prosafxisi_dorou"):0)));
                        if ((rs.getInt("ta") == 3)||(rs.getInt("ta") == 5))
                            totals[1] += rs.getDouble("prosafxisi_dorou");
                        totals[2] += rs.getDouble("adjusted_salary")+((rs.getInt("ta") == 3)||(rs.getInt("ta") == 5)?
                            rs.getDouble("prosafxisi_dorou"):0);
                        row.add(numFormat.format(rs.getDouble("adjusted_salary")+((rs.getInt("ta") == 3)||(rs.getInt("ta") == 5)?
                            rs.getDouble("prosafxisi_dorou"):0)));
                        totals[3] = totals [2];
                        row.add(numFormat.format(rs.getDouble("isfores_ergazomenou")+
                                        rs.getDouble("tapit_isfores_erg")));
                        totals[4]+=rs.getDouble("isfores_ergazomenou")+rs.getDouble("tapit_isfores_erg");                                        
                        row.add(numFormat.format(rs.getDouble("total")+
                                        rs.getDouble("tapit_total")));
                        totals[5] += rs.getDouble("total")+rs.getDouble("tapit_total");
                        row.add(numFormat.format(0));
                        row.add(numFormat.format(0));
                        row.add(numFormat.format(rs.getDouble("total")+
                                        rs.getDouble("tapit_total")));
                        totals[8] += rs.getDouble("total")+rs.getDouble("tapit_total");
                        row.add(Integer.toString(rs.getInt("kat_asfalisis"))); 
                        row.add(Integer.toString(rs.getInt("id"))); //add the id for reference
                        row.add(Integer.toString(rs.getInt("ta"))); //add the ta for future reference
                        // Compute IKA totals
                        switch(rs.getInt("reason_isfores")){
                            case 101 : {ika+=rs.getDouble("total"); break;}
                            case 102 : {ikaEK+=rs.getDouble("total");break;}
                            case 106 : {ikaVarea+=rs.getDouble("total");break;}
                        }
                        switch(rs.getInt("tapit")){
                            case 1:{tapitOld+=rs.getDouble("tapit_total");break;} 
                            case 2:{tapitNew+=rs.getDouble("tapit_total");break;}
                        } 
                        this.data.add(row);                                    
                    } 
                }     
            }
           
           // take care of the last block
           
            ObservableList<String> totalRow = FXCollections.observableArrayList();
            for (int i =0 ; i<5; i++) totalRow.add(i, " ");
            totalRow.add(numFormat.format(totals[0]));
            totalRow.add(" ");
            for (int i = 1; i<9; i++)totalRow.add(6+i, numFormat.format(totals[i]));
            totalRow.add(" ");
            totalRow.add("-1");
            totalRow.add("-1");
            this.data.add(totalRow);
                //Add the IKA category totals
            ObservableList<String> headingIKA = FXCollections.observableArrayList();
            headingIKA.addAll(" ", " ", "Σύνολα ανά Κατηγορία");
            for(int i = 3; i < 18; i++)headingIKA.add(i, " ");
            this.data.add(headingIKA);
                    
            if(ika != 0){
                ObservableList<String> rowIKA = FXCollections.observableArrayList();
                rowIKA.addAll(" ", " ", "IKA ΜΙΚΤΑ-ΤΕΑΜ", numFormat.format(ika));
                for(int i = 4; i < 18; i++)rowIKA.add(i, " ");
                this.data.add(rowIKA);  
            }
            if(ikaEK != 0){
                ObservableList<String> rowIKAek = FXCollections.observableArrayList();
                rowIKAek.addAll(" ", " ", "IKA ΜΙΚΤΑ-ΤΕΑΜ εκ", numFormat.format(ikaEK));
                for(int i = 4; i < 18; i++)rowIKAek.add(i, " ");
                this.data.add(rowIKAek);  
            }
            if(ikaVarea != 0){
                ObservableList<String> rowIKAVarea = FXCollections.observableArrayList();
                rowIKAVarea.addAll(" ", " ", "IKA ΜΙΚΤΑ ΒΑΡΕΑ-ΤΕΑΜ", numFormat.format(ikaVarea));
                for(int i = 4; i < 18; i++)rowIKAVarea.add(i, " ");
                this.data.add(rowIKAVarea);  
            }
            if(tapitOld != 0){
                ObservableList<String> rowTapitOld = FXCollections.observableArrayList();
                rowTapitOld.addAll(" ", " ", "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΠΑΛ)", numFormat.format(tapitOld));
                for(int i = 4; i < 18; i++)rowTapitOld.add(i, " ");
                this.data.add(rowTapitOld);  
            }
            if(tapitNew != 0){
                ObservableList<String> rowTapitNew = FXCollections.observableArrayList();
                rowTapitNew.addAll(" ", " ", "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΝΕΟΙ)", numFormat.format(tapitNew));
                for(int i = 4; i < 18; i++)rowTapitNew.add(i, " ");
                 this.data.add(rowTapitNew); 
            }
            ObservableList<String> totalIKARow = FXCollections.observableArrayList();
            totalIKARow.addAll(" ", " ", " ", numFormat.format(ika + ikaEK + ikaVarea + tapitOld + tapitNew));
            for(int i = 4; i < 18; i++)totalIKARow.add(i, " ");
            this.data.add(totalIKARow);  
       }     
        } catch (SQLException ex) {
            Logger.getLogger(ShowIKA.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
            if (rs != null) rs.close();
            if (stm != null) stm.close();
        }
        
     //prepare the tableView to show the data list
    
    TableColumn<ObservableList, String> counterCol = new TableColumn<>();
    counterCol.setMinWidth(30);
    counterCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(0).toString());
    }); 
    TableColumn<ObservableList, String> idCol = new TableColumn<>();
    idCol.setMinWidth(30);
    idCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(1).toString());
    });  
    TableColumn<ObservableList, String> lastNameCol = new TableColumn<>();
    lastNameCol.setMinWidth(70);
    lastNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(2).toString());
    }); 
    TableColumn<ObservableList, String> firstNameCol = new TableColumn<>();
    firstNameCol.setMinWidth(70);
    firstNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(3).toString());
    }); 
    TableColumn<ObservableList, String> idikotitaCol = new TableColumn<>();
    idikotitaCol.setMinWidth(70);
    idikotitaCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(4).toString());
    }); 
    TableColumn<ObservableList, String> ensimaCol = new TableColumn<>();
    ensimaCol.setMinWidth(30);
    ensimaCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(5).toString());
    });  
    TableColumn<ObservableList, String> vasikesCol = new TableColumn<>();
    vasikesCol.setMinWidth(70);
    vasikesCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(6).toString());
    }); 
    TableColumn<ObservableList, String> lipesCol = new TableColumn<>();
    lipesCol.setMinWidth(50);
    lipesCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(7).toString());
    }); 
    TableColumn<ObservableList, String> total1Col = new TableColumn<>();
    total1Col.setMinWidth(20);
    total1Col.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(8).toString());             
    }); 
    TableColumn<ObservableList, String> apdCol = new TableColumn<>();
    apdCol.setMinWidth(70);
    apdCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(9).toString());
     }); 
    TableColumn<ObservableList, String> asfalCol = new TableColumn<>();
    asfalCol.setMinWidth(70);
    asfalCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(10).toString());
     }); 
    TableColumn<ObservableList, String> total2Col = new TableColumn<>();
    total2Col.setMinWidth(30);
    total2Col.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(11).toString());
     }); 
    TableColumn<ObservableList, String> eeeCol = new TableColumn<>();
    eeeCol.setMinWidth(30);
    eeeCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(12).toString());
     }); 
    TableColumn<ObservableList, String> isforCol = new TableColumn<>();
    isforCol.setMinWidth(70);
    isforCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(13).toString());
     }); 
    TableColumn<ObservableList, String> katavCol = new TableColumn<>();
    katavCol.setMinWidth(70);
    katavCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(14).toString());
     });
    TableColumn<ObservableList, String> katasfCol = new TableColumn<>();
    katasfCol.setMinWidth(70);
    katasfCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(15).toString());
     }); 
    
    
    
    table.setItems(this.data);
    table.getColumns().addAll(counterCol, idCol, lastNameCol, firstNameCol,  
            idikotitaCol, ensimaCol, vasikesCol, lipesCol, total1Col, apdCol, asfalCol, 
            total2Col, eeeCol, isforCol, katavCol, katasfCol);
    
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
        if (workbook.getSheet("ΙΚΑ") != null)           
           workbook.removeSheetAt(workbook.getSheetIndex("ΙΚΑ"));
        HSSFSheet ikaSheet = workbook.createSheet("ΙΚΑ");              
        int rowCounter = 0;
        int cellCounter = 0;
        for (ObservableList<String> r : this.data){
            cellCounter = 0;
            HSSFRow dataRow = ikaSheet.createRow(rowCounter);
            rowCounter++;
            for (String str : r){
                if (cellCounter < r.size()-2)
                    dataRow.createCell(cellCounter).setCellValue(str);
                cellCounter++;
             }
        }
            for (int columnIndex = 0; columnIndex < 16; columnIndex++)
                ikaSheet.autoSizeColumn(columnIndex);   
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
