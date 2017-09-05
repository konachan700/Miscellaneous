package com.jneko.ui;

import com.jneko.hibernate.DirectoryField;
import com.jneko.hibernate.HibernateUtil;
import com.jneko.hibernate.StringField;
import com.jneko.mewdatastore.MainApp;
import com.jneko.ui.toppanel.TopPanel;
import com.jneko.ui.toppanel.TopPanelButton;
import com.jneko.ui.toppanel.TopPanelMenuButton;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.hibernate.criterion.Restrictions;

public class MeWRecordsList extends ScrollPane implements MewTab, MeWClickActionListener {
    private static MeWRecordsList 
            thisClass = null;
    
    private DirectoryField
            currentDir = null;
    
    private final TopPanelButton 
            tpbLevelUp = new TopPanelButton("iconLevelUp", "One level up", c -> {
                    if (currentDir == null) return;
                    if (currentDir.getGID() == 0) {
                        currentDir = null;
                        refresh();
                    } else {
                        final DirectoryField dirVal = (DirectoryField) HibernateUtil.getCurrentSession()
                                .createCriteria(DirectoryField.class)
                                .add(Restrictions.eq("ID", currentDir.getGID()))
                                .uniqueResult();
                        if (dirVal != null) {
                            currentDir = dirVal;
                            refresh();
                        }
                    }
    });
    
    private final TopPanelMenuButton 
            menuBtnAlbum = new TopPanelMenuButton();
    
    private final TopPanel 
            tp = new TopPanel();

    private final VBox 
            nullPaginator = new VBox();

    private final VBox 
            rootContainer = new VBox();
    
    private final ArrayList<MeWSelectable> 
            list = new ArrayList<>();
    
    private MeWSelectable
            selectedItem = null;
    
    private final MenuItem 
            miDelete, miEditDir;
    
    private MeWRecordsList() {
        super();
        
        this.getStyleClass().addAll("maxWidth", "maxHeight", "ScrollPane");
        rootContainer.getStyleClass().addAll("recordsListSpace");

        menuBtnAlbum.addMenuItem("Add new directory...", (c) -> {
            MeWRecordsListAddDir.getInstance().setForNew(currentDir);
            MainApp.newTab(MeWRecordsListAddDir.getInstance()); 
        });
        
        menuBtnAlbum.addMenuItem("Add new text records...", (c) -> {
            MeWRecordsListAddString.getInstance().setForNew(currentDir);
            MainApp.newTab(MeWRecordsListAddString.getInstance()); 
        });

        miEditDir = menuBtnAlbum.addMenuItem("Edit selected directory...", (c) -> {
            if (selectedItem instanceof MeWRecordsListFolder) {
                MeWRecordsListAddDir.getInstance().setForChange(((MeWRecordsListFolder)selectedItem).getData(), currentDir);
                MainApp.newTab(MeWRecordsListAddDir.getInstance());
            }
        });
        miEditDir.setVisible(false);
        
        miDelete = menuBtnAlbum.addMenuItem("Delete selected item.", (c) -> {
            if (selectedItem instanceof MeWRecordsListString) {
                HibernateUtil.beginTransaction(HibernateUtil.getCurrentSession());
                final StringField sfa = ((MeWRecordsListString)selectedItem).getSF();
                if (!sfa.getDirectories().isEmpty()) {
                    sfa.getDirectories().forEach(ela -> {
                        ela.getStringElements().remove(sfa);
                        HibernateUtil.getCurrentSession().save(ela);
                    });
                }
                
                HibernateUtil.getCurrentSession().delete(sfa);
                HibernateUtil.commitTransaction(HibernateUtil.getCurrentSession());
                selectedItem = null;                
                refresh();
            }
        });
        miDelete.setVisible(false);
        
        tp.addNode(tpbLevelUp);
        tp.addSeparator();
        tp.addNode(menuBtnAlbum);
        
        refresh();

        this.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        this.setHbarPolicy(ScrollBarPolicy.NEVER);
        this.setContent(rootContainer);
        this.setFitToWidth(true);
        this.setFitToHeight(false);
    }
    
    public final void refresh() {
        if (currentDir == null) 
            tpbLevelUp.setVisible(false); 
        else 
            tpbLevelUp.setVisible(true);
        
        miDelete.setVisible(false);
        miEditDir.setVisible(false);
        
        rootContainer.getChildren().clear();
        List<DirectoryField> listEl = HibernateUtil.getCurrentSession()
                .createCriteria(DirectoryField.class)
                .add(Restrictions.eq("GID", (currentDir == null) ? 0 : currentDir.getID()))
                .list();
        if (listEl != null) 
            listEl.forEach(element -> {
                final MeWRecordsListFolder el = new MeWRecordsListFolder(element, this);
                list.add(el);
                rootContainer.getChildren().add(el);
            });
        
        if (currentDir == null) {
            final List<StringField> list1 = HibernateUtil.getCurrentSession()
                    .createQuery("FROM StringField r WHERE r.directories IS EMPTY")
                    .list();
            if (!list1.isEmpty()) {
                list1.forEach(c -> {
                        final MeWRecordsListString item1 = new MeWRecordsListString(null, c, this);
                        list.add(item1);
                        rootContainer.getChildren().add(item1);
                    });
            }
        } else {
            final DirectoryField df1 = (DirectoryField) HibernateUtil.getCurrentSession()
                    .createCriteria(DirectoryField.class)
                    .add(Restrictions.eq("ID", (currentDir == null) ? 0 : currentDir.getID()))
                    .uniqueResult();
            if (df1 != null) {
                if (!df1.getStringElements().isEmpty()) {
                    df1.getStringElements().forEach(c -> {
                        final MeWRecordsListString item1 = new MeWRecordsListString(df1, c, this);
                        list.add(item1);
                        rootContainer.getChildren().add(item1);
                    });
                }
            }
        }
        
        //rootContainer.getChildren().addAll(new ExtendedStringFieldElement("arwehetbndfbgb WEg", "", ""));
        
    }
    
    @Override
    public Parent getPanel() {
        return tp;
    }
    
    @Override
    public Parent getPaginator() {
        return nullPaginator;
    }
    
    @Override
    public Parent getMain() {
        return thisClass;
    }
    
    public static MeWRecordsList getInstance() {
        if (thisClass == null) thisClass = new MeWRecordsList();
        return thisClass;
    }

    @Override
    public void OnClick(DirectoryField data, MeWSelectable element) {
        list.forEach(el -> {
            el.setSelected(false);
        });
        element.setSelected(true);
        selectedItem = element;
        
        if (element instanceof MeWRecordsListFolder) {
            miDelete.setVisible(false);
            miEditDir.setVisible(true);
        } else if (element instanceof MeWRecordsListString) {
            miDelete.setVisible(true);
            miEditDir.setVisible(false);
        }
    }

    @Override
    public void OnDoubleClick(DirectoryField data, MeWSelectable element) {
        if (element instanceof MeWRecordsListFolder) {
            currentDir = data;
            refresh();
        }
    }
}
