package jp.kurashina.commons.util;

import org.springframework.stereotype.Component;

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

}
