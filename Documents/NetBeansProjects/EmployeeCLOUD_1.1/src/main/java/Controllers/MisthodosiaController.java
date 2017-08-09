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
    
    
    
}
