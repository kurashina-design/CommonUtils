package jp.kurashina.commons.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ResultResponse<T> {

    private String result;
    private T data;

    public ResultResponse(String result, T data) {
        this.result = result;
        this.data = data;
    }

}
