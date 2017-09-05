package com.jneko.ui.controls;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class LabelElement extends Label {
    public LabelElement(String text) {
        super(text);
        this.getStyleClass().addAll("LabelElementRoot", "maxWidth");
        this.setAlignment(Pos.CENTER_LEFT);
    }
    
}
