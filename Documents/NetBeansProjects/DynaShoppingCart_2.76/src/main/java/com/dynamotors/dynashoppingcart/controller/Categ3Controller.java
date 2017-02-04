package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.Categ3;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.Categ3Facade;
import com.dynamotors.dynashoppingcart.entities.Categ2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

@Named("categ3Controller")
@SessionScoped
public class Categ3Controller implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.Categ3Facade ejbFacade;
    @Inject
    ItemsController itemsController;
    private List<Categ3> items = null;
    private Categ3 selected;
    private List<Categ3> matchedItems = null;
    private List<Categ3> matchedItemsForEdit;    

    
    
    public Categ3Controller() {
    }

    public void startCreate(){
      if(matchedItems != null)matchedItems = null;  
    }
    
    public void startEdit(){
        if(matchedItems != null){matchedItems.clear();
            }else {matchedItems = new ArrayList<>();}
        if (itemsController.getSelected() != null){ 
            matchedItems = itemsController
                    .getSelected().getItemCateg2Id() != null? ejbFacade.findFromParentId(itemsController
                    .getSelected().getItemCateg2Id()) : null;               
            } else       
                matchedItems = null;        
    }
    
    public Categ3 getSelected() {
        return selected;
    }

    public void setSelected(Categ3 selected) {
        this.selected = selected;
    }    

    public List<Categ3> getMatchedItems() {
        return matchedItems;
    }

    public ItemsController getItemsController() {
        return itemsController;
    }

    public void setItemsController(ItemsController itemsController) {
        this.itemsController = itemsController;
    }

    public void setMatchedItems(List<Categ3> matchedItems) {
        this.matchedItems = matchedItems;
    }

    public List<Categ3> getMatchedItemsForEdit() {       
        if (matchedItemsForEdit == null){
            matchedItemsForEdit = itemsController
                    .getSelected().getItemCateg2Id() != null? ejbFacade.findFromParentId(itemsController
                    .getSelected().getItemCateg2Id()) : ejbFacade.findAll();
        }
        return matchedItemsForEdit;
    
    }

    public Categ3Facade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(Categ3Facade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public void setMatchedItemsForEdit(List<Categ3> matchedItemsForEdit) {
        this.matchedItemsForEdit = matchedItemsForEdit;
    }
    
    
    
    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private Categ3Facade getFacade() {
        return ejbFacade;
    }

    public Categ3 prepareCreate() {
        selected = new Categ3();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_Categ").getString("Categ3Created"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_Categ").getString("Categ3Updated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_Categ").getString("Categ3Deleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Categ3> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public void handleMakeChange (ValueChangeEvent event){
        this.setSelected(null);
        Categ2 categ2 = (Categ2)event.getNewValue();
        if(categ2 != null)
            matchedItems = ejbFacade.findFromParentId(categ2.getC2Id());
        else
            matchedItems = this.items;
    }
    
    public void handleMakeChangeWithId (ValueChangeEvent event){
        this.setSelected(null);
        Integer idCateg2 = (Integer)event.getNewValue();
        if(idCateg2 != null)
            matchedItems = ejbFacade.findFromParentId(idCateg2);
        else
            matchedItems = this.items;
    }
    
    public void handleMakeChangeWithIdForEdit (ValueChangeEvent event){
        this.setSelected(null);
        Integer idCateg2 = (Integer)event.getNewValue();
        if(idCateg2 != null)
            matchedItemsForEdit = ejbFacade.findFromParentId(idCateg2);
        else
            matchedItemsForEdit = this.items;
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_Categ").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_Categ").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Categ3 getCateg3(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Categ3> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Categ3> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Categ3.class)
    public static class Categ3ControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            Categ3Controller controller = (Categ3Controller) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "categ3Controller");
            return controller.getCateg3(getKey(value));
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
            if (object instanceof Categ3) {
                Categ3 o = (Categ3) object;
                return getStringKey(o.getC3Id());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Categ3.class.getName()});
                return null;
            }
        }

    }

}
