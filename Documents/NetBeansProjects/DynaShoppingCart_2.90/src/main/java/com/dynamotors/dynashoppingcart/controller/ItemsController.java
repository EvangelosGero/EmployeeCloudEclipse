package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.Items;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.CartFacade;
import com.dynamotors.dynashoppingcart.ejbs.ItemsFacade;
import com.dynamotors.dynashoppingcart.entities.Cart;
import com.dynamotors.dynashoppingcart.entities.CategMake;
import com.dynamotors.dynashoppingcart.entities.CatgDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Named;
import javax.inject.Inject;
import org.primefaces.event.DragDropEvent;
import org.primefaces.event.FlowEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

@Named("itemsController")
@SessionScoped
public class ItemsController  implements Serializable{

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.ItemsFacade ejbFacade;
    //private List<Items> items = null;
    private LazyDataModel<Items> model = new SearchLazyLoader();
    private List<Items> itemsOfSeller = null;
    private Items selected;
    private String filterItemCode = "";
    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.CartFacade cartFacade;
    @Inject
    private UsernmController usernmController;
    @Inject
    private CatgDetailsController catgDetailsController;
    @Inject
    private ItemsDeliveryAvailablePinController itemsDeliveryAvailablePinController;
    @Inject
    private ItemsPaymentOptionsController itemsPaymentOptionsController;
    @Inject
    private CategMakeController categMakeController;
    @Inject
    CategModelController categModelController;
    @Inject
    Categ1Controller categ1Controller;
    @Inject
    Categ2Controller categ2Controller;
    @Inject
    Categ3Controller categ3Controller;
    private List<Items> cartProducts = new ArrayList<>();    
    private Integer selectedMakeId = -1;
    private double productTotal = 0;
    private CategMake[] selectionMakes;
    private String anazitisiString; 
    private Items cartSelected;
    
   
    
   
    public ItemsController() {
    //     this.model = new SearchLazyLoader();
     /*    {   private static final int serialVersionUID = 1L;
            @Override
            public List<Items> load(int first, int pageSize, String sortField, SortOrder sortorder,
                       Map<String, Object> filters){                       
                       List<Items> result =  ejbFacade.getAll(first, pageSize, null, null, null); 
                       model.setRowCount(ejbFacade.getAll(-1, -1, null, null, null).size());                      
                       return result;
                       
                   }         
        }; */  
    }
    @SuppressWarnings("serial")
    final class SearchLazyLoader extends LazyDataModel<Items> implements Serializable{
        private static final long serialVersionUID = 1L;
            @Override
            public List<Items> load(int first, int pageSize, String sortField, SortOrder sortorder,
                       Map<String, Object> filters){
                       Map<String, Object> filter = new HashMap<>();                       
                       filter.put("itemCode", filterItemCode);
                       filter.put("categMake", categMakeController.getSelected() == null ? "" 
                               : categMakeController.getSelected());
                       filter.put("categModel", categModelController.getSelected() == null ? "" 
                               : categModelController.getSelected());
                       filter.put("categ1", categ1Controller.getSelected() == null ? "" 
                               : categ1Controller.getSelected().getC1Id());
                       filter.put("categ2", categ2Controller.getSelected() == null ? "" 
                               : categ2Controller.getSelected().getC2Id());
                       filter.put("categ3", categ3Controller.getSelected() == null ? "" 
                               : categ3Controller.getSelected().getC3Id());
                       List<Items> result =  ejbFacade.getAll(first, pageSize, null, null, filter); 
                       model.setRowCount(ejbFacade.getAll(-1, -1, null, null, filter).size()); 
                       filterItemCode = ""; //Reset manual search (itemCode)
                       return result;
                       
            }
        
            @Override
            public Object getRowKey(Items items) {
                return items != null ? Integer.toString(items.getItemId()).trim() : null;
            }
            
            @Override
            public Items getRowData(String rowKey) {
                List<Items> list = (List<Items>) getWrappedData();        
                for (Items items : list) {
                    if (Integer.toString(items.getItemId()).trim().equals(rowKey.trim())) {
                        return items;
                    }
                }
            return null;
            }
    }
    
    public CartFacade getCartFacade() {
        return cartFacade;
    }

    public void setCartFacade(CartFacade cartFacade) {
        this.cartFacade = cartFacade;
    }
    public CatgDetailsController getCatgDetailsController() {
        return catgDetailsController;
    }

    public void setCatgDetailsController(CatgDetailsController catgDetailsController) {
        this.catgDetailsController = catgDetailsController;
    }
    
    public Items getSelected() {
        return selected;
    }

    public ItemsDeliveryAvailablePinController getItemsDeliveryAvailablePinController() {
        return itemsDeliveryAvailablePinController;
    }

    public void setItemsDeliveryAvailablePinController(ItemsDeliveryAvailablePinController itemsDeliveryAvailablePinController) {
        this.itemsDeliveryAvailablePinController = itemsDeliveryAvailablePinController;
    }    

    public CategMakeController getCategMakeController() {
        return categMakeController;
    }

    public void setCategMakeController(CategMakeController categMakeController) {
        this.categMakeController = categMakeController;
    }

    public CategModelController getCategModelController() {
        return categModelController;
    }

    public void setCategModelController(CategModelController categModelController) {
        this.categModelController = categModelController;
    }

    public Categ1Controller getCateg1Controller() {
        return categ1Controller;
    }

    public void setCateg1Controller(Categ1Controller categ1Controller) {
        this.categ1Controller = categ1Controller;
    }

    public Categ2Controller getCateg2Controller() {
        return categ2Controller;
    }

    public void setCateg2Controller(Categ2Controller categ2Controller) {
        this.categ2Controller = categ2Controller;
    }

    public Categ3Controller getCateg3Controller() {
        return categ3Controller;
    }

    public void setCateg3Controller(Categ3Controller categ3Controller) {
        this.categ3Controller = categ3Controller;
    }

    public void resetFilters(){     //used for Search with String for Itemcode
        categMakeController.setSelected(null);
        categModelController.setSelected(null);
        categModelController.setMatchedItems(null);
        categ1Controller.setSelected(null);
        categ2Controller.setSelected(null);
        categ2Controller.setMatchedItems(null);
        categ3Controller.setSelected(null);
        categ3Controller.setMatchedItems(null);
    }
    
    public String getAnazitisiString() {
        StringBuilder sb = new StringBuilder("");
        if(categMakeController.getSelected() != null) {
            sb = new StringBuilder(categMakeController.getSelected().getMkDescription());
            if (categModelController.getSelected() != null){
                sb.append("|").append(categModelController.getSelected().getMlModelName()); 
            } 
        }
        if (categ1Controller.getSelected() != null){
            sb.append("|").append(categ1Controller.getSelected().getC1Descr());
            if (categ2Controller.getSelected() != null){
                 sb.append("|").append(categ2Controller.getSelected().getC2Descr());
                 if (categ3Controller.getSelected() != null){
                    sb.append("|").append(categ3Controller.getSelected().getC3Descr());
                }
            }
        }
           
       
        return sb.toString();
    }

    public void setAnazitisiString(String anazitisiString) {
        this.anazitisiString = anazitisiString;
    }    
    
    public void setSelected(Items selected) {
        this.selected = selected;
   //     if(selected != null && selected.getItemCode() != null && !selected.getItemCode().equals(""))catgDetailsController.produceDiscountsPerItem(selected.getCatgDetailsId());
    //    if(selected != null && selected.getItemCode() != null && !selected.getItemCode().equals(""))itemsDeliveryAvailablePinController.producePin(selected.getItemCode());
    //    if(selected != null && selected.getItemCode() != null && !selected.getItemCode().equals(""))itemsPaymentOptionsController.produceOptions(selected.getItemCode());
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ItemsFacade getFacade() {
        return ejbFacade;        
    }

    public ItemsFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(ItemsFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public UsernmController getUsernmController() {
        return usernmController;
    }

    public void setUsernmController(UsernmController usernmController) {
        this.usernmController = usernmController;
    }   
            
    public List<Items> getCartProducts() {
        return cartProducts;
    }

    public void setCartProducts(List<Items> cartProducts) {
        this.cartProducts = cartProducts;
    }

    public CategMake[] getSelectionMakes() {
        return selectionMakes;
    }

    public void setSelectionMakes(CategMake[] selectionMakes) {
        this.selectionMakes = selectionMakes;
    }
   
    public double getProductTotal() {
        return productTotal;
    }

    public void setProductTotal(double productTotal) {
        this.productTotal = productTotal;                
    }

    public List<Items> getItemsOfSeller() {
        if(itemsOfSeller == null){
          produceItemsOfSeller();  
        }
        return itemsOfSeller;
    }

    public void setItemsOfSeller(List<Items> itemsOfSeller) {
        this.itemsOfSeller = itemsOfSeller;
    }

    public String getFilterItemCode() {
        return filterItemCode;
    }

    public void setFilterItemCode(String filterItemCode) {
        this.filterItemCode = filterItemCode;
    }

    public ItemsPaymentOptionsController getItemsPaymentOptionsController() {
        return itemsPaymentOptionsController;
    }

    public void setItemsPaymentOptionsController(ItemsPaymentOptionsController itemsPaymentOptionsController) {
        this.itemsPaymentOptionsController = itemsPaymentOptionsController;
    }

    public Integer getSelectedMakeId() {
        return selectedMakeId;
    }

    public void setSelectedMakeId(Integer selectedMakeId) {
        this.selectedMakeId = selectedMakeId;
    }    
   
    public LazyDataModel<Items> getModel() {         
       //this.model = new SearchLazyLoader();           
       return model;
    }
    
    public void setModel(LazyDataModel<Items> model) {
        this.model = model;
    }

    public Items getCartSelected() {
        return cartSelected;
    }

    public void setCartSelected(Items cartSelected) {
        this.cartSelected = cartSelected;
    }
    
    public void filterWord(){
        //this.model = (LazyDataModel<Items>) ejbFacade.filterWord(this.filterItemCode); 
        this.model = new SearchLazyLoader();       
        //this.setFilterItemCode("");
    }
    
    public void produceItemsOfSeller(){
        int sellerId = usernmController.getUsernmLogged().getId();
        this.itemsOfSeller = ejbFacade.produceSellerList(sellerId);
    }
    
    public void produceCartItems(){ 
        this.setSelectedMakeId(-1);
        if(usernmController.isLoggedIn()){
            int id1 = usernmController.getUsernmLogged().getId();            
            List<Cart> initCart = cartFacade.populateCartWithId(id1);
            cartProducts.clear();
            this.productTotal = 0;
            if(initCart != null)
                for(Cart c : initCart){
                    Items cItem = ejbFacade.find(c.getItemId());
                    if(cItem != null){      //Item might have been deleted
                        cItem.setQuantity(c.getQuantity());
                        //find the discount in €
                        double discount = 0;
                        if(cItem.getCatgDetailsId() != null){
                          CatgDetails discountCatg =  catgDetailsController.getItemsAvailableSelectMany().stream()
                                .filter(s -> Objects.equals(s.getId(), cItem.getCatgDetailsId())).findFirst().get();
                          if (discountCatg.getDiscountsValue() != null)discount = discountCatg.getDiscountsValue();
                          if (discountCatg.getDiscountsPers() != null)
                              discount = discount + discountCatg.getDiscountsPers()*cItem.getItemRetailValue();
                        }
                        cItem.setDiscountPrice(discount);
                        cItem.setPriceFinal(cItem.getItemRetailValue() - discount);
                        addToCart(cItem);
                    }
                }
            }        
    }
    
    public void populateProductForCategory(int i) {    
        this.setSelectedMakeId(i);
        
   /*     this.model = new LazyDataModel<Items>(){
            
            private static final int serialVersionUID = 1L;
            @Override
            public List<Items> load(int first, int pageSize, String sortField, SortOrder sortorder,
                Map<String, Object> filters){                
                Map<String, Object> filter = new HashMap<>();
                filter.put("itemCategoryCode", c.trim().equals("All")?"":c.trim());                
                List<Items> result =  ejbFacade.getAll(first, pageSize, null, null, filter);
               //     .stream()
               //    .filter(p -> p.getItemCategoryCode().matches(c.trim().equals("All")?".+":c))
               //     .sorted(Comparator.comparing(Items :: getItemShortDesc).reversed())                
               //     .collect(Collectors.toList());                       
                model.setRowCount(ejbFacade.getAll(-1, -1, null, null, filter).size());
                return result;
            }
        }; */     
    }
   
    public void checkForNulls(Items p){
        if(p.getQuantity() == null || 
                (p.getQuantity() != null && p.getQuantity() == 0))p.setQuantity(1);
    }
    
    public void addToCart(Items p) {        
        addItemNow(p);
    }

    public void handleSpinner(ValueChangeEvent event){        
      //  if(event != null){
      //      item.setQuantity((Integer)event.getNewValue());
      //  System.out.println(event.getNewValue());
      //  }        
    }
    
    public void onProdDrop(DragDropEvent ddEvent) {
        Items p = ((Items) ddEvent.getData());        
        checkForNulls(p);
        addItemNow(p);
        
    }

    private void addItemNow(Items p) {       
        Items cp = ejbFacade.find(p.getItemId());     //populate the new object, the easy way
        cp.setQuantity(p.getQuantity());     //set explicitly the quantity because it is a transient field          
        cartProducts.add(cp);
        p.setQuantity(null);        //Neutralize again the Selection Area        
        productTotal += (!usernmController.isLoggedIn() ? cp.getItemRetailValue() : 
                usernmController.getUsernmLogged().getRoleuser().trim().equals("c") ? 
                cp.getItemRetailValue(): cp.getItemWholesalesValue())*cp.getQuantity();     
    } 
    
    public void removeItemNow(int index ) {
       Items p = cartProducts.remove(index);    //use the Droptable rowIndexVar
       productTotal -= (!usernmController.isLoggedIn() ? p.getItemRetailValue() : 
               usernmController.getUsernmLogged().getRoleuser().trim().equals("c") ? 
                p.getItemRetailValue(): p.getItemWholesalesValue())*p.getQuantity();   
    } 
    
    public String getStringTotal() {
        return String.format("%.2f", productTotal);
    }
    
    private boolean skip = false;

    public boolean isSkip() {
        return skip;
    }

    public void setSkip(boolean skip) {
        this.skip = skip;
    }

    public String onFlowProcess(FlowEvent event) {       

        if (skip) {
            skip = false;   //reset in case user goes back
            return "confirm";
        } else {
            return event.getNewStep();
        }
    }
   
    public void prepareEdit(){
        categModelController.startEdit();
        categ2Controller.startEdit();
        categ3Controller.startEdit();
    }
    
    public Items prepareCreate() {        
        selected = new Items();
        selected.setCategMakesList(new ArrayList<>());
        categModelController.startCreate();
        categ2Controller.startCreate();
        categ3Controller.startCreate();
        initializeEmbeddableKey();
        return selected;
    }
    
    public void create() {       
        
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ItemsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            this.model = new SearchLazyLoader();    // Invalidate list of items to trigger re-query.
            selected = null; // Remove selection
 //           this.itemsOfSeller.clear();
 //           produceItemsOfSeller();
        }
    }

    public void update() {   
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ItemsUpdated"));
        selected = null;
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ItemsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            //model.setRowCount(model.getRowCount() - 1);            
            this.model = new SearchLazyLoader();    // Invalidate list of items to trigger re-query. 
    //        this.itemsOfSeller.clear();
     //       produceItemsOfSeller();
            selected = null; // Remove selection
            
         //   RequestContext.getCurrentInstance()
         //           .execute("PF('dataTableWV').paginator.setPage(PF('dataTableWV').paginator.cfg.pageCount - 1);");
        }
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

    public Items getItems(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Items> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Items> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Items.class)
    public static class ItemsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemsController controller = (ItemsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemsController");
            return controller.getItems(getKey(value));
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
            if (object instanceof Items) {
                Items o = (Items) object;
                return getStringKey(o.getItemId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Items.class.getName()});
                return null;
            }
        }

    }

}
