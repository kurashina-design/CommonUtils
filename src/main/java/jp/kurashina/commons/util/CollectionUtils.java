package jp.kurashina.commons.util;

import java.util.*;

/**
 * コレクション操作（結合、空チェック、ランダム取得など）のためのユーティリティクラス。
 */
public class CollectionUtils {

    /**
     * デフォルトコンストラクタ。
     */
    public CollectionUtils() {
    }

    /**
     * 指定されたすべての要素がコレクションに含まれているかチェックします。
     *
     * @param collection チェック対象のコレクション
     * @param args 含まれているか確認する要素
     * @return すべて含まれていればtrue
     */
    public static boolean containsAll(Collection<?> collection, String... args) {
        return collection.containsAll(Arrays.asList(args));
    }

    /**
     * コレクションが null または空であるかチェックします。
     *
     * @param coll チェック対象のコレクション
     * @return null または空であればtrue
     */
    public static boolean isEmpty(Collection<?> coll) {
        return org.apache.commons.collections4.CollectionUtils.isEmpty(coll);
    }

    /**
     * 2つのコレクションが同一の要素を保持しているかチェックします。
     *
     * @param a コレクションA
     * @param b コレクションB
     * @return 同一であればtrue
     */
    public static boolean isEqualCollection(Collection<?> a, Collection<?> b) {
        return org.apache.commons.collections4.CollectionUtils.isEqualCollection(a, b);
    }

    /**
     * コレクションが null でもなく空でもないかチェックします。
     *
     * @param coll チェック対象のコレクション
     * @return null でもなく空でもなければtrue
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return org.apache.commons.collections4.CollectionUtils.isNotEmpty(coll);
    }

    /**
     * 2つのセットを結合した新しいセットを返します。
     *
     * @param <T> 要素の型
     * @param oneSet 1つ目のセット
     * @param anotherSet 2つ目のセット
     * @return 結合されたセット
     */
    public static <T> Set<T> concat(Set<T> oneSet, Set<T> anotherSet) {
        Set<T> set = new HashSet<>(oneSet);
        set.addAll(anotherSet);
        return set;
    }

    /**
     * 2つのリストを結合した新しいリストを返します。
     *
     * @param <T> 要素の型
     * @param oneSet 1つ目のリスト
     * @param anotherSet 2つ目のリスト
     * @return 結合されたリスト
     */
    public static <T> List<T> concat(List<T> oneSet, List<T> anotherSet) {
        List<T> list = new ArrayList<>(oneSet);
        list.addAll(anotherSet);
        return list;
    }

    /**
     * コレクションから指定された要素を除去します。
     *
     * @param <E> 要素の型
     * @param collection 元のコレクション
     * @param remove 除去する要素のコレクション
     * @return 除去後のコレクション
     */
    public static <E> Collection <E> removeAll(Collection <E> collection, Collection <?> remove) {
        return org.apache.commons.collections4.CollectionUtils.removeAll(collection, remove);
    }

    /**
     * コレクションからランダムに要素を1つ取得します。
     *
     * @param <T> 要素の型
     * @param collection 対象のコレクション
     * @return ランダムに選ばれた要素
     */
    public static <T> T getRandomElement(Collection<T> collection) {
        List<T> list = new ArrayList<>(collection);
        Collections.shuffle(list);
        return list.get(0);
    }
}
