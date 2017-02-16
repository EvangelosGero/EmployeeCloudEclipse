package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.Seller;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.SellerFacade;
import com.dynamotors.dynashoppingcart.entities.Items;

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

@Named("sellerController")
@SessionScoped
public class SellerController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.SellerFacade ejbFacade;
    private List<Seller> items = null;    
    private Seller selected;
    private Items item;
    @Inject
    private UsernmController usernmController;
    @Inject
    private ItemsController itemsController;    
    
    public SellerController() {
    }

    public Seller getSelected() {
        return selected;
    }

    public void setSelected(Seller selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SellerFacade getFacade() {
        return ejbFacade;
    }

    public SellerFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(SellerFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    } 

    public Items getItem() {
        return item;
    }

    public void setItem(Items item) {
        this.item = item;
    }

    public UsernmController getUsernmController() {
        return usernmController;
    }

    public void setUsernmController(UsernmController usernmController) {
        this.usernmController = usernmController;
    }

    public ItemsController getItemsController() {
        return itemsController;
    }

    public void setItemsController(ItemsController itemsController) {
        this.itemsController = itemsController;
    }   
    
    public void viewItem(Items item){
        this.selected = new Seller();
        this.item = item;        
        int sellerId =item.getUsernmSellerId();
        this.selected.setUsernmId(sellerId);
        this.selected.setItemCode(item.getItemCode());
        this.selected.setItemId(item.getItemId());
        this.selected.setItemValue(item.getItemRetailValue());
        this.selected.setSellerAddress(usernmController.getEjbFacade().find(sellerId).getAddress());
        this.selected.setSellerCode(usernmController.getEjbFacade().find(sellerId).getUserName());
        this.selected.setSellerMob(Integer.valueOf(usernmController.getEjbFacade().find(sellerId).getMobile()));
        this.selected.setSellerName(usernmController.getEjbFacade().find(sellerId).getUserName());
    }    
    public Seller prepareCreate() {
        selected = new Seller();
        selected.setUsernmId(usernmController.getUsernmLogged().getId());
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SellerCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SellerUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("SellerDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Seller> getItems() {
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

    public Seller getSeller(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Seller> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Seller> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Seller.class)
    public static class SellerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SellerController controller = (SellerController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "sellerController");
            return controller.getSeller(getKey(value));
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
            if (object instanceof Seller) {
                Seller o = (Seller) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Seller.class.getName()});
                return null;
            }
        }

    }

}
