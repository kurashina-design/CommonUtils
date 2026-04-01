package jp.kurashina.commons.annotation;

import org.springframework.core.annotation.AliasFor;
import java.lang.annotation.*;

/**
 * コントローラーのメソッド引数で、リクエストパラメータから特定のフィールド名のリストを抽出するためのアノテーション。
 * <p>
 * 主に JSON レスポンスの動的フィルタリングに使用されます。
 * ブラケット記法（例: {@code fields=id,children[id,name]}) をサポートし、
 * リゾルバーによってフラットなドット記法（例: {@code ["id", "children.id", "children.name"]}) に変換されます。
 * </p>
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryFields {

    /** @return パラメータ名のエイリアス */
    @AliasFor("name")
    String value() default "fields";

    /** @return 抽出対象とするリクエストパラメータ名（デフォルトは "fields"） */
    @AliasFor("value")
    String name() default "fields";

    /** @return 必須項目の場合は true */
    boolean required() default false;

    /** @return パラメータが未指定の場合のデフォルト値 */
    String defaultValue() default "";

    /**
     * 除外するフィールド名のリスト。
     * <p>
     * 指定されたフィールド、およびその配下のネストされたフィールドが抽出リストから除外されます。
     * 末尾に {@code .*} を付与することで、その階層自体は許可し、配下のみを明示的に禁止する意図を表現できます。
     * </p>
     * * 例: {@code children.assets} を指定した場合、{@code children.assets} および {@code children.assets.id} が除外されます。
     * * @return 除外対象フィールドの配列
     */
    String[] excluding() default {};

    /**
     * 許可する最大深度（ドットの数）。
     * <p>
     * 変換後のドット記法におけるドットの出現回数で制限をかけます。
     * </p>
     * <ul>
     * <li>0: ネスト不可 (例: {@code id}, {@code name} はOK)</li>
     * <li>1: 1階層まで (例: {@code children.id} はOK, {@code children.assets.id} はNG)</li>
     * <li>99: デフォルト（実質的な無制限）</li>
     * </ul>
     *
     * @return 許可する最大深度
     */
    int maxDepth() default 99;
}