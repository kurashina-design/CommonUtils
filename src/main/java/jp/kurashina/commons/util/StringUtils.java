package jp.kurashina.commons.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StringUtils {

    /**
     * 文字列をカンマで分割し、各要素の前後から空白を除去したリストを返します。
     *
     * @param source 分割対象の文字列
     * @return 分割後の文字列リスト
     */
    public static List<String> split(String source) {
        return Arrays.stream(source.split(",")).map(String::trim).toList();
    }

    /**
     * 文字列がnullまたは空であるかをチェックします。
     *
     * @param cs チェック対象の文字列
     * @return 文字列がnullまたは空の場合はtrue、そうでない場合はfalse
     * @see org.apache.commons.lang3.StringUtils#isEmpty(CharSequence)
     */
    public static boolean isEmpty(CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isEmpty(cs);
    }

    /**
     * 文字列がnullでも空でもないかをチェックします。
     *
     * @param cs チェック対象の文字列
     * @return 文字列がnullでも空でもない場合はtrue、そうでない場合はfalse
     * @see org.apache.commons.lang3.StringUtils#isNotEmpty(CharSequence)
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return org.apache.commons.lang3.StringUtils.isNotEmpty(cs);
    }

    /**
     * 配列の要素を区切り文字で連結します。
     *
     * @param array     連結対象の配列
     * @param delimiter 区切り文字
     * @return 連結後の文字列
     * @see org.apache.commons.lang3.StringUtils#join(Object[], String)
     */
    public static String join(Object[] array, String delimiter) {
        return org.apache.commons.lang3.StringUtils.join(array, delimiter);
    }

    /**
     * Iterableの要素を区切り文字で連結します。
     *
     * @param iterable  連結対象のIterable
     * @param separator 区切り文字
     * @return 連結後の文字列
     * @see org.apache.commons.lang3.StringUtils#join(Iterable, String)
     */
    public static String join(Iterable<?> iterable, String separator) {
        return org.apache.commons.lang3.StringUtils.join(iterable, separator);
    }

    /**
     * 文字列から数字のみを抽出します。
     *
     * @param cs 抽出対象の文字列
     * @return 抽出された数字の文字列
     */
    public static String extractNumbers(CharSequence cs) {
        return cs.chars()
                .filter(Character::isDigit)
                .mapToObj(c -> String.valueOf((char) c))
                .collect(Collectors.joining());
    }

    /**
     * 文字列の前後から空白（半角、全角、タブ、改行など）を除去します。
     *
     * @param source 処理対象の文字列
     * @return 空白が除去された文字列
     */
    public static String strip(String source) {
        return org.apache.commons.lang3.StringUtils.strip(source, " \u3000\t\n\r\f"); // 半角SP、全角SP、タブ、改行、復帰、改ページ
    }

    /**
     * 文字列を正規化し、ASCII文字以外を除去します。
     *
     * @param input 処理対象の文字列
     * @return 正規化され、ASCII文字のみになった文字列
     */
    public static String normalizeAsciiOnly(String input) {
        //正規表現で不要な全角文字を除外するフィルタを追加します。
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKC);
        return normalized.replaceAll("[^\\p{ASCII}]", "").trim(); // ASCII以外を除去
    }

    /**
     * 文字列を正規化し、括弧を全角に変換します。
     *
     * @param input 処理対象の文字列
     * @return 正規化され、括弧が全角になった文字列
     */
    public static String normalizeExceptParentheses(String input) {
        //正規表現で不要な全角文字を除外するフィルタを追加します。
        if (input == null) return null;
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKC);
        return normalized.replaceAll("\\(", "（").replaceAll("\\)", "）");
    }

    /**
     * HTML文字列から不要な空白や改行を削除して整形します。
     *
     * @param source 処理対象のHTML文字列
     * @return 整形後のHTML文字列
     */
    public static String htmlString(String source) {
        return source.replaceAll(">\\s*<", "><") // タグ間の0個以上の空白を削除
                .replaceAll("\\s{2,}", " ")
                .replaceAll("　+", "")
                .replaceAll("[\\r\\n]+", "");
    }


    /**
     * 指定された文字列から、全角スペース、半角スペース、タブ、キャリッジリターン、ラインフィードをすべて削除します。
     * Pythonの `re.sub(r'[\u3000 \t\r\n]', '', source)` と同等の機能を提供します。
     *
     * @param source 処理対象の文字列。nullが渡された場合はnullを返します。
     * @return 空白文字が削除された新しい文字列。
     */
    public static String removeSpaces(String source) {
        if (source == null) {
            return null;
        }
        // 正規表現: [\u3000 \t\r\n]
        // Javaの文字列リテラルではバックスラッシュをエスケープする必要があるため、\\u3000, \\t, \\r, \\n と記述します。
        // \u3000: 全角スペース (Unicode U+3000)
        //  : 半角スペース (ASCII Space U+0020)
        // \t: タブ (ASCII Horizontal Tab U+0009)
        // \r: キャリッジリターン (ASCII Carriage Return U+000D)
        // \n: ラインフィード (ASCII Line Feed U+000A)
        return source.replaceAll("[\\u3000 \\t\\r\\n]", "");
    }
}
