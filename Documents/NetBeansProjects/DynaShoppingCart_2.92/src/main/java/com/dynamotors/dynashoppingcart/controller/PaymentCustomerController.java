package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.PaymentCustomer;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.PaymentCustomerFacade;

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

@Named("paymentCustomerController")
@SessionScoped
public class PaymentCustomerController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.PaymentCustomerFacade ejbFacade;
    private List<PaymentCustomer> items = null;
    private PaymentCustomer selected;

    public PaymentCustomerController() {
    }

    public PaymentCustomer getSelected() {
        return selected;
    }

    public void setSelected(PaymentCustomer selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PaymentCustomerFacade getFacade() {
        return ejbFacade;
    }

    public PaymentCustomer prepareCreate() {
        selected = new PaymentCustomer();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_invoice").getString("PaymentCustomerCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_invoice").getString("PaymentCustomerUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_invoice").getString("PaymentCustomerDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<PaymentCustomer> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_invoice").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_invoice").getString("PersistenceErrorOccured"));
            }
        }
    }

    public PaymentCustomer getPaymentCustomer(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<PaymentCustomer> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<PaymentCustomer> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = PaymentCustomer.class)
    public static class PaymentCustomerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PaymentCustomerController controller = (PaymentCustomerController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "paymentCustomerController");
            return controller.getPaymentCustomer(getKey(value));
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
            if (object instanceof PaymentCustomer) {
                PaymentCustomer o = (PaymentCustomer) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), PaymentCustomer.class.getName()});
                return null;
            }
        }

    }

}
