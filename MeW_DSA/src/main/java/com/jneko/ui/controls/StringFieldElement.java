package com.jneko.ui.controls;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class StringFieldElement extends VBox {
    private final TextField
            field = new TextField();
    
    private final Label
            title = new Label();
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public StringFieldElement(String etitle, String etext, String ehelp) {
        super();
        this.getStyleClass().addAll("StringFieldElementRoot", "maxWidth");
        title.getStyleClass().addAll("StringFieldElementLabel", "maxWidth");
        field.getStyleClass().addAll("StringFieldElementText", "maxWidth");
        
        title.setText(etitle);
        field.setText(etext);
        field.setPromptText(ehelp);
        
        this.getChildren().addAll(title, field);
    }
    
    public String getText() {
        return field.getText();
    }
    
    public void SetText(String text) {
        field.setText(text);
    }
}
