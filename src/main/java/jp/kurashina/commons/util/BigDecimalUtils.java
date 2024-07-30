package jp.kurashina.commons.util;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class BigDecimalUtils {

    public static BigDecimal round(Object source, Integer decimal) {
        BigDecimal casted = cast(source);
        if (casted == null) {
            return null;
        }
        if (decimal == null || decimal.equals(0)) {
            return casted;
        }
        return casted.setScale(decimal - 1, RoundingMode.HALF_UP);
    }

    public static BigDecimal roundPercent(Object source, Integer decimal) {
        BigDecimal casted = cast(source);
        if (casted == null) {
            return null;
        }
        if (decimal == null || decimal.equals(0)) {
            return casted;
        }
        casted = casted.multiply(new BigDecimal(100));
        return casted.setScale(decimal - 1, RoundingMode.HALF_UP);
    }

    private static BigDecimal cast(Object source) {
        BigDecimal casted = null;
        if (source instanceof BigDecimal) {
            casted = (BigDecimal) source;
        } else if (source instanceof Double) {
            casted = BigDecimal.valueOf((double) source);
        } else if (source instanceof Long) {
            casted = BigDecimal.valueOf((long) source);
        }
        return casted;
    }

}
