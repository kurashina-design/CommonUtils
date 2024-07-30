package jp.kurashina.commons.util;

public class ObjectUtils {

    @SuppressWarnings("unchecked")
    public static <T> T autoCast(Object obj) {
        return (T) obj;
    }

}
