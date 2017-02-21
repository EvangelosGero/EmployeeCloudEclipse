package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.Categ2;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.Categ2Facade;
import com.dynamotors.dynashoppingcart.entities.Categ1;
import com.dynamotors.dynashoppingcart.entities.CategMake;

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

@Named("categ2Controller")
@SessionScoped
public class Categ2Controller implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.Categ2Facade ejbFacade;
    @Inject
    private Categ3Controller categ3Controller;
    @Inject
    private ItemsController itemsController;
    private List<Categ2> items = null;
    private Categ2 selected;
    private List<Categ2> matchedItems = null;
    private List<Categ2> matchedItemsForEdit = null; 
    private Integer parId = null;

    public Categ2Controller() {
    }

    public void startCreate(){
      if(matchedItems != null)matchedItems = null;  
    }
    
    public void startEdit(){
        if(matchedItems != null){matchedItems.clear();
            }else {matchedItems = new ArrayList<>();}
        if (itemsController.getSelected() != null){ 
            matchedItems = itemsController
                    .getSelected().getItemCateg1Id() != null? ejbFacade.findFromParentId(itemsController
                    .getSelected().getItemCateg1Id()) : null;               
            } else       
                matchedItems = null;        
    }
    
    public Categ2 getSelected() {
        return selected;
    }

    public void setSelected(Categ2 selected) {
        this.selected = selected;
    }

    public Categ3Controller getCateg3Controller() {
        return categ3Controller;
    }

    public void setCateg3Controller(Categ3Controller categ3Controller) {
        this.categ3Controller = categ3Controller;
    }     

    public Integer getParId() {
        return parId;
    }

    public void setParId(Integer parId) {
        this.parId = parId;
    }

    public ItemsController getItemsController() {
        return itemsController;
    }

    public void setItemsController(ItemsController itemsController) {
        this.itemsController = itemsController;
    }
   
    public List<Categ2> getMatchedItems() {
        return matchedItems;
    }

    public void setMatchedItems(List<Categ2> matchedItems) {
        this.matchedItems = matchedItems;
    }
    
    public List<Categ2> getMatchedItemsForEdit() {
        if (matchedItemsForEdit == null && itemsController.getSelected() != null){                   
            matchedItemsForEdit = itemsController
                    .getSelected().getItemCateg1Id() != null? ejbFacade.findFromParentId(itemsController
                    .getSelected().getItemCateg1Id()) : ejbFacade.findAll();
        }
        return matchedItemsForEdit;
    }

    public void setMatchedItemsForEdit(List<Categ2> matchedItemsForEdit) {
        this.matchedItemsForEdit = matchedItemsForEdit;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private Categ2Facade getFacade() {
        return ejbFacade;
    }

    public Categ2 prepareCreate() {
        selected = new Categ2();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_Categ").getString("Categ2Created"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_Categ").getString("Categ2Updated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_Categ").getString("Categ2Deleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Categ2> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public void handleMakeChange (ValueChangeEvent event){
        Categ1 categ1 = (Categ1)event.getNewValue();
        this.setSelected(null);
        if(categ1 != null){   
            matchedItems = ejbFacade.findFromParentId(categ1.getC1Id());        
            categ3Controller.setMatchedItems(null);
        }else{
            this.setMatchedItems(this.items);
            categ3Controller.setMatchedItems(null);
        }
    }
    
    public void handleMakeChangeWithId (ValueChangeEvent event){        
        itemsController.getSelected().setItemCateg2Id(null);
        itemsController.getSelected().setItemCateg3Id(null);
        this.setSelected(null);
        Integer idCateg1 = (Integer)event.getNewValue();
        if(idCateg1 != null){
            matchedItems = ejbFacade.findFromParentId(idCateg1);
            categ3Controller.setMatchedItems(null);
        }else
            this.setMatchedItems(this.items);
            categ3Controller.setMatchedItems(null);
    }
    
    public void handleMakeChangeWithIdForEdit (ValueChangeEvent event){        
        this.setSelected(null);
        Integer idCateg1 = (Integer)event.getNewValue();
        if(idCateg1 != null){
            matchedItemsForEdit = ejbFacade.findFromParentId(idCateg1);
            categ3Controller.setMatchedItemsForEdit(null);
            //this.parId = this.matchedItemsForEdit.isEmpty()? null: this.matchedItemsForEdit.get(0).getC2Id() ;
            //categ3Controller.setMatchedItemsForEdit(parId != null? categ3Controller.getEjbFacade()
            //        .findFromParentId(parId): null);
        }else{
            this.setMatchedItemsForEdit(this.items);
            categ3Controller.setMatchedItemsForEdit(null);
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_Categ").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_Categ").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Categ2 getCateg2(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Categ2> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Categ2> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Categ2.class)
    public static class Categ2ControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.trim().length() == 0) {
                return null;
            }
            Categ2Controller controller = (Categ2Controller) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "categ2Controller");
            return controller.getCateg2(getKey(value));
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
            if (object instanceof Categ2) {
                Categ2 o = (Categ2) object;
                return getStringKey(o.getC2Id());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Categ2.class.getName()});
                return null;
            }
        }

    }

}
