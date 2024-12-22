package sap.ass02.infrastructure.presentation.view.admin;

import sap.ass02.domain.EBike;
import sap.ass02.domain.User;
import sap.ass02.infrastructure.presentation.listener.admin.AdminAddEBikeListener;
import sap.ass02.infrastructure.presentation.listener.admin.AdminAddUserListener;
import sap.ass02.infrastructure.presentation.listener.admin.AdminStartRideListener;
import sap.ass02.infrastructure.presentation.listener.item.plugin.AddPluginListener;
import sap.ass02.infrastructure.presentation.view.VisualiserPanel;
import sap.ass02.infrastructure.presentation.view.dialog.AddEBikeView;
import sap.ass02.infrastructure.presentation.view.dialog.AddRideView;
import sap.ass02.infrastructure.presentation.view.dialog.AddUserView;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Admin view for the console
 */
public final class AdminConsoleView implements AdminView {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, EBike> ebikes = new HashMap<>();
    
    /**
     * Logs a message
     *
     * @param message the message to log
     */
    private void logMessage(String message) {
        System.out.println("[AdminConsoleView]: " + message);
    }
    
    /**
     * Displays the view
     */
    @Override
    public void display() {
        this.logMessage("displaying view");
    }
    
    /**
     * Sets up the view
     */
    @Override
    public void setup() {
        this.logMessage("setting up view");
    }
    
    /**
     * Adds an eBike to the view
     *
     * @param ebike the eBike to add
     */
    @Override
    public void addEBikeToShow(EBike ebike) {
        this.ebikes.put(ebike.getId(), ebike);
    }
    
    /**
     * Adds a user to the view
     *
     * @param user the user to add
     */
    @Override
    public void addUserToShow(User user) {
        this.users.put(user.getId(), user);
    }
    
    /**
     * refreshes the view
     */
    @Override
    public void refresh() {
        this.logMessage("current state:\n" + "global users:" + this.users + "\nglobal eBikes:" + this.ebikes);
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
    
    /**
     * Adds an ebike listener
     *
     * @param listener the listener to add
     */
    @Override
    public void addAddEBikeListener(AdminAddEBikeListener listener) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Opens the add ebike dialog
     */
    @Override
    public void openAddEBikeDialog() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Gets the add ebike dialog
     *
     * @return the add ebike dialog
     */
    @Override
    public AddEBikeView getAddEBikeDialog() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Adds a user listener
     *
     * @param listener the listener to add
     */
    @Override
    public void addAddUserListener(AdminAddUserListener listener) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Opens the add user dialog
     */
    @Override
    public void openAddUserDialog() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Gets the add user dialog
     *
     * @return the add user dialog
     */
    @Override
    public AddUserView getAddUserDialog() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Adds a start ride eBike listener
     *
     * @param listener the listener to add
     */
    @Override
    public void addStartRideEBikeListener(AdminStartRideListener listener) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Opens the start ride dialog
     */
    @Override
    public void openStartRideDialog() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Gets the add ride dialog
     *
     * @return the add ride dialog
     */
    @Override
    public AddRideView getAddRideDialog() {
        throw new UnsupportedOperationException();
    }
    
    /**
     * Adds a plugin listener
     *
     * @param listener the listener to add
     */
    @Override
    public void addPluginListener(AddPluginListener<AdminView> listener) {
        throw new UnsupportedOperationException();
    }
}
