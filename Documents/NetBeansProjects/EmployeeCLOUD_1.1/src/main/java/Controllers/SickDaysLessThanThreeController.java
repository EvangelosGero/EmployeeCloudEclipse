package Controllers;

import Entities.SickDaysLessThanThree;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import EJBs.SickDaysLessThanThreeFacade;
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

@Named("sickDaysLessThanThreeController")
@SessionScoped
public class SickDaysLessThanThreeController implements Serializable {

    @EJB
    private EJBs.SickDaysLessThanThreeFacade ejbFacade;
    private List<SickDaysLessThanThree> items = null;
    private SickDaysLessThanThree selected;
    private Workers selectedWorker;
    @Inject
    private WorkersController workersController;

    public SickDaysLessThanThreeController() {
    }

    public SickDaysLessThanThree getSelected() {
        return selected;
    }

    public void setSelected(SickDaysLessThanThree selected) {
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

    private SickDaysLessThanThreeFacade getFacade() {
        return ejbFacade;
    }
    
    public void selectListener(){
        this.selected.setId(this.selectedWorker.getId().shortValue());
        this.selected.setFirstName(this.selectedWorker.getFirstName());
        this.selected.setLastName(this.selectedWorker.getLastName());
        this.selected.setFatherName(this.selectedWorker.getFatherName());
    }

    public SickDaysLessThanThree prepareCreate() {
        selected = new SickDaysLessThanThree();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_Sick_Days").getString("SickDaysLessThanThreeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_Sick_Days").getString("SickDaysLessThanThreeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_Sick_Days").getString("SickDaysLessThanThreeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<SickDaysLessThanThree> getItems() {
        if (items == null) {
            items = getFacade().findAll();
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_Sick_Days").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_Sick_Days").getString("PersistenceErrorOccured"));
            }
        }
    }

    public SickDaysLessThanThree getSickDaysLessThanThree(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<SickDaysLessThanThree> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<SickDaysLessThanThree> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = SickDaysLessThanThree.class)
    public static class SickDaysLessThanThreeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SickDaysLessThanThreeController controller = (SickDaysLessThanThreeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sickDaysLessThanThreeController");
            return controller.getSickDaysLessThanThree(getKey(value));
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
            if (object instanceof SickDaysLessThanThree) {
                SickDaysLessThanThree o = (SickDaysLessThanThree) object;
                return getStringKey(o.getPk());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), SickDaysLessThanThree.class.getName()});
                return null;
            }
        }

    }

}
