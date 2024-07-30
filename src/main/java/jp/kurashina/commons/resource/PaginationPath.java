package jp.kurashina.commons.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PaginationPath {

    private String path;

    private PaginationPathParameters params = new PaginationPathParameters();
}
