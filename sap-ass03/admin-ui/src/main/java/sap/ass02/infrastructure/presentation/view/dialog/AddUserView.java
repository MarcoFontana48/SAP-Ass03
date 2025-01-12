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
    private JTextField xLocationField;
    private JTextField yLocationField;
    private JButton okButton;
    private JButton cancelButton;
    
    /**
     * Creates a new dialog
     */
    public AddUserView() {
        this.initializeComponents();
        this.setupLayout();
        this.pack();
    }
    
    /**
     * Initializes the components
     */
    private void initializeComponents() {
        this.idField = new JTextField(15);
        this.creditsField = new JTextField(15);
        this.xLocationField = new JTextField(25);
        this.yLocationField = new JTextField(25);
        this.errorField = new JTextField(25);
        this.okButton = new JButton("Ok");
        this.cancelButton = new JButton("Cancel");
    }
    
    /**
     * Sets up the layout
     */
    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("User ID:"));
        inputPanel.add(this.idField);
        inputPanel.add(new JLabel("Credits:"));
        inputPanel.add(this.creditsField);
        inputPanel.add(new JLabel("X Location:"));
        inputPanel.add(this.xLocationField);
        inputPanel.add(new JLabel("Y Location:"));
        inputPanel.add(this.yLocationField);
        
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
    
    /**
     * Gets the user ID
     *
     * @return the user ID
     */
    public String getUserId() {
        if (this.idField.getText().isEmpty()) {
            this.errorField.setText("Please enter user ID");
            return null;
        }
        return this.idField.getText();
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
    
    /**
     * Gets the credits
     *
     * @return the credits
     */
    public int getCredits() {
        if (this.creditsField.getText().isEmpty()) {
            this.errorField.setText("Please enter credits");
            return -1;
        }
        return Integer.parseInt(this.creditsField.getText());
    }
    
    public int getUserXLocation() {
        if (this.xLocationField.getText().isEmpty()) {
            this.errorField.setText("Please enter location");
            return -1;
        }
        return Integer.parseInt(this.xLocationField.getText());
    }
    
    public int getUserYLocation() {
        if (this.yLocationField.getText().isEmpty()) {
            this.errorField.setText("Please enter location");
            return -1;
        }
        return Integer.parseInt(this.yLocationField.getText());
    }
    
    /**
     * Adds an OK button listener
     *
     * @param addUserOkListener the listener to add
     */
    public void addOkButtonListener(AddUserOkListener addUserOkListener) {
        this.okButton.addActionListener(addUserOkListener);
    }
    
    /**
     * Adds a cancel button listener
     *
     * @param addUserCancelListener the listener to add
     */
    public void addCancelButtonListener(AddUserCancelListener addUserCancelListener) {
        this.cancelButton.addActionListener(addUserCancelListener);
    }
    
    /**
     * Closes the dialog
     */
    public void closeDialog() {
        this.setVisible(false);
    }
    
    /**
     * Displays the dialog
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
        this.setTitle("Add user");
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
