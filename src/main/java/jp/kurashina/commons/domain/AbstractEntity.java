package jp.kurashina.commons.domain;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import jp.kurashina.commons.util.EntityFieldUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@MappedSuperclass
public abstract class AbstractEntity extends AbstractAudit {

    public static final Set<String> SEARCHABLE_FIELDS = EntityFieldUtils.getSearchableFields(AbstractEntity.class);
    public static final Set<String> SORTABLE_FIELDS = EntityFieldUtils.getSortableFiels(AbstractEntity.class);

    @Version
    private Integer version;

}
