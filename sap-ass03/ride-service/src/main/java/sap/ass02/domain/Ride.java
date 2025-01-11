package sap.ass02.domain;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sap.ass02.domain.dto.RideDTO;
import sap.ass02.domain.utils.JsonFieldKey;
import sap.ddd.Aggregate;

import java.sql.Date;
import java.util.Optional;

public final class Ride implements Aggregate<RideDTO> {
    private static final Logger LOGGER = LogManager.getLogger(Ride.class);
    private final User user;
    private final EBike bike;
    private final String id;
    private Date startedDate;
    private Optional<Date> endDate;
    private boolean ongoing;
    
    public Ride(String id, User user, EBike bike) {
        this.id = id;
        this.startedDate = new Date(new java.util.Date().getTime());   //! sql date trunks the time part of the date, leaving only 'YYYY-MM-DD'
        this.endDate = Optional.empty();
        this.user = user;
        this.bike = bike;
    }
    
    public Ride(JsonObject asJsonObject) {
        this(
                asJsonObject.getString(JsonFieldKey.RIDE_ID_KEY),
                new User(asJsonObject.getJsonObject(JsonFieldKey.RIDE_USER_KEY)),
                new EBikeImpl(asJsonObject.getJsonObject(JsonFieldKey.RIDE_EBIKE_KEY))
        );
    }
    
    public String getId() {
        return this.id;
    }
    
    public void start() {
        this.ongoing = true;
    }
    
    public void end() {
        LOGGER.trace("Ending ride simulation for ride '{}'...", this.id);
        this.endDate = Optional.of(new Date(new java.util.Date().getTime()));
        this.ongoing = false;
    }
    
    public Date getStartedDate() {
        return this.startedDate;
    }
    
    public void setStartedDate(Date date) {
        this.startedDate = (Date) date.clone();
    }
    
    public boolean isOngoing() {
        return this.ongoing;
    }
    
    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }
    
    public Optional<Date> getEndDate() {
        return this.endDate;
    }
    
    public void setEndDate(Date date) {
        this.endDate = Optional.of(date);
    }
    
    public void setEndDate(Optional<Date> date) {
        this.endDate = date;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public EBike getBike() {
        return this.bike;
    }
    
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
    
    @Override
    public String toJsonString() {
        return this.toJsonObject().encode();
    }
    
    public String toString() {
        return "{ id: " + this.id + ", user: " + this.user.getId() + ", bike: " + this.bike.getBikeId() + " }";
    }
}
