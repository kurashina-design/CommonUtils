package jp.kurashina.commons.resource;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * エンティティ由来でない手書きのResponseの基底クラス。
 * <p>
 * 継承するだけで、null、空文字列、空のコレクション、空のMapはJSONのキーごと出力されなくなります。
 * エンティティ由来のResponseには、このクラスではなく {@link AbstractEntityResponse} を使用してください。
 * Mapを継承する型には使用できず、Mapの要素には {@link JsonInclude.Include#NON_EMPTY} も適用されません。
 * フロントエンドは、キーがない場合を「空」として扱ってください。
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class AbstractResponse {
}
