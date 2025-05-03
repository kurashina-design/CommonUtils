package jp.kurashina.commons.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

    public static LocalDate convertJapaneseToGregorian(String source) {
        DateTimeFormatter japaneseFormatter = DateTimeFormatter.ofPattern("GGyy年M月", Locale.JAPAN);
        java.time.LocalDate gregorianDate = null;
        try {
            java.time.temporal.TemporalAccessor temporalAccessor = japaneseFormatter.parse(source);
            JapaneseDate japaneseDate = JapaneseDate.from(temporalAccessor);
            gregorianDate = java.time.LocalDate.from(japaneseDate);
        } catch (Exception ignored) {
        }
        return gregorianDate;
    }

}
