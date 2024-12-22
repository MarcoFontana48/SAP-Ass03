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

public final class AdminConsoleView implements AdminView {
    private final Map<String, User> users = new HashMap<>();
    private final Map<String, EBike> ebikes = new HashMap<>();
    
    private void logMessage(String message) {
        System.out.println("[AdminConsoleView]: " + message);
    }
    
    @Override
    public void display() {
        this.logMessage("displaying view");
    }
    
    @Override
    public void setup() {
        this.logMessage("setting up view");
    }
    
    @Override
    public void addEBikeToShow(EBike ebike) {
        this.ebikes.put(ebike.getId(), ebike);
    }
    
    @Override
    public void addUserToShow(User user) {
        this.users.put(user.getId(), user);
    }
    
    @Override
    public void refresh() {
        this.logMessage("current state:\n" + "global users:" + this.users + "\nglobal eBikes:" + this.ebikes);
    }
    
    @Override
    public void addNewEffect(String s) {
    
    }
    
    @Override
    public Optional<VisualiserPanel> getVisualizerPanel() {
        return Optional.empty();
    }
    
    @Override
    public void addAddEBikeListener(AdminAddEBikeListener listener) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void openAddEBikeDialog() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public AddEBikeView getAddEBikeDialog() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addAddUserListener(AdminAddUserListener listener) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void openAddUserDialog() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public AddUserView getAddUserDialog() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addStartRideEBikeListener(AdminStartRideListener listener) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void openStartRideDialog() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public AddRideView getAddRideDialog() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void addPluginListener(AddPluginListener<AdminView> listener) {
        throw new UnsupportedOperationException();
    }
}
