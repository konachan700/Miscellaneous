package com.jneko.ui.menu;

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import jiconfont.javafx.IconNode;

public class MewMenuGroup extends VBox {
    private final ArrayList<MewMenuActionListener> serviceAL = new ArrayList<>();
    private final ArrayList<MewMenuItem> items = new ArrayList<>();
    private final MewMenuActionListener serviceALX = (action) -> {
        deselectAll();
        serviceAL.forEach(x -> {
            x.OnClick(action); 
        });
    };
    
    public MewMenuGroup() {
        super();
        this.getStyleClass().addAll("menuSeparator");
    }
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public MewMenuGroup(String groupName, String colorStyle, String iconStyle, MewMenuItem ... node) {
        super();
        
        this.getStyleClass().addAll("menuHeaderBox");
        this.setAlignment(Pos.TOP_LEFT);
                
        final Label header = new Label(groupName);
        this.getChildren().addAll(header);
        header.getStyleClass().addAll("menuHeader", colorStyle);
        //final IconNode iconNode = new IconNode();
        //iconNode.getStyleClass().add(iconStyle);
        //header.setGraphic(iconNode); 
        
        for (MewMenuItem mi : node) {
            mi.registerServiceAL(serviceALX);
            items.add(mi);
            this.getChildren().add(mi);
        }
    }
    
    public void deselectAll() {
        items.forEach(c -> { 
            c.setSelected(false);
        });
    }
    
    protected final void registerServiceAL(MewMenuActionListener s) {
        serviceAL.add(s);
    }
}
