package jnekotabs;

import connections.JNekoSPBackground;
import datasource.JNekoServerInfo;
import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import jneko.JNekoProgressDisplayClass;
import xlist.GlobalSearchFormElement;
import xlist.XListSearchFormActionListener;
import xlistcustomelements.MA4000NewONTElement;

public class UnconfuguredONTs extends javax.swing.JPanel {
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private final UnconfuguredONTs THIS = this;
    private final Color color;
    
    @SuppressWarnings("Convert2Lambda")
    private final XListSearchFormActionListener SearchListener = new XListSearchFormActionListener() {
        @Override
        public void OnButtonClick(String GID, String ID, int buttonID, String searchString) {
            if (buttonID == 1777) {
                Thread t = new Thread(() -> {
                    JNekoProgressDisplayClass.On();
                    JNekoSPBackground.jNekoServerInfo.GetUnconfigureONTs();
                    
                    try {
                        SwingUtilities.invokeAndWait(new Runnable(){
                            @Override
                            public void run(){
                                Refresh();
                            }
                        });
                    } catch (InterruptedException | InvocationTargetException ex) { }
                    
                    JNekoProgressDisplayClass.Off();
                });
                t.start();
            }
            
            if ((buttonID == 1005) || (buttonID == 1007)) {
                if (searchString.length() > 0) xlist1.Search("NEWONTS", searchString); else xlist1.Search("NEWONTS", null);
            }
        }
    };

    public UnconfuguredONTs(Color c) {
        initComponents();
        color = c;
        xlist1.VPBON();
        xlist1.setBackgroundColor(c); 
        Refresh();
    }
    
    public final void Refresh() {
        xlist1.ClearAll();
        
        xlist1.AddGroupHeader("SLOTSEL", color, "Поиск", groupIcon, 0);
        xlist1.AddItem("SF1", "SLOTSEL", new GlobalSearchFormElement(false, "SF1", "SEARCHBOX", SearchListener), "", 0);
            
        xlist1.AddGroupHeader("NEWONTS", color, "Список незарегистрированных ONT", groupIcon, 0);
        xlist1.Commit(true);
        
        if (JNekoSPBackground.jNekoServerInfo.UnconfONTsInfo.size() > 0) {
            for (JNekoServerInfo.UnconfONTInfo oi : JNekoSPBackground.jNekoServerInfo.UnconfONTsInfo) {
                xlist1.AddItem(oi.ELTX, "NEWONTS", new MA4000NewONTElement(oi.ELTX, oi.SLOT, oi.CHANNEL, THIS), "", 0);
            }
            xlist1.Commit(true);
        } else {
            xlist1.AddItemForSimpleList("NOITEMS", "NEWONTS", "Нет активных новых устройств.", groupIcon);
            xlist1.Commit(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        xlist1 = new xlist.XList();

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(xlist1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private xlist.XList xlist1;
    // End of variables declaration//GEN-END:variables
}
