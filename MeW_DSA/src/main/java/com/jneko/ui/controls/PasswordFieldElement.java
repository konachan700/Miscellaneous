package com.jneko.ui.controls;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;

public class PasswordFieldElement extends VBox {
    private final PasswordField
            field = new PasswordField();
    
    private final Label
            title = new Label();
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public PasswordFieldElement(String etitle) {
        super();
        this.getStyleClass().addAll("StringFieldElementRoot", "maxWidth");
        title.getStyleClass().addAll("StringFieldElementLabel", "maxWidth");
        field.getStyleClass().addAll("StringFieldElementText", "maxWidth");
        
        title.setText(etitle);
        field.setPromptText("");
        
        this.getChildren().addAll(title, field);
    }
    
    public String getText() {
        return field.getText();
    }
    
    public void SetText(String text) {
        field.setText(text);
    }
}
