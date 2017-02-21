package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.ItemsReviews;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.ItemsReviewsFacade;

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

@Named("itemsReviewsController")
@SessionScoped
public class ItemsReviewsController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.ItemsReviewsFacade ejbFacade;
    @Inject
    private UsernmController usernmController;
    private List<ItemsReviews> itemsReviewsPerCustomer = null;
    private List<ItemsReviews> items = null;
    private ItemsReviews selected;

    public ItemsReviewsController() {
    }

    public ItemsReviews getSelected() {
        return selected;
    }

    public void setSelected(ItemsReviews selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ItemsReviewsFacade getFacade() {
        return ejbFacade;
    }

    public ItemsReviewsFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(ItemsReviewsFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public UsernmController getUsernmController() {
        return usernmController;
    }

    public void setUsernmController(UsernmController usernmController) {
        this.usernmController = usernmController;
    }

    public List<ItemsReviews> getItemsReviewsPerCustomer() {
        return itemsReviewsPerCustomer;
    }

    public void setItemsReviewsPerCustomer(List<ItemsReviews> itemsReviewsPerCustomer) {
        this.itemsReviewsPerCustomer = itemsReviewsPerCustomer;
    }
    
    public String findItemsReviewsPerCustomer(){
        if (usernmController.isLoggedIn()){            
            return "/views/itemsReviews/ItemsReviewsPerCustomer.xhtml?faces-redirect=true";
        }
        return null;   
    }
    
    public void produceItemsReviewsPerCustomer(){
        if (usernmController.isLoggedIn())
            itemsReviewsPerCustomer = ejbFacade.itemsReviewsPerCustomer(usernmController.getUsernmLogged().getId());
    }
    public ItemsReviews prepareCreate() {
        selected = new ItemsReviews();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_itemsReviews").getString("ItemsReviewsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_itemsReviews").getString("ItemsReviewsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_itemsReviews").getString("ItemsReviewsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ItemsReviews> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_itemsReviews").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_itemsReviews").getString("PersistenceErrorOccured"));
            }
        }
    }

    public ItemsReviews getItemsReviews(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<ItemsReviews> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ItemsReviews> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ItemsReviews.class)
    public static class ItemsReviewsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemsReviewsController controller = (ItemsReviewsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemsReviewsController");
            return controller.getItemsReviews(getKey(value));
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
            if (object instanceof ItemsReviews) {
                ItemsReviews o = (ItemsReviews) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ItemsReviews.class.getName()});
                return null;
            }
        }

    }

}
