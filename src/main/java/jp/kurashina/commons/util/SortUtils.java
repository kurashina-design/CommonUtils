package jp.kurashina.commons.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

/**
 * ソート関連のユーティリティクラス。
 * Spring DataのSortオブジェクトの生成や操作を補助します。
 */
@Component
@Slf4j
public class SortUtils {

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