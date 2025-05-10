package jp.kurashina.commons.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
@Slf4j
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
        try {
            java.time.temporal.TemporalAccessor temporalAccessor = japaneseFormatter.parse(source);
            JapaneseDate japaneseDate = JapaneseDate.from(temporalAccessor);
            return LocalDate.from(japaneseDate);
        } catch (Exception e) {
            // エラーログを出力するか、より具体的な例外処理を行う
            log.warn("和暦の変換に失敗しました: {}", source, e);
            return null;
        }
    }

}
