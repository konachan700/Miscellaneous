package com.jneko.ui;

import com.jneko.hibernate.DirectoryField;
import com.jneko.hibernate.HibernateUtil;
import com.jneko.mewdatastore.MainApp;
import com.jneko.ui.controls.LabelElement;
import com.jneko.ui.controls.StringFieldElement;
import com.jneko.ui.toppanel.TopPanel;
import com.jneko.ui.toppanel.TopPanelButton;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class MeWRecordsListAddDir extends VBox implements MewTab {
    private static MeWRecordsListAddDir 
            thisClass = null;
    
    private final VBox 
            nullPaginator = new VBox();
    
    private final LabelElement 
            titleLabel = new LabelElement("Add new directory/group.");
    
    private final StringFieldElement
            dirTitle = new StringFieldElement("Directory title", "", "Enter text here..."),
            dirText = new StringFieldElement("Directory note", "", "Enter text here...");
    
    private final TopPanel 
            tp = new TopPanel();
        
    private DirectoryField 
            currDir = null, 
            parentDir = null;
    
    private final TopPanelButton 
            tpbSave = new TopPanelButton("iconSave", "Create/Save changes", c -> {
                if (dirTitle.getText().trim().length() < 1) return;
                if (dirText.getText().trim().length() < 1) return;
                
                if (currDir == null) {
                    currDir = new DirectoryField();
                    currDir.setGID((parentDir == null) ? 0 : parentDir.getID()); 
                }  
                
                HibernateUtil.beginTransaction(HibernateUtil.getCurrentSession());
                currDir.setOptName(dirTitle.getText());
                currDir.setOptValue(dirText.getText());
                HibernateUtil.getCurrentSession().save(currDir);
                HibernateUtil.commitTransaction(HibernateUtil.getCurrentSession());
                
                currDir = null;
                MainApp.closeThisTab();
                MeWRecordsList.getInstance().refresh();
            }),
            tpbClose = new TopPanelButton("iconCancel", "Close tab without changes", c -> {
                MainApp.closeThisTab();
            });
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    private MeWRecordsListAddDir() {
        super();
        this.getStyleClass().addAll("maxWidth", "maxHeight","MeWRecordsListAddDirRoot", "recordsListSpace");
        this.getChildren().addAll(
                titleLabel,
                dirTitle,
                dirText
        );
        tp.addNodes(tpbSave, tpbClose);
        currDir = null;
    }
    
    public void setForChange(DirectoryField df, DirectoryField parent) {
        dirTitle.SetText(df.getOptName());
        dirText.SetText(df.getOptValue());
        currDir = df;
        parentDir = parent;
    }
    
    public void setForNew(DirectoryField parent) {
        dirTitle.SetText("");
        dirText.SetText("");
        currDir = null;
        parentDir = parent;
    }
    
    @Override
    public Parent getPaginator() {
        return nullPaginator;
    }

    @Override
    public Parent getPanel() {
        return tp;
    }

    @Override
    public Parent getMain() {
        return this;
    }
    
    public static MeWRecordsListAddDir getInstance() {
        if (thisClass == null) thisClass = new MeWRecordsListAddDir();
        return thisClass;
    }
}
