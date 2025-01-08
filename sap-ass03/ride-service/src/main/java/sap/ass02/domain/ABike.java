package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.utils.JsonFieldKey;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class ABike extends AbstractBike {
    private static final Logger LOGGER = LogManager.getLogger(ABike.class);
    private static final int PERIOD = 50;
    private final double perceptionRadius = 101;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture handler;
    
    public ABike(String id) {
        super(id);
    }

    public ABike(String id, BikeState state, P2d location, V2d direction, double speed, int batteryLevel) {
        super(id, state, location, direction, speed, batteryLevel);
    }
    
    public ABike(JsonObject asJsonObject) {
        this(
                asJsonObject.getString(JsonFieldKey.ABIKE_ID_KEY),
                BikeState.valueOf(asJsonObject.getString(JsonFieldKey.ABIKE_STATE_KEY)),
                new P2d(asJsonObject.getDouble(JsonFieldKey.ABIKE_X_LOCATION_KEY), asJsonObject.getDouble(JsonFieldKey.ABIKE_Y_LOCATION_KEY)),
                new V2d(asJsonObject.getDouble(JsonFieldKey.ABIKE_X_DIRECTION_KEY), asJsonObject.getDouble(JsonFieldKey.ABIKE_Y_DIRECTION_KEY)),
                asJsonObject.getDouble(JsonFieldKey.ABIKE_SPEED_KEY),
                asJsonObject.getInteger(JsonFieldKey.ABIKE_BATTERY_KEY)
        );
    }
    
    public void start() {
        this.handler = this.scheduler.scheduleAtFixedRate(() -> {
            try {
                this.loop();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 0, PERIOD, TimeUnit.MILLISECONDS);
    }
    
    private void loop() {
        switch (this.state) {
            case MAINTENANCE:
                Station station = this.evaluateNearestStation();
                this.changeDirectionTowards(station);
                LOGGER.trace("current agent location: {}", this.location);
                break;
            case MOVING_TO_STATION:
                this.stepForward();
                
                // evaluates if a new nearest station is closer than the current one
                Station newStation = this.evaluateNearestStation();
                this.changeDirectionTowards(newStation);
                
                this.evaluateAgentPositionRelativeTo(newStation);
                LOGGER.trace("current agent location: {}", this.location);
                break;
            case AT_STATION:
                LOGGER.trace("current agent location: {}", this.location);
                this.stop();
                break;
        }
    }
    
    private void evaluateAgentPositionRelativeTo(Place place) {
        if (this.location.getX() == place.location().getX() && this.location.getY() == place.location().getY()) {
            if (place instanceof Station) {
                this.state = BikeState.AT_STATION;
            }
        }
    }
    
    private void stepForward() {
        this.location = new P2d(this.location.getX() + this.direction.x(), this.location.getY() + this.direction.y());
    }
    
    private void changeDirectionTowards(Place place) {
        this.direction = new V2d(place.location().getX() - this.location.getX(), place.location().getY() - this.location.getY());
        this.updateDirection(this.direction);
        if (place instanceof Station) {
            this.state = BikeState.MOVING_TO_STATION;
        }
    }
    
    public void stop() {
        this.handler.cancel(true);
    }
    
    private long getTime() {
        return System.currentTimeMillis();
    }
    
    private Station evaluateNearestStation() {
        Iterable<Station> stations = Environment.getStations();
        Station nearestStation = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Station station : stations) {
            double distance = Math.sqrt(Math.pow(this.location.getX() - station.location().getX(), 2) + Math.pow(this.location.getY() - station.location().getY(), 2));
            if (distance < minDistance && distance <= this.perceptionRadius) {
                minDistance = distance;
                nearestStation = station;
            }
        }
        
        return nearestStation;
    }
}
