package sap.ass02.infrastructure.presentation.view.dialog;

import sap.ass02.infrastructure.presentation.listener.item.user.AddUserCancelListener;
import sap.ass02.infrastructure.presentation.listener.item.user.AddUserOkListener;
import sap.ass02.infrastructure.presentation.view.AppView;
import sap.ass02.infrastructure.presentation.view.VisualiserPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Adapted from AddEBikeDialog
 */
public final class AddUserView extends JDialog implements AppView {
    private JTextField idField, errorField;
    private JTextField creditsField;
    private JButton okButton;
    private JButton cancelButton;
    
    public AddUserView() {
        this.initializeComponents();
        this.setupLayout();
        this.pack();
    }
    
    private void initializeComponents() {
        this.idField = new JTextField(15);
        this.creditsField = new JTextField(15);
        this.errorField = new JTextField(25);
        this.okButton = new JButton("Ok");
        this.cancelButton = new JButton("Cancel");
    }
    
    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("User ID:"));
        inputPanel.add(this.idField);
        inputPanel.add(new JLabel("Credits:"));
        inputPanel.add(this.creditsField);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.okButton);
        buttonPanel.add(this.cancelButton);
        
        // JPanel errorPanel = new JPanel();
        // errorPanel.add(errorField);
        
        this.setLayout(new BorderLayout(10, 10));
        this.add(inputPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        // add(errorPanel, BorderLayout.SOUTH);
    }
    
    public String getUserId() {
        if (this.idField.getText().isEmpty()) {
            this.errorField.setText("Please enter user ID");
            return null;
        }
        return this.idField.getText();
    }
    
    public String getEBikeId() {
        if (this.idField.getText().isEmpty()) {
            this.errorField.setText("Please enter eBike ID");
            return null;
        }
        return this.idField.getText();
    }
    
    public int getCredits() {
        if (this.creditsField.getText().isEmpty()) {
            this.errorField.setText("Please enter credits");
            return -1;
        }
        return Integer.parseInt(this.creditsField.getText());
    }
    
    public void addOkButtonListener(AddUserOkListener addUserOkListener) {
        this.okButton.addActionListener(addUserOkListener);
    }
    
    public void addCancelButtonListener(AddUserCancelListener addUserCancelListener) {
        this.cancelButton.addActionListener(addUserCancelListener);
    }
    
    public void closeDialog() {
        this.setVisible(false);
    }
    
    @Override
    public void display() {
        this.setVisible(true);
    }
    
    @Override
    public void setup() {
        this.setTitle("Add user");
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }
    
    @Override
    public void refresh() {
        this.errorField.setText("");
    }
    
    @Override
    public void addNewEffect(String s) {
    
    }
    
    @Override
    public Optional<VisualiserPanel> getVisualizerPanel() {
        return Optional.empty();
    }
}
