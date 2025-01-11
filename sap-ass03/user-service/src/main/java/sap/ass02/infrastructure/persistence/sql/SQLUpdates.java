package sap.ass02.infrastructure.persistence.sql;

public final class SQLUpdates implements SQLStatement {
    public static final String INSERT_USER =
        """
        INSERT INTO user (id, credit, x_location, y_location)
        VALUES (?, ?, ?, ?)
        """;
    public static final String ADD_CREDITS =
        """
        UPDATE user
        SET credit = credit + ?
        WHERE id = ?
        """;
    public static final String SET_CREDITS =
        """
        UPDATE user
        SET credit = ?
        WHERE id = ?
        """;
}
