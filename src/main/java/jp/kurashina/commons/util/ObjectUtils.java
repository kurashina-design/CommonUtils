package jp.kurashina.commons.util;

import org.springframework.stereotype.Component;

@Component
public class ObjectUtils {

    @SuppressWarnings("unchecked")
    public static <T> T autoCast(Object obj) {
        return (T) obj;
    }

}
