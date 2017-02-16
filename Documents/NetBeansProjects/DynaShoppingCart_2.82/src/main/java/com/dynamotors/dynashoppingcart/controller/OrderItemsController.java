package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.OrderItems;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.CartFacade;
import com.dynamotors.dynashoppingcart.ejbs.ItemsReviewsFacade;
import com.dynamotors.dynashoppingcart.ejbs.ItemsFacade;
import com.dynamotors.dynashoppingcart.ejbs.OrderItemsFacade;
import com.dynamotors.dynashoppingcart.ejbs.OrdersFacade;
import com.dynamotors.dynashoppingcart.entities.Cart;
import com.dynamotors.dynashoppingcart.entities.Items;
import com.dynamotors.dynashoppingcart.entities.ItemsReviews;
import com.dynamotors.dynashoppingcart.entities.Orders;
import com.dynamotors.dynashoppingcart.entities.Usernm;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

@Named("orderItemsController")
@SessionScoped
public class OrderItemsController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.OrderItemsFacade ejbFacade;
     @EJB
    private CartFacade cartFacade;
    @EJB
    private OrdersFacade ordersFacade;
    @EJB
    private ItemsFacade itemsFacade;
    @EJB
    private ItemsReviewsFacade itemsReviewsFacade;
    @Inject
    private UsernmController usernmController;
    @Inject 
    private ItemsController itemsController;
    private List<Cart> cartList = null;
    private List<OrderItems> items = null;
    private List<OrderItems> orderItemsPerCustomer = null;
    private OrderItems selected;
    private int option; 

    public OrderItemsController() {
    }

    public OrderItems getSelected() {
        return selected;
    }

    public void setSelected(OrderItems selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private OrderItemsFacade getFacade() {
        return ejbFacade;
    }

    public ItemsFacade getItemsFacade() {
        return itemsFacade;
    }

    public void setItemsFacade(ItemsFacade itemsFacade) {
        this.itemsFacade = itemsFacade;
    }

    public CartFacade getCartFacade() {
        return cartFacade;
    }

    public void setCartFacade(CartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }

    public OrdersFacade getOrdersFacade() {
        return ordersFacade;
    }

    public void setOrdersFacade(OrdersFacade ordersFacade) {
        this.ordersFacade = ordersFacade;
    }

    public ItemsReviewsFacade getItemsReviewsFacade() {
        return itemsReviewsFacade;
    }

    public void setItemsReviewsFacade(ItemsReviewsFacade itemsReviewsFacade) {
        this.itemsReviewsFacade = itemsReviewsFacade;
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

    public List<Cart> getCartList() {
        return cartList;
    }

    public void setCartList(List<Cart> cartList) {
        this.cartList = cartList;
    }

    public List<OrderItems> getOrderItemsPerCustomer() {
        return orderItemsPerCustomer;
    }

    public void setOrderItemsPerCustomer(List<OrderItems> orderItemsPerCustomer) {
        this.orderItemsPerCustomer = orderItemsPerCustomer;
    }

    public int getOption() {
        return option;
    }

    public void setOption(int option) {
        this.option = option;
    }   
   
    public void pickPaymentMethod(){
        FacesContext facesContext = FacesContext.getCurrentInstance();
        option = Integer.valueOf(facesContext.getExternalContext()
                .getRequestParameterMap().get("paymentMethod"));
    }
    
    //public String orderItemsPersist(){
    public void orderItemsPersist(){
        int usernmId = usernmController.getUsernmLogged().getId();
        cartList = cartFacade.populateCartWithId(usernmId);
        Usernm usernmLogged = usernmController.getUsernmLogged();
        
        // Create order first!!
        if(!cartList.isEmpty()){    
            Orders ordersRecord = new Orders();
            ordersRecord.setAffiliate("Marousi");
            ordersRecord.setClientIp("0.0.0.0");
            ordersRecord.setCountryCodeSession("GR");
            ordersRecord.setCustEmail(usernmLogged.getUserEmail());
            ordersRecord.setCustName(usernmLogged.getFirstName()+" "+usernmLogged.getLastName());
            ordersRecord.setCustPhone(usernmLogged.getMobile());
            ordersRecord.setDeltapayId(0);
            ordersRecord.setDiscountCoupon("");
            ordersRecord.setDiscountPercent(0);
            ordersRecord.setDiscountPrice(BigDecimal.ZERO);
            ordersRecord.setExported(0);
            ordersRecord.setOrderDate( Timestamp.from(LocalDateTime.now().toInstant(ZoneOffset.ofHours(3))));
            ordersRecord.setOrderState((short)1);            
            ordersRecord.setPaymentMethodId(option);
            ordersRecord.setRequestedInvoice(usernmId);
            ordersRecord.setShippingAddress(usernmLogged.getAddress());
            ordersRecord.setShippingCity(usernmLogged.getCity());
            ordersRecord.setShippingCost(BigDecimal.ZERO);
            ordersRecord.setShippingCostId(true);
            ordersRecord.setShippingCountry(usernmLogged.getCountry());
            ordersRecord.setShippingCountryCode("GR");
            ordersRecord.setShippingZip(usernmLogged.getPostalCode());
            ordersRecord.setTotal(new BigDecimal(itemsController.getProductTotal()));
            ordersRecord.setTotalFinal(new BigDecimal(itemsController.getProductTotal()));
            
            
            ordersRecord.setUserId(usernmId);            
            //ordersRecord.setPaymentOption(option == 0? "BANK": "CREDITCARD");
            
            //ordersRecord.setPaymentStatus("RECEIVED");                        
            ordersFacade.create(ordersRecord);
            
            long orderId = ordersRecord.getOrderid();
            
        
        
        for (Cart cartItem : cartList){
            Items it = itemsFacade.find(cartItem.getItemId());
            OrderItems orderItemsRecord = new OrderItems();
            orderItemsRecord.setItemCode(cartItem.getItemCode());
            orderItemsRecord.setItemColor(it.getItemColor());
            orderItemsRecord.setItemDiscount(0);            
            orderItemsRecord.setItemId(cartItem.getItemId());
            orderItemsRecord.setItemLongDescr(it.getItemLongDesc());
            orderItemsRecord.setItemPriceOld(0);
            orderItemsRecord.setItemPriceRetail(it.getItemRetailValue());
            orderItemsRecord.setItemPriceWholesale(it.getItemWholesalesValue() != null ? it.getItemWholesalesValue() : 0);
            orderItemsRecord.setItemSize(it.getItemSize());
            orderItemsRecord.setQuantity(cartItem.getQuantity());
            orderItemsRecord.setReturnsCount(0);
            orderItemsRecord.setUserId(usernmId);
            orderItemsRecord.setOrders(ordersRecord);
            ejbFacade.edit(orderItemsRecord); 
            
            // Update items_reviews table too !!
            
            ItemsReviews irRecord = new ItemsReviews();
            irRecord.setUserId(usernmId);
            irRecord.setItemCode(cartItem.getItemCode());
            irRecord.setUserName(usernmController.getUsernmLogged().getUserName());
            irRecord.setItemId(cartItem.getItemId());
            irRecord.setReviews(null);
            irRecord.setDated(null);
            irRecord.setOrderId(orderId);
            itemsReviewsFacade.edit(irRecord);
                    
            cartFacade.remove(cartItem);    //remove cartItem from Cart table            
        }
       }
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage("Your Order has been registered for Payment"));
        itemsController.getCartProducts().clear(); //empty the cart List in the session bean
       // return "/index.xhtml?faces-redirect=true";
    }
    
    public OrderItems prepareCreate() {
        selected = new OrderItems();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_orders").getString("OrderItemsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_orders").getString("OrderItemsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_orders").getString("OrderItemsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<OrderItems> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_orders").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_orders").getString("PersistenceErrorOccured"));
            }
        }
    }

    public OrderItems getOrderItems(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<OrderItems> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<OrderItems> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = OrderItems.class)
    public static class OrderItemsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            OrderItemsController controller = (OrderItemsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "orderItemsController");
            return controller.getOrderItems(getKey(value));
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
            if (object instanceof OrderItems) {
                OrderItems o = (OrderItems) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), OrderItems.class.getName()});
                return null;
            }
        }

    }

}
