/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ValidationWithBindings;

import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.css.PseudoClass;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 *
 * @author e
 */
public class Validation {
    
    public static BooleanBinding emptyTextFieldBinding(TextField textField, String message) {
		BooleanBinding binding = Bindings.createBooleanBinding(() -> 
			textField.getText().trim().isEmpty(), textField.textProperty());
		configureTextFieldBinding(binding, textField, message);
		return binding ;
	}
    
     public static BooleanBinding emptyDatePickerBinding(DatePicker datePicker, String message) {
		BooleanBinding binding = Bindings.createBooleanBinding(() -> 
			datePicker.getEditor().getText().trim().isEmpty(), datePicker.getEditor().textProperty());
		configureDatePickerBinding(binding, datePicker, message);
		return binding ;
	}
    
    public static BooleanBinding patternTextFieldBinding(TextField textField, Pattern pattern, String message) {
		BooleanBinding binding = Bindings.createBooleanBinding(() -> 
			!pattern.matcher(textField.getText() != null ? textField.getText() : " ").matches(), textField.textProperty());
		configureTextFieldBinding(binding, textField, message);
		return binding ;
	}
        
    private static void configureTextFieldBinding(BooleanBinding binding, TextField textField, String message) {		
		if (textField.getTooltip() == null) {
			textField.setTooltip(new Tooltip());
		}
		String tooltipText = textField.getTooltip().getText();
		binding.addListener((obs, oldValue, newValue) -> {
			updateTextFieldValidationStatus(textField, tooltipText, newValue, message);
		});
		updateTextFieldValidationStatus(textField, tooltipText, binding.get(), message);
	}
    
    private static void configureDatePickerBinding(BooleanBinding binding, DatePicker datePicker, String message) {		
		if (datePicker.getTooltip() == null) {
			datePicker.setTooltip(new Tooltip());
		}
		String tooltipText = datePicker.getTooltip().getText();
		binding.addListener((obs, oldValue, newValue) -> {
			updateDatePickerValidationStatus(datePicker, tooltipText, newValue, message);
		});
		updateDatePickerValidationStatus(datePicker, tooltipText, binding.get(), message);
	}
    
    private static void updateTextFieldValidationStatus(TextField textField,
                String defaultTooltipText, boolean invalid, String message){
                textField.pseudoClassStateChanged(PseudoClass.getPseudoClass("validation-error"), invalid);
                String tooltipText;
                Image errorImage = new Image("/images/error1.png",16,16,true,true);
                ImageView errorView = new ImageView(errorImage);
                //errorView.setScaleX(0.1);
                //errorView.setScaleY(0.1);
                if (invalid){
                    tooltipText = message;
                }else{
                    tooltipText = defaultTooltipText;
                }
                if (tooltipText == null || tooltipText.isEmpty()){
                    textField.setTooltip(null);
                }else{
                    Tooltip tooltip = textField.getTooltip();
                    if (tooltip == null){
                        Tooltip tooltipNew = new Tooltip(tooltipText);
                        tooltipNew.setGraphic(errorView);
                        textField.setTooltip(tooltipNew);
                    }else{
                        tooltip.setText(tooltipText);
                        tooltip.setGraphic(errorView);
                    }
                }
    }
    
    private static void updateDatePickerValidationStatus(DatePicker datePicker,
                String defaultTooltipText, boolean invalid, String message){
                datePicker.getEditor().pseudoClassStateChanged(PseudoClass.getPseudoClass("validation-error"), invalid);
                String tooltipText;
                Image errorImage = new Image("/images/error1.png",16,16,true,true);
                ImageView errorView = new ImageView(errorImage);
                //errorView.setScaleX(0.1);
                //errorView.setScaleY(0.1);
                if (invalid){
                    tooltipText = message;
                }else{
                    tooltipText = defaultTooltipText;
                }
                if (tooltipText == null || tooltipText.isEmpty()){
                    datePicker.setTooltip(null);
                }else{
                    Tooltip tooltip = datePicker.getTooltip();
                    if (tooltip == null){
                        Tooltip tooltipNew = new Tooltip(tooltipText);
                        tooltipNew.setGraphic(errorView);
                        datePicker.setTooltip(tooltipNew);
                    }else{
                        tooltip.setText(tooltipText);
                   //     tooltip.setGraphic(errorView);
                    }
                }
    }
    
}
