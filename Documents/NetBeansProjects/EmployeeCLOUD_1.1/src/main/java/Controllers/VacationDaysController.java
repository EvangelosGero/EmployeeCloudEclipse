package Controllers;

import Controllers.Logic.CreateVacationReport;
import Controllers.Logic.showVacationReport;
import Controllers.util.DropIfExists;
import Entities.VacationDays;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import EJBs.VacationDaysFacade;
import com.dynamotors.timer1._rest.Workers;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import org.primefaces.context.RequestContext;

@Named("vacationDaysController")
@SessionScoped
public class VacationDaysController implements Serializable {

    @EJB
    private EJBs.VacationDaysFacade ejbFacade;
    private List<VacationDays> items = null;
    private VacationDays selected;
    private boolean vacationRadio;
    private Workers selectedWorker;
    @Inject
    private WorkersController workersController;
    @Inject
    private EmplAdminsController emplAdminsController;
    @Inject
    private showVacationReport showVR;

    public VacationDaysController() {
        this.setVacationRadio(true);
    }

    public VacationDays getSelected() {
        return selected;
    }

    public void setSelected(VacationDays selected) {
        this.selected = selected;
    }

    public EmplAdminsController getEmplAdminsController() {
        return emplAdminsController;
    }

    public void setEmplAdminsController(EmplAdminsController emplAdminsController) {
        this.emplAdminsController = emplAdminsController;
    }

    public showVacationReport getShowVR() {
        return showVR;
    }

    public void setShowVR(showVacationReport showVR) {
        this.showVR = showVR;
    }

    public boolean isVacationRadio() {
        return vacationRadio;
    }

    public void setVacationRadio(boolean vacationRadio) {
        this.vacationRadio = vacationRadio;
    }

    public Workers getSelectedWorker() {
        return selectedWorker;
    }

    public void setSelectedWorker(Workers selectedWorker) {
        this.selectedWorker = selectedWorker;
    }
    

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private VacationDaysFacade getFacade() {
        return ejbFacade;
    }

    public WorkersController getWorkersController() {
        return workersController;
    }

    public void setWorkersController(WorkersController workersController) {
        this.workersController = workersController;
    }
        
    public VacationDays prepareCreate() {
        selected = new VacationDays();
        initializeEmbeddableKey();
        return selected;
    }
    
    public void selectListener(){
        this.selected.setId(this.selectedWorker.getId().shortValue());
        this.selected.setFirstName(this.selectedWorker.getFirstName());
        this.selected.setLastName(this.selectedWorker.getLastName());
        this.selected.setFatherName(this.selectedWorker.getFatherName());
    }
    
    public void unSelectListener(){
        
    }
    
    public void createVacationReport() throws SQLException{
        if ( LocalDate.now().getMonth() == Month.JANUARY){
            RequestContext context = RequestContext.getCurrentInstance();        
            context.execute("PF('vacationDlgWV').show();");        
            context.update("vacationGT2:GT2Box");
        }
        else new CreateVacationReport().CreateVacationDBTable(emplAdminsController.getCon());
    }
    
    public void metaforaVacation(){
        PreparedStatement prStm = null;
        Map<Integer, Integer> consumedMap = new HashMap<>();
        List<LocalDate> holidaysList  = new ArrayList<>();
        int currentYear = LocalDate.now().getYear();
        int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
        int tableYear = LocalDate.now().minusMonths(1).getYear();
        Connection con = this.emplAdminsController.getCon();
        Statement stm = null;
        Statement ssss = null;
        ResultSet rsss = null;
        try {
            if (this.isVacationRadio()){
                                              
            String tableStr = "REPORT_"+Integer.toString(previousMonth)
                    +"_"+Integer.toString(tableYear);
            
            
            DropIfExists.exec(con, "VIEW", "TEMP2");
            DropIfExists.exec(con, "VIEW", "TEMP1");
            DropIfExists.exec(con, "TABLE", "TEMP");
            
             /*TEMP1 is used to add any new employees in the list and remove the fired of last year*/ 
            
            
            String query = "CREATE VIEW TEMP1 (ID, firstname, lastname) AS "                   
                    + "SELECT ID, first_name, last_name FROM workers "                    
                    + "WHERE (workers.apolisi = 0 OR (workers.apolisi <> 0 "
                    + "AND (YEAR(workers.diakopi) = " +Integer.toString(tableYear)+ ")"
                    + "AND (MONTH(workers.diakopi) > " +Integer.toString(previousMonth - 1)+ ")))";
            
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            int update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
            
            
            query = "CREATE TABLE TEMP ( " +
                            "ID smallint, " +
                            "firstname varchar(20), " +
                            "lastname varchar(20), " +
                            "ENTITLED_DAYS smallint DEFAULT 0, " +
                            "LASTYEAR_DAYS smallint DEFAULT 0, " +
                            "CONSUMED_DAYS smallint DEFAULT 0, " +
                            "REMAINING_DAYS smallint DEFAULT 0)";            
            
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm!= null){stm.close();} 
            
            
            
            query = "CREATE VIEW TEMP2 (ID, firstname, lastname, entitled_days, lastyear_days, " +
                    "consumed_days, remaining_days) AS " +
            "SELECT TEMP1.ID, TEMP1.firstname, TEMP1.lastname, T1.entitled_days, " +
            "T1.lastyear_days, T1.consumed_days, " +
            "T1.remaining_days " +
            "FROM TEMP1 " +
            "LEFT JOIN (EMPLOYEE_VACATION "  
                    + " AS T1) "
    //        + " JOIN workers "
    //        + "ON T1.id = workers.id "
    //                + "AND (workers.apolisi = 0 OR (workers.apolisi <> 0 "
    //                + "AND (YEAR(workers.diakopi) = " +Integer.toString(tableYear)+ "))) "                            
            +"ON TEMP1.ID = T1.ID ";
            
            
            
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
            update = stm.executeUpdate(query);
            if (stm != null){stm.close();}
            
             /*Compute (IN JAVA !!) the consumed days from VACATION_DAYS table*/        
                
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
        String sql1 = "SELECT * FROM holidays";
        ResultSet rs1 = stm.executeQuery(sql1);
        /* Put rs1 in a List */        
        while (rs1.next()){            
            holidaysList.add(rs1.getDate(1).toLocalDate());
        }
        if (stm != null) { stm.close();}
        if (rs1 != null) {rs1.close();}
        
        new CreateVacationReport(emplAdminsController.getCon()).computeConsumedDays(currentYear) ;       
                       
        query = "INSERT INTO TEMP (ID, firstname, lastname, entitled_days, lastyear_days " +
                ", consumed_days, remaining_days " +
                ") SELECT t.ID, t.firstname, t.lastname, t.ENTITLED_DAYS, t.LASTYEAR_DAYS " +
                ", 0, 0 " +    
                "FROM TEMP2 t " ;
        
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
        update = stm.executeUpdate(query);
        if (stm != null){stm.close();}
    
         /* Set to Zero any null values in TEMP */ 
        
        query = "UPDATE temp set lastyear_days = 0 where lastyear_days IS NULL";
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        update = stm.executeUpdate(query);
        if (stm != null){stm.close();}                        
        query = "UPDATE temp set consumed_days = 0 where consumed_days IS NULL";
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
        update = stm.executeUpdate(query);
        if (stm != null){stm.close();}
    
        /*Now update the consumed days in TEMP*/
    
        for(Map.Entry<Integer, Integer> entry : consumedMap.entrySet())
        {        
            String str = "UPDATE temp SET consumed_days = ? WHERE id = ? ";        
            prStm = con.prepareStatement(str);    
            prStm.setInt(1, entry.getValue());
            prStm.setInt(2, entry.getKey());
            int updatedRows = prStm.executeUpdate();    
            if (prStm != null) {prStm.close();}
        }
        new CreateVacationReport(emplAdminsController.getCon()).createLastYearDayReport(currentYear-1);
                
        String query2 = "SELECT id, remaining_days FROM VACATION_REPORT_" +Integer.toString(currentYear-1);
        ssss = emplAdminsController.getCon().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
        rsss = ssss.executeQuery(query2);
        while (rsss.next()){
                    String query1 = "UPDATE temp SET lastyear_days = "+Integer.toString(rsss.getInt("remaining_days"))+
                            " WHERE temp.id = "+Integer.toString(rsss.getInt("id"));
                    stm = emplAdminsController.getCon().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                    int update1 = stm.executeUpdate(query1);
                    if(stm != null)stm.close();
                
            }
         /* Now compute the remaining days correctly */
    
        query = "UPDATE temp SET remaining_days = entitled_days + lastyear_days - consumed_days ";                            
                           
        stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                            ResultSet.CONCUR_UPDATABLE);
        update = stm.executeUpdate(query); 
    
            JsfUtil.addSuccessMessage("Η δημιουργία του report αδειών ολοκληρώθηκε!"); 
        
          }
            
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally {
                try {
                    
                    String query = "DROP VIEW TEMP2" ;
        
                stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                   ResultSet.CONCUR_UPDATABLE);                      
                int update = stm.executeUpdate(query);
                if (stm != null){stm.close();}
        
                query = "DROP VIEW TEMP1" ;
        
                stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
                update = stm.executeUpdate(query);
                if (stm!= null) {stm.close();}
        
                query = "DROP TABLE EMPLOYEE_VACATION" ;
        
                stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);                      
                update = stm.executeUpdate(query);
                if (stm != null){stm.close();}

                query = "RENAME TABLE TEMP TO EMPLOYEE_VACATION";

                stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
                      
                update = stm.executeUpdate(query);
                if (stm != null){stm.close();}
                    
                    if (stm != null)stm.close();
                    if (rsss != null)rsss.close();
                    if (ssss != null)ssss.close();
                } catch (SQLException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
                
            }
    }
    
    
    
    public String showReportNow(){
        try {
            showVR.ProduceReportTable(emplAdminsController.getCon(), "EMPLOYEE_VACATION");
            return "/views/vacationDays/ShowVacationReport.xhtml?faces-redirect=true";
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }       
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_vacation_days").getString("VacationDaysCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_vacation_days").getString("VacationDaysUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_vacation_days").getString("VacationDaysDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<VacationDays> getItems() {
        if (items == null) {
            items = getFacade().findAll();
            //Initialize employeesIds too
            //List<Short> employeesIdsALL = (List<Short>)items.stream().map(VacationDays::getId).collect(Collectors.toList());
            //this.setEmployeesIds((List<Short>)employeesIdsALL.stream().distinct().collect(Collectors.toList()));
            //this.emplIdsArray = (Short[])this.getEmployeesIds().toArray();
            items.forEach(s -> {
                if(this.workersController.getWorkers((int)s.getId()) != null){
                    s.setLastName(this.workersController.getWorkers((int)s.getId()).getLastName());
                    s.setFirstName(this.workersController.getWorkers((int)s.getId()).getFirstName());
                    s.setFatherName(this.workersController.getWorkers((int)s.getId()).getFatherName());
                }
                else {
                    s.setLastName("");
                    s.setFirstName("");
                    s.setFatherName("");
                }
            });            
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_vacation_days").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_vacation_days").getString("PersistenceErrorOccured"));
            }
        }
    }

    public VacationDays getVacationDays(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<VacationDays> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<VacationDays> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = VacationDays.class)
    public static class VacationDaysControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VacationDaysController controller = (VacationDaysController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "vacationDaysController");
            return controller.getVacationDays(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof VacationDays) {
                VacationDays o = (VacationDays) object;
                return getStringKey(o.getPk());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), VacationDays.class.getName()});
                return null;
            }
        }

    }

}
