package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.RideDTO;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.Aggregate;

import java.sql.Date;
import java.util.Optional;

/**
 * Represents a ride.
 */
public final class Ride implements Aggregate<RideDTO> {
    private static final Logger LOGGER = LogManager.getLogger(Ride.class);
    private final User user;
    private final EBike bike;
    private final String id;
    private Date startedDate;
    private Optional<Date> endDate;
    private boolean ongoing;
    
    /**
     * Instantiates a new ride.
     * @param id The id of the ride.
     * @param user The user.
     * @param bike The bike.
     */
    public Ride(String id, User user, EBike bike) {
        this.id = id;
        this.startedDate = new Date(new java.util.Date().getTime());   //! sql date trunks the time part of the date, leaving only 'YYYY-MM-DD'
        this.endDate = Optional.empty();
        this.user = user;
        this.bike = bike;
    }
    
    /**
     * Instantiates a new ride from a JSON object.
     * @param asJsonObject The JSON object.
     */
    public Ride(JsonObject asJsonObject) {
        this(
                asJsonObject.getString(JsonFieldKey.RIDE_ID_KEY),
                new User(asJsonObject.getJsonObject(JsonFieldKey.RIDE_USER_KEY)),
                new EBikeImpl(asJsonObject.getJsonObject(JsonFieldKey.RIDE_EBIKE_KEY))
        );
    }
    
    /**
     * Gets the id of the ride.
     * @return The id of the ride.
     */
    public String getId() {
        return this.id;
    }
    
    /**
     * Starts the ride.
     */
    public void start() {
        this.ongoing = true;
    }
    
    /**
     * Ends the ride.
     */
    public void end() {
        LOGGER.trace("Ending ride simulation for ride '{}'...", this.id);
        this.endDate = Optional.of(new Date(new java.util.Date().getTime()));
        this.ongoing = false;
    }
    
    /**
     * Gets the started date.
     * @return The started date.
     */
    public Date getStartedDate() {
        return this.startedDate;
    }
    
    /**
     * Sets the started date.
     * @param date The date.
     */
    public void setStartedDate(Date date) {
        this.startedDate = (Date) date.clone();
    }
    
    /**
     * returns whether the ride is ongoing.
     *
     * @return true if the ride is ongoing, false otherwise
     */
    public boolean isOngoing() {
        return this.ongoing;
    }
    
    /**
     * Sets whether the ride is ongoing.
     *
     * @param ongoing true if the ride is ongoing, false otherwise
     */
    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }
    
    /**
     * Gets the end date.
     * @return The end date.
     */
    public Optional<Date> getEndDate() {
        return this.endDate;
    }
    
    /**
     * Sets the end date.
     * @param date The date.
     */
    public void setEndDate(Date date) {
        this.endDate = Optional.of(date);
    }
    
    /**
     * Sets the end date.
     * @param date The date.
     */
    public void setEndDate(Optional<Date> date) {
        this.endDate = date;
    }
    
    /**
     * Gets the user.
     * @return The user.
     */
    public User getUser() {
        return this.user;
    }
    
    /**
     * Gets the bike.
     * @return The bike.
     */
    public EBike getBike() {
        return this.bike;
    }
    
    /**
     * Converts the ride to a DTO.
     * @return The DTO.
     */
    @Override
    public RideDTO toDTO() {
        return new RideDTO(
                this.startedDate,
                this.endDate,
                this.user.toDTO(),
                this.bike.toDTO(),
                this.ongoing,
                this.id);
    }
    
    /**
     * Converts the ride to a JSON object.
     * @return The JSON object.
     */
    @Override
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        json.put(JsonFieldKey.RIDE_ID_KEY, this.id)
                .put(JsonFieldKey.RIDE_USER_KEY, this.user.toJsonObject())
                .put(JsonFieldKey.RIDE_EBIKE_KEY, this.bike.toJsonObject())
                .put(JsonFieldKey.RIDE_START_DATE_KEY, this.startedDate.toString())
                .put(JsonFieldKey.RIDE_ONGONING_KEY, this.ongoing)
                .put(JsonFieldKey.RIDE_END_DATE_KEY, this.endDate.toString());
        return json;
    }
    
    /**
     * Converts the ride to a JSON string.
     * @return The JSON string.
     */
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    /**
     * Converts the ride to a string.
     */
    public String toString() {
        return "{ id: " + this.id + ", user: " + this.user.getId() + ", bike: " + this.bike.getBikeId() + " }";
    }
}
