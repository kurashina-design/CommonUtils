package jp.kurashina.commons.annotation;

import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * コントローラーのメソッド引数で、リクエストパラメータから特定のフィールド名のリストを抽出するためのアノテーション。
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryFields {

    @AliasFor("name")
    String value() default "fields";

    @AliasFor("value")
    String name() default "fields";

    boolean required() default false;

    String defaultValue() default "";

    /**
     * 除外するフィールド名のリスト。
     * 指定されたフィールド名、およびその配下のネストされたフィールドが除外されます。
     */
    String[] excluding() default {};

    /**
     * 許可する最大深度（ドットの数）。
     * <p>0: ネスト不可 (id, name のみ)</p>
     * <p>1: 1階層まで (children.id はOK, children.assets.id はNG)</p>
     * <p>デフォルトは 99 (ほぼ無制限) です。既存の動作を壊さないための設定です。</p>
     *
     * @return 許可する最大深度
     */
    int maxDepth() default 99;
}