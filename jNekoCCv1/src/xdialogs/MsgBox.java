package xdialogs;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.ImageIcon;
import javax.swing.JDialog;

public class MsgBox extends JDialog  {
    public final static int
            BUTTON_NO_CANCEL    = 0,
            BUTTON_YES          = 1;
    
    private int Result = 0;
    
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private xlist.XList xList1;
    
    private final ImageIcon groupIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_16x16/a2e.png"));
    private final ImageIcon YesNoIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/MessageBoxYesNo.png"));
    private final ImageIcon OKIcon = new javax.swing.ImageIcon(getClass().getResource("/icons_32x32/MessageBox_OK.png"));
    
    @SuppressWarnings("Convert2Lambda")
    private final MsgBoxButtonsListener list = new MsgBoxButtonsListener() {
        @Override
        public void ButtonClick(int button) {
            Result = BUTTON_NO_CANCEL;
            switch (button) {
                case 0:
                    MsgBox.this.dispose();
                    break;
                case 1:
                    Result = BUTTON_YES;
                    MsgBox.this.dispose();
                    break;
            }
        }
    };
    
    public int GetResult() {
        return Result;
    }
    
    private void CreateComponents(String title) {
        this.setTitle(title);
        this.setModal(true);
        
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        xList1 = new xlist.XList();
        jPanel1 = new javax.swing.JPanel();

        jPanel1.setBackground(new java.awt.Color(64, 64, 127));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-right.png"))); // NOI18N
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new-logo-left.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 350, Short.MAX_VALUE)
                .addComponent(jLabel1))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(xList1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE)
                .addContainerGap())
        );
    }
    
    private void _post() {
        this.setAlwaysOnTop(true);
        this.setResizable(false); 
        this.getContentPane().add(jPanel1);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.pack(); 
        Dimension s = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds((s.width/2)-(this.getWidth()/2), (s.height/2)-(this.getHeight()/2), this.getWidth(), this.getHeight());
        this.setVisible(true);
    }
    
    public MsgBox(String title, String message, int type) {
        CreateComponents(title);
        xList1.setBackgroundColor(jPanel1.getBackground()); 
        xList1.AddGroupHeader("MSGBOX", jPanel1.getBackground().darker(), title, groupIcon, 0);
        switch (type) {
            case 0:
                xList1.AddItemForSimpleList("NOITEMS", "MSGBOX", message, OKIcon);
                xList1.AddItem("OKBTN", "MSGBOX", new MsgBoxElementOK(list));
                break;
            case 1:
                xList1.AddItemForSimpleList("NOITEMS", "MSGBOX", message, YesNoIcon);
                xList1.AddItem("OKBTN", "MSGBOX", new MsgBoxElementYesNo(list));
                break;
        }
        
        xList1.Commit();
        _post();
    }

    public static void Show(String title, String message) {
        MsgBox jn = new MsgBox(title, message, 0);
    }
    
    public static int ShowYesNo(String title, String message) {
        MsgBox jn = new MsgBox(title, message, 1);
        return jn.GetResult();
    }
}
