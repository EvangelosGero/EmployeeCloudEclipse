/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SalaryReport;

import employeegui.ErrorStage;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardOpenOption.CREATE;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
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
public class CSL01 {
    
    private Connection con = null;
    private String directoryString, tableString, reportTableStr, secondTableString;
    private Statement stm = null;
    private ResultSet rs = null;    
    private final DecimalFormat decimalFormat = new DecimalFormat(".00");
    private final DecimalFormat intFormat = new DecimalFormat("");    
    private final Map<Integer, List<String>> companyMap = new HashMap<>();
    private final Map<Integer, List<Double>> companySumMap = new HashMap<>(); 
    private int previousMonth, tableYear,  companyBuffer, idBuffer ;     
    private int ensimaBuffer = 0;
    private double adjustedSalaryBuffer = 0, isforesErgazomenouBuffer = 0, tapitIsforesErgazomenouBuffer =0,
            ergodotikesIsforesBuffer = 0, tapitErgodotikesIsforesBuffer = 0,
            totalBuffer = 0, tapitTotalBuffer =0;    
    private boolean tapitRow = false;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M_yyyy");
    private final DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("ddMMyyyy");
    private BufferedWriter writer2 = null;
    
    
    public void csl01(Connection con, String tableString) throws SQLException, IOException{
        try {
            this.con = con;
            this.tableString = tableString;
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
                case 5 : new SalaryReport.CreateDoroPashaReport().createDBDoroPashaReport(con, 0);
                        secondTableString = "DORO_PASHA_REPORT_"+Integer.toString(tableYear);
                        break;
                case 7 : new SalaryReport.CreateEpidomaAdeiasReport().createDBEpidomaAdeiasReport(con, 0);
                        secondTableString = "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(tableYear);
                        break;
                case 12 : new SalaryReport.CreateDoroXmasReport().createDBDoroXmasReport(con, 0);
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
                        rs.getString("ipokatastima_ika"), rs.getString("patronymo"), 
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
           
            directoryString = "CSL01_"+Integer.toString(previousMonth)
               +"_"+Integer.toString(tableYear); 
            Charset charset = Charset.forName("UTF-8");           
            rs.first();                    
            companyBuffer = rs.getInt("company");
            idBuffer = -1;                        
            String string = createTitle(companyBuffer);
            rs.beforeFirst();
            while(rs.next()){
              if (rs.getInt("ta") != 6 && rs.getInt("ta") != 7 &&
                     !rs.getString("ASTHENEIA_TEXT").equals("A<3TOTAL")&& 
                     !rs.getString("ASTHENEIA_TEXT").equals("A>3TOTAL")&&
                     !rs.getString("ASTHENEIA_TEXT").equals("A-TOTAL") ){
                if (rs.getInt("company") != companyBuffer ){
                    Path path = Paths.get("C:\\EmployeeGUI\\EmployeeGUIOutput\\"+directoryString+"\\"+companyBuffer);
                    Files.createDirectories(path);
                    Path file = path.resolve("CSL01");
                    try (BufferedWriter writer = Files.newBufferedWriter(file, charset, CREATE)) {
                        string += "EOF\n";
                        writer.write(string, 0, string.length());
                    } catch (IOException x) {
                        System.err.format("IOException: %s%n", x);
                        ErrorStage.showErrorStage(x.getMessage());
                    }
                    companyBuffer = rs.getInt("company");                    
                    string = createTitle(companyBuffer);
                    
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
                    if(rs.getInt("id") != idBuffer){
                        string += employeeHeader(rs.getInt("id"));
                        idBuffer = rs.getInt("id");
                    }
                    tapitRow = (rs.getInt("tapit") == 0 ? false : true);
                    string += createEmployee(rs.getInt("id"), tapitRow);
                    
                    if (tapitRow == true){
                        tapitRow = false;
                        string += createEmployee(rs.getInt("id"), tapitRow);
                          
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
            Path path = Paths.get("C:\\EmployeeGUI\\EmployeeGUIOutput\\"+directoryString+"\\"+companyBuffer);
            Files.createDirectories(path);
            Path file = path.resolve("CSL01");
            writer2 = Files.newBufferedWriter(file, charset, CREATE);
            string += "EOF\n";
            writer2.write(string, 0, string.length());                    
        } catch (IOException ex) {
            Logger.getLogger(ApodixisPDF.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }  catch (SQLException ex) {
            Logger.getLogger(ShowIKA.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
            if (rs != null) rs.close();
            if (stm != null) stm.close();
            if (writer2 != null) writer2.close();
        }
        
    }    
    public String createTitle(int id) throws SQLException{        
        String string = "";
        String[] stringA = new String[28];
        stringA[1] = "1";  //Τύπος εγγραφής
        stringA[2] = "01"; //Πλήθος μέσων που προσκομίζονται (ήταν για δισκέτες, τώρα βάζω 01)
        stringA[3] = "01"; // Α/Α μέσου (για δισκέτες, τώρα βάζω 01)
        stringA[4] = createString(8, "CSL01"); //Ονομα αρχείου ,μήκος 8
        stringA[5] = createString(2,"01"); //Εκδοση
        stringA[6] = createString(2,"01"); //Τύπος Δήλωσης 01 Κανονική, 04 Συμπληρωματική
        stringA[7]= createString(3, companyMap.get(id).get(13));   //Υποκατάστημα ΙΚΑ     
        stringA[8] = createString(50, companyMap.get(id).get(9));  //Ονομασία υποκαταστήματος        
        stringA[9] = createString(80, companyMap.get(id).get(0)+" "+companyMap.get(id).get(1));  //Ονομασία εταιρίας
        stringA[10] = createString(30, companyMap.get(id).get(4));  //Ονομα και επώνυμο υπευθύνου
        stringA[11]= createString(30, companyMap.get(id).get(10));  //Ονομα πατρός υπευθύνου
        stringA[12] = createString(10, companyMap.get(id).get(3)); //A.Μ.Ε
        stringA[13] = createString(9, companyMap.get(id).get(8)); //A.Φ.Μ.
        stringA[14] = createString(50, companyMap.get(id).get(5)); //οδός        
        stringA[15] = createString(10, companyMap.get(id).get(11)); //αριθμός
        stringA[16] = createString(5, companyMap.get(id).get(12)); //τ.κ.
        stringA[17] = createString(30, companyMap.get(id).get(6)); //πόλη
        stringA[18] = createNumString(2, Integer.toString(previousMonth)); //από μήνα
        stringA[19] = createNumString(4, Integer.toString(tableYear)); //από έτος
        stringA[20] = createNumString(2, Integer.toString(previousMonth)); //έως μήνα
        stringA[21] = createNumString(4, Integer.toString(tableYear)); //έως έτος
        stringA[22] = createNumString(8, intFormat.format(companySumMap.get(companyBuffer).get(0))); //σύνολο ημερών ασφάλισης        
        stringA[23] = createNumString(12, decimalFormat.format(companySumMap.get(companyBuffer).get(1)));//σύνολο αποδοχών         
        stringA[24] = createNumString(12, decimalFormat.format(companySumMap.get(companyBuffer).get(2))); //σύνολο καταβλητέων εισφορών
        stringA[25] = createString(8, LocalDate.of(tableYear, previousMonth, 1).with(TemporalAdjusters.lastDayOfMonth()).format(formatter1)); //ημερομηνία υποβολής
        stringA[26] = createString(8, ""); //ημερομηνία παύσης εργασιών
        stringA[27] = createString(30, ""); //κενά   
        for(int i = 1; i < 28; i++)string += stringA[i]; 
        string += "\n";
        return string;
    }   
    
    public String employeeHeader(int id) throws SQLException{
       String string = "";
       String[] stringA = new String[37];
       stringA[28] = "2";  //Τύπος εγγραφής 
       stringA[29] = createNumString(9, rs.getString("am_ika"));
       stringA[30] = createNumString(11, rs.getString("amka"));
       stringA[31] = createString(50, rs.getString("last_name"));
       stringA[32] = createString(30, rs.getString("first_name"));
       stringA[33] = createString(30, rs.getString("father_name"));
       stringA[34] = createString(30, rs.getString("mother_name"));
       stringA[35] = createString(8, rs.getDate("birthdate").toLocalDate().format(formatter1));
       stringA[36] = createNumString(9, rs.getString("afm"));
       for(int i = 28; i < 37; i++)string += stringA[i];
       string += "\n";
       return string;
    }
    
    public String createEmployee(int id, Boolean tapitRow ) throws SQLException{
        String string = "";
        String[] stringA = new String[61];  //for readability indexes comply with IKA manual
        stringA[37] = "3";  //Τύπος εγγραφής 
        stringA[38] = createNumString(4, rs.getString("ar_parartimatos").trim());
        stringA[39] = createNumString(4, (tapitRow == false ? rs.getString("kad") : "0014").trim());
        stringA[40] = createString(1, "1");     //Πλήρες ωράριο (1 = ΝΑΙ - 0 = ΟΧΙ
        stringA[41] = createString(1, "1");     //όλες οι εργάσιμες (1=ΝΑΙ - 0 = ΟΧΙ  
        stringA[42] = createString(1, "0");     //Κυριακές που απασχολήθηκε  
        stringA[43] = createNumString(6, rs.getString("kodikos_idikotitas").trim());
        stringA[44] = createNumString(2, rs.getString("idiki_per_asfalisis").trim().length() > 0 ? 
                rs.getString("idiki_per_asfalisis").trim() : "00");
        stringA[45] = createNumString(4, tapitRow == false ? rs.getString("reason_isfores") : 
                rs.getInt("tapit") == 1 ? "027" : "024" );        
        stringA[46] = createNumString(2, Integer.toString(previousMonth));
        stringA[47] = createNumString(4,  Integer.toString(tableYear));
        stringA[48] = createString(8, rs.getString("reason_salary").equals("ΑΣΘΕΝΕΙΑ") ? rs.getDate("start_date").toLocalDate().format(formatter1) : " ");
        stringA[49] = createString(8, rs.getString("reason_salary").equals("ΑΣΘΕΝΕΙΑ") ? rs.getDate("end_date").toLocalDate().format(formatter1) : " ");
        stringA[50] = createNumString(2, tapitRow == true && rs.getInt("ta") == 1 && !rs.getString("reason_salary").equals("ΑΣΘΕΝΕΙΑ")? "15" : 
                rs.getString("reason_salary").equals("ΑΣΘΕΝΕΙΑ") ? "08" : rs.getInt("ta") == 3 ? "04" :
                        rs.getInt("ta") == 4 ? "05": rs.getInt("ta") == 5 ? "03" : "01" );       
        stringA[51] = createNumString(3, Integer.toString(rs.getInt("ensima")+ensimaBuffer));
        stringA[52] = createNumString(10, decimalFormat.format(rs.getInt("relation") == 0 ? 0 : rs.getDouble("salary")));
        stringA[53] = createNumString(10, decimalFormat.format(rs.getDouble("adjusted_salary")
                +(rs.getInt("ta") == 3 || rs.getInt("ta") == 5 ? rs.getDouble("prosafxisi_dorou") : 0)+adjustedSalaryBuffer));
        stringA[54] = createNumString(10, tapitRow == true ? decimalFormat.format(rs.getDouble("tapit_isfores_erg")
                +tapitIsforesErgazomenouBuffer) :
                decimalFormat.format(rs.getDouble("isfores_ergazomenou")+isforesErgazomenouBuffer));
        stringA[55] = createNumString(10, tapitRow == true ? decimalFormat.format(rs.getDouble("tapit_ergod_isfores")
                +tapitErgodotikesIsforesBuffer) :
                decimalFormat.format(rs.getDouble("ergodotikes_isfores")+ergodotikesIsforesBuffer));
        stringA[56] = createNumString(11, tapitRow == true ? decimalFormat.format(rs.getDouble("tapit_total")
                +tapitTotalBuffer) :
                decimalFormat.format(rs.getDouble("total")+totalBuffer));
        stringA[57] = createNumString(10, "00");  //Επιδότηση ασφαλισμένου
        stringA[58] = createNumString(5, "00");  //Επιδότηση εργοδότη %
        stringA[59] = createNumString(10, "00");  //Επιδότηση εργοδότη        
        stringA[60] = createNumString(11, tapitRow == true ? decimalFormat.format(rs.getDouble("tapit_total")
                +tapitTotalBuffer) :
                decimalFormat.format(rs.getDouble("total")+totalBuffer));        
        for(int i = 37; i < 61; i++)string += stringA[i]; 
        string += "\n";
        return string;
    }
    
    public String createString(int len, String data){
        if (data == null)data = " ";
        char[] arrayC = new char[len];  
        Arrays.fill(arrayC, ' ');
        char[] arrayData = data.toCharArray();
        for(int i = 0; i < arrayData.length; i++)arrayC[i] = arrayData[i];
        return new String(arrayC);        
    }
    
    public String createNumString(int len, String data){
        if(data == null)data = "0";
        String dataNumbers = "";
        char[] arrayC = new char[len];  
        Arrays.fill(arrayC, '0');
        String[] helpStr = data.trim().split("[.,]");
        for(int i = 0; i < helpStr.length ; i++) dataNumbers += helpStr[i] ;
        char[] arrayData = dataNumbers.toCharArray();        
        for(int i = 0; i < arrayData.length; i++)arrayC[arrayC.length -1 - i] = arrayData[arrayData.length -1 - i];        
        return new String(arrayC);       
    }
}

