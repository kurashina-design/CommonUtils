package jp.kurashina.commons.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * コントローラーのメソッド引数で、リクエストパラメータから特定のフィールド名のリストを抽出するためのアノテーション。
 * <p>
 * 単純なカンマ区切りに加え、ブラケット {@code []} を使用したネスト構造の指定をサポートしています。
 * 抽出されたフィールド名はドット記法に正規化されます。
 * </p>
 * 
 * <p><b>使用例:</b></p>
 * <ul>
 *   <li>{@code ?fields=id,name} &rarr; {@code ["id", "name"]}</li>
 *   <li>{@code ?fields=user[id,name]} &rarr; {@code ["user.id", "user.name"]}</li>
 *   <li>{@code ?fields=order[id,items[id,price]]} &rarr; {@code ["order.id", "order.items.id", "order.items.price"]}</li>
 * </ul>
 * 
 * <p>主にレスポンスに含めるフィールドを限定する場合（JSONの動的フィルタリングなど）に使用されます。</p>
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryFields {

    /**
     * {@link #name} のエイリアス。
     *
     * @return バインドするリクエストパラメータ名
     */
    @AliasFor("name")
    String value() default "fields";

    /**
     * バインドするリクエストパラメータの名前。
     * <p>デフォルトは "fields" です。</p>
     *
     * @return リクエストパラメータ名
     */
    @AliasFor("value")
    String name() default "fields";

    /**
     * クエリ変数が必須かどうか。
     * <p>デフォルトは {@code false} です。{@code true} に設定すると、
     * リクエストに含まれない場合に例外がスローされます。</p>
     *
     * @return 必須の場合はtrue
     */
    boolean required() default false;

    /**
     * リクエストパラメータが指定されていない場合に使用されるデフォルト値。
     * <p>カンマ区切りやブラケット記法が使用できます。</p>
     *
     * @return デフォルト値の文字列
     */
    String defaultValue() default "";

    /**
     * 除外するフィールド名のリスト。
     * <p>指定されたフィールド名、またはそのフィールドを親に持つネストされたフィールドを除外します。</p>
     * <p>例: {@code excluding = "heavyEntity"} を指定した場合、{@code heavyEntity} だけでなく
     * {@code heavyEntity.id} や {@code heavyEntity.details.name} も除外されます。</p>
     *
     * @return 除外するフィールド名の配列
     */
    String[] excluding() default {};

}
