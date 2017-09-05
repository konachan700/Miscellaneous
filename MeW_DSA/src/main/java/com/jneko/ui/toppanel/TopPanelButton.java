package com.jneko.ui.toppanel;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import jiconfont.javafx.IconNode;

public class TopPanelButton extends Button {
    private final TopPanelButtonActionListener al;
    private final IconNode iconNode = new IconNode();
    
    public TopPanelButton(String iconStyle, String tooltip, TopPanelButtonActionListener a) {
        al = a;
        this.getStyleClass().addAll("topPanelButton");
        this.setAlignment(Pos.CENTER);
        iconNode.getStyleClass().addAll(iconStyle, "topPanelButtonIcon");
        this.setGraphic(iconNode); 
        this.setOnMouseClicked(c -> {
            if (al != null) al.OnClick(c);
        });
        this.setTooltip(new Tooltip(tooltip));
    } 
}
