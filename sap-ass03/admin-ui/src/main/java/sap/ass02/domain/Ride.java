package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ass02.domain.dto.RideDTO;
import sap.ddd.Entity;

import java.sql.Date;
import java.util.Optional;

/**
 * Represents a ride of a user on an eBike
 */
public class Ride implements Entity<RideDTO> {
    private static final Logger LOGGER = LogManager.getLogger(Ride.class);
    private final User user;
    private final EBike ebike;
    private final String id;
    private Date startedDate;
    private Optional<Date> endDate;
    private boolean ongoing;
    
    /**
     * Creates a new ride
     *
     * @param id    the ride id
     * @param user  the user
     * @param ebike the ebike
     */
    public Ride(String id, User user, EBike ebike) {
        this.id = id;
        this.startedDate = new Date(new java.util.Date().getTime());   //! sql date trunks the time part of the date, leaving only 'YYYY-MM-DD'
        this.endDate = Optional.empty();
        this.user = user;
        this.ebike = ebike;
    }
    
    /**
     * Gets the ride id
     *
     * @return the ride id
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * Starts the ride
     */
    public void start() {
        this.ongoing = true;
    }
    
    /**
     *  Ends the ride
     */
    public void end() {
        LOGGER.trace("Ending ride simulation for ride '{}'...", this.id);
        this.endDate = Optional.of(new Date(new java.util.Date().getTime()));
        this.ongoing = false;
    }
    
    /**
     * Gets the date the ride started
     *
     * @return the date the ride started
     */
    public Date getStartedDate() {
        return this.startedDate;
    }
    
    /**
     * Sets the date the ride started
     *
     * @param date the date the ride started
     */
    public void setStartedDate(Date date) {
        this.startedDate = (Date) date.clone();
    }
    
    /**
     * Checks if the ride is ongoing
     *
     * @return true if the ride is ongoing, false otherwise
     */
    public boolean isOngoing() {
        return this.ongoing;
    }
    
    /**
     * Sets the ride as ongoing or not
     *
     * @param ongoing true if the ride is ongoing, false otherwise
     */
    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }
    
    /**
     * Gets the date the ride ended
     *
     * @return the date the ride ended
     */
    public Optional<Date> getEndDate() {
        return this.endDate;
    }
    
    /**
     * Sets the date the ride ended
     *
     * @param date the date the ride ended
     */
    public void setEndDate(Date date) {
        this.endDate = Optional.of(date);
    }
    
    /**
     * Sets the date the ride ended
     *
     * @param date the date the ride ended
     */
    public void setEndDate(Optional<Date> date) {
        this.endDate = date;
    }
    
    /**
     * Gets the user
     *
     * @return the user
     */
    public User getUser() {
        return this.user;
    }
    
    /**
     * Gets the ebike
     *
     * @return the ebike
     */
    public EBike getEBike() {
        return this.ebike;
    }
    
    /**
     * Converts the ride to a DTO
     *
     * @return the DTO
     */
    @Override
    public RideDTO toDTO() {
        return new RideDTO(this.startedDate, this.endDate, this.user.toDTO(), this.ebike.toDTO(), this.ongoing, this.id);
    }
    
    /**
     * Converts the ride to a JSON string
     *
     * @return the JSON string
     */
    @Override
    public String toJsonString() {
        JsonObject json = new JsonObject();
        json.put(JsonFieldKey.JSON_RIDE_ID_KEY, this.id)
                .put(JsonFieldKey.JSON_RIDE_USER_ID_KEY, this.user.getId())
                .put(JsonFieldKey.JSON_RIDE_EBIKE_ID_KEY, this.ebike.getId())
                .put(JsonFieldKey.JSON_RIDE_START_DATE_KEY, this.startedDate.toString())
                .put(JsonFieldKey.JSON_RIDE_ONGONING_KEY, this.ongoing)
                .put(JsonFieldKey.JSON_RIDE_END_DATE_KEY, this.endDate.toString());
        return json.encode();
    }
    
    @Override
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.put(JsonFieldKey.JSON_RIDE_ID_KEY, this.id)
                .put(JsonFieldKey.JSON_RIDE_USER_ID_KEY, this.user.toJsonObject())
                .put(JsonFieldKey.JSON_RIDE_EBIKE_ID_KEY, this.ebike.toJsonObject())
                .put(JsonFieldKey.JSON_RIDE_START_DATE_KEY, this.startedDate.toString())
                .put(JsonFieldKey.JSON_RIDE_ONGONING_KEY, this.ongoing)
                .put(JsonFieldKey.JSON_RIDE_END_DATE_KEY, this.endDate.toString());
        return json;
    }
    
    /**
     * Converts the ride to a string
     *
     * @return the string
     */
    public String toString() {
        return "{ id: " + this.id + ", user: " + this.user.getId() + ", bike: " + this.ebike.getId() + " }";
    }
}
