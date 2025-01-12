package sap.ass02.infrastructure.persistence.sql;

/**
 * Interface for SQL statements.
 */
public final class SQLQueries implements SQLStatement {
    public static final String SELECT_USER_BY_ID =
            """
            SELECT *
            FROM user
            WHERE id = ?
            """;
    public static final String SELECT_ALL_USERS =
            """
            SELECT *
            FROM user
            """;
}
