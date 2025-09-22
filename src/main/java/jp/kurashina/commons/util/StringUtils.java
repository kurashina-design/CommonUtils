package jp.kurashina.commons.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StringUtils {

    public static List<String> split(String source) {
        return Arrays.stream(source.split(",")).map(String::trim).toList();
    }

    public static boolean isEmpty(CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isEmpty(cs);
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isNotEmpty(cs);
    }

    public static String join(Object[] array, String delimiter) {
        return org.apache.commons.lang3.StringUtils.join(array, delimiter);
    }

    public static String join(Iterable<?> iterable, String separator) {
        return org.apache.commons.lang3.StringUtils.join(iterable, separator);
    }

    public static String extractNumbers(CharSequence cs) {
        return cs.chars()
                .filter(Character::isDigit)
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }

    public static String strip(String source) {
        return org.apache.commons.lang3.StringUtils.strip(source, " \u3000\t\n\r\f"); // 半角SP、全角SP、タブ、改行、復帰、改ページ
    }

    public static String normalizeAsciiOnly(String input) {
        //正規表現で不要な全角文字を除外するフィルタを追加します。
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKC);
        return normalized.replaceAll("[^\\p{ASCII}]", "").trim(); // ASCII以外を除去
    }

    public static String normalizeExceptParentheses(String input) {
        //正規表現で不要な全角文字を除外するフィルタを追加します。
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKC);
        return normalized.replaceAll("\\(", "（").replaceAll("\\)", "）");
    }

    public static String htmlString(String source) {
        return source.replaceAll(">\\s*<", "><") // タグ間の0個以上の空白を削除
                .replaceAll("\\s{2,}", " ")
                .replaceAll("　+", "")
                .replaceAll("[\\r\\n]+", "");
    }

}
