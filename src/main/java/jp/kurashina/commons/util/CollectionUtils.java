package jp.kurashina.commons.util;


import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
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
}


