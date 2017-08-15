/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.Logic.CreateReport;
import Controllers.Logic.CreateVacationReport;
import Controllers.Logic.Misthodosia.ApodixisPDF;
import Controllers.Logic.Misthodosia.CreateEAReport;
import Controllers.Logic.Misthodosia.CreateSalaryReport;
import Controllers.util.JsfUtil;
import com.dynamotors.timer1._rest.Workers;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * FXML Controller class
 *
 * @author egdyn_000
 */
@Named("paretisiController")
@SessionScoped
public class ParetisiController implements Serializable {
    
    @Inject
    private EmplAdminsController emplAdminsController;
    private String nameTextField;    
    private String fatherNameTextField;    
    private String lastNameTextField; 
    private String ipolipesAdeiesTextField;    
    private String dailyAdeiaCostTextField;   
    private String totalAdeiesCostTextField;    
    private String totalCostTextField = null;
    private Workers selected = null;    
    private Statement stm = null;
    private PreparedStatement prs = null;
    private ResultSet rs = null, rs1 = null;
    private LocalDate hireDate, apolisiDate;
    private int currentYear, remainingDays, relation;
    private double salary; 
    private final Map<Integer, List<String>> idMap  = new HashMap<>();
    private FileOutputStream out;
    private FileInputStream in = null;
    
    
    private DecimalFormatSymbols symbols;
    private DecimalFormat numFormat;
    private final Map<Integer, String> subsidiariesMap = new HashMap<>();
    private final Map<Integer, List<String>> companyMap = new HashMap<>();
    int simpliromena = 0, imeromisthia =0;
    double misthoi = 0, proMisthoi = 0; 
    private int subsidiary, company, katAsfalisis;
    private String jobTitle, afm, kentroKostous, amEpikourikou, amIka, reportTableStr, salaryTableStr;
    private double apozimiosiMiLifthisasAdeias, epidomaAdeias,
            pliroteoEpidomaAdeias;
    private final DecimalFormat decimalFormat = new DecimalFormat(".##");
    
    private String misthodosiaTextField;
    
    private String epidomaAdeiasTextField;
    
    private String adeiaLifthisaTextField;
    private String temporaryTableStr;
    
    private String pliroteoTotal;
    
    private String pliroteoEpidomaAdeiasTextField;
    
    private String pliroteoMisthodosiaTextField;
    private double pliroteoMisthodosia, pliroteoLiftheisa, misthodosia, adeiaLifthisa;
    private java.util.Date diakopiDate = null;
    private String pliroteoLiftheisaTextField;

    public java.util.Date getDiakopiDate() {
        return diakopiDate;
    }

    public void setDiakopiDate(java.util.Date diakopiDate) {
        this.diakopiDate = diakopiDate;
    }

    public Workers getSelected() {
        return selected;
    }

    public void setSelected(Workers selected) {
        this.selected = selected;
    }

    public EmplAdminsController getEmplAdminsController() {
        return emplAdminsController;
    }

    public void setEmplAdminsController(EmplAdminsController emplAdminsController) {
        this.emplAdminsController = emplAdminsController;
    }

    public String getEpidomaAdeiasTextField() {
        return epidomaAdeiasTextField;
    }

    public void setEpidomaAdeiasTextField(String epidomaAdeiasTextField) {
        this.epidomaAdeiasTextField = epidomaAdeiasTextField;
    }

    public String getPliroteoEpidomaAdeiasTextField() {
        return pliroteoEpidomaAdeiasTextField;
    }

    public void setPliroteoEpidomaAdeiasTextField(String pliroteoEpidomaAdeiasTextField) {
        this.pliroteoEpidomaAdeiasTextField = pliroteoEpidomaAdeiasTextField;
    }

    public String getNameTextField() {
        return nameTextField;
    }

    public void setNameTextField(String nameTextField) {
        this.nameTextField = nameTextField;
    }

    public String getFatherNameTextField() {
        return fatherNameTextField;
    }

    public void setFatherNameTextField(String fatherNameTextField) {
        this.fatherNameTextField = fatherNameTextField;
    }

    public String getLastNameTextField() {
        return lastNameTextField;
    }

    public void setLastNameTextField(String lastNameTextField) {
        this.lastNameTextField = lastNameTextField;
    }

    public String getIpolipesAdeiesTextField() {
        return ipolipesAdeiesTextField;
    }

    public void setIpolipesAdeiesTextField(String ipolipesAdeiesTextField) {
        this.ipolipesAdeiesTextField = ipolipesAdeiesTextField;
    }

    public String getDailyAdeiaCostTextField() {
        return dailyAdeiaCostTextField;
    }

    public void setDailyAdeiaCostTextField(String dailyAdeiaCostTextField) {
        this.dailyAdeiaCostTextField = dailyAdeiaCostTextField;
    }

    public String getTotalAdeiesCostTextField() {
        return totalAdeiesCostTextField;
    }

    public void setTotalAdeiesCostTextField(String totalAdeiesCostTextField) {
        this.totalAdeiesCostTextField = totalAdeiesCostTextField;
    }

    public String getTotalCostTextField() {
        return totalCostTextField;
    }

    public void setTotalCostTextField(String totalCostTextField) {
        this.totalCostTextField = totalCostTextField;
    }

    public String getMisthodosiaTextField() {
        return misthodosiaTextField;
    }

    public void setMisthodosiaTextField(String misthodosiaTextField) {
        this.misthodosiaTextField = misthodosiaTextField;
    }

    public String getAdeiaLifthisaTextField() {
        return adeiaLifthisaTextField;
    }

    public void setAdeiaLifthisaTextField(String adeiaLifthisaTextField) {
        this.adeiaLifthisaTextField = adeiaLifthisaTextField;
    }

    public String getPliroteoTotal() {
        return pliroteoTotal;
    }

    public void setPliroteoTotal(String pliroteoTotal) {
        this.pliroteoTotal = pliroteoTotal;
    }

    public String getPliroteoMisthodosiaTextField() {
        return pliroteoMisthodosiaTextField;
    }

    public void setPliroteoMisthodosiaTextField(String pliroteoMisthodosiaTextField) {
        this.pliroteoMisthodosiaTextField = pliroteoMisthodosiaTextField;
    }

    public String getPliroteoLiftheisaTextField() {
        return pliroteoLiftheisaTextField;
    }

    public void setPliroteoLiftheisaTextField(String pliroteoLiftheisaTextField) {
        this.pliroteoLiftheisaTextField = pliroteoLiftheisaTextField;
    }
    
    
    
    @PostConstruct
    public void init() {               
        symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        numFormat = new DecimalFormat(".00", symbols);
        
        
        try {
            
            stm = emplAdminsController.getCon().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);            
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
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{try {
            if (rs != null)rs.close() ;
            if (stm != null)stm.close();
            //if (con != null) con.close();
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));         
        }
        }  
    } 
    public void handleOristikopoiisiBtn(){
        
        try {
            stm = emplAdminsController.getCon().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            String sql = "select * from workers WHERE id = "+this.selected.getId().toString();
            rs = stm.executeQuery(sql);
            rs.first();
            rs.updateInt("apolisi", 3);
            rs.updateDate("diakopi", java.sql.Date.valueOf(diakopiDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
            
            rs.updateRow();
            if (rs != null)rs.close();
            
            sql = "SELECT * FROM apozimioseis WHERE id = "+this.selected.getId().toString();
            rs = stm.executeQuery(sql);
            if (rs.first() == false){
              rs.close();              
              sql = "INSERT INTO apozimioseis VALUES (?, ?, ?, ?, ? )";
              prs = emplAdminsController.getCon().prepareStatement(sql);
              prs.setInt(1, this.selected.getId());
              prs.setDouble(2, 0);
              prs.setDouble(3, apozimiosiMiLifthisasAdeias);
              prs.setInt(4, remainingDays);
              prs.setInt(5, 0);
              prs.executeUpdate();                 
            }else{
              rs.updateInt("id", this.selected.getId());
              rs.updateDouble("apozimiosi", 0);
              rs.updateDouble("mi_lifthises_adeies", apozimiosiMiLifthisasAdeias);
              rs.updateInt("imeres_adeias", remainingDays);
              rs.updateInt("imeres_apozimiosis", 0);
              rs.updateRow();
            }
         JsfUtil.addSuccessMessage("Η οριστικοποίηση έγινε επιτυχώς");
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
            try {
                if (rs != null)rs.close();
                if (stm != null)stm.close();
                if (prs != null)prs.close();
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    
    public void handleExcelBtn(){
         
        HSSFWorkbook workbook = null;
        String excelPath = "C:\\EmployeeGUI\\EmployeeGUIOutput\\"
                   +this.selected.getLastName()+"_"+this.selected.getFirstName()+"_paraitisi.xls"; 
        try {
        File excelFile = new File(excelPath);
        if (excelFile.exists()){
            in = new FileInputStream(excelFile); 
            workbook = new HSSFWorkbook(in);
        }else{            
            workbook = new HSSFWorkbook();  
        }
        if (workbook.getSheet("Παραίτηση") != null)           
           workbook.removeSheetAt(workbook.getSheetIndex("Παραίτηση"));
        HSSFSheet pliromiSheet = workbook.createSheet("Παραίτηση");
        HSSFRow heading = pliromiSheet.createRow(0);
        List<String> headings = new ArrayList<>();
        headings.add("Όνομα");
        headings.add("Επώνυμο");
        headings.add("Πατρώνυμο");       
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
        dataRow.createCell(0).setCellValue(nameTextField);
        dataRow.createCell(1).setCellValue(lastNameTextField);
        dataRow.createCell(2).setCellValue(fatherNameTextField);       
        dataRow.createCell(3).setCellValue(totalAdeiesCostTextField);
        dataRow.createCell(4).setCellValue(misthodosiaTextField);
        dataRow.createCell(5).setCellValue(adeiaLifthisaTextField);
        dataRow.createCell(6).setCellValue(epidomaAdeiasTextField);
        dataRow.createCell(7).setCellValue(totalCostTextField);
        dataRow.createCell(8).setCellValue(pliroteoMisthodosiaTextField);
        dataRow.createCell(9).setCellValue(pliroteoLiftheisaTextField);
        dataRow.createCell(10).setCellValue(pliroteoEpidomaAdeiasTextField);
        dataRow.createCell(11).setCellValue(pliroteoTotal);                       
               
        for (int columnIndex = 0; columnIndex < 12; columnIndex++)
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
            try {
                if(out != null)out.close();
                if(in != null)in.close();
            } catch (IOException ex) {
                Logger.getLogger(ApolisiController.class.getName()).log(Level.SEVERE, null, ex);
            }
       }
  }
    
      
    public void handleIpologismosBtn() {
        try {
            String query;
            numFormat = new DecimalFormat(".00");
            
            /* Create a recent employee_vacation table to capture the latest vacation days */
            
            new CreateVacationReport().CreateVacationDBTable(this.emplAdminsController.getCon());
            
            
            
            /* find which is the current Year */
            
            currentYear = this.getDiakopiDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear();   
            
            epidomaAdeias = 0;
            pliroteoEpidomaAdeias = 0;            
           
            stm = this.emplAdminsController.getCon().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                
                // Now compute epidoma adeias
                
            if (this.diakopiDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.of(currentYear, 8, 1))){          
                computeEpidomaAdeias(this.selected.getId());
                query = "SELECT kostos, pliroteo FROM "+temporaryTableStr+" WHERE id = "+this.selected.getId().toString();
                rs = stm.executeQuery(query);
                rs.first();
                epidomaAdeias = rs.getDouble(1);
                pliroteoEpidomaAdeias = rs.getDouble(2);
                if (rs != null)rs.close();
            }
                epidomaAdeiasTextField =  numFormat.format(epidomaAdeias);
                pliroteoEpidomaAdeiasTextField = numFormat.format(pliroteoEpidomaAdeias);
                
                //Compute the salary
                
                computeSalary(this.selected.getId());
                misthodosiaTextField = numFormat.format(misthodosia);
                pliroteoMisthodosiaTextField = numFormat.format(pliroteoMisthodosia);
                adeiaLifthisaTextField = numFormat.format(adeiaLifthisa);
                pliroteoLiftheisaTextField = numFormat.format(pliroteoLiftheisa);
                
                //Now compute the rest
                
                query = "SELECT t1.first_name, t1.father_name, t1.last_name, t1.hire_date, "
                        + " t2.remaining_days, t1.salary, t1.relation, t1.subsidiary, t1.company, "
                        + "t1.kat_asfalisis, t1.job_title, "
                        + "t1.afm, t1.kentro_kostous, t1.am_epikourikou , t1.am_ika"
                        + " FROM workers AS t1, employee_vacation AS t2 WHERE t1.id = "
                        + this.selected.getId().toString()
                        +" AND t1.id = t2.id";
                
                rs = stm.executeQuery(query);
                rs.first();
                nameTextField = rs.getString(1);
                fatherNameTextField = rs.getString(2);
                lastNameTextField = rs.getString(3);
                hireDate = rs.getDate(4).toLocalDate();
                apolisiDate = this.diakopiDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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
                
                ipolipesAdeiesTextField = Integer.toString(remainingDays);
                dailyAdeiaCostTextField = numFormat.format(relation == 0 ? salary/25 : salary);
                apozimiosiMiLifthisasAdeias = computeApozimiosiMiLifthisasAdeias(remainingDays, salary, relation);
                totalAdeiesCostTextField = numFormat.format(apozimiosiMiLifthisasAdeias);
                totalCostTextField = numFormat.format(apozimiosiMiLifthisasAdeias + 
                        misthodosia+adeiaLifthisa+epidomaAdeias);
                pliroteoTotal = numFormat.format(apozimiosiMiLifthisasAdeias + 
                        pliroteoMisthodosia+ pliroteoLiftheisa+pliroteoEpidomaAdeias);
                
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }finally{
                if (rs != null)try {
                    rs.close();
                } catch (SQLException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } 
    }
        
    public double computeApozimiosiMiLifthisasAdeias(int remainingDays, double salary, int relation){
        
        double apozimiosi;
        double salaryFinal;
        salaryFinal = relation == 0 ? salary/25 : salary;
        apozimiosi = remainingDays * 1.2 * salaryFinal; 
        return apozimiosi;
    }
    
    public void resetIpologismosForm(){
        this.adeiaLifthisaTextField = null;        
        this.dailyAdeiaCostTextField = null;
        this.epidomaAdeiasTextField = null;
        this.ipolipesAdeiesTextField = null;
        this.misthodosiaTextField = null;
        this.pliroteoEpidomaAdeiasTextField = null;
        this.pliroteoLiftheisaTextField = null;        
        this.pliroteoMisthodosiaTextField = null;
        this.totalAdeiesCostTextField = null;
        this.totalCostTextField = null;
    } 
    
    public void handlePdf() {
       
        String fileString = this.selected.getLastName()+"_"+this.selected.getFirstName()+"_apodixi_paraitisis.pdf";
        File newFile = new File("C:\\EmployeeGUI\\EmployeeGUIOutput\\"+fileString);
        Document document = new Document();
        PdfWriter writer = null;
         try {
        writer = PdfWriter.getInstance(document, new FileOutputStream(newFile));
        
        document.open();
        String [] param = new String[69];
        Arrays.fill(param, " ");
        param[0] = companyMap.get(company).get(0);
        param[1] = companyMap.get(company).get(1);
        param[2] = companyMap.get(company).get(5);
        param[3] = Integer.toString(LocalDate.now().getMonthValue())+"/"
                +Integer.toString(LocalDate.now().getYear());
        param[4] = companyMap.get(company).get(7);
        param[5] = "Μη Ληφθείσα Αδεια";
        param[6] = companyMap.get(company).get(8);
        param[7] = lastNameTextField;
        param[8] = jobTitle;
        param[9] = nameTextField;
        param[10] = subsidiariesMap.get(subsidiary);
        param[11] = fatherNameTextField;
        param[12] = kentroKostous;
        param[13] = afm;
        param[14] = (relation==0 ? "ΥΠΑΛΛΗΛΟΣ" : "ΗΜΕΡΟΜΙΣΘΙΟΣ");
        param[15] = amIka;
        param[16] = amEpikourikou;
        param[17] = numFormat.format(salary);
        param[23] = numFormat.format(remainingDays * 1.2);
        param[24] = decimalFormat.format(apozimiosiMiLifthisasAdeias);
        param[64] = decimalFormat.format(apozimiosiMiLifthisasAdeias);        
        param[65] = decimalFormat.format(0);
        param[66] = decimalFormat.format(0);
        param[67] = param[64];
        param[68] = param[64];
        
        ApodixisPDF apodixisPDF = new ApodixisPDF();
        apodixisPDF.createPDF(param, document); 
        
        if(stm.isClosed())stm = this.emplAdminsController.getCon().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        if (this.diakopiDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isBefore(LocalDate.of(currentYear, 8, 1))){
                rs1 = stm.executeQuery("SELECT * FROM "+temporaryTableStr+" WHERE id = "
                    +this.selected.getId().toString());
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
                document.newPage();
        
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
                +this.selected.getId().toString()
                +" ORDER BY ta, reason_salary");
        while(rs1.next()){
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
        JsfUtil.addSuccessMessage("Το αρχείο PDF δημιουργήθηκε");
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }catch (DocumentException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }catch (IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
        finally{ if (rs1 != null)try {
            rs1.close();
        } catch (SQLException ex) {
            Logger.getLogger(ApolisiController.class.getName()).log(Level.SEVERE, null, ex);
        }
            document.close();
            writer.close(); 
         }
        
         
            
        
    }
    public void computeEpidomaAdeias(int id){
       
        try {
            String qry = "UPDATE workers SET apolisi = 0 WHERE id = "+Integer.toString(id);//To make epidoma adeias run
            stm.executeUpdate(qry);
            new CreateEAReport().createDBEpidomaAdeiasReport(this.emplAdminsController.getCon(), 0);
            String epidomaAdeiasTableStr = "EPIDOMA_ADEIAS_REPORT_"+
                Integer.toString(currentYear) ;
            temporaryTableStr = "EPIDOMA_ADEIAS_TEMPORARY_"+Integer.toString(currentYear) ;
            
            //Check if temporaryTable exists and if not create it            
            
            DatabaseMetaData databaseMetaData = this.emplAdminsController.getCon().getMetaData();            
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
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
           if(rs1 != null)try {
               rs1.close();
           } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           }
        }
    }
    
    public void computeSalary(int id){
        try {
            misthodosia = 0;
            pliroteoMisthodosia = 0;
            adeiaLifthisa = 0;
            pliroteoLiftheisa = 0;
            
            reportTableStr = "REPORT_"+Integer.toString(this.diakopiDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getMonthValue())
                    +"_"+Integer.toString(this.diakopiDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getYear());
            salaryTableStr = "SALARY_"+reportTableStr;
            
            //if the tables exist read the data out of them. If they don't exist create them
            
            DatabaseMetaData databaseMetaData = this.emplAdminsController.getCon().getMetaData();
            rs1 = databaseMetaData.getTables(null, null, reportTableStr , null);
            if (!rs1.next()){
                new CreateReport().CreateMonthlyDBTable(this.emplAdminsController.getCon(), 
                        this.diakopiDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            }
            if(rs1 != null)rs1.close();
            rs1 = databaseMetaData.getTables(null, null, salaryTableStr , null);
            if (!rs1.next()){
                new CreateSalaryReport().CreateDBSalaryReport(this.emplAdminsController.getCon(), 
                        this.diakopiDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
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
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
           if(rs1 != null)try {
               rs1.close();
           } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           }
        }
    }
       
}

