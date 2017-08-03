/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import com.dynamotors.timer1._rest.Workers;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author evgero
 */
@Named("mazikiDiorthosiController")
@SessionScoped
public class MazikiDiorthosiController implements Serializable {
    
    @Inject
    private EmplAdminsController emplAdminsController;
    private List<Workers> selectedList = new ArrayList<>();
    private PreparedStatement prStm ;
    private Statement workersStm ;
    private ResultSet workersRs, rs;
    java.util.Date startDate = null;
    java.util.Date endDate = null;
    private LocalDate hireDate, apolisiDate;
    private List<LocalDate> holidaysList ;
    private Map<Integer, List<List<LocalDate>>> vacationDaysMap = new HashMap<>();
    private Map<Integer, List<List<LocalDate>>> sickLess3DaysMap = new HashMap<>();
    private Map<Integer, List<List<LocalDate>>> sickMore3DaysMap = new HashMap<>();

    public List<Workers> getSelectedList() {
        return selectedList;
    }

    public void setSelectedList(List<Workers> selectedList) {
        this.selectedList = selectedList;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public void handleDiorthoseBtn(){
        
    }
    
}
