package sap.ass02.infrastructure.persistence.sql;

public final class SQLUpdates implements SQLStatement {
    public static final String INSERT_RIDE =
            """
            INSERT INTO rides (id, user_id, ebike_id, start_date, end_date, ongoing)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    
    public static final String UPDATE_RIDE_END =
            """
            UPDATE rides
            SET end_date = ?, ongoing = ?
            WHERE id = ?
            """;
}
