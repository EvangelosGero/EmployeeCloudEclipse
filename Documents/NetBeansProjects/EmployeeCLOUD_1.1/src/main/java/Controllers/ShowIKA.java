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
import java.util.Arrays;
import java.util.Collections;
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
@Named("showIKA")
@SessionScoped
public class ShowIKA implements Serializable{

    @Inject
    private EmplAdminsController emplAdminsController;
    private Connection con = null;
    private Statement stm = null;
    private ResultSet rs = null;    
    private DecimalFormat numFormat ;
    private DecimalFormatSymbols symbols ;
    private String tableString, reasonSalary;
    private final Map<Integer, String> subsidiariesMap = new HashMap<>();
    private final Map<Integer, List<String>> companyMap = new HashMap<>();
    private List<List<String>> data; 
    private int company = -1, subsidiary = -1, ta = -1, counter =0;
    private int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
    private int tableYear = LocalDate.now().minusMonths(1).getYear();
    private double totals[] = {0, 0, 0, 0, 0, 0, 0, 0, 0};
    private double ika = 0, ikaEK = 0, ikaVarea = 0, tapitOld = 0, tapitNew = 0;    
    private FileOutputStream out = null;
    private FileInputStream in = null;
    private String ReportTableString ;
    private List<List<String>> dataRegular ;    
    private List<List<String>> dataDoro;
    
    
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

    
    
    
    public String handleShowIKA(){
        try {  
            int previousMonthL = LocalDate.now().minusMonths(1).getMonthValue();
            if(previousMonthL == 4 || previousMonthL == 7 || previousMonthL == 12)
             showIKAMerger(this.emplAdminsController.getCon(),  -1, -1, null);
            else
                showIKA(this.emplAdminsController.getCon(), null, null);
               
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }        
        return "/views/misthodosia/IKA.xhtml?faces-redirect=true";        
    }
     
    public String handleShowIKADoroPasha(){
        try {
            showIKA(this.emplAdminsController.getCon(),
                    "DORO_PASHA_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/IKA.xhtml?faces-redirect=true";
    }
    
    public String handleShowIKAEpidomaAdeias(){
        try {
            showIKA(this.emplAdminsController.getCon(), 
                "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/IKA.xhtml?faces-redirect=true"; 
    }
    
    public String handleShowIKADoroXmas(){
        try {
            showIKA(this.emplAdminsController.getCon(), 
                "DORO_XMAS_REPORT_"+Integer.toString(LocalDate.now().minusMonths(1).getYear()), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/IKA.xhtml?faces-redirect=true"; 
    }    
    
    public void showIKAMerger(Connection con, int prevMonth, int tablYear, String tableString) throws SQLException{
         
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
        showIKA(con, tableString, null);  
        dataRegular = this.data;
        dataDoro = new ArrayList<>();
        showIKA(con, ReportTableString, null);
        dataDoro = data;       
        dataRegular.addAll(dataDoro);
        showIKA(con, tableString, dataRegular); 
    }


    public void showIKA (Connection con, String tableString, List<List<String>> data1) throws SQLException{
         //initialize the Maps
        try {
            this.con = con;
            this.data = data1;
            this.tableString = tableString;
            symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            numFormat = new DecimalFormat(".##", symbols);
            
                 // find which is the previous month 
            previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
            tableYear = LocalDate.now().minusMonths(1).getYear();            
            String reportTableStr = "REPORT_"+Integer.toString(previousMonth)
                    +"_"+Integer.toString(tableYear);
            if (tableString == null)this.tableString = "SALARY_"+reportTableStr;
            
           if (data == null){
            this.data = new ArrayList<>();       
       
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
                    List<String> totalRow = new ArrayList<>();
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
                    List<String> headingIKA = new ArrayList<>();
                    headingIKA.addAll(Arrays.asList(" ", " ", "Σύνολα ανά Κατηγορία"));
                    for(int i = 3; i < 18; i++)headingIKA.add(i, " ");
                    this.data.add(headingIKA);
                    
                    if(ika != 0){
                    List<String> rowIKA = new ArrayList<>();
                    rowIKA.addAll(Arrays.asList(" ", " ", "IKA ΜΙΚΤΑ-ΤΕΑΜ", numFormat.format(ika)));
                    for(int i = 4; i < 18; i++)rowIKA.add(i, " ");
                    this.data.add(rowIKA);  
                    }
                    if(ikaEK != 0){
                    List<String> rowIKAek = new ArrayList<>();
                    rowIKAek.addAll(Arrays.asList(" ", " ", "IKA ΜΙΚΤΑ-ΤΕΑΜ εκ", numFormat.format(ikaEK)));
                    for(int i = 4; i < 18; i++)rowIKAek.add(i, " ");
                    this.data.add(rowIKAek);  
                    }
                    if(ikaVarea != 0){
                    List<String> rowIKAVarea = new ArrayList<>();
                    rowIKAVarea.addAll(Arrays.asList(" ", " ", "IKA ΜΙΚΤΑ ΒΑΡΕΑ-ΤΕΑΜ", numFormat.format(ikaVarea)));
                    for(int i = 4; i < 18; i++)rowIKAVarea.add(i, " ");
                    this.data.add(rowIKAVarea);  
                    }
                    if(tapitOld != 0){
                    List<String> rowTapitOld = new ArrayList<>();
                    rowTapitOld.addAll(Arrays.asList(" ", " ", "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΠΑΛ)", numFormat.format(tapitOld)));
                    for(int i = 4; i < 18; i++)rowTapitOld.add(i, " ");
                    this.data.add(rowTapitOld);  
                    }
                    if(tapitNew != 0){
                    List<String> rowTapitNew = new ArrayList<>();
                    rowTapitNew.addAll(Arrays.asList(" ", " ", "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΝΕΟΙ)", numFormat.format(tapitNew)));
                    for(int i = 4; i < 18; i++)rowTapitNew.add(i, " ");
                    this.data.add(rowTapitNew); 
                    }
                    List<String> totalIKARow = new ArrayList<>();
                    totalIKARow.addAll(Arrays.asList(" ", " ", " ", numFormat.format(ika + ikaEK + ikaVarea + tapitOld + tapitNew)));
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
                    List<String> heading = new ArrayList<>(Arrays.asList(" ", " ", " ",
                            " "," "," "," ","Κατάσταση Ασφάλισης Προσωπικού "+Integer.toString(tableYear)
                    +"/"+Integer.toString(previousMonth)+" "+reasonSalary, " ", " ", " ", " ", " ", " ", " ", " ", "-1", "-1"));
                    this.data.add(heading);                  
                    List<String> subheading1 = new ArrayList<>(Arrays.asList(
                    "Επων.Επιχ.:", companyMap.get(company).get(0), " ", "Είδος Επιχ.:", 
                    companyMap.get(company).get(1), " ", "Τηλ.:", companyMap.get(company).get(2), " ",
                    "Αρ.Μητρ.Εργ.:", companyMap.get(company).get(3), " ", " ", " ", " ", " ", "-1", "-1"));
                    this.data.add(subheading1);
                    List<String> subheading2 = new ArrayList<>(Arrays.asList(
                    "Υπεύθυνος:", companyMap.get(company).get(4), " ", "Διεύθυνση:", 
                    subsidiariesMap.get(subsidiary), " ", "Πόλη:", companyMap.get(company).get(6), " ",
                    "Πόλη:", companyMap.get(company).get(7), " ", " ", " ", " ", " ", "-1", "-1"));
                    this.data.add(subheading2);
                    List<String> subheading3 = new ArrayList<>(Arrays.asList(
                    "ΑΦΜ:", companyMap.get(company).get(8), " ", " ", " ", " ", " ", " ", " ", " ", " ",
                            " ", " ", " ", " ", " ", "-1", "-1"));
                    this.data.add(subheading3);
                     List<String> subheading4 = new ArrayList<>(Arrays.asList(
                    " ", " ", "Στοιχεία", "Εργαζομένου ", " ", "Ημέρες ", "Αποδοχές ", " ", " ", "Αποδοχές ", "Εισφορές ",
                            " ", " ", "Επιδοτ. ", " ", "Κατ. ", "-1", "-1"));
                    this.data.add(subheading4);
                    List<String> subheading5 = new ArrayList<>(Arrays.asList(
                    "Α/Α", "Α.Μ.", "Επώνυμο", "Ονομα", "Ειδικότητα", "Εργασ.", "Βασικές", "Λοιπές",                          
                       "Σύνολο", "Α.Π.Δ.", "Ασφ/νου", "Σύνολο", "Ε.Ε.Ε", "Εισφορ.", "Καταβ/τέο", "Ασφαλ.", "-1", "-1"));
                    this.data.add(subheading5);
                }
                    //now create the rows and be careful to add all 'ta' together (ΑΣΘΕΝΕΙΑ)
                    
                    if ("ΑΣΘΕΝΕΙΑ".equals(rs.getString("reason_salary"))){
                        for (List<String> r : this.data) {
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
                        List<String> row = new ArrayList<>();
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
           
            List<String> totalRow = new ArrayList<>();
            for (int i =0 ; i<5; i++) totalRow.add(i, " ");
            totalRow.add(numFormat.format(totals[0]));
            totalRow.add(" ");
            for (int i = 1; i<9; i++)totalRow.add(6+i, numFormat.format(totals[i]));
            totalRow.add(" ");
            totalRow.add("-1");
            totalRow.add("-1");
            this.data.add(totalRow);
                //Add the IKA category totals
            List<String> headingIKA = new ArrayList<>();
            headingIKA.addAll(Arrays.asList(" ", " ", "Σύνολα ανά Κατηγορία"));
            for(int i = 3; i < 18; i++)headingIKA.add(i, " ");
            this.data.add(headingIKA);
                    
            if(ika != 0){
                List<String> rowIKA = new ArrayList<>();
                rowIKA.addAll(Arrays.asList(" ", " ", "IKA ΜΙΚΤΑ-ΤΕΑΜ", numFormat.format(ika)));
                for(int i = 4; i < 18; i++)rowIKA.add(i, " ");
                this.data.add(rowIKA);  
            }
            if(ikaEK != 0){
                List<String> rowIKAek = new ArrayList<>();
                rowIKAek.addAll(Arrays.asList(" ", " ", "IKA ΜΙΚΤΑ-ΤΕΑΜ εκ", numFormat.format(ikaEK)));
                for(int i = 4; i < 18; i++)rowIKAek.add(i, " ");
                this.data.add(rowIKAek);  
            }
            if(ikaVarea != 0){
                List<String> rowIKAVarea = new ArrayList<>();
                rowIKAVarea.addAll(Arrays.asList(" ", " ", "IKA ΜΙΚΤΑ ΒΑΡΕΑ-ΤΕΑΜ", numFormat.format(ikaVarea)));
                for(int i = 4; i < 18; i++)rowIKAVarea.add(i, " ");
                this.data.add(rowIKAVarea);  
            }
            if(tapitOld != 0){
                List<String> rowTapitOld = new ArrayList<>();
                rowTapitOld.addAll(Arrays.asList(" ", " ", "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΠΑΛ)", numFormat.format(tapitOld)));
                for(int i = 4; i < 18; i++)rowTapitOld.add(i, " ");
                this.data.add(rowTapitOld);  
            }
            if(tapitNew != 0){
                List<String> rowTapitNew = new ArrayList<>();
                rowTapitNew.addAll(Arrays.asList(" ", " ", "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΝΕΟΙ)", numFormat.format(tapitNew)));
                for(int i = 4; i < 18; i++)rowTapitNew.add(i, " ");
                 this.data.add(rowTapitNew); 
            }
            List<String> totalIKARow = new ArrayList<>();
            totalIKARow.addAll(Arrays.asList(" ", " ", " ", numFormat.format(ika + ikaEK + ikaVarea + tapitOld + tapitNew)));
            for(int i = 4; i < 18; i++)totalIKARow.add(i, " ");
            this.data.add(totalIKARow);  
       }     
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
            if (rs != null) rs.close();
            if (stm != null) stm.close();
        }
        
     //prepare the tableView to show the data list
            
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
        for (List<String> r : this.data){
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

