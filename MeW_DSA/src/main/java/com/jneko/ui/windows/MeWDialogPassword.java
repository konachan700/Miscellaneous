package com.jneko.ui.windows;

import com.jneko.ui.controls.LabelElement;
import com.jneko.ui.controls.PasswordFieldElement;
import com.jneko.ui.toppanel.TopPanel;
import com.jneko.ui.toppanel.TopPanelButton;

public class MeWDialogPassword extends MeWDialog {
    private final LabelElement 
            titleLabel = new LabelElement("Please, enter password."),
            errorLabel = new LabelElement("");
    
    private final PasswordFieldElement
            password = new PasswordFieldElement("Password");
    
    private boolean
            exit = false;
    
    private final TopPanelButton
            tpbOK = new TopPanelButton("iconSave", "Create/Save changes", c -> {
                exit = false;
                this.hide();
            }),
            tpbClose = new TopPanelButton("iconCancel", "Create/Save changes", c -> {
                exit = true;
                this.hide();
            });
    
    private final TopPanel 
            tp = new TopPanel();
    
    public MeWDialogPassword() {
        super(550, 200, "Enter password - JNeko MeW DSA", "/styles/Styles.css");
        super.getMainBox().getChildren().addAll(
                titleLabel,
                password,
                errorLabel
        );
            
        tp.addNodes(tpbOK, tpbClose);
        super.getHeaderBox().getChildren().addAll(
                tp
        );
        
        super.setResizable(false);
        super.setOnCloseRequest(c -> {
            c.consume();
        });
    }
    
    public boolean isExit() {
        return exit;
    }
    
    public String getPassword() {
        return password.getText();
    }
    
    public void displayError(String value) {
        errorLabel.setText(value);
    }
}
