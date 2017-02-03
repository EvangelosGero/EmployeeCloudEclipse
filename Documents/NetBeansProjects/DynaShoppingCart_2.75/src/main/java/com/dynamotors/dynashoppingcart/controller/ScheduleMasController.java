package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.ScheduleMas;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.ScheduleMasFacade;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

@Named("scheduleMasController")
@SessionScoped
public class ScheduleMasController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.ScheduleMasFacade ejbFacade;    
    private List<ScheduleMas> items = null;
    private ScheduleMas selected;
    private Map<Integer, String> scheduleMasMap = null;    

    public ScheduleMasController() {
    }

    public ScheduleMas getSelected() {
        return selected;
    }

    public void setSelected(ScheduleMas selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ScheduleMasFacade getFacade() {
        return ejbFacade;
    }    
       
    public ScheduleMas prepareCreate() {
        selected = new ScheduleMas();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ScheduleMasCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ScheduleMasUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ScheduleMasDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }   
    

    public List<ScheduleMas> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    
    public Map<Integer, String> getScheduleMasMap() {
        if (this.scheduleMasMap == null) {
            this.scheduleMasMap = this.getItems().stream()
                .collect(Collectors.toMap(ScheduleMas::getScheduleOrder, 
                        ScheduleMas::getScheduleType));                     
        }
        return this.scheduleMasMap;
    }    
    
    public void setScheduleMasMap(Map<Integer, String> scheduleMasMap) {
        this.scheduleMasMap = scheduleMasMap;
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

    public ScheduleMas getScheduleMas(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<ScheduleMas> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ScheduleMas> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ScheduleMas.class)
    public static class ScheduleMasControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ScheduleMasController controller = (ScheduleMasController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "scheduleMasController");
            return controller.getScheduleMas(getKey(value));
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
            if (object instanceof ScheduleMas) {
                ScheduleMas o = (ScheduleMas) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ScheduleMas.class.getName()});
                return null;
            }
        }

    }

}
