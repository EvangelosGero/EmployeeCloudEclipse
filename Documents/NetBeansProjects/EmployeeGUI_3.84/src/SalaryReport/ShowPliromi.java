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
import java.util.List;
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
import utilities.DropIfExists;

/**
 *
 * @author egdyn_000
 */
public class ShowPliromi {
    private VBox vBox;
    private Connection con = null;
    Statement stm = null;
    ResultSet rs, rs1 = null;
    String tableString;
    private TableView<ObservableList> table = new TableView<>();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private DecimalFormatSymbols symbols;
    private DecimalFormat numFormat;
    private double total = 0;
    private FileOutputStream out;
    private FileInputStream in = null;
    private Label messageLabel = new Label();
    private int previousMonth, tableYear, currentYear, update; 
  
  public VBox showPliromi(Connection con, String tableString) throws SQLException{
    
    try{  
        this.con = con;
        this.tableString = tableString;
        
        symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        numFormat = new DecimalFormat(".##", symbols);
        
                 /* find which is the previous month */         
        previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
        tableYear = LocalDate.now().minusMonths(1).getYear(); 
        currentYear = LocalDate.now().getYear();                                              
        String reportTableStr = "REPORT_"+Integer.toString(previousMonth)
                    +"_"+Integer.toString(tableYear);
        if (tableString == null)this.tableString = "SALARY_"+reportTableStr;                           
        
        DropIfExists.exec("VIEW", "TOTALS");
        String query;
        if(this.tableString.charAt(0) == 'S')   //Astheneia exists only in TA=1 Taktikes apodoxes
            query = "CREATE VIEW totals AS SELECT SUM(pliroteo) AS total, id  FROM "+this.tableString+
                " WHERE (ta != 1) OR (ta=1 AND  (astheneia_text = 'A-TOTAL' OR astheneia_text = '' OR astheneia_text IS NULL ))"
                + " GROUP BY id ";
        else    query = "CREATE VIEW totals AS SELECT SUM(pliroteo) AS total, id  FROM "+this.tableString                
                + " GROUP BY id ";
        
         stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
         int   update = stm.executeUpdate(query);
         if (stm != null){stm.close();} 
         
        query = "SELECT t1.total, t2.first_name, t2.last_name, t2.father_name FROM "
                + "totals AS t1 "
                + "INNER JOIN workers AS t2 ON t1.id = t2.id "
                + "ORDER BY last_name, first_name";
        
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rs = stm.executeQuery(query);
        ObservableList<String> heading = FXCollections.observableArrayList();
        heading.add(" ");
        //heading.add(tableString.substring(14, tableString.length()));
        heading.add(this.tableString);
        heading.add(" ");
        heading.add(" ");
        data.add(heading); 
        while (rs.next()){
            double totalPart = rs.getDouble("total");
            total += totalPart;
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(numFormat.format(totalPart)); 
            row.add(rs.getString("last_name"));
            row.add(rs.getString("first_name"));
            row.add(rs.getString("father_name"));
            data.add(row);
        }
         if(rs != null)rs.close();
         if(stm != null)stm.close();
         query = "DROP VIEW totals";
         stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
         update = stm.executeUpdate(query);
  } catch (SQLException ex) {
            Logger.getLogger(ShowMisthodotiki.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
            if(rs != null)rs.close();
            if(stm != null)stm.close();
        }
    ObservableList<String> totalRow = FXCollections.observableArrayList();    
    totalRow.add(numFormat.format(total));
    totalRow.add(" ");
    totalRow.add(" ");
    totalRow.add(" ");
    data.add(totalRow);
    
    TableColumn<ObservableList, String> pliromiCol = new TableColumn<>("Πληρωτέο");
    pliromiCol.setMinWidth(100);
    pliromiCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(0).toString());
    }); 
    TableColumn<ObservableList, String> lastNameCol = new TableColumn<>("Επώνυμο");
    lastNameCol.setMinWidth(100);
    lastNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(1).toString());
    });  
    TableColumn<ObservableList, String> firstNameCol = new TableColumn<>("Όνομα");
    firstNameCol.setMinWidth(100);
    firstNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(2).toString());
    }); 
    TableColumn<ObservableList, String> fatherNameCol = new TableColumn<>("Πατρώνυμο");
    fatherNameCol.setMinWidth(100);
    fatherNameCol.setCellValueFactory(param -> {                                                             
             return new SimpleStringProperty(param.getValue().get(3).toString());
    });
    
    table.setItems(data);
    table.getColumns().addAll(pliromiCol, lastNameCol, firstNameCol, fatherNameCol);
    
    table.setPrefHeight(650);
    Label label = new Label("Εταιρία DYNAMOTORS A.E.");
    HBox hBox = new HBox(5);
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
    vBox.getChildren().addAll(label, table, hBox);
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
        if (workbook.getSheet("Πληρωμή") != null)           
           workbook.removeSheetAt(workbook.getSheetIndex("Πληρωμή"));
        HSSFSheet pliromiSheet = workbook.createSheet("Πληρωμή");
        HSSFRow heading = pliromiSheet.createRow(0);
        List<String> headings = new ArrayList<>();
        headings.add("Πληρωτέο");
        headings.add("Επώνυμο");
        headings.add("Όνομα");
        headings.add("Πατρώνυμο");
        int counter = 0;
        for(String s : headings){
        heading.createCell(counter).setCellValue(s);
        counter++;
        } 
        int rowCounter = 1;
        int cellCounter = 0;
        for (ObservableList<String> r : data){
            cellCounter = 0;
            HSSFRow dataRow = pliromiSheet.createRow(rowCounter);
            rowCounter++;
            for (String str : r){
                dataRow.createCell(cellCounter).setCellValue(str);
                cellCounter++;
             }
        }
        for (int columnIndex = 0; columnIndex < 4; columnIndex++)
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
         
}
