package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.ItemsDeliveryAvailablePin;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.ItemsDeliveryAvailablePinFacade;

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

@Named("itemsDeliveryAvailablePinController")
@SessionScoped
public class ItemsDeliveryAvailablePinController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.ItemsDeliveryAvailablePinFacade ejbFacade;
    private List<ItemsDeliveryAvailablePin> items = null;
    private ItemsDeliveryAvailablePin selected;
    private List<ItemsDeliveryAvailablePin> availPin = null;

    public ItemsDeliveryAvailablePinController() {
    }

    public ItemsDeliveryAvailablePin getSelected() {
        return selected;
    }

    public void setSelected(ItemsDeliveryAvailablePin selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ItemsDeliveryAvailablePinFacade getFacade() {
        return ejbFacade;
    }

    public ItemsDeliveryAvailablePinFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(ItemsDeliveryAvailablePinFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public List<ItemsDeliveryAvailablePin> getAvailPin() {
        return availPin;
    }

    public void setAvailPin(List<ItemsDeliveryAvailablePin> availPin) {
        this.availPin = availPin;
    }   
    
 /*   public void producePin(String itemCode){
        availPin = ejbFacade.producePinList(itemCode);
    }*/

    public ItemsDeliveryAvailablePin prepareCreate() {
        selected = new ItemsDeliveryAvailablePin();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ItemsDeliveryAvailablePinCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ItemsDeliveryAvailablePinUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ItemsDeliveryAvailablePinDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ItemsDeliveryAvailablePin> getItems() {
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

    public ItemsDeliveryAvailablePin getItemsDeliveryAvailablePin(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<ItemsDeliveryAvailablePin> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ItemsDeliveryAvailablePin> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ItemsDeliveryAvailablePin.class)
    public static class ItemsDeliveryAvailablePinControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemsDeliveryAvailablePinController controller = (ItemsDeliveryAvailablePinController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemsDeliveryAvailablePinController");
            return controller.getItemsDeliveryAvailablePin(getKey(value));
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
            if (object instanceof ItemsDeliveryAvailablePin) {
                ItemsDeliveryAvailablePin o = (ItemsDeliveryAvailablePin) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ItemsDeliveryAvailablePin.class.getName()});
                return null;
            }
        }

    }

}
