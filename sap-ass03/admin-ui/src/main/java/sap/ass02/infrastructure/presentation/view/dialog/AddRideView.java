package sap.ass02.infrastructure.presentation.view.dialog;

import sap.ass02.infrastructure.presentation.listener.item.ride.AddReachUserListener;
import sap.ass02.infrastructure.presentation.listener.item.ride.AddRideStartListener;
import sap.ass02.infrastructure.presentation.listener.item.ride.AddRideStopListener;
import sap.ass02.infrastructure.presentation.view.AppView;
import sap.ass02.infrastructure.presentation.view.VisualiserPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * Adapted from AddEBikeDialog
 */
public final class AddRideView extends JDialog implements AppView {
    private JTextField userIdField, eBikeIdField, errorField;
    private JLabel rideInfoLabel;
    private JButton startRide;
    private JButton stopRide;
    private JButton bikeReachUser;
    
    /**
     * Creates a new AddRideView
     */
    public AddRideView() {
        this.initializeComponents();
        this.setupLayout();
        this.pack();
    }
    
    /**
     * Initializes the components
     */
    private void initializeComponents() {
        this.userIdField = new JTextField(15);
        this.eBikeIdField = new JTextField(15);
        this.errorField = new JTextField(25);
        this.startRide = new JButton("Start ride");
        this.stopRide = new JButton("Stop ride");
        this.bikeReachUser = new JButton("Reach user");
        this.rideInfoLabel = new JLabel("No ride started");
    }
    
    /**
     * Sets up the layout
     */
    private void setupLayout() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        inputPanel.add(new JLabel("user ID:"));
        inputPanel.add(this.userIdField);
        
        inputPanel.add(new JLabel("eBike ID:"));
        inputPanel.add(this.eBikeIdField);
        
        inputPanel.add(this.rideInfoLabel);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(this.startRide);
        buttonPanel.add(this.stopRide);
        buttonPanel.add(this.bikeReachUser);
        
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
        if (this.eBikeIdField.getText().isEmpty()) {
            this.errorField.setText("Please enter eBike ID");
            return null;
        }
        return this.eBikeIdField.getText();
    }
    
    /**
     * Gets the user ID
     *
     * @return the user ID
     */
    public String getUserId() {
        if (this.userIdField.getText().isEmpty()) {
            this.errorField.setText("Please enter user ID");
            return null;
        }
        return this.userIdField.getText();
    }
    
    /**
     * Sets the ride ID
     *
     * @param rideId the ride ID
     */
    public void setRideId(String rideId) {
        this.rideInfoLabel.setText(rideId);
    }
    
    /**
     * Gets the ride ID
     *
     * @return the ride ID
     */
    public String getRideId() {
        return this.rideInfoLabel.getText();
    }
    
    /**
     * Adds a start ride button listener
     *
     * @param addRideStartListener the listener
     */
    public void addRideStartButtonListener(AddRideStartListener addRideStartListener) {
        this.startRide.addActionListener(addRideStartListener);
    }
    
    /**
     * Adds a stop ride button listener
     *
     * @param addRideStopListener the listener
     */
    public void addRideStopButtonListener(AddRideStopListener addRideStopListener) {
        this.stopRide.addActionListener(addRideStopListener);
    }
    
    /**
     * Adds a reach user button listener
     *
     * @param addReachUserListener the listener
     */
    public void addReachUserListener(AddReachUserListener addReachUserListener) {
        this.bikeReachUser.addActionListener(addReachUserListener);
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
        this.setTitle("Add ride");
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
     * @param s the effect
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
