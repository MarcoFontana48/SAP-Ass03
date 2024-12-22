package sap.ass02.infrastructure.presentation.view.user;

import sap.ass02.domain.EBike;
import sap.ass02.infrastructure.presentation.listener.item.plugin.AddPluginListener;
import sap.ass02.infrastructure.presentation.listener.user.UserStartRideListener;
import sap.ass02.infrastructure.presentation.view.AbstractGUIView;
import sap.ass02.infrastructure.presentation.view.VisualiserPanel;
import sap.ass02.infrastructure.presentation.view.dialog.AddRideView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class UserGUIView extends AbstractGUIView<UserView> implements UserView {
    private static final Logger LOGGER = LogManager.getLogger(UserGUIView.class);
    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;
    private final JButton startRideButton = new JButton("Start Ride");
    private UserVisualiserPanel centralPanel;
    private final AddRideView addRideDialog = new AddRideView();
    private final JPanel topPanel = new JPanel();
    
    @Override
    public void display() {
        LOGGER.trace("About to display user GUI: '{}'", this.getClass().getSimpleName());
        this.setVisible(true);
        LOGGER.debug("Now showing user GUI: '{}'", this.getClass().getSimpleName());
    }
    
    @Override
    public void openStartRideDialog() {
        LOGGER.trace("Opening start ride dialog");
        this.addRideDialog.setVisible(true);
    }
    
    @Override
    public AddRideView getAddRideDialog() {
        return this.addRideDialog;
    }
    
    @Override
    public void addStartRideEBikeListener(final UserStartRideListener listener) {
        this.startRideButton.addActionListener(listener);
    }
    
    @Override
    public void setup() {
        LOGGER.trace("'{}' setup started", this.getClass().getSimpleName());
        
        this.setTitle("EBike App");
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        
        this.setLayout(new BorderLayout());
        
        this.topPanel.add(this.startRideButton);
        this.topPanel.add(this.addPluginButton);
        this.add(this.topPanel, BorderLayout.NORTH);
        
        this.centralPanel = new UserVisualiserPanel(WIDTH, HEIGHT);
        this.add(this.centralPanel, BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        LOGGER.trace("'{}' setup completed", this.getClass().getSimpleName());
    }
    
    @Override
    public void refresh() {
        this.centralPanel.refresh();
    }
    
    @Override
    public void addNewEffect(String s) {
        LOGGER.trace("Adding new effect: '{}'", s);
        JButton newEffectButton = new JButton("Apply effect");
        newEffectButton.addActionListener(e -> {
            LOGGER.trace("New effect button clicked: '{}'", s);
            this.centralPanel.applyEffect(s);
        });
        LOGGER.trace("Adding new effect button: '{}'", newEffectButton);
        this.topPanel.add(newEffectButton);
        LOGGER.trace("Revalidating top panel");
        this.topPanel.revalidate();
    }
    
    @Override
    public Optional<VisualiserPanel> getVisualizerPanel() {
        return Optional.ofNullable(this.centralPanel);
    }
    
    @Override
    public void addEBikeToShow(EBike ebike) {
        LOGGER.trace("Adding ebike to show: '{}'", ebike);
        this.centralPanel.addEBikeToShow(ebike);
    }
    
    @Override
    public void addPluginListener(AddPluginListener<UserView> listener) {
        this.addPluginButton.addActionListener(listener);
    }
    
    public static class UserVisualiserPanel extends JPanel implements VisualiserPanel {
        private final Map<String, EBike> ebikes = new HashMap<>();
        private final long dx;
        private final long dy;
        private Color ebikesAvailableColor = Color.BLACK;
        private Color ebikesIsUseColor = Color.BLACK;
        private Color ebikesMantainanceColor = Color.BLACK;
        private Color userOutOfCreditsColor = Color.BLACK;
        private Color userDefaultColor = Color.BLACK;
        
        public UserVisualiserPanel(int w, int h) {
            this.setSize(w, h);
            this.dx = w / 2 - 20;
            this.dy = h / 2 - 20;
        }
        
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0, 0, this.getWidth(), this.getHeight());
            
            if (this.ebikes != null) {
                for (EBike b : this.ebikes.values()) {
                    LOGGER.trace("Drawing ebike: '{}' : ", b, b.getId(), b.getState(), b.getLocation(), b.getDirection(), b.getSpeed(), b.getBatteryLevel());
                    var p = b.getLocation();
                    int x0 = (int) (this.dx + p.getX());
                    int y0 = (int) (this.dy - p.getY());
                    
                    // Set color based on bike state
                    switch (b.getState()) {
                        case AVAILABLE:
                            g2.setColor(this.ebikesAvailableColor);
                            break;
                        case IN_USE:
                            g2.setColor(this.ebikesIsUseColor);
                            break;
                        case MAINTENANCE:
                            g2.setColor(this.ebikesMantainanceColor);
                            break;
                        default:
                            g2.setColor(Color.BLACK);
                            break;
                    }
                    
                    g2.drawOval(x0, y0, 20, 20);
                    g2.drawString(b.getId(), x0, y0 + 35);
                    g2.drawString("(" + (int) p.getX() + "," + (int) p.getY() + ")", x0, y0 + 50);
                }
            }
        }
        
        public void addEBikeToShow(EBike ebike) {
            LOGGER.trace("Adding ebike to show: k={}, v={}", ebike.getId(), ebike);
            this.ebikes.put(ebike.getId(), ebike);
        }
        
        public void refresh() {
            this.repaint();
        }
        
        @Override
        public void applyEffect(String pluginId) {
            LOGGER.trace("Applying effect: '{}'", pluginId);
            this.refresh();
        }
        
        @Override
        public void setAvailableColorBike(Color color) {
            this.ebikesAvailableColor = color;
            
        }
        
        @Override
        public void setIsUseColorBike(Color color) {
            this.ebikesIsUseColor = color;
        }
        
        @Override
        public void setMantainanceColorBike(Color color) {
            this.ebikesMantainanceColor = color;
        }
        
        @Override
        public void setOutOfCreditsUserColor(Color color) {
            this.userOutOfCreditsColor = color;
        }
        
        @Override
        public void setDefaultUserColor(Color color) {
            this.userDefaultColor = color;
        }
    }
    
}
