package jp.kurashina.commons.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class DateTimeUtils {

    public static ZonedDateTime asiaTokyo(int year, int month, int day, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        return localDateTime.atZone(ZoneId.of("Asia/Tokyo"));
    }

    public static ZonedDateTime asiaTokyo(int year, int month, int day) {
        return asiaTokyo(year, month, day, 0, 0, 0);
    }

    public static ZonedDateTime utc(int year, int month, int day, int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute, second);
        return localDateTime.atZone(ZoneId.of("UTC"));
    }

    public static ZonedDateTime utc(int year, int month, int day) {
        return utc(year, month, day, 0, 0, 0);
    }

}
