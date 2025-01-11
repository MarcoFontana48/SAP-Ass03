package sap.ass02.infrastructure.presentation.view.admin;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.*;
import sap.ass02.infrastructure.presentation.listener.admin.AdminAddEBikeListener;
import sap.ass02.infrastructure.presentation.listener.admin.AdminAddUserListener;
import sap.ass02.infrastructure.presentation.listener.admin.AdminStartRideListener;
import sap.ass02.infrastructure.presentation.listener.item.plugin.AddPluginListener;
import sap.ass02.infrastructure.presentation.view.AbstractGUIView;
import sap.ass02.infrastructure.presentation.view.VisualiserPanel;
import sap.ass02.infrastructure.presentation.view.dialog.AddBikeView;
import sap.ass02.infrastructure.presentation.view.dialog.AddRideView;
import sap.ass02.infrastructure.presentation.view.dialog.AddUserView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The admin GUI view
 */
public final class AdminGUIView extends AbstractGUIView<AdminView> implements AdminView {
    private static final Logger LOGGER = LogManager.getLogger(AdminGUIView.class);
    private static final int WIDTH = 800;
    private static final int HEIGHT = 500;
    private final JButton addUserButton = new JButton("Add User");
    private final JButton addEBikeButton = new JButton("Add EBike");
    private final JButton startRideButton = new JButton("Start Ride");
    private final AddUserView addUserDialog = new AddUserView();
    private final AddBikeView addEBikeDialog = new AddBikeView();
    private AdminVisualiserPanel centralPanel;
    private final AddRideView addRideDialog = new AddRideView();
    private final JPanel topPanel = new JPanel();
    
    /**
     * displays a new Admin GUI view.
     */
    @Override
    public void display() {
        LOGGER.trace("About to display user GUI: '{}'", this.getClass().getSimpleName());
        this.setVisible(true);
        LOGGER.debug("Now showing user GUI: '{}'", this.getClass().getSimpleName());
    }
    
    /**
     * opens the add user dialog.
     */
    @Override
    public void openAddUserDialog() {
        LOGGER.trace("Opening add user dialog");
        this.addUserDialog.setVisible(true);
    }
    
    /**
     * opens the add ebike dialog.
     */
    @Override
    public void openAddEBikeDialog() {
        LOGGER.trace("Opening add ebike dialog");
        this.addEBikeDialog.setVisible(true);
    }
    
    /**
     * opens the start ride dialog.
     */
    @Override
    public void openStartRideDialog() {
        LOGGER.trace("Opening start ride dialog");
        this.addRideDialog.setVisible(true);
    }
    
    /**
     * gets the add user dialog.
     */
    @Override
    public AddUserView getAddUserDialog() {
        return this.addUserDialog;
    }
    
    /**
     * gets the add ebike dialog.
     */
    @Override
    public AddBikeView getAddEBikeDialog() {
        return this.addEBikeDialog;
    }
    
    /**
     * gets the add ride dialog.
     */
    @Override
    public AddRideView getAddRideDialog() {
        return this.addRideDialog;
    }
    
    /**
     * adds an add user listener.
     *
     * @param listener the listener
     */
    @Override
    public void addAddUserListener(final AdminAddUserListener listener) {
        this.addUserButton.addActionListener(listener);
    }
    
    /**
     * adds an add ebike listener.
     *
     * @param adminAddEBikeListener the listener
     */
    @Override
    public void addAddEBikeListener(final AdminAddEBikeListener adminAddEBikeListener) {
        this.addEBikeButton.addActionListener(adminAddEBikeListener);
    }
    
    /**
     * adds a start ride listener.
     *
     * @param listener the listener
     */
    @Override
    public void addStartRideEBikeListener(final AdminStartRideListener listener) {
        this.startRideButton.addActionListener(listener);
    }
    
    /**
     * adds a plugin listener.
     *
     * @param listener the listener
     */
    @Override
    public void addPluginListener(AddPluginListener<AdminView> listener) {
        this.addPluginButton.addActionListener(listener);
    }
    
    /**
     * sets up the admin GUI view.
     */
    @Override
    public void setup() {
        LOGGER.trace("'{}' setup started", this.getClass().getSimpleName());
        
        this.setTitle("EBike App");
        this.setSize(WIDTH, HEIGHT);
        this.setResizable(false);
        
        this.setLayout(new BorderLayout());
        
        this.topPanel.add(this.addUserButton);
        this.topPanel.add(this.addEBikeButton);
        this.topPanel.add(this.startRideButton);
        this.topPanel.add(this.addPluginButton);
        this.add(this.topPanel, BorderLayout.NORTH);
        
        this.centralPanel = new AdminVisualiserPanel(WIDTH, HEIGHT);
        this.add(this.centralPanel, BorderLayout.CENTER);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        LOGGER.trace("'{}' setup completed", this.getClass().getSimpleName());
    }
    
    /**
     * adds an ebike to show.
     *
     * @param ebike the ebike
     */
    @Override
    public void addEBikeToShow(EBike ebike) {
        this.centralPanel.addBikeToShow(ebike);
    }
    
    /**
     * adds an abike to show.
     *
     * @param abike the ebike
     */
    @Override
    public void addABikeToShow(ABike abike) {
        this.centralPanel.addBikeToShow(abike);
    }
    
    /**
     * adds a user to show.
     *
     * @param user the user
     */
    @Override
    public void addUserToShow(User user) {
        this.centralPanel.addUserToShow(user);
    }
    
    /**
     * refreshes the view.
     */
    @Override
    public void refresh() {
        this.centralPanel.refresh();
    }
    
    /**
     * adds a new effect.
     *
     * @param s the effect
     */
    @Override
    public void addNewEffect(String s) {
        LOGGER.trace("Adding new effect: '{}'", s);
        JButton newEffectButton = new JButton(s);
        newEffectButton.addActionListener(e -> {
            LOGGER.trace("New effect button clicked: '{}'", s);
            this.centralPanel.applyEffect(s);
        });
        LOGGER.trace("Adding new effect button: '{}'", newEffectButton);
        this.topPanel.add(newEffectButton);
        LOGGER.trace("Revalidating top panel");
        this.topPanel.revalidate();
    }
    
    /**
     * gets the visualizer panel.
     */
    @Override
    public Optional<VisualiserPanel> getVisualizerPanel() {
        return Optional.ofNullable(this.centralPanel);
    }
    
    /**
     * class of the admin visualizer panel.
     */
    public static class AdminVisualiserPanel extends JPanel implements VisualiserPanel {
        private final Map<String, AbstractBike> bikes = new HashMap<>();
        private final Map<String, User> users = new HashMap<>();
        private final List<Station> stations = List.of(     //assuming the environment won't change over time and the stations are fixed
            new Station(new P2d(100, -100)),
            new Station(new P2d(-100, -100)),
            new Station(new P2d(100, 100)),
            new Station(new P2d(-100, 100))
        );
        private final long dx;
        private final long dy;
        private Color ebikesAvailableColor = Color.BLACK;
        private Color ebikesIsUseColor = Color.BLACK;
        private Color ebikesMantainanceColor = Color.BLACK;
        private Color usersOutOfCreditsColor = Color.BLACK;
        private Color usersDefaultColor = Color.BLACK;
        
        /**
         * creates a new admin visualizer panel.
         *
         * @param w the width
         * @param h the height
         */
        public AdminVisualiserPanel(int w, int h) {
            this.setSize(w, h);
            this.dx = w / 2 - 20;
            this.dy = h / 2 - 20;
        }
        
        /**
         * paints the panel.
         *
         * @param g the graphics
         */
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.clearRect(0, 0, this.getWidth(), this.getHeight());
            
            if (this.bikes != null) {
                for (AbstractBike b : this.bikes.values()) {
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

            if (this.users != null) {
                var y = 20;
                g2.setColor(Color.BLACK);
                for (User u : this.users.values()) {
                    
                    // Set color based on credit
                    if (u.getCredit() <= 0) {
                        g2.setColor(this.usersOutOfCreditsColor);
                    } else {
                        g2.setColor(this.usersDefaultColor);
                    }
                    
                    g2.drawRect(10, y, 20, 20);
                    g2.drawString(u.getId() + " - credit: " + u.getCredit() + " - location: (" + u.getXLocation() + "," + u.getYLocation() + ")", 35, y + 15);
                    y += 25;
                }
            }
            
            if (this.stations != null) {
                for (Station s : this.stations) {
                    var p = s.location();
                    int x0 = (int) (this.dx + p.getX());
                    int y0 = (int) (this.dy - p.getY());
                    g2.setColor(Color.RED);
                    g2.draw(new Rectangle(x0, y0, 10, 10));
                    g2.drawString("Station", x0, y0 + 35);
                    g2.drawString("(" + (int) p.getX() + "," + (int) p.getY() + ")", x0, y0 + 50);
                }
            }
        }
        
        /**
         * adds a bike to show.
         *
         * @param ebike the ebike
         */
        public void addBikeToShow(AbstractBike ebike) {
            this.bikes.put(ebike.getId(), ebike);
        }
        
        /**
         * adds a user to show.
         *
         * @param users the user
         */
        public void addUserToShow(User users) {
            this.users.put(users.getId(), users);
        }
        
        /**
         * refreshes the panel.
         */
        public void refresh() {
            this.repaint();
        }
        
        /**
         * applies an effect.
         *
         * @param pluginId the plugin id
         */
        @Override
        public void applyEffect(String pluginId) {
            LOGGER.trace("Applying effect: '{}'", pluginId);
            this.refresh();
        }
        
        /**
         * sets the available color of the bike.
         *
         * @param color the color
         */
        @Override
        public void setAvailableColorBike(Color color) {
            LOGGER.trace("Setting available ebike color to: '{}'", color);
            this.ebikesAvailableColor = color;
        }
        
        /**
         * sets the in use color of the bike.
         *
         * @param color the color
         */
        @Override
        public void setIsUseColorBike(Color color) {
            LOGGER.trace("Setting in use ebike color to: '{}'", color);
            this.ebikesIsUseColor = color;
        }
        
        /**
         * sets the maintenance color of the bike.
         *
         * @param color the color
         */
        @Override
        public void setMantainanceColorBike(Color color) {
            LOGGER.trace("Setting maintenance ebike color to: '{}'", color);
            this.ebikesMantainanceColor = color;
        }
        
        /**
         * sets the out of credits user color.
         *
         * @param color the color
         */
        @Override
        public void setOutOfCreditsUserColor(Color color) {
            LOGGER.trace("Setting idle user color to: '{}'", color);
            this.usersOutOfCreditsColor = color;
        }
        
        /**
         * sets the default user color.
         *
         * @param color the color
         */
        @Override
        public void setDefaultUserColor(Color color) {
            LOGGER.trace("Setting riding user color to: '{}'", color);
            this.usersDefaultColor = color;
        }
    }
}
