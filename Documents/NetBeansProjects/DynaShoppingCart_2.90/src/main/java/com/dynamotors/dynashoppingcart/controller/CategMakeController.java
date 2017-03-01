package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.CategMake;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.CategMakeFacade;

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


@Named("categMakeController")
@SessionScoped
public class CategMakeController implements Serializable {


    @EJB private com.dynamotors.dynashoppingcart.ejbs.CategMakeFacade ejbFacade; 
    @Inject
    CategModelController categModelController;
     
    private List<CategMake> items = null;
    private CategMake selected;
    private CategMake nullMake = null;

    public CategMakeController() {
    }

    public CategMake getSelected() {
        return selected;
    }

    public void setSelected(CategMake selected) {
        this.selected = selected;        
    }

    public CategModelController getCategModelController() {
        return categModelController;
    }

    public void setCategModelController(CategModelController categModelController) {
        this.categModelController = categModelController;
    }

    public CategMake getNullMake() {
        return nullMake;
    }

    public void setNullMake(CategMake nullMake) {
        this.nullMake = nullMake;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CategMakeFacade getFacade() {
        return ejbFacade;
    }
    
    public void handleChangeOfMake(CategMake make){ //Called from index.html to synchronize westPane
       this.selected = make;
       categModelController.setSelected(null);        
        if(make != null)
            categModelController.setMatchedItems(categModelController.getEjbFacade().findFromParentId(make.getMkId()));
        else
            categModelController.setMatchedItems(null);
    }
    
    public CategMake prepareCreate() {
        selected = new CategMake();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_Categ").getString("CategMakeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_Categ").getString("CategMakeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_Categ").getString("CategMakeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<CategMake> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_Categ").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_Categ").getString("PersistenceErrorOccured"));
            }
        }
    }

    public CategMake getCategMake(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<CategMake> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<CategMake> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(value = "categMakeCC")
    public static class CategMakeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CategMakeController controller = (CategMakeController)facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "categMakeController");
            return controller.getCategMake(getKey(value));
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
            if (object instanceof CategMake) {
                CategMake o = (CategMake) object;
                return getStringKey(o.getMkId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), CategMake.class.getName()});
                return null;
            }
        }

    }

}
