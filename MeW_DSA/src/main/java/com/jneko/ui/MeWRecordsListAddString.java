package com.jneko.ui;

import com.jneko.hibernate.DirectoryField;
import com.jneko.hibernate.HibernateUtil;
import com.jneko.hibernate.StringField;
import com.jneko.hibernate.StringFieldTypes;
import com.jneko.mewdatastore.MainApp;
import com.jneko.ui.controls.ExtendedStringFieldElement;
import com.jneko.ui.controls.LabelElement;
import com.jneko.ui.toppanel.TopPanel;
import com.jneko.ui.toppanel.TopPanelButton;
import java.util.ArrayList;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class MeWRecordsListAddString extends ScrollPane implements MewTab, MeWDeleteActionListener {
    private static MeWRecordsListAddString 
            thisClass = null;
    
    private final VBox
            rootContainer = new VBox();
    
    private final VBox 
            nullPaginator = new VBox();

    private final LabelElement 
            titleLabel = new LabelElement("Add new elements.");
    
    private final ArrayList<ExtendedStringFieldElement> 
            elements = new ArrayList<>();
    
    private final TopPanel 
            tp = new TopPanel();
        
    private DirectoryField 
            parentDir = null;
    
    private boolean isNew = true;
    
    private final TopPanelButton 
            tpbSave = new TopPanelButton("iconSave", "Create/Save changes", c -> {
                if (isNew) {
                    if (elements.isEmpty()) return;
                    HibernateUtil.beginTransaction(HibernateUtil.getCurrentSession());
                    elements.forEach(el -> {
                        final StringField f = new StringField();
                        f.setOptName(el.getText());
                        f.setOptValue(el.getValue());

                        if (parentDir != null) parentDir.getStringElements().add(f);
                        HibernateUtil.getCurrentSession().save(f);
                    });
                    if (parentDir != null) HibernateUtil.getCurrentSession().save(parentDir);
                    HibernateUtil.commitTransaction(HibernateUtil.getCurrentSession());
                }
                
                MainApp.closeThisTab();
                MeWRecordsList.getInstance().refresh();
            }),
            tpbClose = new TopPanelButton("iconCancel", "Close tab without changes", c -> {
                MainApp.closeThisTab();
            }),
            tpbAddSimpleString = new TopPanelButton("iconAddMore", "Add more field", c -> {
                genElement();
            });
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    private MeWRecordsListAddString() {
        super();
        this.getStyleClass().addAll("maxWidth", "maxHeight", "ScrollPane");        
        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setContent(rootContainer);
        this.setFitToWidth(true);
        this.setFitToHeight(false);        
        
        rootContainer.getStyleClass().addAll("MeWRecordsListAddDirRoot", "recordsListSpace");
        rootContainer.getChildren().addAll(
                titleLabel
        );
        tp.addNodes(tpbSave, tpbClose);
        tp.addSeparator();
        tp.addNodes(tpbAddSimpleString);
        genElement();
    }
    
    private void genElement() {
        final ExtendedStringFieldElement el = new ExtendedStringFieldElement("Field name/value", "", "", "Enter text here...", StringFieldTypes.SIMPLE_STRING, this);
        elements.add(el);
        rootContainer.getChildren().addAll(el);
    }
    
    public void setForChange(DirectoryField parent, ArrayList<StringField> sf) {
        isNew = false;
        parentDir = parent;
        elements.clear();
        rootContainer.getChildren().clear();
        rootContainer.getChildren().addAll(titleLabel);
        sf.forEach(el -> {
            final ExtendedStringFieldElement el1 = new ExtendedStringFieldElement("Field name/value", el.getOptName(), el.getOptValue(), "Enter text here...", el.getType(), this);
            elements.add(el1);
            rootContainer.getChildren().add(el1);
        });
    }
    
    public void setForNew(DirectoryField parent) {
        isNew = true;
        parentDir = parent;
        elements.clear();
        rootContainer.getChildren().clear();
        rootContainer.getChildren().addAll(titleLabel);
        genElement();
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
    
    public static MeWRecordsListAddString getInstance() {
        if (thisClass == null) thisClass = new MeWRecordsListAddString();
        return thisClass;
    }

    @Override
    public void OnDelete(Parent sf) {
        if (isNew) {
            if (sf instanceof ExtendedStringFieldElement) { 
                elements.remove((ExtendedStringFieldElement) sf);
                rootContainer.getChildren().remove(sf);
            }
        }
    }
}
