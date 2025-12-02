package jp.kurashina.commons.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
@Target({ ElementType.PARAMETER, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PerPageValidator.class)
public @interface ValidPerPage {

    String message() default "Invalid perPage value"; // メッセージは汎用的に

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    // ★★★ ここから属性を追加 ★★★

    /**
     * 最小値を指定します。
     */
    int min() default 1;

    /**
     * 最大値を指定します。
     */
    int max() default 100;

}
