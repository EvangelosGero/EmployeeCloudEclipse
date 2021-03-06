package Controllers;

import Controllers.Logic.CreateReport;
import Controllers.Logic.PersonTimer;
import Controllers.Logic.ShowReport;
import com.dynamotors.timer1._rest.Timer;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import EJBs.TimerFacade;

import java.io.Serializable;
import java.sql.SQLException;
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

@Named("timerController")
@SessionScoped
public class TimerController implements Serializable {

    @EJB
    private EJBs.TimerFacade ejbFacade;
    private List<Timer> items = null;
    private Timer selected;
    @Inject
    private EmplAdminsController emplAdminsController;
    @Inject
    private ShowReport showReport;

    public TimerController() {
    }
    
    public EmplAdminsController getEmplAdminsController() {
        return emplAdminsController;
    }

    public void setEmplAdminsController(EmplAdminsController emplAdminsController) {
        this.emplAdminsController = emplAdminsController;
    }

    public ShowReport getShowReport() {
        return showReport;
    }

    public void setShowReport(ShowReport showReport) {
        this.showReport = showReport;
    }

                
    public Timer getSelected() {
        return selected;
    }

    public void setSelected(Timer selected) {
        this.selected = selected;
    }
       
    public void createTimerReport(){
        new CreateReport().CreateMonthlyDBTable(emplAdminsController.getCon(), null);
    }
    
    public String showReportNow(){
        try {
            showReport.ProduceReportTable(emplAdminsController.getCon(), null);
            return "/views/timer/ShowReport.xhtml?faces-redirect=true";
        } catch (SQLException ex) {
            Logger.getLogger(TimerController.class.getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }       
    }
    
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private TimerFacade getFacade() {
        return ejbFacade;
    }

    public Timer prepareCreate() {
        selected = new Timer();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TimerCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TimerUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TimerDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Timer> getItems() {
        if (items == null) {
            items = getFacade().findAll();
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Timer getTimer(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Timer> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Timer> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Timer.class)
    public static class TimerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TimerController controller = (TimerController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "timerController");
            return controller.getTimer(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Timer) {
                Timer o = (Timer) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Timer.class.getName()});
                return null;
            }
        }

    }

}
