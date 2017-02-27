package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.Usernm;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.UsernmFacade;
import com.dynamotors.dynashoppingcart.entities.Items;
import java.io.IOException;

import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
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
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Named("usernmController")
@SessionScoped
public class UsernmController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.UsernmFacade ejbFacade;
    private List<Usernm> items = null;
    private Usernm selected;
    private Usernm usernm, usernmLogged;   
    private Usernm newUsernm = new Usernm();
    private String useremail;    
    private String password; 
    private String newPassword;
    private boolean loggedIn = false;  
    
    @Inject
    private MenuController menuController;
    @Inject
    private ItemsController itemsController;
    @Inject
    CategMakeController categMakeController;
    @Inject
    CategModelController categModelController;
    @Inject
    Categ1Controller categ1Controller;
    @Inject
    Categ2Controller categ2Controller;
    @Inject
    Categ3Controller categ3Controller;
    
    public MenuController getMenuController() {
        return menuController;
    }

    public ItemsController getItemsController() {
        return itemsController;
    }

    public void setItemsController(ItemsController itemsController) {
        this.itemsController = itemsController;
    }

    public void setMenuController(MenuController menuController) {
        this.menuController = menuController;
    }  

    public UsernmFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(UsernmFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public Usernm getUsernmLogged() {
        return usernmLogged;
    }

    public void setUsernmLogged(Usernm usernmLogged) {
        this.usernmLogged = usernmLogged;
    }  

    public Usernm getUsernm() {
        return usernm;
    }

    public void setUsernm(Usernm usernm) {
        this.usernm = usernm;
    }

    public Usernm getNewUsernm() {
        return newUsernm;
    }

    public void setNewUsernm(Usernm newUsernm) {
        this.newUsernm = newUsernm;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    

    public UsernmController() {
    }

    public Usernm getSelected() {
        return selected;
    }

    public void setSelected(Usernm selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private UsernmFacade getFacade() {
        return ejbFacade;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest();

    public String loginMeIn() throws IOException  {
        FacesMessage msg = null;
        try{
        this.setUsernmLogged(ejbFacade.validateUser(useremail, password)); 
        }
        catch (EJBException ejbExc){
            usernmLogged = null;
        }
        this.loggedIn = usernmLogged != null;
//      FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("usernmLoggedInId", usernmLogged.getId());
        //if (request instanceof HttpServletRequest) {
          //  HttpServletRequest rq = (HttpServletRequest) request;
//            rq.setAttribute("useremail", reqem);
 //       }
        menuController.init();
        if (this.loggedIn){
      
            if(usernmLogged.getRoleuser().contains("a"))
                return "/views/items/List.xhtml?faces-redirect=true";
            else if(usernmLogged.getRoleuser().contains("s")){
                itemsController.produceItemsOfSeller();
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
                return null;
            }
            else {
                if(itemsController.getCartProducts() == null || itemsController.getCartProducts().isEmpty())
                    itemsController.produceCartItems();
                else{  //Here we cover the case where the user logs in AFTER he has added to his cart some items
                    List<Items> initialCartProducts = new ArrayList<>();
                    double initialProductTotal = itemsController.getProductTotal();
                    initialCartProducts.addAll(itemsController.getCartProducts());
                    itemsController.produceCartItems();
                    itemsController.getCartProducts().addAll(initialCartProducts);
                    itemsController.setProductTotal(itemsController.getProductTotal() + initialProductTotal);
                }
                ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
                ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
                return null;
            }
        }else{            
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "InvalidCredentials");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            //return "/views/items/welcome.xhtml";
            return null;
        }     
        
    }
    
     public String annulateFromHome(){
        categMakeController.setSelected(null);
        categModelController.setSelected(null);
        categModelController.setMatchedItems(null);
        categ1Controller.setSelected(null);
        categ2Controller.setSelected(null);
        categ2Controller.setMatchedItems(null);
        categ3Controller.setSelected(null);
        categ3Controller.setMatchedItems(null);
     //   try {
     //       FacesContext.getCurrentInstance().getExternalContext().dispatch("/index.xhtml");            
     //   } catch (IOException ex) {
      //      Logger.getLogger(UsernmController.class.getName()).log(Level.SEVERE, null, ex);
      //  }
        //  return "/index.xhtml?faces=redirect=true";
       // if (this.loggedIn && usernmLogged.getRoleuser().contains("c"))
       //     return null;
        return "backHome";
    }

     public String navigateHelper(){
        if (this.loggedIn){      
            if(usernmLogged.getRoleuser().contains("a"))
                return "/views/items/List.xhtml";
            else if(usernmLogged.getRoleuser().contains("s")){
                itemsController.produceItemsOfSeller();
                return "/views/seller/SellerList.xhtml";
            }
            else {
                if(itemsController.getCartProducts() == null || itemsController.getCartProducts().isEmpty())
                    itemsController.produceCartItems();
                return "/index.xhtml";
            }
        }else{          
            return "/index.xhtml";            
        } 
     }
     
    public String loginMeOut() throws ServletException {        
        this.loggedIn = false;
        usernmLogged = null;
        itemsController.getCartProducts().clear();
        itemsController.setProductTotal(0);
        itemsController.setSelected(null);
        categMakeController.setSelected(null);
        categModelController.setSelected(null);
        categModelController.setMatchedItems(null);
        categ1Controller.setSelected(null);
        categ2Controller.setSelected(null);
        categ2Controller.setMatchedItems(null);
        categ3Controller.setSelected(null);
        categ3Controller.setMatchedItems(null);
        
        
    //    newUsernm = null;
        if (request instanceof HttpServletRequest) {
           HttpServletRequest rq = (HttpServletRequest) request;
            rq.logout();
        }
        menuController.init();
        return "/index.xhtml?faces-redirect=true";
    }
    
    public void saveUsernmInfo(){
        FacesMessage msg1 = null;
        if (this.newUsernm.getId() == null){
            if (this.newUsernm.getUserEmail() != null){
                this.newUsernm.setUsertype("customer");
                this.newUsernm.setRoleuser("c");
                LocalDate createDate = LocalDate.now();
                this.newUsernm.setCreateDate(Date.valueOf(createDate));
                ejbFacade.create(this.newUsernm);
                msg1 = new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Δημιουργήθηκε νέος χρήστης στην Database","Εγγραφή ΟΚ");  
                                       
            }
        }else{
            if (ejbFacade.find(this.newUsernm.getId()) == null){
                this.newUsernm.setId(null);
                this.newUsernm.setUsertype("customer");
                this.newUsernm.setRoleuser("c");
                LocalDate createDate = LocalDate.now();
                this.newUsernm.setCreateDate(Date.valueOf(createDate));
                ejbFacade.create(this.newUsernm);
                msg1 = new FacesMessage(FacesMessage.SEVERITY_WARN, 
                        "Δημιουργήθηκε νέος χρήστης στην Database", "Εγγραφή ΟΚ");
            }
            else
                ejbFacade.edit(this.newUsernm);
                msg1 = new FacesMessage(FacesMessage.SEVERITY_WARN, 
                        "Ενημερώθηκε ο χρήστης "+usernm.getId().toString(), "Εγγραφή ΟΚ");
                if (this.loggedIn)this.setUsernmLogged(this.newUsernm);
        }       
        prepareAddNewUser();
        FacesContext.getCurrentInstance().addMessage(null, msg1);
    }

    public void changeUsernmLogged(){
        prepareAddNewUser();
        this.setNewUsernm(usernmLogged);        
    }
    
    public void prepareAddNewUser(){
        newUsernm = new Usernm();
        items = null; //Trigger findAll;
    }
    
    public void updateUsernmLogged (){
       ejbFacade.edit(this.usernmLogged);
    }
    
    public void changepassword() throws SQLException {
	
        boolean confirm = false;
        confirm = ejbFacade.changepassword(useremail, password, newPassword);

        if (confirm) {
            FacesMessage msg = new FacesMessage("change password is successful");
            FacesContext.getCurrentInstance().addMessage(null, msg);
	} else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
		"Change of password is unsuccessful. Please try with valid data","");
		
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }
    public Usernm prepareCreate() {
        selected = new Usernm();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("UsernmCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("UsernmUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("UsernmDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Usernm> getItems() {
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

    public Usernm getUsernm(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Usernm> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Usernm> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Usernm.class)
    public static class UsernmControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            UsernmController controller = (UsernmController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "usernmController");
            return controller.getUsernm(getKey(value));
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
            if (object instanceof Usernm) {
                Usernm o = (Usernm) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Usernm.class.getName()});
                return null;
            }
        }

    }

}
