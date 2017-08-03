/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.Logic.AbsentReportPOJO;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author evgero
 */
@Named("absentReportController")
@SessionScoped
public class AbsentReportController implements Serializable{
    
    private List<AbsentReportPOJO> data = new ArrayList<>();
    private PreparedStatement prStm;
    private Statement workersStm ;
    private ResultSet workersRs, rs; 
    LocalDate startDate = null;
    LocalDate endDate = null;
    LocalDate hireDate, apolisiDate;
    String justify = "ΑΔΙΚΑΙΟΛΟΓΗΤΗ";

    public List<AbsentReportPOJO> getData() {
        return data;
    }

    public void setData(List<AbsentReportPOJO> data) {
        this.data = data;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
}
