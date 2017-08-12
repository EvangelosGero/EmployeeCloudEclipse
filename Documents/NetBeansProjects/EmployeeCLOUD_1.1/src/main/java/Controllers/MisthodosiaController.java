/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.Logic.Misthodosia.ApdPdf;
import Controllers.Logic.Misthodosia.ApodixisPDF;
import Controllers.Logic.Misthodosia.CSL01;
import Controllers.Logic.Misthodosia.CreateDoroXmasReport;
import Controllers.Logic.Misthodosia.CreateEAReport;
import Controllers.Logic.Misthodosia.CreatePashaReport;
import Controllers.util.JsfUtil;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.time.LocalDate;
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
     
    
    
}
