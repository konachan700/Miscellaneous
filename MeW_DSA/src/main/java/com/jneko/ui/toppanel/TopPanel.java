package com.jneko.ui.toppanel;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class TopPanel extends HBox {
    public TopPanel() {
        super();
        this.getStyleClass().addAll("maxWidth");
        this.setAlignment(Pos.CENTER_LEFT);
    }
    
    public void addSeparator() {
        final VBox sep1 = new VBox();
        sep1.getStyleClass().addAll("maxWidth");
        this.getChildren().add(sep1);
    }
    
    public void addFixedSeparator() {
        final VBox sep1 = new VBox();
        sep1.getStyleClass().addAll("topPanelSeparator");
        this.getChildren().add(sep1);
    }
    
    public void addNode(Node element) {
        this.getChildren().add(element);
    }
    
    public void addNodes(Node ... element) {
        this.getChildren().addAll(element);
    }
}
