package jp.kurashina.commons.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DateTimeUtilsTest {

    @Test
    void asiaTokyoCreatesZonedDateTime() {
        ZonedDateTime value = DateTimeUtils.asiaTokyo(2026, 11, 28, 9, 30, 15);

        assertThat(value.getZone()).isEqualTo(ZoneId.of("Asia/Tokyo"));
        assertThat(value.toLocalDateTime()).hasToString("2026-11-28T09:30:15");
        assertThat(DateTimeUtils.asiaTokyo(2026, 1, 2).toLocalDateTime()).hasToString("2026-01-02T00:00");
    }

    @Test
    void utcCreatesZonedDateTime() {
        ZonedDateTime value = DateTimeUtils.utc(2026, 11, 28, 0, 0, 1);

        assertThat(value.getZone()).isEqualTo(ZoneId.of("UTC"));
        assertThat(value.toLocalDateTime()).hasToString("2026-11-28T00:00:01");
        assertThat(DateTimeUtils.utc(2026, 1, 2).toLocalDateTime()).hasToString("2026-01-02T00:00");
    }

    @Test
    void asiaTokyoAndUtcDifferByNineHours() {
        assertThat(DateTimeUtils.utc(2026, 1, 1).toInstant())
                .isEqualTo(DateTimeUtils.asiaTokyo(2026, 1, 1, 9, 0, 0).toInstant());
    }

    @Test
    void convertJapaneseToGregorianReturnsYearAndMonth() {
        assertThat(DateTimeUtils.convertJapaneseToGregorian("令和5年8月")).isEqualTo(Map.of("year", 2023, "month", 8));
        assertThat(DateTimeUtils.convertJapaneseToGregorian("平成 31年 04月")).isEqualTo(Map.of("year", 2019, "month", 4));
        assertThat(DateTimeUtils.convertJapaneseToGregorian("昭和64年1月")).isEqualTo(Map.of("year", 1989, "month", 1));
    }

    @Test
    void convertJapaneseToGregorianReturnsNullForInvalidInput() {
        assertThat(DateTimeUtils.convertJapaneseToGregorian("not a date")).isNull();
    }

    @Test
    void convertJapaneseDateToLocalDateParsesWithOrWithoutLeadingZeros() {
        assertThat(DateTimeUtils.convertJapaneseDateToLocalDate("令和5年8月18日")).isEqualTo(LocalDate.of(2023, 8, 18));
        assertThat(DateTimeUtils.convertJapaneseDateToLocalDate("令和05年08月01日")).isEqualTo(LocalDate.of(2023, 8, 1));
        assertThat(DateTimeUtils.convertJapaneseDateToLocalDate("平成 2年 1月 9日")).isEqualTo(LocalDate.of(1990, 1, 9));
    }

    @Test
    void convertJapaneseDateToLocalDateReturnsNullForEmptyOrInvalidInput() {
        assertThat(DateTimeUtils.convertJapaneseDateToLocalDate(null)).isNull();
        assertThat(DateTimeUtils.convertJapaneseDateToLocalDate("")).isNull();
        assertThat(DateTimeUtils.convertJapaneseDateToLocalDate("令和5年13月1日")).isNull();
    }
}
