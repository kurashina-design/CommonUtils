package jp.kurashina.commons.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * コントローラーのメソッド引数で、リクエストパラメータからソート条件のリストを抽出するためのアノテーション。
 * <p>
 * {@code List<String>} 型の引数に使用し、リクエストパラメータからソートキーのリストを取得します。
 * 各ソートキーは、通常はフィールド名ですが、先頭にハイフン {@code -} を付けることで降順を意味します。
 * </p>
 * 
 * <p><b>使用例:</b></p>
 * <ul>
 *   <li>{@code ?sort=priority,-createdAt} &rarr; {@code ["priority", "-createdAt"]}</li>
 * </ul>
 * 
 * <p>抽出されたリストは {@link jp.kurashina.commons.util.SortUtils} などのユーティリティを使用して
 * {@link org.springframework.data.domain.Sort} オブジェクトに変換して利用することが想定されています。</p>
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QuerySort {

    /**
     * {@link #name} のエイリアス。
     *
     * @return バインドするリクエストパラメータ名
     */
    @AliasFor("name")
    String value() default "sort";

    /**
     * バインドするリクエストパラメータの名前。
     * <p>デフォルトは "sort" です。</p>
     *
     * @return リクエストパラメータ名
     */
    @AliasFor("value")
    String name() default "sort";

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
     * <p>カンマ区切りの文字列を指定することで、複数のソート条件をデフォルトとして設定できます。
     * (例: {@code defaultValue = "priority,-createdAt"})</p>
     *
     * @return デフォルト値の文字列
     */
    String defaultValue() default "";

}
