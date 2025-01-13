package sap.ass02.infrastructure.persistence.utils;

import java.sql.Date;
import java.text.ParseException;

/**
 * Utility class for date formatting.
 */
public class DateFormatUtils {
    
    /**
     * Converts a date string to a SQL date.
     * @param date the date string
     * @return the SQL date
     * @throws ParseException if the date string is invalid
     */
    public static Date toSqlDate(String date) throws ParseException {
        return new Date(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(date).getTime());
    }
}
