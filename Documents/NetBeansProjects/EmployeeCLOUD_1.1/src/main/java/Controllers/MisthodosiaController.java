/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.History.ReportEnum;
import Controllers.History.ShowOldReports;
import Controllers.Logic.CreateReport;
import Controllers.Logic.CreateVacationReport;
import Controllers.Logic.Misthodosia.ApdPdf;
import Controllers.Logic.Misthodosia.ApodixisPDF;
import Controllers.Logic.Misthodosia.CSL01;
import Controllers.Logic.Misthodosia.CreateDoroXmasReport;
import Controllers.Logic.Misthodosia.CreateEAReport;
import Controllers.Logic.Misthodosia.CreatePashaReport;
import Controllers.Logic.Misthodosia.CreateSalaryReport;
import Controllers.util.JsfUtil;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author evgero
 */
@Named("misthodosiaController")
@SessionScoped
public class MisthodosiaController implements Serializable {
    
    @Inject
    private EmplAdminsController emplAdminsController;
    @Inject
    private ShowMisthodotiki showMisthodotiki;
    @Inject
    ShowOldReports showOldReports;
    private String historyTitle;
    private String historyLabel;
    private java.util.Date historyDate;
    private int report;
    private List<Integer> yearList = new ArrayList<>();
    private int selectedYear;

    public MisthodosiaController() {
        int currentYear = LocalDate.now().getYear();
        for (int i = currentYear - 20 ; i < currentYear + 21; i++)yearList.add(i);
        this.historyDate = null;
        this.selectedYear = 0;
    } 

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public List<Integer> getYearList() {
        return yearList;
    }

    public void setYearList(List<Integer> yearList) {
        this.yearList = yearList;
    }
    
    

    public EmplAdminsController getEmplAdminsController() {
        return emplAdminsController;
    }

    public void setEmplAdminsController(EmplAdminsController emplAdminsController) {
        this.emplAdminsController = emplAdminsController;
    }

    public ShowMisthodotiki getShowMisthodotiki() {
        return showMisthodotiki;
    }

    public void setShowMisthodotiki(ShowMisthodotiki showMisthodotiki) {
        this.showMisthodotiki = showMisthodotiki;
    }

    public ShowOldReports getShowOldReports() {
        return showOldReports;
    }

    public void setShowOldReports(ShowOldReports showOldReports) {
        this.showOldReports = showOldReports;
    }

    public String getHistoryTitle() {
        return historyTitle;
    }

    public void setHistoryTitle(String historyTitle) {
        this.historyTitle = historyTitle;
    }

    public String getHistoryLabel() {
        return historyLabel;
    }

    public void setHistoryLabel(String historyLabel) {
        this.historyLabel = historyLabel;
    }

    public Date getHistoryDate() {
        return historyDate;
    }

    public void setHistoryDate(Date historyDate) {
        this.historyDate = historyDate;
    }

    public int getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int selectedYear) {
        this.selectedYear = selectedYear;
    }
    
    
    
    
    public void createDoroPashaReport() {
        try {         
            new CreatePashaReport().createDBDoroPashaReport(this.emplAdminsController.getCon(), 0);
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }
    }
    
    public void createEAReport() {
        try {         
            new CreateEAReport().createDBEpidomaAdeiasReport(this.emplAdminsController.getCon(), 0);
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }
    }
    
    public void createXmasReport() {
        try {         
            new CreateDoroXmasReport().createDBDoroXmasReport(this.emplAdminsController.getCon(), 0);
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }
    }  
    
    public void createApdPdf() {
        try {         
            new ApdPdf().apdPdf(this.emplAdminsController.getCon(), null);           
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }
    } 
    
    public void createCSL01() {
        try {         
            new CSL01().csl01(this.emplAdminsController.getCon(), null);           
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));  
        }
    }
    
 public void createApodixisPDF() {
        try {         
            new ApodixisPDF().apodixisPDF(this.emplAdminsController.getCon(), null);           
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }
    } 
 
 public void createDPApodixisPDF() {
        try {         
            new ApodixisPDF().apodixisPDF(this.emplAdminsController.getCon(), 
                    "DORO_PASHA_REPORT_"+Integer.toString(LocalDate.now().getYear()));           
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }
  } 
 
 public void createEAApodixisPDF() {
        try {         
            new ApodixisPDF().apodixisPDF(this.emplAdminsController.getCon(), 
                    "EPIDOMA_ADEIAS_REPORT_"+Integer.toString(LocalDate.now().getYear()));           
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }
  }
 
 public void createDXApodixisPDF() {
        try {         
            new ApodixisPDF().apodixisPDF(this.emplAdminsController.getCon(), 
                    "DORO_XMAS_REPORT_"+Integer.toString(LocalDate.now().minusMonths(1).getYear()));           
            } catch (SQLException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
               JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            }
  }
 
 public void showTimerReports(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.TIMER);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
 }
 
 public void handleOldVacationReports(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.VACATION);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }
 
   public void handleShowMisthodotikiHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.MISTHODOTIKH);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowMisthodotikiDoroPashaHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.MISTHODOTIKHDP);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowMisthodotikiEpidomaAdeiasHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.MISTHODOTIKHEA);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowMisthodotikiDoroXmasHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.MISTHODOTIKHXMAS);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowArthroHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.ARTHRO);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowArthroDoroPashaHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.ARTHRODP);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowArthroEpidomaAdeiasHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.ARTHROEA);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowArthroDoroXmasHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.ARTHROXMAS);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowIKAHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.IKA);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowIkaDoroPashaHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.IKADP);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowIkaEpidomaAdeiasHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.IKAEA);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowIkaDoroXmasHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.IKAXMAS);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowPliromiHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.PLIROMI);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowPliromiDoroPashaHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.PLIROMIDP);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowPliromiEpidomaAdeiasHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.PLIROMIEA);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowPliromiDoroXmasHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.PLIROMIXMAS);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowApodixisHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.APODIXIS);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowApodixisDoroPashaHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.APODIXISDP);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowApodixisEpidomaAdeiasHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.APODIXISEA);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowApodixisDoroXmasHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.APODIXISXMAS);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowAPDHistory(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.APD);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handleShowCSL01History(){
        try { 
            showOldReports.oldReportsList(this.emplAdminsController.getCon(), ReportEnum.CSL01);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
   }
   
   public void handlePrepareCreateOldTimerReports(){
       this.historyTitle = "ΕΠΑΝΑΔΗΜΙΟΥΡΓΙΑ ΠΑΛΑΙΟΥ TIMER REPORT";
       this.historyLabel = "Παρακαλώ επιλέξτε μήνα και έτος :";
       this.report = 1;       
   }
   
   public void createOldTimerReports(){
       LocalDate localDate = this.historyDate != null ? this.historyDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
       switch(this.report){
           case 1:{
               new CreateReport().CreateMonthlyDBTable(this.emplAdminsController.getCon(), localDate);
               break;
           }
           case 2:{
           try {
               new CreateSalaryReport().CreateDBSalaryReport(this.emplAdminsController.getCon(), localDate);
               break;
           } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           }
           }
           case 3:{
           try {
               new CreatePashaReport().createDBDoroPashaReport(this.emplAdminsController.getCon(), this.selectedYear);
               break;
           } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           }
           }
           case 4:{
           try {
               new CreateEAReport().createDBEpidomaAdeiasReport(this.emplAdminsController.getCon(), this.selectedYear);
               break;
           } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           }
           }
           case 5:{
           try {
               new CreateDoroXmasReport().createDBDoroXmasReport(this.emplAdminsController.getCon(), this.selectedYear);
               break;
           } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
           }
           }
           case 6:{          
               new CreateVacationReport(this.emplAdminsController.getCon()).createLastYearDayReport(this.selectedYear);
               break;           
           }
       }
       
   }
 
   public void prepareCreateOldSalaryReport(){
       this.historyTitle="ΕΠΑΝΑΔΗΜΙΟΥΡΓΙΑ ΠΑΛΑΙΟΥ REPORT ΜΙΣΘΟΔΟΣΙΑΣ ";
       this.historyLabel = "Παρακαλώ επιλέξτε μήνα και έτος :";
       this.report = 2;
   }
   
   public void prepareCreateOldDPReport(){
       this.historyTitle="ΕΠΑΝΑΔΗΜΙΟΥΡΓΙΑ ΠΑΛΑΙΟΥ REPORT ΔΩΡΟΥ ΠΑΣΧΑ";
       this.historyLabel = "Παρακαλώ επιλέξτε έτος :";
       this.report = 3;
   }
   
   public void prepareCreateOldEAReport(){
       this.historyTitle="ΕΠΑΝΑΔΗΜΙΟΥΡΓΙΑ ΠΑΛΑΙΟΥ REPORT ΕΠΙΔΟΜΑΤΟΣ ΑΔΕΙΑΣ";
       this.historyLabel = "Παρακαλώ επιλέξτε έτος :";
       this.report = 4;
   }
   
   public void prepareCreateOldXmasReport(){
       this.historyTitle="ΕΠΑΝΑΔΗΜΙΟΥΡΓΙΑ ΠΑΛΑΙΟΥ REPORT ΔΩΡΟΥ ΧΡΙΣΤΟΥΓΕΝΝΩΝ";
       this.historyLabel = "Παρακαλώ επιλέξτε έτος :";
       this.report = 5;
   }
   
   public void prepareCreateOldVacationReport(){
       this.historyTitle="ΕΠΑΝΑΔΗΜΙΟΥΡΓΙΑ ΠΑΛΑΙΟΥ REPORT ΑΔΕΙΩΝ";
       this.historyLabel = "Παρακαλώ επιλέξτε έτος :";
       this.report = 6;
   }
   
}
