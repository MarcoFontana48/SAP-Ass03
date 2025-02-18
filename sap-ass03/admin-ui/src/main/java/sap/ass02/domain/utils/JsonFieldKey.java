package sap.ass02.domain.utils;

/**
 * JSON keys for eBike, user and ride
 */
public final class JsonFieldKey {
    /**
     * JSON keys for eBike
     */
    public static final String EBIKE_ID_KEY = "ebike_id";
    public static final String EBIKE_STATE_KEY = "state";
    public static final String EBIKE_X_LOCATION_KEY = "x_location";
    public static final String EBIKE_Y_LOCATION_KEY = "y_location";
    public static final String EBIKE_X_DIRECTION_KEY = "x_direction";
    public static final String EBIKE_Y_DIRECTION_KEY = "y_direction";
    public static final String EBIKE_SPEED_KEY = "speed";
    public static final String EBIKE_BATTERY_KEY = "battery";
    public static final String BIKE_TYPE_KEY = "type";
    
    /**
     * JSON keys for user
     */
    public static final String USER_ID_KEY = "user_id";
    public static final String USER_CREDIT_KEY = "credit";
    
    /**
     * JSON keys for ride
     */
    public static final String JSON_RIDE_ID_KEY = "ride_id";
    public static final String JSON_RIDE_USER_ID_KEY = USER_ID_KEY;
    public static final String JSON_RIDE_EBIKE_ID_KEY = EBIKE_ID_KEY;
    public static final String JSON_RIDE_START_DATE_KEY = "start_date";
    public static final String JSON_RIDE_END_DATE_KEY = "end_date";
    public static final String JSON_RIDE_ONGONING_KEY = "ongoing";
    public static final String JSON_RIDE_ACTION = "action";
    
    /**
     * JSON keys for aBike
     */
    public static final String ABIKE_ID_KEY = "abike_id";
    public static final String ABIKE_STATE_KEY = "state";
    public static final String ABIKE_X_LOCATION_KEY = "x_location";
    public static final String ABIKE_Y_LOCATION_KEY = "y_location";
    public static final String ABIKE_X_DIRECTION_KEY = "x_direction";
    public static final String ABIKE_Y_DIRECTION_KEY = "y_direction";
    public static final String ABIKE_SPEED_KEY = "speed";
    public static final String ABIKE_BATTERY_KEY = "battery";
    public static final String USER_X_LOCATION_KEY = "x_location";
    public static final String USER_Y_LOCATION_KEY = "y_location";
}
