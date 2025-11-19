package jp.kurashina.commons.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.JapaneseDate;
import java.time.chrono.JapaneseEra;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DateTimeUtils {

    // 定数として定義（パフォーマンス向上・コードの共通化）
    private static final DateTimeFormatter JAPANESE_DATE_FORMATTER = DateTimeFormatter
            .ofPattern("GGGGy年M月d日")
            .withLocale(Locale.JAPAN)
            .withChronology(JapaneseChronology.INSTANCE);

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

    public static Map<String, Integer> convertJapaneseToGregorian(String source) {
        // 全角スペース・半角スペースの除去
        String normalizedSource = source.replace(" ", "").replaceAll("　", "");

        // 年月の数字の先頭の0を除去
        normalizedSource = normalizedSource
                .replaceAll("(?<=年)0(\\d)", "$1")  // 年の後の0を除去
                .replaceAll("(?<=月)0(\\d)", "$1");  // 月の後の0を除去
        normalizedSource += "1日";

        DateTimeFormatter japaneseFormatter = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("G", Locale.JAPAN))
                .append(DateTimeFormatter.ofPattern("y年M月d日", Locale.JAPAN))
                .toFormatter()
                .withChronology(JapaneseChronology.INSTANCE)
                .withLocale(Locale.JAPAN);

        try {
            TemporalAccessor temporalAccessor = japaneseFormatter.parse(normalizedSource);
            JapaneseDate japaneseDate = JapaneseDate.from(temporalAccessor);
            LocalDate localDate = LocalDate.from(japaneseDate);
            Map<String, Integer> resultMap = new HashMap<>();
            resultMap.put("year", localDate.getYear());
            resultMap.put("month", localDate.getMonthValue());
            return resultMap;
        } catch (Exception e) {
            log.warn("和暦の変換に失敗しました: {}", source, e);
            return null;
        }
    }

    /**
     * 和暦日付文字列（例: 令和5年8月18日、令和05年08月01日）をLocalDateに変換します。
     *
     * @param source 和暦日付文字列
     * @return 変換されたLocalDate。変換に失敗した場合はnull。
     */
    public static LocalDate convertJapaneseDateToLocalDate(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }

        // 全角スペース・半角スペースの除去
        String normalizedSource = source.replace(" ", "").replaceAll("　", "");

        // 年月日の数字の先頭の0を除去
        // 正規表現の意味: 数字以外(?<!\d)に続く0(\d)年/月/日 を $1年/月/日 に置換
        normalizedSource = normalizedSource
                .replaceAll("(?<!\\d)0(\\d)年", "$1年")
                .replaceAll("(?<!\\d)0(\\d)月", "$1月")
                .replaceAll("(?<!\\d)0(\\d)日", "$1日");

        try {
            TemporalAccessor temporalAccessor = JAPANESE_DATE_FORMATTER.parse(normalizedSource);
            return LocalDate.from(temporalAccessor);
        } catch (Exception e) {
            log.warn("和暦日付の変換に失敗しました: {}", source, e);
            return null;
        }
    }
}
