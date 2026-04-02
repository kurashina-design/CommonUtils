package jp.kurashina.commons.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * 監査情報（作成者、作成日時、更新者、更新日時）を保持するレスポンスの基底クラス。
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class AbstractAuditResponse {

    /** 作成者 */
    private String createdBy;

    /** 作成日時 */
    private ZonedDateTime createdDate;

    /** 更新者 */
    private String lastModifiedBy;

    /** 更新日時 */
    private ZonedDateTime lastModifiedDate;

}
