/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.History;

import static Controllers.History.ReportEnum.APODIXIS;
import static Controllers.History.ReportEnum.APODIXISDP;
import static Controllers.History.ReportEnum.APODIXISEA;
import static Controllers.History.ReportEnum.APODIXISXMAS;
import Controllers.Logic.Misthodosia.ApdPdf;
import Controllers.Logic.Misthodosia.ApodixisPDF;
import Controllers.Logic.Misthodosia.CSL01;
import Controllers.Logic.ShowReport;
import Controllers.Logic.showVacationReport;
import Controllers.ShowArthro;
import Controllers.ShowIKA;
import Controllers.ShowMisthodotiki;
import Controllers.ShowPliromi;
import Controllers.util.JsfUtil;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author evgero
 */
@Named("showOldReports")
@SessionScoped
public class ShowOldReports implements Serializable{
    
    @Inject
    private ShowReport showReport;
    @Inject
    private showVacationReport showVR;
    @Inject
    private ShowMisthodotiki showMisthodotiki;
    @Inject
    private ShowArthro showArthro;
    @Inject
    private ShowIKA showIKA;
    @Inject ShowPliromi showPliromi;
    private Connection con;
    private ResultSet rs;
    private List<String> list = new ArrayList<>();   
    
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M_yyyy");
    private ReportEnum type;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
                         
    public void oldReportsList(Connection con, ReportEnum type1) throws SQLException{
       this.type = type1;
       this.con = con;
        try {
            DatabaseMetaData dm = con.getMetaData();            
            rs = dm.getTables(null, null, null, null);
            if(type == ReportEnum.TIMER){
                Pattern pattern = Pattern.compile("REPORT_..*_....", Pattern.CASE_INSENSITIVE);
                while(rs.next())
                 if (pattern.matcher(rs.getString("TABLE_NAME")).matches())
                     list.add(rs.getString("TABLE_NAME"));
                
                Collections.sort(list, new Comparator<String>(){
           
                @Override
                public int compare(String t1, String t2){
                    return YearMonth.parse(t1.substring(7, t1.length()), formatter).
                            compareTo(YearMonth.parse(t2.substring(7, t2.length()), formatter));
                }
                });                          
            }
            if(type == ReportEnum.VACATION){
                Pattern pattern = Pattern.compile("VACATION_REPORT_....", Pattern.CASE_INSENSITIVE);
                while(rs.next())
                 if (pattern.matcher(rs.getString("TABLE_NAME")).matches())
                     list.add(rs.getString("TABLE_NAME"));
                
                Collections.sort(list, new Comparator<String>(){                    
           
                @Override
                public int compare(String t1, String t2){
                    return t1.substring(t1.length()-4).
                            compareTo(t2.substring(t2.length()-4));
                }
                });                        
            }
            if(type == ReportEnum.MISTHODOTIKH || type == ReportEnum.ARTHRO || type == ReportEnum.IKA
                    || type == ReportEnum.PLIROMI || type == ReportEnum.APODIXIS || type == ReportEnum.APD || type == ReportEnum.CSL01 ){
                Pattern pattern = Pattern.compile("SALARY_REPORT_..*_....", Pattern.CASE_INSENSITIVE);
                while(rs.next())
                 if (pattern.matcher(rs.getString("TABLE_NAME")).matches())
                     list.add(rs.getString("TABLE_NAME"));
                
                Collections.sort(list, new Comparator<String>(){                    
           
                @Override
                public int compare(String t1, String t2){
                    return YearMonth.parse(t1.substring(14, t1.length()), formatter).
                            compareTo(YearMonth.parse(t2.substring(14, t2.length()), formatter));
                }
                });                   
            }
            if(type == ReportEnum.MISTHODOTIKHDP || type == ReportEnum.MISTHODOTIKHEA 
                    || type == ReportEnum.MISTHODOTIKHXMAS || type == ReportEnum.ARTHRODP || type == ReportEnum.ARTHROEA 
                    || type == ReportEnum.ARTHROXMAS || type == ReportEnum.IKADP || type == ReportEnum.IKAEA 
                    || type == ReportEnum.IKAXMAS || type == ReportEnum.PLIROMIDP || type == ReportEnum.PLIROMIEA 
                    || type == ReportEnum.PLIROMIXMAS || type == ReportEnum.APODIXISDP || type == ReportEnum.APODIXISEA 
                    || type == ReportEnum.APODIXISXMAS){
                Pattern pattern = null;
                switch (type){
                    case ARTHRODP:
                    case IKADP:
                    case PLIROMIDP:
                    case MISTHODOTIKHDP:
                    case APODIXISDP:{
                        pattern = Pattern.compile("DORO_PASHA_REPORT_....", Pattern.CASE_INSENSITIVE);
                        break;
                    }
                    case ARTHROEA:
                    case IKAEA:
                    case PLIROMIEA:
                    case MISTHODOTIKHEA:
                    case APODIXISEA:{
                        pattern = Pattern.compile("EPIDOMA_ADEIAS_REPORT_....", Pattern.CASE_INSENSITIVE);
                        break;
                    }
                    case ARTHROXMAS:
                    case IKAXMAS:
                    case PLIROMIXMAS:
                    case MISTHODOTIKHXMAS:
                    case APODIXISXMAS:{
                        pattern = Pattern.compile("DORO_XMAS_REPORT_....", Pattern.CASE_INSENSITIVE);
                        break;
                    }
                }
                while(rs.next())
                 if (pattern.matcher(rs.getString("TABLE_NAME")).matches())
                     list.add(rs.getString("TABLE_NAME"));
                
                Collections.sort(list, new Comparator<String>(){                    
           
                @Override
                public int compare(String t1, String t2){
                    return t1.substring(t1.length()-4).
                            compareTo(t2.substring(t2.length()-4));
                }
                });                   
            }
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
            if (rs != null)rs.close();
        }
    }    
        
   public String navigateToReport(String selReport){
            String navString = null;
           try {  
               switch (type){                   
                   case TIMER :{                    
                         showReport.ProduceReportTable(con, selReport); 
                         navString = "/views/timer/ShowReport.xhtml?faces-redirect=true";                                                 
                        break;
                    }
                   case VACATION :{                    
                        showVR.ProduceReportTable(con, selReport);
                        navString = "/views/vacationDays/ShowVacationReport.xhtml?faces-redirect=true";
                        break;
                    }
                   case MISTHODOTIKH :{
                        YearMonth yearMonth =  YearMonth.parse(selReport.substring(14, selReport.length()), formatter);
                        if (yearMonth.getMonthValue() == 4 || yearMonth.getMonthValue() == 7 || yearMonth.getMonthValue() == 12)
                            showMisthodotiki.showMisthodotikiMerger(con, yearMonth.getMonthValue(), yearMonth.getYear(), selReport);                                                                      
                        else showMisthodotiki.showMisthodotiki(con, selReport , null);
                        navString = "/views/misthodosia/Misthodotiki.xhtml?faces-redirect=true";
                        break;
                   }
                   case ARTHRO :{
                        YearMonth yearMonth =  YearMonth.parse(selReport.substring(14, selReport.length()), formatter);
                        if (yearMonth.getMonthValue() == 4 || yearMonth.getMonthValue() == 7 || yearMonth.getMonthValue() == 12)
                            showArthro.showArthroMerger(con, yearMonth.getMonthValue(), yearMonth.getYear(), selReport);                                                                      
                        else showArthro.showArthro(con, selReport , null);
                        navString = "/views/misthodosia/Arthro.xhtml?faces-redirect=true";
                        break;
                   }
                   case IKA :{
                        YearMonth yearMonth =  YearMonth.parse(selReport.substring(14, selReport.length()), formatter);
                        if (yearMonth.getMonthValue() == 4 || yearMonth.getMonthValue() == 7 || yearMonth.getMonthValue() == 12)
                            showIKA.showIKAMerger(con, yearMonth.getMonthValue(), yearMonth.getYear(), selReport);                                                                      
                        else showIKA.showIKA(con, selReport , null);
                        navString = "/views/misthodosia/IKA.xhtml?faces-redirect=true";
                        break;
                   }                   
                   case APODIXIS:
                   case APODIXISEA:
                   case APODIXISDP:
                   case APODIXISXMAS:{
                        new ApodixisPDF().apodixisPDF(con,  selReport);
                        break;
                   }
                   case APD:{
                        new ApdPdf().apdPdf(con,  selReport);
                        break;
                   }
                   case CSL01:{
                        new CSL01().csl01(con,  selReport);
                        break;
                   }
                   case MISTHODOTIKHXMAS :
                   case MISTHODOTIKHEA :
                   case MISTHODOTIKHDP :{
                        showMisthodotiki.showMisthodotiki(con, selReport , null);
                        navString = "/views/misthodosia/Misthodotiki.xhtml?faces-redirect=true";                           
                        break;
                   }
                   case ARTHROXMAS :
                   case ARTHROEA :
                   case ARTHRODP :{
                        showArthro.showArthro(con, selReport , null);
                        navString = "/views/misthodosia/Arthro.xhtml?faces-redirect=true";                          
                        break;
                   }
                   case IKAXMAS :
                   case IKAEA :
                   case IKADP :{
                        showIKA.showIKA(con, selReport , null);
                        navString = "/views/misthodosia/IKA.xhtml?faces-redirect=true";                           
                        break;
                   }
                   case PLIROMI:
                   case PLIROMIXMAS :
                   case PLIROMIEA :
                   case PLIROMIDP :{
                        showPliromi.showPliromi(con,  selReport);
                        navString = "/views/misthodosia/Pliromi.xhtml?faces-redirect=true";
                        break;
                   }
               }    
               
           } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           } catch (IOException ex) { 
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
     //   });
       return navString; 
    }
    
}

