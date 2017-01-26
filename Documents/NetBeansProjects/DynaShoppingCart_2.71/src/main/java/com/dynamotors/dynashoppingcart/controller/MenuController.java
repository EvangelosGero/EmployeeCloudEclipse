package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.Menu;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.MenuFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;


@Named("menuController")
@SessionScoped
public class MenuController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.MenuFacade ejbFacade;
    private List<Menu> items = null;
    private Menu selected;
    private MenuModel model1;
    
    @Inject
    UsernmController usernmController ;

    public MenuController() {
    }

    public UsernmController getUsernmController() {
        return usernmController;
    }

    public void setUsernmController(UsernmController usernmController) {
        this.usernmController = usernmController;
    }   

    public MenuFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(MenuFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public MenuModel getModel1() {
        return model1;
    }

    public void setModel1(MenuModel model1) {
        this.model1 = model1;
    }   
    
    public Menu getSelected() {
        return selected;
    }

    public void setSelected(Menu selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MenuFacade getFacade() {
        return ejbFacade;
    }
    
    @PostConstruct
    public void init(){
        model1 = new DefaultMenuModel();
        DefaultMenuItem home = new DefaultMenuItem("Home");
      //  home.setUrl("/index.xhtml");
       // home.setCommand("#{categMakeController.annulateCategs()}");
       // home.setProcess("@this");  
       //home.setHref("/index.xhtml");
        home.setCommand("#{usernmController.annulateFromHome()}");
        home.setImmediate(true);
        home.setAjax(true);        
       // home.setUpdate(":CatgMasListForm3");
        model1.addElement(home);
        
        DefaultSubMenu subMenu = null;
        String menuOldName = null;
        String role ="%" + "c" + "%"; 
        if (usernmController.getUsernmLogged() != null)role = "%" + usernmController.getUsernmLogged().getRoleuser().trim() + "%";
                
        for (Menu r : ejbFacade.findMenuFromRole(role)){
            if (r!=null){
                if (!r.getMenuShortName().equals(menuOldName)){
                    subMenu = new DefaultSubMenu();
                    subMenu.setIcon(null);
                    subMenu.setLabel(r.getMenuShortName());
                    model1.addElement(subMenu);
                }
            menuOldName = r.getMenuShortName();
            
            DefaultMenuItem item = new DefaultMenuItem();
            item.setValue(r.getMenuLongName());
            item.setUrl(r.getMenuRunOption());
            subMenu.addElement(item);
            }        
        }
        //add quit option to menu
        DefaultMenuItem quit = new DefaultMenuItem("Quit");
        quit.setUrl("http://www.google.com");
        model1.addElement(quit);
    }

    public Menu prepareCreate() {
        selected = new Menu();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MenuCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MenuUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MenuDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Menu> getItems() {
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

    public Menu getMenu(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Menu> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Menu> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Menu.class)
    public static class MenuControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MenuController controller = (MenuController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "menuController");
            return controller.getMenu(getKey(value));
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
            if (object instanceof Menu) {
                Menu o = (Menu) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Menu.class.getName()});
                return null;
            }
        }

    }

}
