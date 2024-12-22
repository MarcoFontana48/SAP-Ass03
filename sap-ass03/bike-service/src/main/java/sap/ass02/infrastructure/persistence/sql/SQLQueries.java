package sap.ass02.infrastructure.persistence.sql;

public final class SQLQueries implements SQLStatement {
    public static final String SELECT_EBIKE_BY_ID =
            """
            SELECT id, state, x_location, y_location, x_direction, y_direction, speed, battery
            FROM ebike
            WHERE id = ?
            """;
    public static final String SELECT_ALL_EBIKES =
            """
            SELECT id, state, x_location, y_location, x_direction, y_direction, speed, battery
            FROM ebike
            """;
}
