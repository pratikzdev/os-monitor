package util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    /**
     * Gets 24 Hour prior date time
     * example:
     * current - Thu Jun 02 18:36:49 PDT 2022
     * after -24
     * Wed Jun 01 00:36:49 PDT 2022
     * after setting hour as right now
     * Wed Jun 01 18:36:49 PDT 2022
     *
     * @return Date which is exactly 24 hours behind
     */
    public static Date get24HourPriorDateTime() {
        GregorianCalendar calendar = new GregorianCalendar();
        int hour = calendar.get(Calendar.HOUR);

        calendar.getTime();
        calendar.set(Calendar.HOUR, -24);
        calendar.getTime();
        calendar.set(Calendar.HOUR, hour);

        Date date = calendar.getTime();
        return date;
    }

    /**
     * Converts to SQL Timestamp
     * @param dateTime Input java util Date
     * @return SQL Timestamp
     */
    public Timestamp convertDateToTimeStamp(Date dateTime) {
        Timestamp timestamp = new Timestamp(dateTime.getTime());
        return timestamp;
    }
}
