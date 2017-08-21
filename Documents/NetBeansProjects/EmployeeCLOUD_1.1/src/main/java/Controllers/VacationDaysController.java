package Controllers;

import Controllers.Logic.CreateVacationReport;
import Controllers.Logic.showVacationReport;
import Entities.VacationDays;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import EJBs.VacationDaysFacade;
import com.dynamotors.timer1._rest.Workers;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
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
        int currentYear = LocalDate.now().getYear();
        Statement stm = null;
        Statement ssss = null;
        ResultSet rsss = null;
        try {
            if (this.isVacationRadio()){
                new CreateVacationReport().createLastYearDayReport(currentYear-1);
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
            }
            new CreateVacationReport().CreateVacationDBTable(emplAdminsController.getCon());
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally {
                {try {
                    if (stm != null)stm.close();
                    if (rsss != null)rsss.close();
                    if (ssss != null)ssss.close();
                } catch (SQLException ex) {
                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
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
