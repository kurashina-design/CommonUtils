package jp.kurashina.commons.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsTest {

    @Test
    void splitTrimsEachElement() {
        assertThat(StringUtils.split(" a , b,c ")).containsExactly("a", "b", "c");
        assertThat(StringUtils.split("single")).containsExactly("single");
    }

    @Test
    void emptyChecksHandleNull() {
        assertThat(StringUtils.isEmpty(null)).isTrue();
        assertThat(StringUtils.isEmpty("")).isTrue();
        assertThat(StringUtils.isEmpty(" ")).isFalse();
        assertThat(StringUtils.isNotEmpty("a")).isTrue();
        assertThat(StringUtils.isNotEmpty(null)).isFalse();
    }

    @Test
    void joinConcatenatesWithDelimiter() {
        assertThat(StringUtils.join(new Object[]{"a", 1, "b"}, ",")).isEqualTo("a,1,b");
        assertThat(StringUtils.join(List.of("x", "y"), "-")).isEqualTo("x-y");
    }

    @Test
    void extractNumbersKeepsDigitsOnly() {
        assertThat(StringUtils.extractNumbers("TEL 03-1234-5678")).isEqualTo("0312345678");
        assertThat(StringUtils.extractNumbers("abc")).isEmpty();
    }

    @Test
    void stripRemovesHalfAndFullWidthWhitespaceAtEnds() {
        assertThat(StringUtils.strip("　 \t名前\n\r")).isEqualTo("名前");
        assertThat(StringUtils.strip("名　前")).isEqualTo("名　前");
        assertThat(StringUtils.strip(null)).isNull();
    }

    @Test
    void normalizeAsciiOnlyConvertsFullWidthAndDropsOthers() {
        assertThat(StringUtils.normalizeAsciiOnly("ＡＢＣ１２３テスト ")).isEqualTo("ABC123");
        assertThat(StringUtils.normalizeAsciiOnly(null)).isNull();
    }

    @Test
    void normalizeExceptParenthesesUsesFullWidthParentheses() {
        assertThat(StringUtils.normalizeExceptParentheses("ＡＢＣ(株)")).isEqualTo("ABC（株）");
        assertThat(StringUtils.normalizeExceptParentheses("（有）１")).isEqualTo("（有）1");
        assertThat(StringUtils.normalizeExceptParentheses(null)).isNull();
    }

    @Test
    void htmlStringRemovesWhitespaceBetweenTags() {
        assertThat(StringUtils.htmlString("<p>\n  <b>a</b>   <i>b</i>\n</p>")).isEqualTo("<p><b>a</b><i>b</i></p>");
        assertThat(StringUtils.htmlString("<p>a    b</p>")).isEqualTo("<p>a b</p>");
    }

    @Test
    void removeSpacesRemovesAllSpaceCharacters() {
        assertThat(StringUtils.removeSpaces(" 久留米　400\tわ\r\n1089 ")).isEqualTo("久留米400わ1089");
        assertThat(StringUtils.removeSpaces(null)).isNull();
    }

    @Test
    void normalizeAndRemoveSpacesMatchesDocumentedExample() {
        assertThat(StringUtils.normalizeAndRemoveSpaces("久留米　４００　わ　１０８９")).isEqualTo("久留米400わ1089");
        assertThat(StringUtils.normalizeAndRemoveSpaces(null)).isNull();
    }
}
