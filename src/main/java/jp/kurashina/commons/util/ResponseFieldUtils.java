package jp.kurashina.commons.util;

import jp.kurashina.commons.dto.ResponseField;

import java.util.List;

public class ResponseFieldUtils {

    public static boolean isEmpty(ResponseField responseField) {
        if (responseField == null) {
            return true;
        }
        return responseField.isEmpty();
    }

    public static boolean isNotEmpty(ResponseField responseField) {
        return !isEmpty(responseField);
    }

    public static ResponseField build(List<String> fieldList) {
        if (CollectionUtils.isNotEmpty(fieldList)) {
            return new ResponseField(fieldList);
        }
        return null;
    }

    public static boolean containsAll(ResponseField responseField, String... args) {
        return CollectionUtils.containsAll(responseField.getFields(), args);
    }

}
