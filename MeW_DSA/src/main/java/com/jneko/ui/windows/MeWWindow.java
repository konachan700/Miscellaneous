package com.jneko.ui.windows;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MeWWindow {
    private final Scene scene;
    private final Stage thisStage;

    private final VBox
            menuPane = new VBox(),
            contentPane = new VBox(),
            mainBox = new VBox(),
            menuBox = new VBox();
    
    private final HBox
//            headerPane = new HBox(),
            rootPane = new HBox(),
            headerBox = new HBox(),
            headerLogoBox = new HBox(),
            footerBox = new HBox();
    
    public MeWWindow(Stage stage) {
        this(stage, 1000, 600, "JNeko MeW Data Storage Application", "/styles/Styles.css");
    }
    
    public MeWWindow(Stage stage, int width, int height, String title, String css) {
        rootPane.getChildren().addAll(
                menuPane,
                contentPane
        );
        
        contentPane.getChildren().addAll(
                headerBox,
                mainBox,
                footerBox
        );
        
        menuPane.getChildren().addAll(
                headerLogoBox,
                menuBox
        );
        
        headerLogoBox.getChildren().add(new ImageView(new Image(getClass().getResourceAsStream("/images/mew-dsa-logo-48-2.png"))));

        thisStage = stage;
        scene = new Scene(rootPane, width, height);
        scene.getStylesheets().add(css);
        
//        headerPane.getStyleClass().addAll("windowHeader", "maxWidth");
        headerBox.getStyleClass().addAll("maxWidth");
        headerLogoBox.getStyleClass().addAll("windowHeaderLogo");
        footerBox.getStyleClass().addAll("windowFooter", "maxWidth");
        menuBox.getStyleClass().addAll("windowMenu", "maxHeight");
        mainBox.getStyleClass().addAll("windowContentBox", "maxHeight", "maxWidth");
        
        menuBox.setAlignment(Pos.TOP_LEFT);
        contentPane.setAlignment(Pos.TOP_LEFT);
        
        thisStage.setMinWidth(width);
        thisStage.setMinHeight(height);
        thisStage.setTitle(title);
        thisStage.setScene(scene);
    }
    
    public Scene getScene() {
        return scene;
    }
    
    public VBox getMainBox() {
        return mainBox;
    }
    
    public VBox getMenuBox() {
        return menuBox;
    }
    
    public HBox getHeaderBox() {
        return headerBox;
    }
    
    public HBox getFooterBox() {
        return footerBox;
    }
    
    public void show() {
        thisStage.show();
    }
    
    public void showModal() {
        thisStage.showAndWait();
    }
}
