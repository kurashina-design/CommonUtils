package jp.kurashina.commons.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class CurrentNumberOfElementsMap {

    private Long start;
    private Long end;

}
