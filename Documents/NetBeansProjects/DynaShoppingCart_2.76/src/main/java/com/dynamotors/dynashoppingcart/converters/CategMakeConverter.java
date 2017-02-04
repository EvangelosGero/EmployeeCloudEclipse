/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.converters;

import com.dynamotors.dynashoppingcart.ejbs.CategMakeFacade;
import com.dynamotors.dynashoppingcart.entities.CategMake;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author evgero
 */
@Named
@ApplicationScoped
public class CategMakeConverter implements Converter {

    @Inject
    private CategMakeFacade ejbFacade; 
    
     
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {               
                return ejbFacade.find(Integer.parseInt(value));
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Conversion Error", "Not a valid theme."));
            }
        }
        else {
            return null;
        }
    }
 
   
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((CategMake) object).getMkId());
        }
        else {
            return null;
        }
    }   
}         