package Controllers;

import Entities.EmplAdmins;
import Controllers.util.JsfUtil;
import Controllers.util.JsfUtil.PersistAction;
import EJBs.EmplAdminsFacade;
import java.io.IOException;

import java.io.Serializable;
import java.sql.SQLException;
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
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

@Named("emplAdminsController")
@SessionScoped
public class EmplAdminsController implements Serializable {

    @EJB
    private EJBs.EmplAdminsFacade ejbFacade;
    private List<EmplAdmins> items = null;
    private EmplAdmins selected;   
    private EmplAdmins emplAdmin, emplAdminLogged;   
    private EmplAdmins newEmplAdmin = new EmplAdmins();
    private String emplAdminemail;    
    private String password; 
    private String newPassword;
    private boolean loggedIn = false;

    public EmplAdminsController() {
    }

    public EmplAdmins getSelected() {
        return selected;
    }

    public void setSelected(EmplAdmins selected) {
        this.selected = selected;
    }

    public EmplAdmins getEmplAdmin() {
        return emplAdmin;
    }

    public void setEmplAdmin(EmplAdmins emplAdmin) {
        this.emplAdmin = emplAdmin;
    }

    public EmplAdmins getEmplAdminLogged() {
        return emplAdminLogged;
    }

    public void setEmplAdminLogged(EmplAdmins emplAdminLogged) {
        this.emplAdminLogged = emplAdminLogged;
    }

    public EmplAdmins getNewEmplAdmin() {
        return newEmplAdmin;
    }

    public void setNewEmplAdmin(EmplAdmins newEmplAdmin) {
        this.newEmplAdmin = newEmplAdmin;
    }

    public String getEmplAdminemail() {
        return emplAdminemail;
    }

    public void setEmplAdminemail(String emplAdminemail) {
        this.emplAdminemail = emplAdminemail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EmplAdminsFacade getFacade() {
        return ejbFacade;
    }
    
    public String loginMeIn() throws IOException  {
        FacesMessage msg = null;
        try{
        this.setEmplAdminLogged(ejbFacade.validateUser(emplAdminemail, password)); 
        }
        catch (EJBException ejbExc){
            emplAdminLogged = null;
        }
        this.loggedIn = emplAdminLogged != null;
        if (this.loggedIn){
            return "/index.xhtml?faces-redirect=true";            
        }else{            
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Login Error", "InvalidCredentials");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            //return "/views/items/welcome.xhtml";
            return null;
        }     
        
    }
    
    public String loginMeOut() throws ServletException {        
        this.loggedIn = false;
        emplAdminLogged = null;
        Object request = FacesContext.getCurrentInstance().getExternalContext().getRequest(); 
    //    newUsernm = null;
        if (request instanceof HttpServletRequest) {
           HttpServletRequest rq = (HttpServletRequest) request;
            rq.logout();
        }
        //menuController.init();
        return "/index.xhtml?faces-redirect=true";
    }
    
    public void changepassword() throws SQLException {
	
        boolean confirm = false;
        confirm = ejbFacade.changepassword(emplAdminemail, password, newPassword);

        if (confirm) {
            FacesMessage msg = new FacesMessage("change password is successful");
            FacesContext.getCurrentInstance().addMessage(null, msg);
	} else {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
		"Change of password is unsuccessful. Please try with valid data","");
		
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public EmplAdmins prepareCreate() {
        selected = new EmplAdmins();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("EmplAdminsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("EmplAdminsUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("EmplAdminsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<EmplAdmins> getItems() {
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

    public EmplAdmins getEmplAdmins(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<EmplAdmins> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<EmplAdmins> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = EmplAdmins.class)
    public static class EmplAdminsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmplAdminsController controller = (EmplAdminsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "emplAdminsController");
            return controller.getEmplAdmins(getKey(value));
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
            if (object instanceof EmplAdmins) {
                EmplAdmins o = (EmplAdmins) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), EmplAdmins.class.getName()});
                return null;
            }
        }

    }

}
