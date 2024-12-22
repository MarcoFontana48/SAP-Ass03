package sap.ass02.infrastructure.presentation.view.dialog;

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
    
    public AddRideView() {
        this.initializeComponents();
        this.setupLayout();
        this.pack();
    }
    
    private void initializeComponents() {
        this.userIdField = new JTextField(15);
        this.eBikeIdField = new JTextField(15);
        this.errorField = new JTextField(25);
        this.startRide = new JButton("Start ride");
        this.stopRide = new JButton("Stop ride");
        this.rideInfoLabel = new JLabel("No ride started");
    }
    
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
        
        // JPanel errorPanel = new JPanel();
        // errorPanel.add(errorField);
        
        this.setLayout(new BorderLayout(10, 10));
        this.add(inputPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        // add(errorPanel, BorderLayout.SOUTH);
    }
    
    public String getEBikeId() {
        if (this.eBikeIdField.getText().isEmpty()) {
            this.errorField.setText("Please enter eBike ID");
            return null;
        }
        return this.eBikeIdField.getText();
    }
    
    public String getUserId() {
        if (this.userIdField.getText().isEmpty()) {
            this.errorField.setText("Please enter user ID");
            return null;
        }
        return this.userIdField.getText();
    }
    
    public void setRideId(String rideId) {
        this.rideInfoLabel.setText(rideId);
    }
    
    public String getRideId() {
        return this.rideInfoLabel.getText();
    }
    
    public void addRideStartButtonListener(AddRideStartListener addRideStartListener) {
        this.startRide.addActionListener(addRideStartListener);
    }
    
    public void addRideStopButtonListener(AddRideStopListener addRideStopListener) {
        this.stopRide.addActionListener(addRideStopListener);
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
        this.setTitle("Add ride");
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
