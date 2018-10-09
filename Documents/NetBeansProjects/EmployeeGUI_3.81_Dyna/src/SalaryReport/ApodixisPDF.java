/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SalaryReport;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import employeegui.ErrorStage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author egdyn_000
 */
public class ApodixisPDF {

    private Connection con = null;
    private String fileString, tableString, reportTableStr;
    private Statement stm, stm1 = null;
    private ResultSet rs, rs1 = null;
    private final DecimalFormat numFormat = new DecimalFormat(".###");
    private final DecimalFormat decimalFormat = new DecimalFormat(".##");
    private final Map<Integer, String> subsidiariesMap = new HashMap<>();
    private final Map<Integer, List<String>> companyMap = new HashMap<>();
    private final Map<Integer, Double> cutHoursMap = new HashMap<>();
    private int previousMonth, tableYear, currentYear;

    public void apodixisPDF(Connection con, String tableString) throws SQLException{
        try {
            this.con = con;
            this.tableString = tableString;
                         
                 // find which is the previous month 
           previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
           tableYear = LocalDate.now().minusMonths(1).getYear();
           currentYear = LocalDate.now().getYear();
           reportTableStr = "REPORT_"+Integer.toString(previousMonth)
                    +"_"+Integer.toString(tableYear);
           if (tableString == null)this.tableString = "SALARY_"+reportTableStr;
           else reportTableStr = tableString.substring(7);
       
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery("SELECT * FROM subsidiaries");
            while (rs.next())subsidiariesMap.put(rs.getInt("id"), rs.getString("name"));
            if (rs != null) rs.close();
            if (!tableString.contains("DORO_PASHA") && !tableString.contains("EPIDOMA_ADEIAS") && !tableString.contains("DORO_XMAS")){
                rs = stm.executeQuery("SELECT id, cut_hours FROM "+reportTableStr);
                while (rs.next())cutHoursMap.put(rs.getInt("id"), rs.getDouble("cut_hours"));
                if (rs != null) rs.close();
            }
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

           rs = stm.executeQuery("SELECT t1.*, t2.subsidiary, t2.company, t2.kat_asfalisis, t2.job_title, "
                   + "t2.afm, t2.am_ika, t2.kentro_kostous, t2.am_epikourikou "
                   + " FROM "+this.tableString+" AS t1, workers AS t2 "
                   + "WHERE t1.id = t2.id "
                   + "ORDER BY company, t1.last_name, t1.first_name, ta, reason_salary, astheneia_text ");
            String [] param = new String[69];
            Arrays.fill(param, " ");               
            
            if (tableString == null)
                fileString = "APODIXIS_"+Integer.toString(previousMonth)
                +"_"+Integer.toString(tableYear)+".pdf";
            else if (tableString.contains("DORO_PASHA"))fileString = "APODIXIS_ΔΠ"
                +"_"+tableString.substring(18)+".pdf";
            else if (tableString.contains("EPIDOMA_ADEIAS"))fileString = "APODIXIS_EA"
                +"_"+tableString.substring(22)+".pdf";
            else if (tableString.contains("DORO_XMAS"))fileString = "APODIXIS_ΔΧ"
                +"_"+tableString.substring(17)+".pdf";
            else fileString = "APODIXIS_"+reportTableStr.substring(7)+".pdf";  
            
            File newFile = new File("C:\\EmployeeGUI\\EmployeeGUIOutput\\"+fileString);
            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(newFile));
            document.open();
            int itemsPerPage =0;
            double isforesErgBuffer = 0;
            double tapitBuffer = 0;
            double fmyBuffer = 0;
            double isforaAllilegiisBuffer = 0;
            double adjSalaryBuffer = 0;
            while(rs.next()){
                if (!("ΑΣΘΕΝΕΙΑ".equals(rs.getString("reason_salary")))){
                param[0] = companyMap.get(rs.getInt("company")).get(0);
                param[1] = companyMap.get(rs.getInt("company")).get(1);
                param[2] = companyMap.get(rs.getInt("company")).get(5);
                param[3] = tableString == null ? Integer.toString (previousMonth)+"/"+Integer.toString(tableYear) :
                        tableString.contains("DORO_PASHA") ? "4/"+tableString.substring(18): 
                        tableString.contains("EPIDOMA_ADEIAS") ? "7/"+tableString.substring(22) : 
                        tableString.contains("DORO_XMAS") ? "12/"+tableString.substring(17) :
                        tableString.substring(14).replace('_', '/')   ;                             
                param[4] = companyMap.get(rs.getInt("company")).get(7);
                param[5] = rs.getInt("ta")==1 ? "Τακτικές" : rs.getInt("ta")==2 ? "Άδεια Ληφθείσα" :
                        rs.getInt("ta")==3 ? "Δώρο Πάσχα" : rs.getInt("ta")==4 ? "Επίδομα Αδείας" : 
                        "Δώρο Χριστουγέννων";
                param[6] = companyMap.get(rs.getInt("company")).get(8);
                param[7] = rs.getString("last_name");
                param[8] = rs.getString("job_title");
                param[9] = rs.getString("first_name");
                param[10] = subsidiariesMap.get(rs.getInt("subsidiary"));
                param[11] = rs.getString("father_name");
                param[12] = rs.getString("kentro_kostous");
                param[13] = rs.getString("afm");
                param[14] = (rs.getInt("relation")==0 ? "ΥΠΑΛΛΗΛΟΣ" : "ΗΜΕΡΟΜΙΣΘΙΟΣ");
                param[15] = rs.getString("AM_IKA");
                param[16] = rs.getString("am_epikourikou");
                param[17] = numFormat.format(rs.getDouble("salary"));
                param[21] = rs.getString("reason_isfores_text");
                param[22] = decimalFormat.format(rs.getDouble("isfores_ergazomenou")+isforesErgBuffer);
                param[23] = (rs.getInt("ta")==1 ? numFormat.format(rs.getDouble("hours_misthou")/6.66666666666666)
                        : numFormat.format(rs.getInt("ensima")*1.2));
                param[24] = decimalFormat.format(rs.getDouble("adjusted_salary"));
                param[27] = (rs.getInt("tapit")==0?" ":(rs.getInt("tapit")==1?
                    "ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ(ΠΑΛ)":"ΠΡΟΝΟΙΑ ΕΡΓ/ΛΩΝ ΜΕΤΑΛΛΟΥ-ΝΕΟΙ"));
                param[28] = (rs.getInt("tapit")==0?" ":  
                        decimalFormat.format(rs.getDouble("tapit_isfores_erg")+tapitBuffer));
                if (!tableString.contains("DORO_PASHA") && !tableString.contains("EPIDOMA_ADEIAS") && !tableString.contains("DORO_XMAS")){
                    double cutHours = cutHoursMap.get(rs.getInt("id"));
                    if ((cutHours > 0) && (rs.getInt("ta")==1)){                        
                        param[29] = "'Ωρες παρ./απουσ.";
                        double cutSalary = -(rs.getInt("relation") == 0 ? (rs.getDouble("salary")/25)*(cutHours/6.66666666) 
                            : rs.getDouble("salary")*(cutHours/6.66666666));
                        param[30] = decimalFormat.format(-cutHours);
                        param[31] = decimalFormat.format(cutSalary);                    
                    }else if(param[29].equals(" ")){ 
                        if (!param[36].equals(" ")){    //shift ΑΣΘΕΝΕΙΕΣ  up one row.
                           param[29] = param[36];
                           param[30] = param[37];
                           param[31] = param[38];
                           param[36] = param[43];
                           param[37] = param[44];
                           param[38] = param[45];
                           param[43] = param[50];
                           param[44] = param[51];
                           param[45] = " ";
                           param[50] = " ";
                           param[51] = " ";
                        }
                     }
                }else if (rs.getInt("ta") == 3 || rs.getInt("ta") == 5){
                    param[29] = "Προσαύξ. Δώρου";
                    param[31] = decimalFormat.format(rs.getDouble("prosafxisi_dorou"));
                }
                param[34] = (rs.getDouble("fmy") == 0 ? " " : "Φόρος" );                        
                param[35] = (rs.getDouble("fmy") == 0 ? " " : 
                        decimalFormat.format(rs.getDouble("fmy")+fmyBuffer));
                param[41] = (rs.getDouble("isfora_allilegiis") == 0 ? " " : "Εισφoρά Αλληλεγγύης" );                        
                param[42] = (rs.getDouble("isfora_allilegiis") == 0 ? " " : 
                        decimalFormat.format(rs.getDouble("isfora_allilegiis")+isforaAllilegiisBuffer));
                param[64] = decimalFormat.format(rs.getDouble("adjusted_salary")
                        +(rs.getInt("ta") == 3 || rs.getInt("ta") == 5 ? rs.getDouble("prosafxisi_dorou") : 0)
                        +adjSalaryBuffer);
                param[65] = decimalFormat.format(0);
                param[66] = decimalFormat.format(rs.getDouble("kratisis")+isforesErgBuffer+
                        tapitBuffer+fmyBuffer+isforaAllilegiisBuffer);
                param[67] = param[64];
                param[68] = decimalFormat.format(rs.getDouble("kathara")+adjSalaryBuffer-isforesErgBuffer
                    -tapitBuffer-fmyBuffer-isforaAllilegiisBuffer);
                
                isforesErgBuffer = 0;
                tapitBuffer = 0;
                fmyBuffer = 0;
                isforaAllilegiisBuffer = 0;
                adjSalaryBuffer = 0;
                
                createPDF(param, document);
                Arrays.fill(param, " ");
                itemsPerPage++;
                if( itemsPerPage == 2){
                    itemsPerPage = 0;
                    document.newPage();
                }
              }else if (rs.getString("ASTHENEIA_TEXT").equals("A<3TOTAL")|| rs.getString("ASTHENEIA_TEXT").equals("A>3TOTAL")){ 
                if (rs.getString("ASTHENEIA_TEXT").equals("A<3TOTAL")){ 
                    param[36] ="ΑΣΘΕΝΕΙΑ <=3";
                    param[37] = numFormat.format(rs.getInt("ensima")*0.6);
                    param[38] = numFormat.format(rs.getDouble("adjusted_salary"));
                }
                if (rs.getString("ASTHENEIA_TEXT").equals("A>3TOTAL")){
                    if (param[36].equals(" ")){
                        param[36] ="ΑΣΘΕΝΕΙΑ >3";
                        param[37] = numFormat.format(rs.getInt("ensima"));
                        param[38] = numFormat.format(rs.getDouble("adjusted_salary"));
                        param[43] = "Επιδότηση Ασθενείας";
                        param[44] = numFormat.format(rs.getDouble("epidotisi_astheneias"));
                    }else{
                        param[43] ="ΑΣΘΕΝΕΙΑ >3";
                        param[44] = numFormat.format(rs.getInt("ensima"));
                        param[45] = numFormat.format(rs.getDouble("adjusted_salary"));
                        param[50] = "Επιδότηση Ασθενείας";
                        param[51] = numFormat.format(rs.getDouble("epidotisi_astheneias"));
                    }
                }
                    adjSalaryBuffer += rs.getDouble("adjusted_salary");
                    isforesErgBuffer += rs.getDouble("isfores_ergazomenou");
                    tapitBuffer += rs.getDouble("tapit_isfores_erg");
                    fmyBuffer += rs.getDouble("fmy");
                    isforaAllilegiisBuffer += rs.getDouble("isfora_allilegiis");
              }                  
            }
            document.close();
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(ApodixisPDF.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        } catch (DocumentException ex) {
            Logger.getLogger(ApodixisPDF.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        } catch (SQLException ex) {
            Logger.getLogger(ShowIKA.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
            if (rs != null) rs.close();
            if (stm != null) stm.close();
        }
    }    
    public void createPDF(String[] parameter, Document document) throws DocumentException, IOException{
            

            PdfPTable headerTable = new PdfPTable(4);
            headerTable.setWidthPercentage(100);
            headerTable.setSpacingBefore(0);
            headerTable.setSpacingAfter(0);
            BaseFont bfTimes = BaseFont.createFont("/fonts/arialuni.ttf"
                    ,BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font1 = new Font(bfTimes  , 8, Font.BOLD);
            Font font2 = new Font(bfTimes  , 14, Font.BOLD);
            Font font3 = new Font(bfTimes , 7, Font.BOLD);
            Font font4 = new Font(bfTimes, 7);
            Font font5 = new Font(bfTimes, 6);
            PdfPCell defaultCell = headerTable.getDefaultCell();
            defaultCell.setPaddingLeft(5);
            defaultCell.setPaddingRight(5);           
            Chunk chunk1 = new Chunk(parameter[0], font1);
            Chunk chunk2 = new Chunk(" "+parameter[1], font1);
            Phrase phrase1 = new Phrase(chunk1);
            phrase1.add(chunk2);
            PdfPCell cell1 = new PdfPCell(phrase1);
            cell1.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell1);
            Phrase phrase2 = new Phrase("ΕΞΟΦΛΗΤΙΚΗ ΑΠΟΔΕΙΞΗ ΜΙΣΘΟΔΟΣΙΑΣ", font2);
            PdfPCell cell2 = new PdfPCell(phrase2);
            cell2.setColspan(3);
            cell2.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell2.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell2);
            Phrase phrase3 = new Phrase(parameter[2], font1);
            PdfPCell cell3 = new PdfPCell(phrase3);
            cell3.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell3);
            Chunk chunk3 = new Chunk("Μισθ. Περίοδος : ", font1);
            Chunk chunk4 = new Chunk(parameter[3], font1);
            Phrase phrase4 = new Phrase(chunk3);
            phrase4.add(chunk4);
            PdfPCell cell4 = new PdfPCell(phrase4);
            cell4.setColspan(3);
            cell4.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell4.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell4);
            PdfPCell cell5 = new PdfPCell(new Phrase(parameter[4], font1));
            cell5.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell5);
            PdfPCell cell6 = new PdfPCell(new Phrase("Είδος Αποδοχών : "+parameter[5], font1));
            cell6.setColspan(3);
            cell6.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell6.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell6);
            PdfPCell cell7 = new PdfPCell(new Phrase("Α.Φ.Μ. : "+parameter[6], font1));
            cell7.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell7);
            PdfPCell cell8 = new PdfPCell(new Phrase(" "));
            cell8.setColspan(3);
            cell8.setBorder(Rectangle.NO_BORDER);
            headerTable.addCell(cell8);            
            
            Paragraph paragraphWorker = new Paragraph("Στοιχεία Εργαζομένου", font3);
            paragraphWorker.setAlignment(Element.ALIGN_CENTER);
            paragraphWorker.setSpacingBefore(0);
            paragraphWorker.setSpacingAfter(2);
            
            PdfPTable workerTable = new PdfPTable(4);
            workerTable.setWidthPercentage(100);
            float[] columnWidths = {1f, 1.5f, 1f, 1.5f};
            workerTable.setWidths(columnWidths);          
            createWorkerTableRow(workerTable, "Επώνυμο", parameter[7], "Ειδικότητα", 
                    parameter[8], font1, font4);
            createWorkerTableRow(workerTable, "Όνομα", parameter[9], "Διεύθ. Εργασίας", 
                    parameter[10], font1, font4);
            createWorkerTableRow(workerTable, "Όν. Πατρός", parameter[11], "Κέντρο Κόστους", 
                    parameter[12], font1, font4);
            createWorkerTableRow(workerTable, "Α.Φ.Μ.", parameter[13], "Εργ. Κατάσταση", 
                    parameter[14], font1, font4);
            createWorkerTableRow(workerTable, "Α.Μ. ΙΚΑ", parameter[15], "Α.Μ. Επικουρικού", 
                     parameter[16], font1, font4);
            createWorkerTableRow(workerTable, "Σύμβαση", "", "",  "", font1, font4); 
            workerTable.setSpacingAfter(3);
            
            PdfPTable pliromiTable = new PdfPTable(6);
            pliromiTable.setWidthPercentage(100);
            float[] pliromiColumnWidths = {3f, 1f, 3f, 1f, 3f, 1f}; 
            pliromiTable.setWidths(pliromiColumnWidths);
            createPliromiCell(pliromiTable, "ΑΠΟΔΟΧΕΣ", font3);
            createPliromiCell(pliromiTable, "ΠΡΟΣΘΕΤΑ ΕΠΙΔΟΜΑΤΑ", font3);
            createPliromiCell(pliromiTable, "ΚΡΑΤΗΣΕΙΣ", font3);
            createPliromiRow(pliromiTable, pliromiSubTable("Μισθός", parameter[17], font4), parameter[18],
            parameter[19], parameter[20], parameter[21], parameter[22], font4, font4);
            createPliromiRow(pliromiTable, pliromiSubTable("Ημέρες Εργασίας", parameter[23], font4), parameter[24],
            parameter[25], parameter[26], parameter[27], parameter[28], font4, font4);
            createPliromiRow(pliromiTable, pliromiSubTable(parameter[29], parameter[30], font4), parameter[31],
            parameter[32], parameter[33], parameter[34], parameter[35], font4, font4);
            createPliromiRow(pliromiTable, pliromiSubTable(parameter[36], parameter[37], font4), parameter[38],
            parameter[39], parameter[40], parameter[41], parameter[42], font4, font4);
            createPliromiRow(pliromiTable, pliromiSubTable(parameter[43], parameter[44], font4), parameter[45],
            parameter[46], parameter[47], parameter[48], parameter[49], font4, font4);
            createPliromiRow(pliromiTable, pliromiSubTable(parameter[50], parameter[51], font4), parameter[52],
            parameter[53], parameter[54], parameter[55], parameter[56], font4, font4);
            createPliromiRow(pliromiTable, pliromiSubTable(parameter[57], parameter[58], font4), parameter[59],
            parameter[60], parameter[61], parameter[62], parameter[63], font4, font4);
            createPliromiRow(pliromiTable, pliromiSubTable(" ", "ΑΠΟΔΟΧΕΣ", font3), parameter[64],
            "ΕΠΙΔΟΜΑΤΑ", parameter[65], "ΚΡΑΤΗΣΕΙΣ", parameter[66], font3, font4);
            createPliromiRow(pliromiTable, pliromiSubTable(" ", " ", font3), " ",
            "ΣΥΝΟΛΟ ΑΠΟΔΟΧΩΝ", parameter[67], "ΤΕΛΙΚΟ ΠΛΗΡΩΤΕΟ", parameter[68], font3, font4);
            Paragraph subHeader = new Paragraph("ΥΠΕΥΘΥΝΗ ΔΗΛΩΣΗ ΕΡΓΑΖΟΜΕΝΟΥ", font3);
            subHeader.setAlignment(Element.ALIGN_LEFT);
            subHeader.setSpacingBefore(0);
            subHeader.setSpacingAfter(0);
            Paragraph dilosi = new Paragraph("Δηλώνω ανεπιφύλακτα ότι κατά την ανωτέρω Μισθολογική"
            + "Περίοδο πραγματοποίησα τα ως άνω Ημερομίσθια και τις αντίστοιχες ώρες που αναφέρονται στο"
            + "παρόν εκκαθαριστικό σημείωμα - απόδειξη πληρωμής για κανονική εργασία, Υπερωρία, "
            + "Νυχτερινή Εργασία, Κυριακές ή Εορτές και Αργίες. Με την παρούσα Εκκαθάριση και Απόδειξη"
            + "Πληρωμής δέχομαι ότι εξοφλούνται οι αποδοχέσ μου για την παραπάνω Μισθολογική Περίοδο και "
            + "ουδεμία άλλη αξίωση έχω ή διατηρώ απαίτηση εκ του λόγου αυτού κατά του εργοδότη μου. Η "
            + "παρούσα της οποίας έλαβα όμοι αντίγραφο αναγνώσθηκε και εφόσον υπογράφεται από εμένα καθίσταται "
            + "έγκυρος για οποιαδήποτε χρήση ακόμη και ενώπιον παντός Πολιτικού, Διοικητικού ή Ποινικού "
            + "Δικαστηρίου.", font5);
            dilosi.setSpacingBefore(0);
            dilosi.setSpacingAfter(0);
            Paragraph ipografes = new Paragraph("   Ο Εργοδότης                             "
                    + "                                                                             "
                    + "                                     Ο Δηλών και λαβών Εργαζόμενος", font1 );           
            ipografes.setSpacingBefore(0);
            ipografes.setSpacingAfter(0);

            document.add(headerTable);
            document.add(paragraphWorker);
            document.add(workerTable);            
            document.add(pliromiTable);
            document.add(subHeader);
            document.add(dilosi);
            document.add(ipografes);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
                       
    }    
    public void createWorkerTableRow(PdfPTable workerTable, String s1, String s2, String s3, String s4,
            Font font1, Font font2){
        PdfPCell cellw1 = new PdfPCell( new Phrase(s1, font1));
        cellw1.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.LEFT);
        workerTable.addCell(cellw1);           
        PdfPCell cellw2 = new PdfPCell(new Phrase(s2, font2));
        cellw2.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
        workerTable.addCell(cellw2); 
        PdfPCell cellw3 = new PdfPCell(new Phrase(s3, font1));
        cellw3.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.LEFT);
        workerTable.addCell(cellw3); 
        PdfPCell cellw4 = new PdfPCell(new Phrase(s4, font2));
        cellw4.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
        workerTable.addCell(cellw4);
    }
    public void createPliromiCell(PdfPTable pliromiTable, String s1, Font font3){         
        PdfPCell cellp1 = new PdfPCell(new Phrase(s1, font3));
        cellp1.setColspan(2);
        cellp1.setHorizontalAlignment(Element.ALIGN_CENTER);
        pliromiTable.addCell(cellp1); 
    }
    public PdfPTable pliromiSubTable(String s1, String s2, Font font) throws DocumentException{
        PdfPTable cellTable = new PdfPTable(2);
        float[] columnWidths = {1.2f, 1f};
        cellTable.setWidths(columnWidths);
        PdfPCell cellp1 = new PdfPCell(new Phrase(s1, font)); 
        cellp1.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.LEFT);
        cellTable.addCell(cellp1);
        PdfPCell cellp2 = new PdfPCell(new Phrase(s2, font));
        cellp2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellp1.setBorder(Rectangle.BOTTOM | Rectangle.TOP | Rectangle.RIGHT);
        cellTable.addCell(cellp2);
        return cellTable;
    }
    public void createPliromiRow(PdfPTable pliromiTable, PdfPTable cellTable, String s1,
            String s2, String s3, String s4, String s5, Font font1, Font font2){
        PdfPCell cellp1 = new PdfPCell(cellTable);
        pliromiTable.addCell(cellp1);
        PdfPCell cellp2 = new PdfPCell(new Phrase(s1, font2));
        pliromiTable.addCell(cellp2);
        PdfPCell cellp3 = new PdfPCell(new Phrase(s2, font1));
        pliromiTable.addCell(cellp3);
        PdfPCell cellp4 = new PdfPCell(new Phrase(s3, font2));
        pliromiTable.addCell(cellp4);
        PdfPCell cellp5 = new PdfPCell(new Phrase(s4, font1));
        pliromiTable.addCell(cellp5);
        PdfPCell cellp6 = new PdfPCell(new Phrase(s5, font2));
        pliromiTable.addCell(cellp6);
    }    
}
