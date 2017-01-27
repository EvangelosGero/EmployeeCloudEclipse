package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.Cart;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.CartFacade;
import com.dynamotors.dynashoppingcart.entities.Items;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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

@Named("cartController")
@SessionScoped
public class CartController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.CartFacade ejbFacade;
    private List<Cart> items = null;
    private Cart cartItem;
    private Cart selected;
    @Inject
    private ItemsController itemsController;
    @Inject
    private UsernmController usernmController;
    private int spinner1;

    public Cart getCartItem() {
        return cartItem;
    }

    public void setCartItem(Cart cartItem) {
        this.cartItem = cartItem;
    }   

    public UsernmController getUsernmController() {
        return usernmController;
    }

    public int getSpinner1() {
        return spinner1;
    }

    public void setSpinner1(int spinner1) {
        this.spinner1 = spinner1;
    }

    public void setUsernmController(UsernmController usernmController) {
        this.usernmController = usernmController;
    }

    public CartFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(CartFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public ItemsController getItemsController() {
        return itemsController;
    }

    public void setItemsController(ItemsController itemsController) {
        this.itemsController = itemsController;
    }    

    public CartController() {
    }

    public Cart getSelected() {
        return selected;
    }

    public void setSelected(Cart selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CartFacade getFacade() {
        return ejbFacade;
    }
    
    public void cartPersist(){
        List<Cart> cartToClear = ejbFacade.findAll();
        Integer id1 = usernmController.getUsernmLogged().getId();
        for (Cart c : cartToClear)
            if (Objects.equals(c.getUsercode(), id1))ejbFacade.remove(c);
       
        for(Items item : itemsController.getCartProducts()){
//        int id1 = (int)FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("usernmLoggedInId");            
            cartItem = new Cart();
            cartItem.setUsercode(id1);
            cartItem.setShopper(usernmController.getUsernmLogged().getUserName());
            cartItem.setItemCode(item.getItemCode());
            cartItem.setItemValue(usernmController.getUsernmLogged().getRoleuser().equals('s') ? 
                    item.getItemWholesalesValue(): item.getItemRetailValue());
            cartItem.setCartDate(Date.valueOf(LocalDate.now()));
            cartItem.setItemId(item.getItemId());
            cartItem.setQuantity(item.getQuantity());
            ejbFacade.edit(cartItem);
        }       
    }

    public Cart prepareCreate() {
        selected = new Cart();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CartCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CartUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CartDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Cart> getItems() {
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

    public Cart getCart(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Cart> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Cart> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Cart.class)
    public static class CartControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CartController controller = (CartController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cartController");
            return controller.getCart(getKey(value));
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
            if (object instanceof Cart) {
                Cart o = (Cart) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Cart.class.getName()});
                return null;
            }
        }

    }

}
