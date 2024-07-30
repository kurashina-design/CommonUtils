package jp.kurashina.commons.resource;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaginationPathParameters {

    private Integer page;

    @JsonProperty("per_page")
    private Integer perPage;

    private String sort;

    private Long directoryId;

}
