package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.CategModel;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.CategModelFacade;
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
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

@Named("categModelController")
@SessionScoped
public class CategModelController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.CategModelFacade ejbFacade;
    @Inject
    ItemsController itemsController;
    private List<CategModel> items = null;
    private CategModel selected;
    private List<CategModel> matchedItems = null;
    private List<CategModel> matchedItemsList =null;
    private CategModel nullModel = null;
    

    public CategModelController() {
    }
       
    public void startEdit(){
        if(matchedItemsList != null){matchedItemsList.clear();
            }else {matchedItemsList = new ArrayList<>();}
        if (itemsController.getSelected() != null){            
            if(matchedItemsList != null){matchedItemsList.clear();
            }else {matchedItemsList = new ArrayList<>();}
            List<CategMake> categMake = null;
            if (itemsController.getSelected().getCategMakesList() != null){ 
                categMake = new ArrayList<>(itemsController.getSelected().getCategMakesList());
                if(!categMake.isEmpty()){
                    for(CategMake mk : categMake)
                        matchedItemsList.addAll(ejbFacade.findFromParentId(mk.getMkId()));
                } else       
                    matchedItemsList = null;
            } else       
                matchedItemsList = null;
        }else{
           matchedItemsList = null;
        }
    }
    
    public void startCreate(){
      if(matchedItemsList != null)matchedItemsList = null;  
    }
   
    public CategModel getSelected() {
        return selected;
    }

    public void setSelected(CategModel selected) {
        this.selected = selected;
    }

    public List<CategModel> getMatchedItems() {
        return matchedItems;
    }

    public CategModel getNullModel() {
        return nullModel;
    }

    public void setNullModel(CategModel nullModel) {
        this.nullModel = nullModel;
    }

    public void setMatchedItems(List<CategModel> matchedItems) {
        this.matchedItems = matchedItems;
    }

    public List<CategModel> getMatchedItemsList() {
        /*if (matchedItemsList == null && itemsController.getSelected() != null){                   
            matchedItemsList = itemsController
                    .getSelected().getCategModelsList() != null ? new ArrayList<>(itemsController
                    .getSelected().getCategModelsList()) 
                    : ejbFacade.findAll();
        }  */      
        return matchedItemsList;
    }

    public void setMatchedItemsList(List<CategModel> matchedItemsList) {        
        this.matchedItemsList = matchedItemsList;
    }

    public ItemsController getItemsController() {
        return itemsController;
    }

    public void setItemsController(ItemsController itemsController) {
        this.itemsController = itemsController;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CategModelFacade getFacade() {
        return ejbFacade;
    }

    public CategModelFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(CategModelFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public CategModel prepareCreate() {
        selected = new CategModel();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_Categ").getString("CategModelCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_Categ").getString("CategModelUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_Categ").getString("CategModelDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<CategModel> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    
    public void handleMakeChange (ValueChangeEvent event){
        this.setSelected(null);
        CategMake categMake = (CategMake)event.getNewValue();
        if(categMake != null)
            matchedItems = ejbFacade.findFromParentId(categMake.getMkId());
        else
            matchedItems = null;
    }
    
    public void handleMakeChangeList (ValueChangeEvent event){        
        this.setSelected(null);
        if(matchedItemsList != null){matchedItemsList.clear();
        }else {matchedItemsList = new ArrayList<>();}
        if(event.getNewValue() != null){
            List<CategMake> categMake = (List<CategMake>)event.getNewValue();
            if(!categMake.isEmpty()){
                for(CategMake mk : categMake)
                matchedItemsList.addAll(ejbFacade.findFromParentId(mk.getMkId()));
            } else       
                matchedItemsList= null;
        } else       
                matchedItemsList = null;
    }
    
    public void itemSelected(AjaxBehaviorEvent event) {
    System.out.println("Selected item: " + "selectedItem");
    System.out.println("Selected item removed? " + "selectedItemRemoved");
}

    public String navigateAndSetCart(){
        itemsController.produceCartItems();
        return "/views/items/welcome.xhtml?faces-redirect=true";
    }
    
    public String navigateAndSetCartForIndexPage(){
        itemsController.produceCartItems();
        return "/views/items/welcome.xhtml?faces-redirect=true";
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

    public CategModel getCategModel(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<CategModel> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<CategModel> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(value="categModelCC")
    public static class CategModelControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CategModelController controller = (CategModelController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "categModelController");
            return controller.getCategModel(getKey(value));
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
            if (object instanceof CategModel) {
                CategModel o = (CategModel) object;
                return getStringKey(o.getMlId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), CategModel.class.getName()});
                return null;
            }
        }

    }

}
