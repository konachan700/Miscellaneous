package com.jneko.ui;

import com.jneko.hibernate.DirectoryField;
import com.jneko.hibernate.StringField;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class MeWRecordsListString extends MeWSelectableListElement {
    private final DirectoryField data;
    private final MeWClickActionListener al;
    private final StringField sf1;
    
    private final VBox 
            container = new VBox();
    
    private final TextField
            title = new TextField("Test Record X"),
            text = new TextField("test test test test test test test test test test test test test test");
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public MeWRecordsListString(DirectoryField df, StringField sf, MeWClickActionListener _al) {
        super("iconString01");
        super.setAL(sel -> {
            if (sel) 
                title.getStyleClass().addAll("folderElementSelectedText");
             else 
                title.getStyleClass().removeAll("folderElementSelectedText");
        });
        
        data = df;
        al = _al;
        sf1 = sf;

        container.getStyleClass().addAll("maxWidth");
        
        title.getStyleClass().addAll("maxWidth", "folderElementField","folderElementTitle");
        title.setAlignment(Pos.CENTER_LEFT);
        title.setEditable(false);
        title.setCursor(Cursor.HAND);
        
        text.getStyleClass().addAll("maxWidth", "folderElementField", "folderElementText");
        text.setAlignment(Pos.TOP_LEFT);
        text.setEditable(false);
        text.setCursor(Cursor.HAND);
        
        if (sf1 != null) {
            title.setText(sf1.getOptName());
            text.setText(sf1.getOptValue());
            
            this.setOnMouseClicked(c -> {
                onClick(c);
            });
            
            title.setOnMouseClicked(c -> {
                onClick(c);
            });
            
            text.setOnMouseClicked(c -> {
                onClick(c);
            });
        }
        
        container.getChildren().addAll(title, text);
        super.setContent(container);
    }
    
    private void onClick(MouseEvent c) {
        if (al == null) return;
        if (c.getClickCount() == 1) 
            al.OnClick(data, this); 
        else if (c.getClickCount() == 2) 
            al.OnDoubleClick(data, this); 
    }
    
    public DirectoryField getData() {
        return data;
    }
    
    public StringField getSF() {
        return sf1;
    }
}
