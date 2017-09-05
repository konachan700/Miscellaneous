package com.jneko.ui.menu;

import java.util.ArrayList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class MewMenuItem extends Label {
    private final MewMenuActionListener mil;
    private boolean selected = false;
    private final ArrayList<MewMenuActionListener> serviceAL = new ArrayList<>();

    public MewMenuItem(String text, MewMenuActionListener m) {
        super(text);
        mil = m;
        this.getStyleClass().addAll("menuItem");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setOnMouseClicked(c -> {
            if (!selected) {
                mil.OnClick(c);
                serviceAL.forEach(x -> {
                    x.OnClick(c); 
                });
                setSelected(true);
            }
        });
    }
    
    public MewMenuItem defaultSelected() {
        setSelected(true);
        return this;
    }
    
    public final void setSelected(boolean s) {
        selected = s;
        if (s) 
            this.getStyleClass().add("menuSelectedItem");
        else
            this.getStyleClass().removeAll("menuSelectedItem");
    }
    
    public final boolean isSelected() {
        return selected;
    }
    
    protected final void registerServiceAL(MewMenuActionListener s) {
        serviceAL.add(s);
    }
}
