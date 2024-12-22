package sap.ass02.infrastructure.persistence.sql;

public interface SQLStatement {
    public static final String SELECT_RIDE_BY_ID =
            """
            SELECT id, user_id, ebike_id, start_date, end_date, ongoing
            FROM rides
            WHERE id = ?
            """;
    public static final String SELECT_RIDE_BY_USER_EBIKE_ID =
            """
            SELECT id, user_id, ebike_id, start_date, end_date, ongoing
            FROM rides
            WHERE user_id = ? AND ebike_id = ?
            """;
    public static final String SELECT_ONGOING_RIDE_BY_USER_EBIKE_ID =
            """
            SELECT id, user_id, ebike_id, start_date, end_date, ongoing
            FROM rides
            WHERE user_id = ? AND ebike_id = ? AND ongoing = 1
            """;
    public static final String SELECT_ALL_RIDES =
            """
            SELECT id, user_id, ebike_id, start_date, end_date, ongoing
            FROM rides
            """;
}
