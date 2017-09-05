package com.jneko.ui.controls;

import com.jneko.hibernate.StringFieldTypes;
import com.jneko.ui.MeWDeleteActionListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jiconfont.javafx.IconNode;

public class ExtendedStringFieldElement extends VBox {
    private final IconNode 
            iconRemove = new IconNode();
    
    private final TextField
            field = new TextField(),
            fieldValue = new TextField();
    
    private final Label
            title = new Label();
    
    private final ComboBox<String>
            cb = new ComboBox<>();
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ExtendedStringFieldElement(String etitle, String etext, String eValue, String ehelp, StringFieldTypes sel, MeWDeleteActionListener al) {
        super();
        this.getStyleClass().addAll("StringFieldElementRoot", "maxWidth");
        title.getStyleClass().addAll("StringFieldElementLabel", "maxWidth");
        field.getStyleClass().addAll("StringFieldElementText", "maxWidth");
        fieldValue.getStyleClass().addAll("StringFieldElementText", "maxWidth");
        cb.getStyleClass().addAll("StringFieldElementCombo");
        iconRemove.getStyleClass().addAll("iconRemoveButton");
        
        title.setText(etitle);
        field.setText(etext);
        field.setPromptText("Enter name here...");
        fieldValue.setText(eValue);
        fieldValue.setPromptText("Enter value here...");
        
        final StringFieldTypes[] typeArr = StringFieldTypes.values();
        for (StringFieldTypes t : typeArr) {
            cb.getItems().add(t.getFullName());
        }
        
        if (sel == null)
            cb.setValue(StringFieldTypes.SIMPLE_STRING.getFullName());
        else 
            cb.setValue(sel.getFullName());
        
        if (al != null) 
            iconRemove.setOnMouseClicked(c -> {
                al.OnDelete(this);
            });

        final HBox v = new HBox(8);
        v.getStyleClass().addAll("maxWidth");
        v.getChildren().addAll(field, fieldValue, cb, iconRemove);
        
        this.getChildren().addAll(title, v);
    }
    
    public StringFieldTypes getType() {
        return StringFieldTypes.valueOf(cb.getValue());
    }
    
    public void setType(StringFieldTypes t) {
        cb.setValue(t.getFullName());
    }
    
    public String getText() {
        return field.getText();
    }
    
    public void SetText(String text) {
        field.setText(text);
    }
    
    public String getValue() {
        return fieldValue.getText();
    }
    
    public void SetValue(String text) {
        fieldValue.setText(text);
    }
}
