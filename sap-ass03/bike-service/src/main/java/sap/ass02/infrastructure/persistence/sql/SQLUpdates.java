package sap.ass02.infrastructure.persistence.sql;

/**
 * SQL statements for updates.
 */
public final class SQLUpdates implements SQLStatement {
    public static final String INSERT_EBIKE =
            """
            INSERT INTO ebike (id, state, x_location, y_location, x_direction, y_direction, speed, battery)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;
    public static final String UPDATE_EBIKE =
            """
            UPDATE ebike
            SET state = ?, x_location = ?, y_location = ?, x_direction = ?, y_direction = ?, speed = ?, battery = ?
            WHERE id = ?
            """;
}
