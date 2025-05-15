package jp.kurashina.commons.util;


import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CollectionUtils {


    public static boolean containsAll(Collection<?> collection, String... args) {
        for (String arg : args) {
            if (!collection.contains(arg)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(Collection<?> coll) {
        return org.apache.commons.collections4.CollectionUtils.isEmpty(coll);
    }

    public static boolean isEqualCollection(Collection<?> a, Collection<?> b) {
        return org.apache.commons.collections4.CollectionUtils.isEqualCollection(a, b);
    }

    public static boolean isNotEmpty(Collection<?> coll) {
        return org.apache.commons.collections4.CollectionUtils.isNotEmpty(coll);
    }

    public static <T> Set<T> concat(Set<T> oneSet, Set<T> anotherSet) {
        return Stream.concat(oneSet.stream(), anotherSet.stream()).collect(Collectors.toSet());
    }

    public static <T> List<T> concat(List<T> oneSet, List<T> anotherSet) {
        return Stream.concat(oneSet.stream(), anotherSet.stream()).collect(Collectors.toList());
    }

    public static <E> Collection <E> removeAll(Collection <E> collection, Collection <?> remove) {
        return org.apache.commons.collections4.CollectionUtils.removeAll(collection, remove);
    }

    public static <T> T getRandomElement(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }

        // Collectionはインデックスでのアクセスができないため、
        // ストリームを使用してランダムな位置までスキップする方法か、
        // 配列やリストに変換する方法のいずれかを選択する必要があります

        // 方法1: 配列に変換して取得
        Object[] array = collection.toArray();
        Random random = new Random();
        return (T) array[random.nextInt(array.length)];

        // 方法2: ストリームを使用
        /*
        return collection.stream()
                .skip(new Random().nextInt(collection.size()))
                .findFirst()
                .orElse(null);
        */
    }

}


