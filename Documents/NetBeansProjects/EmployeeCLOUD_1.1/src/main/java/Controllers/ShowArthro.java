/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

/**
 *
 * @author evgero
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
*/


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
import java.util.TreeSet;
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
 * @author egdyn_000
 */

@Named("showArthro")
@SessionScoped
public class ShowArthro implements Serializable {
    
    @Inject
    private EmplAdminsController emplAdminsController;
    private Connection con = null;
    private Statement stm = null;
    private ResultSet rs = null; 
    private DecimalFormatSymbols symbols;
    private DecimalFormat numFormat;
    private String tableString, reasonSalary;
    private final Map<Integer, String> subsidiariesMap = new HashMap<>();
    private final Map<Integer, List<String>> companyMap = new HashMap<>();
    private List<List<String>> data;
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

    
    
    
    public String handleShowArthro(){
        try {  
            int previousMonthL = LocalDate.now().minusMonths(1).getMonthValue();
            if(previousMonthL == 4 || previousMonthL == 7 || previousMonthL == 12)
             showArthroMerger(this.emplAdminsController.getCon(),  -1, -1, null);
            else
                showArthro(this.emplAdminsController.getCon(), null, null);
               
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }        
        return "/views/misthodosia/Arthro.xhtml?faces-redirect=true";        
    }
     
    public String handleShowArthroDoroPasha(){
        try {
            showArthro(this.emplAdminsController.getCon(),
                    "DORO_PASHA_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/Arthro.xhtml?faces-redirect=true";
    }
    
    public String handleShowArthroEpidomaAdeias(){
        try {
            showArthro(this.emplAdminsController.getCon(), 
                "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(LocalDate.now().getYear()), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/Arthro.xhtml?faces-redirect=true"; 
    }
    
    public String handleShowArthroDoroXmas(){
        try {
            showArthro(this.emplAdminsController.getCon(), 
                "DORO_XMAS_REPORT_"+Integer.toString(LocalDate.now().minusMonths(1).getYear()), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }
      return "/views/misthodosia/Arthro.xhtml?faces-redirect=true"; 
    }    
    
    public void showArthroMerger(Connection con, int prevMonth, int tablYear, String tableString) throws SQLException{
         
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
        showArthro(con, tableString, null);  
        dataRegular = this.data;
        dataDoro = new ArrayList<>();
        showArthro(con, ReportTableString, null);
        dataDoro = data;       
        dataRegular.addAll(dataDoro);
        showArthro(con, tableString, dataRegular); 
    }

    public void showArthro (Connection con, String tableString, List<List<String>> data1) throws SQLException{
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
            currentYear = LocalDate.now().getYear();
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
            
            List<String> heading = new ArrayList<>(Arrays.asList(
                        previousMonth+"/"+tableYear, " ", " ", " "));
            this.data.add(heading); 
            while(rs.next()){
              if((rs.getInt("ta") != 1) || (rs.getInt("ta") == 1 && (rs.getString("astheneia_text").equals("A-TOTAL")
                        || rs.getString("astheneia_text").equals("") || rs.getString("astheneia_text") == null))){
                if((rs.getInt("company") != company) || (rs.getInt("subsidiary") != subsidiary) 
                        || (rs.getInt("ta") != ta)){           
                    
                    // add the rows
                    
                    if (this.data.size() != 1)createRows();  
                    
                    if((rs.getInt("company") != firstCompany)){
                        List<String> grandTotalRow = new ArrayList<>(Arrays.asList("Γενικό Σύνολο", " ", 
                            numFormat.format(totalHreosi), numFormat.format(totalPistosi)));
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
                    List<String> heading0 = new ArrayList<>(Arrays.asList(
                        companyMap.get(rs.getInt("company")).get(0)+" "+ companyMap.get(rs.getInt("company")).get(1)
                        , " ", " ", " ")); 
                    List<String> heading1 = new ArrayList<>(Arrays.asList(
                        subsidiariesMap.get(rs.getInt("subsidiary")) , " ", " ", " "));
                    List<String> heading2 = new ArrayList<>(Arrays.asList(
                        reasonSalary, " ", "ΧΡΕΩΣΗ", "ΠΙΣΤΩΣΗ"));
                    this.data.addAll(Arrays.asList(heading0, heading1, heading2));
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
        List<String> grandTotalRow = new ArrayList<>(Arrays.asList("Γενικό Σύνολο", " ", 
                numFormat.format(totalHreosi), numFormat.format(totalPistosi)));
        this.data.add(grandTotalRow);
       } 
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
            if (rs != null) rs.close();
            if (stm != null) stm.close();
        }
        
   
    
    }
    public void createRows() throws SQLException{        
        if(logar600000 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Εμμισθου Προσ/κού", "600000", numFormat.format(logar600000), " "));
            this.data.add(row);
        }
        if(logar600006 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Εμμισθου Προσ/κού", "600006", numFormat.format(logar600006), " "));
            this.data.add(row);
        }
        if(logar600003 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Εμμισθου Προσ/κού", "600003", numFormat.format(logar600003), " "));
            this.data.add(row);
        }
        if(logar600007 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Εμμισθου Προσ/κού", "600007", numFormat.format(logar600007), " "));
            this.data.add(row);
        }
        if(logar600500 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Εμμισθου Προσ/κού", "600500", numFormat.format(logar600500), " "));
            this.data.add(row);
        }
        if(logar600108misth != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Εμμισθου Προσ/κού", "600108", numFormat.format(logar600108misth), " "));
            this.data.add(row);
        }               
        if(logar600100 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600100", numFormat.format(logar600100), " "));
            this.data.add(row);
        }
        if(logar600106 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600106", numFormat.format(logar600106), " "));
            this.data.add(row);
        }
        if(logar600103 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600103", numFormat.format(logar600103), " "));
            this.data.add(row);
        }
        if(logar600107 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600107", numFormat.format(logar600107), " "));
            this.data.add(row);
        }
        if(logar600501 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600501", numFormat.format(logar600501), " "));
            this.data.add(row);
        }
        if(logar600108imeromisth != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Ημ/σθιου Προσ/κού", "600108", numFormat.format(logar600108imeromisth), " "));
            this.data.add(row);
        }  
        if(logar600005 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Εμμισθου Προσ/κού Ασθένεια", "600005", numFormat.format(logar600005), " "));
            this.data.add(row);
        }
        if(logar600105 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Αμοιβές Ημ/σθιου Προσ/κού Ασθένεια", "600105", numFormat.format(logar600105), " "));
            this.data.add(row);
        }
        if(logar600300 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Εργοδ.Εισφ.Εμμισθου Προσ/κου ΙΚΑ", "600300", numFormat.format(logar600300), " "));
            this.data.add(row);
        }
        if(logar600400 != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Εργοδ.Εισφ.Ημ/σθιου Προσ/κου ΙΚΑ", "600400", numFormat.format(logar600400), " "));
            this.data.add(row);
        }
        if(logar550000IKA != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "ΙΚΑ ΜΙΚΤΑ-ΤΕΑΜ", "550000-ΙΚΑ", " ", numFormat.format(logar550000IKA)));
            this.data.add(row);
        }
        if(logar550000IKAEK != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "ΙΚΑ ΜΙΚΤΑ-ΤΕΑΜ εκ", "550000-ΙΚΑ", " ", numFormat.format(logar550000IKAEK)));
            this.data.add(row);
        }
        if(logar550000IKAVarea != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "ΙΚΑ ΜΙΚΤΑ ΒΑΡΕΑ-ΤΕΑΜ", "550000-ΙΚΑ", " ", numFormat.format(logar550000IKAVarea)));
            this.data.add(row);
        }
        if(logar550300tapitOld != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΠΑΛ)", "550000-ΤΑΠΙΤ", " ", numFormat.format(logar550300tapitOld)));
            this.data.add(row);
        }
        if(logar550300tapitNew != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΝΕΟΙ)", "550000-ΤΑΠΙΤ", " ", numFormat.format(logar550300tapitNew)));
            this.data.add(row);
        }                    
        if(logar540300misth != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Φόρος Έμμισθου Προσ.", "540300", " ", numFormat.format(logar540300misth)));
                        this.data.add(row);
                    }
        if(logar540300imeromisth != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Φόρος Ημ/μίσθιου Προσ.", "540300", " ", numFormat.format(logar540300imeromisth)));
                        this.data.add(row);
                    }
        if(isfAllilmisth != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Εισφορά Αλληλεγγύης Έμμισθου Προσ.", "540301", " ", numFormat.format(isfAllilmisth)));
                        this.data.add(row);
                    }
        if(isfAllilimeromisth != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Εισφορά Αλληλεγγύης Ημ/μίσθιου Προσ.", "540301", " ", numFormat.format(isfAllilimeromisth)));
                        this.data.add(row);
                    }
        if(logar530000misth != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Δικαιούχοι Αμοιβών Έμμισθου Προσ.", "530000", " ", numFormat.format(logar530000misth)));
                        this.data.add(row);
                    }
        if(logar530000imeromisth != 0){
            List<String> row = new ArrayList<>(Arrays.asList(
                "Δικαιούχοι Αμοιβών Ημ/μίσθιου Προσ.", "530000", " ", numFormat.format(logar530000imeromisth)));
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
        List<String> totalsRow = new ArrayList<>(Arrays.asList("Σύνολα ", " ", 
                numFormat.format(hreosi), numFormat.format(pistosi)));
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
        for (List<String> r : this.data){
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

