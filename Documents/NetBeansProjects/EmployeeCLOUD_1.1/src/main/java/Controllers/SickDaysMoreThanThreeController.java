package Controllers;

import Entities.SickDaysMoreThanThree;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import EJBs.SickDaysMoreThanThreeFacade;
import com.dynamotors.timer1._rest.Workers;

import java.io.Serializable;
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

@Named("sickDaysMoreThanThreeController")
@SessionScoped
public class SickDaysMoreThanThreeController implements Serializable {

    @EJB
    private EJBs.SickDaysMoreThanThreeFacade ejbFacade;
    private List<SickDaysMoreThanThree> items = null;
    private SickDaysMoreThanThree selected;
    private Workers selectedWorker;
    @Inject
    private WorkersController workersController;

    public SickDaysMoreThanThreeController() {
    }

    public SickDaysMoreThanThree getSelected() {
        return selected;
    }

    public void setSelected(SickDaysMoreThanThree selected) {
        this.selected = selected;
    }

    public Workers getSelectedWorker() {
        return selectedWorker;
    }

    public void setSelectedWorker(Workers selectedWorker) {
        this.selectedWorker = selectedWorker;
    }

    public WorkersController getWorkersController() {
        return workersController;
    }

    public void setWorkersController(WorkersController workersController) {
        this.workersController = workersController;
    }
        
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SickDaysMoreThanThreeFacade getFacade() {
        return ejbFacade;
    }

    public SickDaysMoreThanThree prepareCreate() {
        selected = new SickDaysMoreThanThree();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_Sick_Days").getString("SickDaysMoreThanThreeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_Sick_Days").getString("SickDaysMoreThanThreeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_Sick_Days").getString("SickDaysMoreThanThreeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SickDaysMoreThanThree> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_Sick_Days").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_Sick_Days").getString("PersistenceErrorOccured"));
            }
        }
    }

    public SickDaysMoreThanThree getSickDaysMoreThanThree(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<SickDaysMoreThanThree> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SickDaysMoreThanThree> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = SickDaysMoreThanThree.class)
    public static class SickDaysMoreThanThreeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SickDaysMoreThanThreeController controller = (SickDaysMoreThanThreeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sickDaysMoreThanThreeController");
            return controller.getSickDaysMoreThanThree(getKey(value));
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
            if (object instanceof SickDaysMoreThanThree) {
                SickDaysMoreThanThree o = (SickDaysMoreThanThree) object;
                return getStringKey(o.getPk());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SickDaysMoreThanThree.class.getName()});
                return null;
            }
        }

    }

}
