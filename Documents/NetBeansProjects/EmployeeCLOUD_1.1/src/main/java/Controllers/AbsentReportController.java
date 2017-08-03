/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.Logic.AbsentReportPOJO;
import Controllers.util.JsfUtil;
import java.io.Serializable;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author evgero
 */
@Named("absentReportController")
@SessionScoped
public class AbsentReportController implements Serializable{
    
    @Inject
    private EmplAdminsController emplAdminsController;
    private List<AbsentReportPOJO> data = new ArrayList<>();
    private PreparedStatement prStm;
    private Statement workersStm ;
    private ResultSet workersRs, rs; 
    java.util.Date startDate = null;
    java.util.Date endDate = null;
    LocalDate hireDate, apolisiDate;
    String justify = "ΑΔΙΚΑΙΟΛΟΓΗΤΗ";
    private final FacesContext facesContext = FacesContext.getCurrentInstance();

    public List<AbsentReportPOJO> getData() {
        return data;
    }

    public void setData(List<AbsentReportPOJO> data) {
        this.data = data;
    }
    
    public java.util.Date getStartDate() {
        return startDate;
    }

    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    public java.util.Date getEndDate() {
        return endDate;
    }

    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }
    
    public void handleShowCard() throws SQLException {  
       if(startDate != null && endDate != null){
        try{ 
            LocalDate endLocalDate = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            if (data!=null)data.clear();          
            String query = "SELECT workers.id, workers.first_name, workers.last_name, workers.hire_date, "
                    + "workers.apolisi, workers.diakopi "
                    + "FROM workers ";               
            workersStm = emplAdminsController.getCon().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            workersRs = workersStm.executeQuery(query);   
            
            int cnt=0;
            workersRs.beforeFirst();
            while (workersRs.next()){ 
             cnt++;   
            }                       
            
            double i=0;
            workersRs.beforeFirst();
            while (workersRs.next()){            
                i=i+1;       
                hireDate = workersRs.getDate(4).toLocalDate();                
                LocalDate counterDate = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (workersRs.getInt(5) != 0)apolisiDate = workersRs.getDate(6).toLocalDate();
                        else apolisiDate = LocalDate.now().plusDays(1);   
                while (((counterDate.isBefore(endLocalDate)) || (counterDate.isEqual(endLocalDate)))
                        &&(counterDate.isBefore(apolisiDate)) || (counterDate.isEqual(apolisiDate))){
                    if ((counterDate.getDayOfWeek().getValue()!= 6) &&
                     (counterDate.getDayOfWeek().getValue()!= 7) && (counterDate.isAfter(hireDate))){ 
                        if(absent(counterDate, workersRs.getInt(1))){                            
              
                        AbsentReportPOJO row = new AbsentReportPOJO(                
                        workersRs.getInt(1),
                        workersRs.getString(2),
                        workersRs.getString(3),
                        java.util.Date.from(counterDate.atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        justify);
            
                        data.add(row);
                     } 
                    }
                counterDate = counterDate.plusDays(1);                    
                }                               
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_vacation_days").getString("PersistenceErrorOccured"));
            
        }
        finally{            
                if (workersRs != null){workersRs.close();}
                if (workersStm != null){workersStm.close();}                
        }
                
      }      
     
    }
    
    private boolean absent (LocalDate date, int id) throws SQLException{
        boolean holiday = false, vacation = false, sickLess3 = false, sickMore3 = false;
        boolean here = false;
        justify = "ΑΔΙΚΑΙΟΛΟΓΗΤΗ";
       String qry = "SELECT workers.id FROM workers join timer on timer.code = workers.code "              
               + "WHERE ((workers.id = ?) AND (DATE(timer.starttime) = ?))";
        try {
          prStm = emplAdminsController.getCon().prepareStatement(qry);
          prStm.setInt(1, id);
          prStm.setDate(2, java.sql.Date.valueOf(date));          
          rs = prStm.executeQuery();
          if (rs.next()){here = true;} else {here = false;}
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);                
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
        }finally {
            if (rs != null) rs.close();
            if (prStm != null)prStm.close();
        }
        if (!here){            
            qry = "SELECT holidays_column FROM holidays WHERE holidays_column = ?";
            try {
                prStm = emplAdminsController.getCon().prepareStatement(qry);                
                prStm.setDate(1, java.sql.Date.valueOf(date));          
                rs = prStm.executeQuery();
                if (rs.next()){ holiday = true;} else {holiday = false;}
                here = holiday;
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);                
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
            }finally {
                 if (rs != null) rs.close();
                 if (prStm != null)prStm.close();
            }
            if(!holiday){
                qry = "SELECT id FROM vacation_days "
                    + "WHERE (id  = ?) AND (? BETWEEN exit_day AND back_day)";
            try {
                prStm = emplAdminsController.getCon().prepareStatement(qry); 
                prStm.setInt(1, id);
                prStm.setDate(2, java.sql.Date.valueOf(date));          
                rs = prStm.executeQuery(); 
                if (rs.next()) {vacation = true;} else {vacation = false;}
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);                
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
            }finally {
                if (rs != null) rs.close();
                if (prStm != null)prStm.close();
            }
            if (vacation){justify = "ΑΔΕΙΑ";} else {
                qry = "SELECT id FROM sick_days_less_than_three "
                    + "WHERE (id  = ?) AND (? BETWEEN exit_day AND back_day)";
            try {
                prStm = emplAdminsController.getCon().prepareStatement(qry); 
                prStm.setInt(1, id);
                prStm.setDate(2, java.sql.Date.valueOf(date));          
                rs = prStm.executeQuery();
                if (rs.next()) sickLess3 = true;
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);                
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
            }finally {
                if (rs != null) rs.close();
                if (prStm != null)prStm.close();
            }
            if (sickLess3){justify = "ΑΣΘΕΝΕΙΑ<3";} else {
                qry = "SELECT id FROM sick_days_more_than_three "
                    + "WHERE (id  = ?) AND (? BETWEEN exit_day AND back_day)";
            try {
                prStm = emplAdminsController.getCon().prepareStatement(qry); 
                prStm.setInt(1, id);
                prStm.setDate(2, java.sql.Date.valueOf(date));          
                rs = prStm.executeQuery();
                if (rs.next()) sickMore3 = true;
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);                
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Error", ex.getMessage()));
            }finally {
                if (rs != null) rs.close();
                if (prStm != null)prStm.close();
            }
            if (sickMore3){justify = "ΑΣΘΕΝΕΙΑ>3";}
            }  
            }
            
            }
        }
        
      return  !here;  
    }
    
}
