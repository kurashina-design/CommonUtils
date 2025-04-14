package jp.kurashina.commons.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QuerySort {

    /**
     * Alias for {@link #name}.
     */
    @AliasFor("name")
    String value() default "sort";

    /**
     * The name of the request parameter to bind to.
     * <p>Defaults to "sort."</p>
     */
    @AliasFor("value")
    String name() default "sort";

    /**
     * Whether the query variable is required.
     * <p>Defaults to {@code false}. Switch this to {@code true} if
     * you prefer an exception being thrown if the query
     * variable is missing in the incoming request.</p>
     */
    boolean required() default false;

    String defaultValue() default "";

}
