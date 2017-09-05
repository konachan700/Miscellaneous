package com.jneko.ui;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import jiconfont.javafx.IconNode;

public class MeWSelectableListElement extends HBox implements MeWSelectable {
    private final VBox 
            container = new VBox(),
            iconContainer = new VBox();
    
    private final IconNode 
            icon = new IconNode();
    
    private MeWSelectableActionListener
            al = null;
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public MeWSelectableListElement(String iconStyle) {
        this.getStyleClass().addAll("folderElementRoot", "maxWidth");
        this.setCursor(Cursor.HAND);
        container.getStyleClass().addAll("maxWidth", "folderElementContSel");
        icon.getStyleClass().addAll(iconStyle);
        
        iconContainer.setAlignment(Pos.CENTER);
        iconContainer.getStyleClass().addAll("iconContainer");
        iconContainer.getChildren().add(icon);
        
        this.getChildren().addAll(iconContainer, container);
    }
    
    public void setAL(MeWSelectableActionListener styleCallback) {
        al = styleCallback;
    }
    
    public void setContent(Parent p) {
        container.getChildren().clear();
        container.getChildren().add(p);
    }

    @Override
    public void setSelected(boolean sel) {
        if (sel) 
            container.getStyleClass().addAll("folderElementSelected");
         else 
            container.getStyleClass().removeAll("folderElementSelected");
        if (al != null) al.setSelected(sel);
    }

    @Override
    public boolean isSelected() {
        return container.getStyleClass().contains("folderElementSelected");
    }
}
