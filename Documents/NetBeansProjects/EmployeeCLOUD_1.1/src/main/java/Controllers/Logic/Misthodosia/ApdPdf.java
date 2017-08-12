/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic.Misthodosia;
 

import Controllers.util.JsfUtil;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

/**
 *
 * @author egdyn_000
 */
public class ApdPdf {
    
    private Connection con = null;
    private String fileString, tableString, reportTableStr, secondTableString;
    private Statement stm = null;
    private ResultSet rs = null;    
    private final DecimalFormat decimalFormat = new DecimalFormat(".00");
    private final DecimalFormat intFormat = new DecimalFormat(""); 
    private final Map<Integer, List<String>> companyMap = new HashMap<>();
    private final Map<Integer, List<Double>> companySumMap = new HashMap<>(); 
    private int previousMonth, tableYear,  companyBuffer ;
    private BaseFont bfTimes;
    private Font font;
    private final TabSettings tabSettings = new TabSettings(130f);
    private int ensimaBuffer = 0;
    private double adjustedSalaryBuffer = 0, isforesErgazomenouBuffer = 0, tapitIsforesErgazomenouBuffer =0,
            ergodotikesIsforesBuffer = 0, tapitErgodotikesIsforesBuffer = 0,
            totalBuffer = 0, tapitTotalBuffer =0;    
    private boolean tapitRow = false;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M_yyyy");
    
    public void apdPdf(Connection con, String tableString1) throws SQLException{
        try {
            this.con = con;
            this.tableString = tableString1;
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
                // find which is the previous month 
            previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
            tableYear = LocalDate.now().minusMonths(1).getYear();            
            reportTableStr = "REPORT_"+Integer.toString(previousMonth)
                    +"_"+Integer.toString(tableYear);
            if (tableString == null)this.tableString = "SALARY_"+reportTableStr;
            else {
                reportTableStr = tableString.substring(7);
                YearMonth yearMonth =  YearMonth.parse(tableString.substring(14, tableString.length()), 
                                formatter);
                previousMonth = yearMonth.getMonthValue();
                tableYear = yearMonth.getYear();
            }            
            
            //run again for a  final time doro Pasha or epidoma adeias or doro Xmas report
            
            switch (previousMonth){
                case 5 : new CreatePashaReport().createDBDoroPashaReport(con, 0);
                        secondTableString = "DORO_PASHA_REPORT_"+Integer.toString(tableYear);
                        break;
                case 7 : new CreateEAReport().createDBEpidomaAdeiasReport(con, 0);
                        secondTableString = "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(tableYear);
                        break;
                case 12 : new CreateDoroXmasReport().createDBDoroXmasReport(con, 0);
                        secondTableString = "DORO_XMAS_REPORT_"+Integer.toString(tableYear);               
                        break; 
            }
            
            //Fetch comapny information to companyMap
            
            rs = stm.executeQuery("SELECT * FROM company_information");
            while (rs.next()){
                List<String> value = new ArrayList<>();
                Collections.addAll(value, rs.getString("name"), rs.getString("idos_epihirisis"), rs.getString("phone_number"),
                        rs.getString("ar_mitr_erg"), rs.getString("ipefthinos"), rs.getString("address"),
                        rs.getString("town"), rs.getString("edra"), rs.getString("afm"), 
                        rs.getString("ipokatastima_ika"),rs.getString("patronymo"), 
                        rs.getString("arithmos_odou"), rs.getString("TK"), rs.getString("KODIKOS_IPOK_IKA"));
                companyMap.put(rs.getInt("id"), value);
            }
            if (rs != null)rs.close();
            
            //Fetch the Sums in companySumsMap
            String sumQry;
            
            if (previousMonth == 5 || previousMonth == 12)
                sumQry = "SELECT company, SUM(ensima + t1.tapit*ensima) AS imeres_asfalisis, "
                    + "SUM(adjusted_salary + t1.tapit*adjusted_salary + prosafxisi_dorou) AS apodohes, "
                    + "SUM(total)+SUM(tapit_total) AS katavl_isfores FROM "
                    + "( SELECT ID, First_Name ,Last_Name, father_name, AM_IKA, salary, TA, ensima, "+
                "hours_misthou, reason_salary, ' ' AS reason_salary_2, adjusted_salary, 0 AS prosafxisi_dorou, "+
                "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "+
                "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, tapit_total, "+
                "tapit, xartosimo, OGA, FMY, kratisis, kathara, kostos, prokatavoli, relation, pliroteo "+
                "FROM "+tableString+
                " UNION ALL "+
                "SELECT ID, First_Name ,Last_Name, father_name, AM_IKA, salary, TA, ensima, "+
                "hours_misthou, reason_salary, reason_salary_2, adjusted_salary, prosafxisi_dorou, "+
                "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "+
                " ' ' AS astheneia_text, 0 AS epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, tapit_total, "+
                "tapit, xartosimo, OGA, FMY, kratisis, kathara, kostos, prokatavoli, relation, pliroteo "+ 
                "FROM "+secondTableString+" )"    
                    +" AS t1, "
                    + "workers AS t2 WHERE t1.id = t2.id  AND  t1.ASTHENEIA_TEXT != 'A<3TOTAL'" +
                     "AND t1.ASTHENEIA_TEXT != 'A>3TOTAL'" +
                     "AND t1.ASTHENEIA_TEXT != 'A-TOTAL'  GROUP BY company";
            else
                sumQry = "SELECT company, SUM(ensima + t1.tapit*ensima) AS imeres_asfalisis, SUM(adjusted_salary + t1.tapit*adjusted_salary) AS apodohes, "
                    + "SUM(total)+SUM(tapit_total) AS katavl_isfores FROM "+tableString+" AS t1, "
                    + "workers AS t2 WHERE t1.id = t2.id AND  t1.ASTHENEIA_TEXT != 'A<3TOTAL'" +
                     "AND t1.ASTHENEIA_TEXT != 'A>3TOTAL'" +
                     "AND t1.ASTHENEIA_TEXT != 'A-TOTAL'  GROUP BY company";
            rs = stm.executeQuery(sumQry);
            while (rs.next()){
                List<Double> value = new ArrayList<>();
                Collections.addAll(value, rs.getDouble("imeres_asfalisis"), rs.getDouble("apodohes"), 
                        rs.getDouble("katavl_isfores"));
                companySumMap.put(rs.getInt("company"), value);
            }
            if (rs != null)rs.close();
            
            //start building the report with SQL
            String qry;
            
            if (previousMonth == 5 || previousMonth == 12)
                qry = "SELECT t1.*, t2.* "+
                "FROM ( SELECT ID, First_Name ,Last_Name, father_name, AM_IKA, salary, TA, ensima, "+
                "hours_misthou, reason_salary, ' ' AS reason_salary_2, adjusted_salary, 0 AS prosafxisi_dorou, "+
                "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "+
                "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, tapit_total, "+
                "tapit, xartosimo, OGA, FMY, kratisis, kathara, kostos, prokatavoli, relation, pliroteo "+
                "FROM "+tableString+
                " UNION ALL "+
                "SELECT ID, First_Name ,Last_Name, father_name, AM_IKA, salary, TA, ensima, "+
                "hours_misthou, reason_salary, reason_salary_2, adjusted_salary, prosafxisi_dorou, "+
                "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "+
                " ' ' AS astheneia_text, 0 AS epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, tapit_total, "+
                "tapit, xartosimo, OGA, FMY, kratisis, kathara, kostos, prokatavoli, relation, pliroteo "+ 
                "FROM "+secondTableString+" ) AS t1, workers AS t2 "+
                "WHERE t1.id = t2.id "+
                "ORDER BY company, t1.last_name, t1.first_name, reason_salary, ta DESC ";
            else if (previousMonth == 7)
                qry = "SELECT t1.*, t2.* "+
                "FROM ( SELECT ID, First_Name ,Last_Name, father_name, AM_IKA, salary, TA, ensima, "+
                "hours_misthou, reason_salary, adjusted_salary, "+
                "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "+
                "astheneia_text, epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, tapit_total, "+
                "tapit, xartosimo, OGA, FMY, kratisis, kathara, kostos, prokatavoli, relation, pliroteo "+
                "FROM "+tableString+
                " UNION ALL "+
                "SELECT ID, First_Name ,Last_Name, father_name, AM_IKA, salary, TA, ensima, "+
                "hours_misthou, reason_salary, adjusted_salary, "+
                "isfores_ergazomenou, ergodotikes_isfores, mee, total, reason_isfores, reason_isfores_text, "+
                " ' ' AS astheneia_text, 0 AS epidotisi_astheneias, tapit_isfores_erg, tapit_ergod_isfores, tapit_mee, tapit_total, "+
                "tapit, xartosimo, OGA, FMY, kratisis, kathara, kostos, prokatavoli, relation, pliroteo "+ 
                "FROM "+secondTableString+" ) AS t1, workers AS t2 "+
                "WHERE t1.id = t2.id "+
                "ORDER BY company, t1.last_name, t1.first_name, reason_salary, ta DESC ";
            else
                qry = "SELECT t1.*, t2.* "//First ΑΣΘΕΝΕΙΑ which has always ta = 1
                   + "FROM "+tableString+" AS t1, workers AS t2 "
                   + "WHERE t1.id = t2.id "
                   + "ORDER BY company, t1.last_name, t1.first_name, reason_salary, ta DESC";
            
            rs = stm.executeQuery(qry);
            String [] param = new String[69];
            Arrays.fill(param, " ");             
           
            fileString = "APD_"+Integer.toString(previousMonth)
               +"_"+Integer.toString(tableYear)+".pdf";
            
            File newFile = new File("C:\\EmployeeGUI\\EmployeeGUIOutput\\"+fileString);
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(newFile));
            document.open();
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            String relativeWebPath = "/resources/fonts/arialuni.ttf";
            ServletContext servletContext = (ServletContext) externalContext.getContext();
            String absoluteDiskPath = servletContext.getRealPath(relativeWebPath);
            bfTimes = BaseFont.createFont(absoluteDiskPath
                    ,BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            font = new Font(bfTimes  , 8, Font.BOLD, BaseColor.BLUE);       
            int itemsPerPage =1; 
            rs.first();                    
            companyBuffer = rs.getInt("company");
            
            createTitle(companyBuffer, document);
            rs.beforeFirst();
            while(rs.next()){
              if (rs.getInt("ta") != 6 && rs.getInt("ta") != 7 &&
                     !rs.getString("ASTHENEIA_TEXT").equals("A<3TOTAL")&& 
                     !rs.getString("ASTHENEIA_TEXT").equals("A>3TOTAL")&&
                     !rs.getString("ASTHENEIA_TEXT").equals("A-TOTAL") ){
                if (rs.getInt("company") != companyBuffer ){                    
                    companyBuffer = rs.getInt("company");
                    itemsPerPage = 1;
                    document.newPage();
                    createTitle(companyBuffer, document);
                }
                if (rs.getInt("ta") == 2){// ΑΛ to be put in the Buffer
                    ensimaBuffer = rs.getInt("ensima");
                    adjustedSalaryBuffer = rs.getDouble("adjusted_salary");
                    isforesErgazomenouBuffer = rs.getDouble("isfores_ergazomenou");
                    tapitIsforesErgazomenouBuffer = rs.getDouble("tapit_isfores_erg");
                    ergodotikesIsforesBuffer = rs.getDouble("ergodotikes_isfores");
                    tapitErgodotikesIsforesBuffer = rs.getDouble("tapit_ergod_isfores");
                    totalBuffer = rs.getDouble("total");         
                    tapitTotalBuffer = rs.getDouble("tapit_total");
                }else{
                    tapitRow = (rs.getInt("tapit") == 0 ? false : true);
                    createEmployee(rs.getInt("id"), document, tapitRow);                   
                    itemsPerPage++;
                    if( itemsPerPage == 2){
                        itemsPerPage = 0;
                        document.newPage();
                        createHeader(rs.getInt("company"), document);
                    }
                    if (tapitRow == true){
                        tapitRow = false;
                        createEmployee(rs.getInt("id"), document, tapitRow);
                        itemsPerPage++;
                        if( itemsPerPage == 2){
                            itemsPerPage = 0;
                            document.newPage();
                            createHeader(rs.getInt("company"), document);
                        }    
                    }
                    //Zero all the Buffers
                    ensimaBuffer = 0;
                    adjustedSalaryBuffer = 0;
                    isforesErgazomenouBuffer = 0;
                    tapitIsforesErgazomenouBuffer = 0;
                    ergodotikesIsforesBuffer = 0;
                    tapitErgodotikesIsforesBuffer = 0;
                    totalBuffer = 0;         
                    tapitTotalBuffer = 0;
                }
              }   
            }    
            document.close();
            writer.close();
            JsfUtil.addSuccessMessage("Το αρχείο PDF της ΑΠΔ ολοκληρώθηκε");
        } catch (IOException | SQLException | DocumentException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));   
        }finally{
            if (rs != null) rs.close();
            if (stm != null) stm.close();
        }        
    }    
    public void createTitle(int id, Document document) throws DocumentException, SQLException{
        Paragraph paragraph1 = new Paragraph("ΑΝΑΛΥΤΙΚΗ ΠΕΡΙΟΔΙΚΗ ΔΗΛΩΣΗ", font);
        paragraph1.setAlignment(Element.ALIGN_CENTER);         
        document.add(paragraph1);
        document.add( new Paragraph(" "));
        createLine("ΤΥΠΟΣ ΔΗΛΩΣΗΣ", "1 KANONIKH", document);
        document.add(new Paragraph("ΥΠΟΚΑΤΑΣΤΗΜΑ ΙΚΑ ", font));
        createLine("ΥΠΟΒΟΛΗΣ :", companyMap.get(id).get(13) + companyMap.get(id).get(9), document);        
        document.add( new Paragraph(" "));
        createLine("ΕΠΩΝΥΜΙΑ ΕΡΓΟΔΟΤΗ :", companyMap.get(id).get(0)+" "+
                companyMap.get(id).get(1)+" "+companyMap.get(id).get(4), document);
        createLine(" ", companyMap.get(id).get(10), document);  //Πατρώνυμο
        document.add( new Paragraph(" "));
        createLine("A.M.E. :"+companyMap.get(id).get(3),"                  ΔΙΕΥΘΥΝΣΗ", document);
        createLine("A.Φ.Μ. :"+companyMap.get(id).get(8), companyMap.get(id).get(5)
                +" "+companyMap.get(id).get(11), document);
        createLine(" ",companyMap.get(id).get(12)+" "+companyMap.get(id).get(6), document);
        createLine(" ","ΑΠΟ ΜΗΝΑ/ΕΤΟΣ:"+Integer.toString(previousMonth)+"/"+
                Integer.toString(tableYear)+"           ΕΩΣ ΜΗΝΑ/ΕΤΟΣ:"+Integer.toString(previousMonth)+"/"+
                Integer.toString(tableYear), document);
        document.add( new Paragraph(" "));
        createLine("ΣΥΝΟΛΑ ΑΝΑ ΜΗΝΑ:", Integer.toString(previousMonth)+"/"+
                Integer.toString(tableYear), document); 
        createLine("ΗΜΕΡΩΝ ΑΣΦΑΛΙΣΗΣ:", intFormat.format(companySumMap.get(companyBuffer).get(0)), document);     
        createLine("ΑΠΟΔΟΧΩΝ:", decimalFormat.format(companySumMap.get(companyBuffer).get(1)), document);
        createLine("ΚΑΤΑΒΛ. ΕΙΣΦΟΡΩΝ:", decimalFormat.format(companySumMap.get(companyBuffer).get(2)), document);
        
        
        document.add( new Paragraph(" "));
        
        
    }
    public void createLine(String string1, String string2, Document document) throws DocumentException{
        Paragraph paragraph = new Paragraph(string1, font);
        paragraph.setTabSettings(tabSettings);
        paragraph.add(Chunk.TABBING);
        Chunk chunk = new Chunk(string2);
        paragraph.add(chunk);
        document.add(paragraph);        
    }
    public void createNumLine(String string1, String string2, Document document) throws DocumentException{
        Paragraph paragraph = new Paragraph(string1, font);
        paragraph.setTabSettings(tabSettings);
        paragraph.add(Chunk.TABBING);        
        Chunk chunk = new Chunk(string2);       
        paragraph.add(chunk);        
        document.add(paragraph);        
    }
    public void createHeader(int companyId, Document document) throws DocumentException{
        document.add(new Paragraph("Α.Μ.Ε.:"+companyMap.get(companyId).get(3)+
                " Α.Φ.Μ.:"+companyMap.get(companyId).get(8), font));
        document.add(new Paragraph("ΑΠΟ ΜΗΝΑ/ΕΤΟΣ:"+Integer.toString(previousMonth)+"/"+
                Integer.toString(tableYear)+"ΕΩΣ ΜΗΝΑ/ΕΤΟΣ:"+Integer.toString(previousMonth)+"/"+
                Integer.toString(tableYear), font));
        //document.add( new Paragraph(" "));
    }
    public void createEmployee(int id, Document document, Boolean tapitRow ) throws DocumentException, SQLException{
        createLine("ΑΡ.ΠΑΡΑΡΤ. ΚΑΔ:", rs.getString("ar_parartimatos")+"/"+(tapitRow == false ? rs.getString("kad") : "0014"), document);
        createLine("ΑΡΙΘΜ.ΜΗΤΡΩΟΥ.ΑΣΦ:", rs.getString("am_ika"), document);
        createLine("Α.Μ.Κ.Α:", rs.getString("amka"), document);
        createLine("ΕΠΩΝΥΜΟ:", rs.getString("last_name"), document);
        createLine("ΟΝΟΜΑ:", rs.getString("first_name"), document);
        createLine("ΟΝΟΜΑ ΠΑΤΡΟΣ:", rs.getString("father_name"), document);
        createLine("ΟΝΟΜΑ ΜΗΤΡΟΣ:", rs.getString("mother_name"), document);
        createLine("ΗΜ/ΝΙΑ ΓΕΝΝΗΣΗΣ:", rs.getString("birthdate"), document);
        createLine("Α.Φ.Μ.:", rs.getString("afm"), document);
        createLine("ΠΛΗΡΕΣ ΩΡΑΡΙΟ:", "ΝΑΙ", document);
        createLine("ΟΛΕΣ ΟΙ ΕΡΓΑΣΙΜΕΣ:", "ΝΑΙ", document);
        createLine("ΚΥΡΙΑΚΕΣ:", "0", document);
        createLine("ΚΩΔΙΚΟΣ ΕΙΔΙΚΟΤΗΤΑΣ:", rs.getString("kodikos_idikotitas"), document);
        createLine("ΕΙΔ.ΠΕΡΙΠΤ.ΑΣΦΑΛ.:", rs.getString("idiki_per_asfalisis"), document);
        createLine("ΠΑΚΕΤΟ ΚΑΛΥΨΗΣ:", (tapitRow == false ? rs.getString("reason_isfores") : 
                (rs.getInt("tapit") == 1 ? "027" : "024" )), document);
        createLine("ΜΙΣΘΟΛ. ΠΕΡΙΟΔΟΣ:", Integer.toString(previousMonth)+"/"+
                Integer.toString(tableYear), document);
        createLine("ΑΠΟ ΗΜ/ΝΙΑ ΑΠΑΣΧ:", rs.getString("reason_salary").equals("ΑΣΘΕΝΕΙΑ") ? rs.getDate("start_date").toString() : " ", document);
        createLine("ΕΩΣ ΗΜ/ΝΙΑ ΑΠΑΣΧ:", rs.getString("reason_salary").equals("ΑΣΘΕΝΕΙΑ") ? rs.getDate("end_date").toString() : " ", document);
        createLine("ΤΥΠΟΣ ΑΠΟΔΟΧΩΝ:", tapitRow == true && rs.getInt("ta") == 1 && !rs.getString("reason_salary").equals("ΑΣΘΕΝΕΙΑ")? "15" : 
                rs.getString("reason_salary").equals("ΑΣΘΕΝΕΙΑ") ? "08" : rs.getInt("ta") == 3 ? "04" :
                        rs.getInt("ta") == 4 ? "05": rs.getInt("ta") == 5 ? "03" : "01" , document);
        createNumLine("ΗΜΕΡΕΣ ΑΣΦΑΛΙΣΗΣ:", Integer.toString(rs.getInt("ensima")+ensimaBuffer), document);
        createNumLine("ΗΜΕΡΟΜΙΣΘΙΟ:", decimalFormat.format(rs.getInt("relation") == 0 ? 0 : rs.getDouble("salary")), document);
        createNumLine("ΑΠΟΔΟΧΕΣ:", decimalFormat.format(rs.getDouble("adjusted_salary")
                +(rs.getInt("ta") == 3 || rs.getInt("ta") == 5 ? rs.getDouble("prosafxisi_dorou") : 0)+adjustedSalaryBuffer), document);
        createNumLine("ΕΙΣΦΟΡΕΣ ΑΣΦΑΛΙΣΜ.:", tapitRow == true ? decimalFormat.format(rs.getDouble("tapit_isfores_erg")
                +tapitIsforesErgazomenouBuffer) :
                decimalFormat.format(rs.getDouble("isfores_ergazomenou")+isforesErgazomenouBuffer), document);
        createNumLine("ΕΙΣΦΟΡΕΣ ΕΡΓΟΔΟΤΗ:", tapitRow == true ? decimalFormat.format(rs.getDouble("tapit_ergod_isfores")
                +tapitErgodotikesIsforesBuffer) :
                decimalFormat.format(rs.getDouble("ergodotikes_isfores")+ergodotikesIsforesBuffer), document);
        createNumLine("ΣΥΝΟΛΙΚΕΣ ΕΙΣΦΟΡΕΣ:", tapitRow == true ? decimalFormat.format(rs.getDouble("tapit_total")
                +tapitTotalBuffer) :
                decimalFormat.format(rs.getDouble("total")+totalBuffer), document);
        createNumLine("ΕΠΙΔΟΤ.ΑΣΦΑΛ.(ΠΟΣΟ):", "0,00", document);
        createNumLine("ΕΠΙΔΟΤΗΣΗ ΕΡΓΟΔ.(%):", "0,00", document);
        createNumLine("ΕΠΙΔΟΤ.ΕΡΓΟΔ.(ΠΟΣΟ):", "0,00", document);
        createNumLine("ΚΑΤΑΒΛ.ΕΙΣΦΟΡΕΣ:", tapitRow == true ? decimalFormat.format(rs.getDouble("tapit_total")
                +tapitTotalBuffer) :
                decimalFormat.format(rs.getDouble("total")+totalBuffer), document);
        document.add( new Paragraph(" "));          
    }
}

