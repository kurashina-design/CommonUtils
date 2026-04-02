package jp.kurashina.commons.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;

/**
 * Bean操作（プロパティコピー、マップ変換など）のためのユーティリティクラス。
 */
@Slf4j
public class BeanUtils {

    /**
     * デフォルトコンストラクタ。
     */
    public BeanUtils() {
    }

    /**
     * ソースオブジェクトからターゲットオブジェクトへプロパティをコピーします。
     *
     * @param source コピー元オブジェクト
     * @param target コピー先オブジェクト
     */
    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    /**
     * ソースオブジェクトの非 null プロパティのみをターゲットオブジェクトへコピーします（パッチ更新用）。
     *
     * @param source コピー元オブジェクト
     * @param target コピー先オブジェクト
     */
    public static void copyPatchProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
    }

    /**
     * 特定のプロパティを除外してコピーします。
     *
     * @param source コピー元オブジェクト
     * @param target コピー先オブジェクト
     * @param ignoreProperties 除外するプロパティ名の配列
     */
    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    /**
     * 特定のプロパティを除外してコピーします。
     *
     * @param source コピー元オブジェクト
     * @param target コピー先オブジェクト
     * @param ignoreProperties 除外するプロパティ名のセット
     */
    public static void copyProperties(Object source, Object target, Set<String> ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties.toArray(new String[0]));
    }

    /**
     * 指定された特定のプロパティのみをコピーします。
     *
     * @param source コピー元オブジェクト
     * @param target コピー先オブジェクト
     * @param specificProperties コピー対象とするプロパティ名のコレクション
     * @param ignoreProperties 除外するプロパティ名のセット
     */
    public static void copySpecificProperties(Object source, Object target, Collection<String> specificProperties, Set<String> ignoreProperties) {
        Set<String> ignores = new HashSet<>();
        if (ignoreProperties != null) {
            ignores.addAll(ignoreProperties);
        }

        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!specificProperties.contains(fieldName)) {
                ignores.add(fieldName);
            }
        }
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignores.toArray(new String[0]));
    }

    /**
     * 指定された特定のプロパティのみをコピーします。
     *
     * @param source コピー元オブジェクト
     * @param target コピー先オブジェクト
     * @param specificProperties コピー対象とするプロパティ名のコレクション
     */
    public static void copySpecificProperties(Object source, Object target, Collection<String> specificProperties) {
        copySpecificProperties(source, target, specificProperties, null);
    }

    /**
     * ネストされたフィールドリストを階層構造のマップに変換します。
     *
     * @param fieldList フィールドパスのリスト (例: "user.profile.name")
     * @return 階層構造を表すマップ
     */
    public static Map<String, Set<String>> fieldListToMap(Collection<String> fieldList) {
        Map<String, Set<String>> map = new HashMap<>();
        if (fieldList == null) {
            return map;
        }

        for (String fieldPath : fieldList) {
            String[] parts = fieldPath.split("\\.");
            if (parts.length > 0) {
                String root = parts[0];
                map.computeIfAbsent(root, k -> new HashSet<>());

                if (parts.length > 1) {
                    StringBuilder subPath = new StringBuilder();
                    for (int i = 1; i < parts.length; i++) {
                        if (i > 1) subPath.append(".");
                        subPath.append(parts[i]);
                    }
                    map.get(root).add(subPath.toString());
                }
            }
        }
        return map;
    }

    private static String[] getNullPropertyNames(Object source) {
        final org.springframework.beans.BeanWrapper src = new org.springframework.beans.BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) emptyNames.add(pd.getName());
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * ソートキー候補のセットから、実際にソート可能なキーのセットを生成します（プレフィックス対応）。
     *
     * @param setOfSortKeySources ソートキーの候補
     * @return 生成されたソートキーセット
     */
    public static Set<String> generateSortKeySet(Set<String> setOfSortKeySources) {
        Set<String> set = new HashSet<>();
        setOfSortKeySources.forEach(s -> {
            set.add(s);
            set.add("-" + s);
        });
        return set;
    }
}
