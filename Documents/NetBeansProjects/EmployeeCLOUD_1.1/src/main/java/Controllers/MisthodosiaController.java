/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.Logic.Misthodosia.CreateSalaryReport;
import Controllers.util.JsfUtil;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.context.RequestContext;

/**
 *
 * @author evgero
 */
@Named("misthodosiaController")
@SessionScoped
public class MisthodosiaController implements Serializable {
    
    @Inject
    private EmplAdminsController emplAdminsController;
    private String salaryTableStr;
    private double epidotisi = 0;
    private CreateSalaryReport createSalaryReport;

    public double getEpidotisi() {
        return epidotisi;
    }

    public void setEpidotisi(double epidotisi) {
        this.epidotisi = epidotisi;
    }

    public CreateSalaryReport getCreateSalaryReport() {
        return createSalaryReport;
    }

    public void setCreateSalaryReport(CreateSalaryReport createSalaryReport) {
        this.createSalaryReport = createSalaryReport;
    }
    
    public void createMisthodosiaReport() {
        try {
            this.createSalaryReport = new CreateSalaryReport();
            this.createSalaryReport.setFirstRun(true);            
            salaryTableStr = createSalaryReport.CreateDBSalaryReport(this.emplAdminsController.getCon(), null);
            } catch (InterruptedException ex) {
               Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));            
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
    }
    
    public void saveEpidotisi(){
        try {
            for(int pk : this.createSalaryReport.getEpidotisiMap().keySet())
                Collections.replaceAll(this.createSalaryReport.getEpidotisiMap().get(pk), null, new Double(0));
            this.createSalaryReport.setFirstRun(false);
            salaryTableStr = createSalaryReport.CreateDBSalaryReport(this.emplAdminsController.getCon(), null);
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
           JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        } catch (InterruptedException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
           JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }
    
}
