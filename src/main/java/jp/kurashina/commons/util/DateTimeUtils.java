package jp.kurashina.commons.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.JapaneseDate;
import java.time.chrono.JapaneseEra;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

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
        // 全角スペース・半角スペースの除去
        String normalizedSource = source.replace(" ", "").replaceAll("　", "");

        // JapaneseEraから全ての元号を取得して正規表現パターンを構築
        String eraPattern = Arrays.stream(JapaneseEra.values())
                .map(era -> era.getDisplayName(TextStyle.FULL, Locale.JAPAN))
                .collect(Collectors.joining("|"));

        // 年月の数字の先頭の0を除去
        normalizedSource = normalizedSource
                .replaceAll("(?<=年)0(\\d)", "$1")  // 年の後の0を除去
                .replaceAll("(?<=月)0(\\d)", "$1")  // 月の後の0を除去
                .replaceAll("(" + eraPattern + ")0(\\d)", "$1$2"); // 元号の後の0を除去

        DateTimeFormatter japaneseFormatter = DateTimeFormatter.ofPattern("GGyy年M月", Locale.JAPAN);
        try {
            java.time.temporal.TemporalAccessor temporalAccessor = japaneseFormatter.parse(normalizedSource);
            JapaneseDate japaneseDate = JapaneseDate.from(temporalAccessor);
            return LocalDate.from(japaneseDate);
        } catch (Exception e) {
            log.warn("和暦の変換に失敗しました: {}", source, e);
            return null;
        }
    }



}
