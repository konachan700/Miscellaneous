package com.jneko.ui.menu;

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class MewMenu extends VBox {
    //private final static Image logoImage = new Image(new File("./style/icons/logo7.png").toURI().toString());
    //private final ImageView imgLogoNode = new ImageView(logoImage);
    
    private final ArrayList<MewMenuGroup> items = new ArrayList<>();
    private final MewMenuActionListener serviceALX = (action) -> {
        items.forEach(c -> {
            c.deselectAll();
        });
    };
    
    public MewMenu(MewMenuGroup ... mg) {
        super();
        this.setAlignment(Pos.TOP_LEFT);

        //final Label logo = new Label();
        //logo.setAlignment(Pos.TOP_RIGHT);
        //logo.getStyleClass().addAll("menu_app_logo", "menu_max_width");
        //logo.setGraphic(imgLogoNode);
        //this.getChildren().add(logo);
        
        for (MewMenuGroup m : mg) {
            m.registerServiceAL(serviceALX);
            items.add(m);
            this.getChildren().add(m);
        }
    }
    
    public void deselectAll() {
        items.forEach(c -> { 
            c.deselectAll();
        });
    }
}
