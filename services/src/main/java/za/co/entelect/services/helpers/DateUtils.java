package za.co.entelect.services.helpers;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class DateUtils {

    public static ZoneOffset DEFAULT_TIMEZONE = ZoneOffset.ofHours(2);

    public static LocalDateTime convert(Date date, int hour, int minute) {
        return LocalDateTime.from(
            date.toInstant()
                .atOffset(DEFAULT_TIMEZONE)
                .withHour(hour)
                .withMinute(minute)
        );
    }

    public static Date convert(LocalDateTime date) {
        return Date.from(date.atOffset(DEFAULT_TIMEZONE).toInstant());
    }

    public static Integer getHour(LocalDateTime dateTime) {
        return dateTime.atOffset(DateUtils.DEFAULT_TIMEZONE).getHour();
    }

    /**
     * Returns the minute of the hour in 15 minute increments, so possible values are 00, 15, 30, 45.
     *
     * @param dateTime Input time.
     * @return Minutes rounded to the nearest 15.
     */
    public static Integer getMinute(LocalDateTime dateTime) {
        Integer minutes = dateTime.atOffset(DateUtils.DEFAULT_TIMEZONE).getMinute();
        if (minutes <= 7) {
            return 0;
        } else if (minutes <= 22) {
            return 15;
        } else if (minutes <= 37) {
            return 30;
        } else if (minutes <= 52) {
            return 45;
        }

        return 0;
    }
}
