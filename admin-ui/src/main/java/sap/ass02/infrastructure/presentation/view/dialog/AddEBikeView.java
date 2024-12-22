package sap.ass02.infrastructure.presentation.view.dialog;

import sap.ass02.infrastructure.presentation.listener.item.ebike.AddEBikeCancelListener;
import sap.ass02.infrastructure.presentation.listener.item.ebike.AddEBikeOkListener;
import sap.ass02.infrastructure.presentation.view.AppView;
import sap.ass02.infrastructure.presentation.view.VisualiserPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Adapted from AddEBikeDialog
 */
public final class AddEBikeView extends JDialog implements AppView {
    
    private JTextField idField, errorField;
    private JButton okButton;
    private JButton cancelButton;
    
    public AddEBikeView() {
        this.initializeComponents();
        this.setupLayout();
        this.pack();
    }
    
    private void initializeComponents() {
        this.idField = new JTextField(15);
        this.errorField = new JTextField(25);
        this.okButton = new JButton("Ok");
        this.cancelButton = new JButton("Cancel");
    }
    
    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.add(new JLabel("eBike ID:"));
        inputPanel.add(this.idField);
        
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
    
    public String getEBikeId() {
        if (this.idField.getText().isEmpty()) {
            this.errorField.setText("Please enter eBike ID");
            return null;
        }
        return this.idField.getText();
    }
    
    public void addOkButtonListener(AddEBikeOkListener addEBikeOkListener) {
        this.okButton.addActionListener(addEBikeOkListener);
    }
    
    public void addCancelButtonListener(AddEBikeCancelListener addEBikeCancelListener) {
        this.cancelButton.addActionListener(addEBikeCancelListener);
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
        this.setTitle("Add eBike");
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
