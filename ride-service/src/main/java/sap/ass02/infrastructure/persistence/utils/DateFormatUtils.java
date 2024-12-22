package sap.ass02.infrastructure.persistence.utils;

import java.sql.Date;
import java.text.ParseException;

public class DateFormatUtils {
    
    public static Date toSqlDate(String date) throws ParseException {
        return new Date(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(date).getTime());
    }
}
