package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.ItemsPaymentOptions;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.ItemsPaymentOptionsFacade;

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

@Named("itemsPaymentOptionsController")
@SessionScoped
public class ItemsPaymentOptionsController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.ItemsPaymentOptionsFacade ejbFacade;
    private List<ItemsPaymentOptions> items = null;
    private ItemsPaymentOptions selected;
    private List<ItemsPaymentOptions> paymentOptions = null;

    public ItemsPaymentOptionsController() {
    }

    public ItemsPaymentOptions getSelected() {
        return selected;
    }

    public void setSelected(ItemsPaymentOptions selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ItemsPaymentOptionsFacade getFacade() {
        return ejbFacade;
    }

    public ItemsPaymentOptionsFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(ItemsPaymentOptionsFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public List<ItemsPaymentOptions> getPaymentOptions() {
        return paymentOptions;
    }

    public void setPaymentOptions(List<ItemsPaymentOptions> paymentOptions) {
        this.paymentOptions = paymentOptions;
    }
    
    public void produceOptions(String itemCode){
        paymentOptions = ejbFacade.produceOptions(itemCode);
    }

    public ItemsPaymentOptions prepareCreate() {
        selected = new ItemsPaymentOptions();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ItemsPaymentOptionsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ItemsPaymentOptionsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ItemsPaymentOptionsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ItemsPaymentOptions> getItems() {
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

    public ItemsPaymentOptions getItemsPaymentOptions(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<ItemsPaymentOptions> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ItemsPaymentOptions> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ItemsPaymentOptions.class)
    public static class ItemsPaymentOptionsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemsPaymentOptionsController controller = (ItemsPaymentOptionsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemsPaymentOptionsController");
            return controller.getItemsPaymentOptions(getKey(value));
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
            if (object instanceof ItemsPaymentOptions) {
                ItemsPaymentOptions o = (ItemsPaymentOptions) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ItemsPaymentOptions.class.getName()});
                return null;
            }
        }

    }

}
