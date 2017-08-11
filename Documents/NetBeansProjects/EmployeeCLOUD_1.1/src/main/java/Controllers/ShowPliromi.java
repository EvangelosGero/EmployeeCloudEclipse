/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.util.DropIfExists;
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
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author evgero
 */
@Named("showPliromi")
@SessionScoped
public class ShowPliromi implements Serializable{   
    
    @Inject
    private EmplAdminsController emplAdminsController;
    private Connection con = null;
    Statement stm = null;
    ResultSet rs, rs1 = null;
    String tableString;    
    private List<List<String>> data = new ArrayList<>();
    private DecimalFormatSymbols symbols;
    private DecimalFormat numFormat;
    private double total = 0;
    private FileOutputStream out;
    private FileInputStream in = null;    
    private int tableYear = LocalDate.now().minusMonths(1).getYear();
    private int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();

    public EmplAdminsController getEmplAdminsController() {
        return emplAdminsController;
    }

    public void setEmplAdminsController(EmplAdminsController emplAdminsController) {
        this.emplAdminsController = emplAdminsController;
    }

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }
    
   public String handleShowPliromi(){
        try {  
            showPliromi(this.emplAdminsController.getCon(), null);
               
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }        
        return "/views/misthodosia/Pliromi.xhtml?faces-redirect=true";        
    }
     
    public String handleShowPliromiDoroPasha(){
        try {
            showPliromi(this.emplAdminsController.getCon(),
                    "DORO_PASHA_REPORT_"+Integer.toString(LocalDate.now().getYear()));
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/Pliromi.xhtml?faces-redirect=true";
    }
    
    public String handleShowPliromiEpidomaAdeias(){
        try {
            showPliromi(this.emplAdminsController.getCon(), 
                "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(LocalDate.now().getYear()));
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/Pliromi.xhtml?faces-redirect=true"; 
    }
    
    public String handleShowPliromiDoroXmas(){
        try {
            showPliromi(this.emplAdminsController.getCon(), 
                "DORO_XMAS_REPORT_"+Integer.toString(LocalDate.now().minusMonths(1).getYear()));
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/Pliromi.xhtml?faces-redirect=true"; 
    }     
  
  public void showPliromi(Connection con, String tableString) throws SQLException{
    
    try{  
        this.con = con;
        this.tableString = tableString;
        
        symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        numFormat = new DecimalFormat(".##", symbols);
        
                 /* find which is the previous month */         
        previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
        tableYear = LocalDate.now().minusMonths(1).getYear();                                                       
        String reportTableStr = "REPORT_"+Integer.toString(previousMonth)
                    +"_"+Integer.toString(tableYear);
        if (tableString == null)this.tableString = "SALARY_"+reportTableStr;                           
        
        DropIfExists.exec(emplAdminsController.getCon() ,"VIEW", "TOTALS");
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
        List<String> heading = new ArrayList<>();
        heading.add(" ");
        //heading.add(tableString.substring(14, tableString.length()));
        heading.add(this.tableString);
        heading.add(" ");
        heading.add(" ");
        data.add(heading); 
        while (rs.next()){
            double totalPart = rs.getDouble("total");
            total += totalPart;
            List<String> row = new ArrayList<>();
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
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
            if(rs != null)rs.close();
            if(stm != null)stm.close();
        }
    List<String> totalRow = new ArrayList<>(); 
    totalRow.add(numFormat.format(total));
    totalRow.add(" ");
    totalRow.add(" ");
    totalRow.add(" ");
    data.add(totalRow);
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
        for (List<String> r : data){
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
            JsfUtil.addSuccessMessage("Excel written successfully..");
             
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        } catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
           if(out != null)out.close();
           if(in != null)in.close();
       }
  }    
         
}
