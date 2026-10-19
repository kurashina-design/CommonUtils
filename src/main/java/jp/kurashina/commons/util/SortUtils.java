package jp.kurashina.commons.util;

import jp.kurashina.commons.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;

/**
 * ソート関連のユーティリティクラス。
 * Spring DataのSortオブジェクトの生成や操作を補助します。
 */
@Component
@Slf4j
public class SortUtils {

    private static final Pattern SORT_KEY_PATTERN = Pattern.compile(
            "^-?[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)*$");
    private static final String INVALID_SORT_FORMAT_MESSAGE =
            "Invalid sort format. Use fieldName or -fieldName.";

    /**
     * Sortオブジェクトがnullまたは空であるかをチェックします。
     *
     * @param sort チェック対象のSortオブジェクト
     * @return Sortオブジェクトがnullまたは空の場合はtrue、そうでない場合はfalse
     */
    public static boolean isEmpty(Sort sort) {
        if (sort == null) {
            return true;
        }
        return sort.isEmpty();
    }

    /**
     * Sortオブジェクトがnullでも空でもないかをチェックします。
     *
     * @param sort チェック対象のSortオブジェクト
     * @return Sortオブジェクトがnullでも空でもない場合はtrue、そうでない場合はfalse
     */
    public static boolean isNotEmpty(Sort sort) {
        return !isEmpty(sort);
    }

    /**
     * ソートキーの書式と、呼び出し側が指定した許可リストとの完全一致を検証します。
     * 書式は {@code fieldName}、{@code -fieldName}、またはドット区切りのパスを許可し、
     * 書式が不正な場合は入力値を含まない
     * {@code "Invalid sort format. Use fieldName or -fieldName."} というメッセージで
     * {@link BadRequestException} をスローします。
     * 許可リストに存在しない場合は、先頭の {@code -} を除いた項目名を使った
     * {@code "Unknown sort field: 項目名"} というメッセージで {@link BadRequestException} をスローします。
     * 比較では大文字小文字を区別し、ドット区切りのパスも分解せずそのまま比較します。
     * <p>
     * 既存の {@link #getSort(List, Set)} は許可されていない項目を除外しますが、
     * このメソッドは不正な項目を {@link BadRequestException} にします。
     * </p>
     *
     * @param sortList 検証するソートキーのリスト。{@code null} または空の場合は検証しません
     * @param sortableFields 許可する項目名の集合。空の場合はすべて拒否します
     * @throws IllegalArgumentException {@code sortableFields} が {@code null} の場合
     * @throws BadRequestException ソートキーの書式が不正、または許可リストに存在しない場合
     */
    public static void validateSortKeys(List<String> sortList, Set<String> sortableFields) {
        if (sortableFields == null) {
            throw new IllegalArgumentException("sortableFields must not be null");
        }
        if (CollectionUtils.isEmpty(sortList)) {
            return;
        }

        for (String sortKey : sortList) {
            if (sortKey == null || !SORT_KEY_PATTERN.matcher(sortKey).matches()) {
                throw new BadRequestException(INVALID_SORT_FORMAT_MESSAGE);
            }

            String fieldName = StringUtils.removeStart(sortKey, "-");
            if (!sortableFields.contains(fieldName)) {
                throw new BadRequestException("Unknown sort field: " + fieldName);
            }
        }
    }

    /**
     * ソートキーのリストとソート可能なフィールドのセットに基づいてSortオブジェクトを生成します。
     * ソートキーがソート可能なフィールドに含まれていない場合、エラーログが出力され、そのキーは無視されます。
     *
     * @param sortList ソートキーのリスト (例: "field1", "-field2" (降順))
     * @param sortableFields ソート可能なフィールド名のセット
     * @return 生成されたSortオブジェクト。sortListが空または有効なソートキーがない場合はnull。
     */
    public static Sort getSort(List<String> sortList, Set<String> sortableFields) {
        if (CollectionUtils.isNotEmpty(sortList)) {
            List<Sort.Order> orderList = new ArrayList<>();
            sortList.forEach(sortKey -> {
                // ソートキーがソート可能なフィールドに含まれているかチェック
                // ここではsortKeyからプレフィックスを除去したフィールド名でチェックする必要があります。
                // 例: "-field" -> "field"
                String fieldName = StringUtils.removeStart(sortKey, "-");
                if (sortableFields.contains(fieldName)) {
                    orderList.add(getOrderByConvertingPrefix(sortKey));
                } else {
                    log.error("\"" + sortKey + "\" not allowed sort key.");
                }
            });
            if (!orderList.isEmpty()) {
                return Sort.by(orderList);
            }
        }
        return null;
    }

    /**
     * ソートキーのリスト、デフォルトソートキーのリスト、およびソート可能なフィールドのセットに基づいてSortオブジェクトを生成します。
     * sortListが空の場合、defaultSortListが使用されます。
     *
     * @param sortList ソートキーのリスト (例: "field1", "-field2" (降順))
     * @param defaultSortList sortListが空の場合に使用されるデフォルトのソートキーリスト
     * @param sortableFields ソート可能なフィールド名のセット
     * @return 生成されたSortオブジェクト。
     */
    public static Sort getSort(List<String> sortList, List<String> defaultSortList, Set<String> sortableFields) {
        if (CollectionUtils.isNotEmpty(sortList)) {
            return getSort(sortList, sortableFields);
        } else {
            return getSort(defaultSortList, sortableFields);
        }
    }

    /**
     * ソートキーのリストに基づいてSortオブジェクトを生成します。
     * このメソッドはソート可能なフィールドのチェックを行いません。
     *
     * @param sortList ソートキーのリスト (例: "field1", "-field2" (降順))
     * @return 生成されたSortオブジェクト。sortListが空の場合はnull。
     */
    public static Sort getSort(List<String> sortList) {
        if (CollectionUtils.isNotEmpty(sortList)) {
            List<Sort.Order> orderList = new ArrayList<>();
            sortList.forEach(s -> orderList.add(getOrderByConvertingPrefix(s)));
            return Sort.by(orderList);
        }
        return null;
    }

    /**
     * ソートキーのリストとデフォルトソートキーのリストに基づいてSortオブジェクトを生成します。
     * sortListが空の場合、defaultSortListが使用されます。
     * このメソッドはソート可能なフィールドのチェックを行いません。
     *
     * @param sortList ソートキーのリスト (例: "field1", "-field2" (降順))
     * @param defaultSortList sortListが空の場合に使用されるデフォルトのソートキーリスト
     * @return 生成されたSortオブジェクト。
     */
    public static Sort getSort(List<String> sortList, List<String> defaultSortList) {
        if (CollectionUtils.isNotEmpty(sortList)) {
            return getSort(sortList);
        } else {
            return getSort(defaultSortList);
        }
    }

    /**
     * ソートパラメータ文字列からSort.Orderオブジェクトを生成します。
     * パラメータが "-" で始まる場合、降順として扱われます。
     *
     * @param sortParam ソートパラメータ文字列 (例: "fieldName" (昇順), "-fieldName" (降順))
     * @return 生成されたSort.Orderオブジェクト
     */
    public static Sort.Order getOrderByConvertingPrefix(String sortParam) {
        if (StringUtils.startsWith(sortParam, "-")) {
            return Sort.Order.desc(StringUtils.removeStart(sortParam, "-"));
        } else {
            return Sort.Order.asc(sortParam);
        }
    }

    /**
     * 指定されたフィールドを降順でソートするSortオブジェクトを生成します。
     * フィールド名が "-" で始まる場合、そのプレフィックスは除去されます。
     *
     * @param sortParam ソート対象のフィールド名 (例: "fieldName" または "-fieldName")
     * @return 降順のSortオブジェクト
     */
    public static Sort desc(String sortParam) {
        if (sortParam.startsWith("-")) {
            return Sort.by(Sort.Direction.DESC, StringUtils.removeStart(sortParam, "-"));
        } else {
            return Sort.by(Sort.Direction.DESC, sortParam);
        }
    }

    /**
     * 指定されたフィールドを昇順でソートするSortオブジェクトを生成します。
     * フィールド名が "-" で始まる場合、そのプレフィックスは除去されます。
     *
     * @param sortParam ソート対象のフィールド名 (例: "fieldName" または "-fieldName")
     * @return 昇順のSortオブジェクト
     */
    public static Sort asc(String sortParam) {
        if (sortParam.startsWith("-")) {
            return Sort.by(Sort.Direction.ASC, StringUtils.removeStart(sortParam, "-"));
        } else {
            return Sort.by(Sort.Direction.ASC, sortParam);
        }
    }

    /**
     * Sortオブジェクトを文字列形式に変換します。
     * 例: "field1: ASC,field2: DESC" -> "field1ASC,field2DESC"
     *
     * @param sort 変換対象のSortオブジェクト
     * @return 変換された文字列。sortが空の場合は空文字列。
     */
    public static String toString(Sort sort) {
        if (SortUtils.isNotEmpty(sort)) {
            return sort.toString().replace(": ", "");
        }
        return "";
    }

    /**
     * ソートキーのリストをSQLのORDER BY句に似た文字列形式に変換します。
     * 例: ["field1", "-field2"] -> "field1 ASC,field2 DESC"
     *
     * @param sortList ソートキーのリスト (例: "field1", "-field2")
     * @return SQLのORDER BY句に似た文字列
     */
    public static String toString(List<String> sortList) {
        StringJoiner joiner = new StringJoiner(",");
        for (String sort : sortList) {
            if (sort.startsWith("-")) {
                joiner.add(StringUtils.removeStart(sort, "-") + " DESC");
            } else {
                joiner.add(sort + " ASC");
            }
        }
        return joiner.toString();
    }

}
