package com.jneko.ui.windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class MeWDialog extends Stage {
    private final Scene scene;

    private final VBox
            rootPane = new VBox(),
            contentPane = new VBox(),
            mainBox = new VBox();
    
    private final HBox
            headerBox = new HBox();
    
    public MeWDialog() {
        this(1000, 600, "JNeko MeW Data Storage Application", "/styles/Styles.css");
    }
    
    public MeWDialog(int width, int height, String title, String css) {
        super();
        
        rootPane.getChildren().addAll(
                headerBox,
                mainBox
        );

        scene = new Scene(rootPane, width, height);
        scene.getStylesheets().add(css);
        
        headerBox.getStyleClass().addAll("maxWidth");
        mainBox.getStyleClass().addAll("windowContentBox", "maxHeight", "maxWidth");
        contentPane.setAlignment(Pos.TOP_LEFT);
        
        super.setMinWidth(width);
        super.setMinHeight(height);
        super.setTitle(title);
        super.setScene(scene);
    }
    
    public VBox getMainBox() {
        return mainBox;
    }

    public HBox getHeaderBox() {
        return headerBox;
    }
}
