package com.jneko.mewdatastore;

import com.jneko.hibernate.HibernateUtil;
import com.jneko.ui.MeWRecordsList;
import com.jneko.ui.windows.MeWWindow;
import com.jneko.ui.MewTab;
import com.jneko.ui.menu.MewMenu;
import com.jneko.ui.menu.MewMenuGroup;
import com.jneko.ui.menu.MewMenuItem;
import com.jneko.ui.windows.MeWDialogPassword;
import java.io.File;
import java.util.Stack;
import javafx.application.Application;
import javafx.stage.Stage;
import jiconfont.icons.GoogleMaterialDesignIcons;
import jiconfont.javafx.IconFontFX;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.stage.WindowEvent;

public class MainApp extends Application {
    private static MainApp thisClass = null;
    private MeWWindow win;
    
    private static final Stack<MewTab> tabs = new Stack<>();
    private static MewTab currentTab = null;
    
    @Override
    public void start(Stage stage) throws Exception {
        if (thisClass == null) thisClass = this;
        IconFontFX.register(GoogleMaterialDesignIcons.getIconFont());
        
        if (!checkDir()) {
            //TODO: add msgbox
            Platform.exit();
            return;
        }
        
        final MeWDialogPassword mp = new MeWDialogPassword();
        while (true) {
            mp.showAndWait();
            if (mp.isExit()) {
                Platform.exit();
                return;
            }
            try {
                HibernateUtil.init("database", "JNekoMeW", "278tyg8ergvbq78egv8"); 
                break;
            } catch (Throwable t) {
                mp.displayError("Incorrect password!");
            }
        }

        win = new MeWWindow(stage);
        final MewMenu mn = new MewMenu(
                new MewMenuGroup(
                        "Local storage", "menuHeaderBlack", "menuHeaderIcon",
                        new MewMenuItem("Groups", (c) -> {
                            clearMe();
                            showTab(MeWRecordsList.getInstance());
                            MeWRecordsList.getInstance().refresh();
                        }).defaultSelected(),
                        new MewMenuItem("Search", (c) -> {
                            clearMe();
                            
                        })
                ),
                new MewMenuGroup(),
                new MewMenuGroup(
                        "Settings", "menuHeaderBlack", "menuHeaderIcon",
                        new MewMenuItem("Main settings", (c) -> {

                        })
                )
            );
        
        stage.setOnHiding((WindowEvent event) -> {
            HibernateUtil.dispose();
            Platform.exit(); 
        });
        
        win.getMenuBox().getChildren().add(mn);
        showTab(MeWRecordsList.getInstance());
        MeWRecordsList.getInstance().refresh();
        win.show();
    }
    
    private void showTab(MewTab t) {
        win.getFooterBox().getChildren().add(t.getPaginator());
        win.getHeaderBox().getChildren().add(t.getPanel());
        win.getMainBox().getChildren().add(t.getMain());
        currentTab = t;
    }
    
    private void clearMe() {
        tabs.clear();
        win.getFooterBox().getChildren().clear();
        win.getHeaderBox().getChildren().clear();
        win.getMainBox().getChildren().clear();
    }
    
    private void clear() {
        win.getFooterBox().getChildren().clear();
        win.getHeaderBox().getChildren().clear();
        win.getMainBox().getChildren().clear();
    }
    
//    public boolean checkDBFile() {
//        return !((!dbDir.canRead()) | (!dbDir.canWrite()));
//        //database.mv.db
//    }
    
    private boolean checkDir() {
        final File dbDir = new File("./database");
        if (!dbDir.exists()) {
            if (!dbDir.mkdirs()) return false;
        }
        
        return !((!dbDir.canRead()) | (!dbDir.canWrite()));
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    public static void newTab(MewTab tab) {
        tabs.push(currentTab);
        thisClass.clear();
        thisClass.showTab(tab);
    }
    
    public static void closeThisTab() {
        final MewTab t = tabs.pop();
        thisClass.clear();
        thisClass.showTab(t);
    }
}
