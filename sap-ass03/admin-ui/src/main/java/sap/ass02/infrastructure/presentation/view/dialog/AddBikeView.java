package sap.ass02.infrastructure.presentation.view.dialog;

import sap.ass02.infrastructure.presentation.listener.item.ebike.AddEBikeCancelListener;
import sap.ass02.infrastructure.presentation.listener.item.ebike.AddBikeOkListener;
import sap.ass02.infrastructure.presentation.view.AppView;
import sap.ass02.infrastructure.presentation.view.VisualiserPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Adapted from AddEBikeDialog
 */
public final class AddBikeView extends JDialog implements AppView {
    private JTextField idField, errorField;
    private JCheckBox asAgentBike;
    private JButton okButton;
    private JButton cancelButton;
    
    /**
     * Creates a new AddBikeView
     */
    public AddBikeView() {
        this.initializeComponents();
        this.setupLayout();
        this.pack();
    }
    
    /**
     * Initializes the components
     */
    private void initializeComponents() {
        this.idField = new JTextField(15);
        this.errorField = new JTextField(25);
        this.asAgentBike = new JCheckBox();
        this.okButton = new JButton("Ok");
        this.cancelButton = new JButton("Cancel");
    }
    
    /**
     * Sets up the layout
     */
    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("eBike ID:"));
        inputPanel.add(this.idField);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.okButton);
        buttonPanel.add(this.cancelButton);
        
        JPanel asAgentBike = new JPanel();
        asAgentBike.add(new JLabel("As Agent Bike:"));
        asAgentBike.add(this.asAgentBike);
        
        // JPanel errorPanel = new JPanel();
        // errorPanel.add(errorField);
        
        this.setLayout(new BorderLayout(10, 10));
        this.add(inputPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        // add(errorPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Gets the eBike ID
     *
     * @return the eBike ID
     */
    public String getEBikeId() {
        if (this.idField.getText().isEmpty()) {
            this.errorField.setText("Please enter eBike ID");
            return null;
        }
        return this.idField.getText();
    }
    
    public boolean isAgentBike() {
        return this.asAgentBike.isSelected();
    }
    
    /**
     * Adds an OK button listener
     *
     * @param addBikeOkListener the listener to add
     */
    public void addOkButtonListener(AddBikeOkListener addBikeOkListener) {
        this.okButton.addActionListener(addBikeOkListener);
    }
    
    /**
     * Adds a cancel button listener
     *
     * @param addEBikeCancelListener the listener to add
     */
    public void addCancelButtonListener(AddEBikeCancelListener addEBikeCancelListener) {
        this.cancelButton.addActionListener(addEBikeCancelListener);
    }
    
    /**
     * closes the dialog
     */
    public void closeDialog() {
        this.setVisible(false);
    }
    
    /**
     * displays the dialog
     */
    @Override
    public void display() {
        this.setVisible(true);
    }
    
    /**
     * Sets up the dialog
     */
    @Override
    public void setup() {
        this.setTitle("Add eBike");
        this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
    }
    
    /**
     * Refreshes the dialog
     */
    @Override
    public void refresh() {
        this.errorField.setText("");
    }
    
    /**
     * Adds a new effect
     *
     * @param s the effect to add
     */
    @Override
    public void addNewEffect(String s) {
    
    }
    
    /**
     * Gets the visualizer panel
     *
     * @return the visualizer panel
     */
    @Override
    public Optional<VisualiserPanel> getVisualizerPanel() {
        return Optional.empty();
    }
}
