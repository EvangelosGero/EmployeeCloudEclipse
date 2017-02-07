/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.converters;

import com.dynamotors.dynashoppingcart.ejbs.Categ1Facade;
import com.dynamotors.dynashoppingcart.entities.Categ1;
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
public class Categ1Converter implements Converter {

    @Inject
    private Categ1Facade ejbFacade; 
        
    public Object getAsObject(FacesContext fc, UIComponent uic, String value) {
        if(value != null && value.trim().length() > 0) {
            try {               
                return ejbFacade.find(Integer.parseInt(value));
            } catch(NumberFormatException e) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, 
                        "Conversion Error", "Not a valid Category."));
            }
        }
        else {
            return null;
        }
    }
 
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        if(object != null) {
            return String.valueOf(((Categ1) object).getC1Id());
        }
        else {
            return null;
        }
    }   
}       
