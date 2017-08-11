/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.Logic.Misthodosia.CreateDoroXmasReport;
import Controllers.Logic.Misthodosia.CreateEAReport;
import Controllers.Logic.Misthodosia.CreatePashaReport;
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
@Named("showMisthodotiki")
@SessionScoped
public class ShowMisthodotiki implements Serializable{  
    
    @Inject
    private EmplAdminsController emplAdminsController;
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
    private FileOutputStream out = null;
    private FileInputStream in = null;
    private int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
    private int tableYear = LocalDate.now().minusMonths(1).getYear();
    private String ReportTableString ;
    private List<List<String>> dataRegular ;    
    private List<List<String>> dataDoro;    

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;     
    }

    public EmplAdminsController getEmplAdminsController() {
        return emplAdminsController;
    }

    public void setEmplAdminsController(EmplAdminsController emplAdminsController) {
        this.emplAdminsController = emplAdminsController;
    }    
      
     public String handleShowMisthodotiki(){
        try {  
            int previousMonthL = LocalDate.now().minusMonths(1).getMonthValue();
            if(previousMonthL == 4 || previousMonthL == 7 || previousMonthL == 12)
             showMisthodotikiMerger(this.emplAdminsController.getCon(),  -1, -1, null);
            else
                showMisthodotiki(this.emplAdminsController.getCon(), null, null);
               
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }        
        return "/views/misthodosia/Misthodotiki.xhtml?faces-redirect=true";        
    }
     
    public String handleShowMisthodotikiDoroPasha(){
        try {
            showMisthodotiki(this.emplAdminsController.getCon(),
                    "DORO_PASHA_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/Misthodotiki.xhtml?faces-redirect=true";
    }
    
    public String handleShowMisthodotikiEpidomaAdeias(){
        try {
            showMisthodotiki(this.emplAdminsController.getCon(), 
                "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/Misthodotiki.xhtml?faces-redirect=true"; 
    }
    
    public String handleShowMisthodotikiDoroXmas(){
        try {
            showMisthodotiki(this.emplAdminsController.getCon(), 
                "DORO_XMAS_REPORT_"+Integer.toString(LocalDate.now().minusMonths(1).getYear()), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/Misthodotiki.xhtml?faces-redirect=true"; 
    }    
    
    public void showMisthodotikiMerger(Connection con, int prevMonth, int tablYear, String tableString) throws SQLException{
         
        this.con = con;
        if(prevMonth != -1)this.previousMonth = prevMonth;
        if(tablYear != -1)this.tableYear = tablYear;
        String prefix = previousMonth == 4 ? "DORO_PASHA_REPORT_" : previousMonth == 7 ? "EPIDOMA_ADEIAS_REPORT_" :
                "DORO_XMAS_REPORT_" ;                
        ReportTableString = prefix+Integer.toString(tableYear);
        
        // Run the final report
        
        switch (previousMonth){
            case 5 : new CreatePashaReport().createDBDoroPashaReport(con, 0);
                    break;
            case 7 : new CreateEAReport().createDBEpidomaAdeiasReport(con, 0); 
                    break;
            case 12 : new CreateDoroXmasReport().createDBDoroXmasReport(con, 0);
                    break;
        }        
        dataRegular = new ArrayList<>();
        showMisthodotiki(con, tableString, null);  
        dataRegular = this.data;
        dataDoro = new ArrayList<>();
        showMisthodotiki(con, ReportTableString, null);
        dataDoro = data;       
        dataRegular.addAll(dataDoro);
        showMisthodotiki(con, tableString, dataRegular); 
    }
    
    public void showMisthodotiki(Connection con, String tableString, List<List<String>> data1) throws SQLException{
        
       
       this.con = con;
       this.data = data1;
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
                List<String> row1 = new ArrayList<>();
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
                
                List<String> row2 = new ArrayList<>();
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
                List<String> row3 = new ArrayList<>();
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
                
                List<String> row4 = new ArrayList<>();
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
            List<String> subtotalRow = new ArrayList<>();                                    
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
            List<String> subtotalRow = new ArrayList<>();                                    
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
    List<String> grandTotalRow = new ArrayList<>();
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
        JsfUtil.addSuccessMessage("Excel written successfully..");
             
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

