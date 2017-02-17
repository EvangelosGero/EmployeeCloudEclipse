package com.dynamotors.dynashoppingcart.controller;

import com.dynamotors.dynashoppingcart.entities.ItemsImages;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil;
import com.dynamotors.dynashoppingcart.controller.util.JsfUtil.PersistAction;
import com.dynamotors.dynashoppingcart.ejbs.ItemsImagesFacade;
import java.io.IOException;
import java.io.InputStream;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named("itemsImagesController")
@SessionScoped
public class ItemsImagesController implements Serializable {

    @EJB
    private com.dynamotors.dynashoppingcart.ejbs.ItemsImagesFacade ejbFacade;
    private List<ItemsImages> items = null;
    private ItemsImages selected;    
    @Inject
    private ItemsController itemsController;

    public ItemsImagesController() {
    }

    public ItemsImages getSelected() {
        return selected;
    }

    public void setSelected(ItemsImages selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ItemsImagesFacade getFacade() {
        return ejbFacade;
    }

    public ItemsController getItemsController() {
        return itemsController;
    }

    public void setItemsController(ItemsController itemsController) {
        this.itemsController = itemsController;
    }

    public ItemsImagesFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(ItemsImagesFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }   
            
    public ItemsImages prepareCreate() {
        selected = new ItemsImages();
        if(itemsController.getSelected() != null)selected.setItems(itemsController.getSelected());
        initializeEmbeddableKey();
        return selected;
    }
    
    public void handleFileUpload(FileUploadEvent event) throws IOException {
        UploadedFile uploadedFile = event.getFile();
        InputStream inputStr = null;
        try{
            inputStr = uploadedFile.getInputstream();            
        }catch(IOException e){
            Logger.getLogger(ItemsImagesController.class.getName()).log(Level.SEVERE, null, e);
        }
        Path destFile = Paths.get("C:", "ShoppingCart", "images1", uploadedFile.getFileName()); 
                       
        try {
            Files.copy(inputStr, destFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(ItemsImagesController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle_itemsImages").getString("ItemsImagesCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle_itemsImages").getString("ItemsImagesUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle_itemsImages").getString("ItemsImagesDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ItemsImages> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_itemsImages").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle_itemsImages").getString("PersistenceErrorOccured"));
            }
        }
    }

    public ItemsImages getItemsImages(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<ItemsImages> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ItemsImages> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = ItemsImages.class)
    public static class ItemsImagesControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ItemsImagesController controller = (ItemsImagesController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "itemsImagesController");
            return controller.getItemsImages(getKey(value));
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
            if (object instanceof ItemsImages) {
                ItemsImages o = (ItemsImages) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ItemsImages.class.getName()});
                return null;
            }
        }

    }

}
