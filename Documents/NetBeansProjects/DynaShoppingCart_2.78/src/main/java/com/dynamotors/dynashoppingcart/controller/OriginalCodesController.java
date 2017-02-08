package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.OriginalCodes;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.OriginalCodesFacade;

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

@Named("originalCodesController")
@SessionScoped
public class OriginalCodesController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.OriginalCodesFacade ejbFacade;
    @Inject
    ItemsController itemsController;
    private List<OriginalCodes> items = null;
    private OriginalCodes selected;

    public OriginalCodesController() {
    }

    public OriginalCodes getSelected() {
        return selected;
    }

    public void setSelected(OriginalCodes selected) {
        this.selected = selected;
    }

    public ItemsController getItemsController() {
        return itemsController;
    }

    public void setItemsController(ItemsController itemsController) {
        this.itemsController = itemsController;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private OriginalCodesFacade getFacade() {
        return ejbFacade;
    }

    public OriginalCodes prepareCreate() {
        selected = new OriginalCodes();
        if(itemsController.getSelected() != null)selected.setItems(itemsController.getSelected());
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_OriginalCodes").getString("OriginalCodesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_OriginalCodes").getString("OriginalCodesUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_OriginalCodes").getString("OriginalCodesDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<OriginalCodes> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_OriginalCodes").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_OriginalCodes").getString("PersistenceErrorOccured"));
            }
        }
    }

    public OriginalCodes getOriginalCodes(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<OriginalCodes> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<OriginalCodes> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = OriginalCodes.class)
    public static class OriginalCodesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            OriginalCodesController controller = (OriginalCodesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "originalCodesController");
            return controller.getOriginalCodes(getKey(value));
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
            if (object instanceof OriginalCodes) {
                OriginalCodes o = (OriginalCodes) object;
                return getStringKey(o.getOcId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), OriginalCodes.class.getName()});
                return null;
            }
        }

    }

}
