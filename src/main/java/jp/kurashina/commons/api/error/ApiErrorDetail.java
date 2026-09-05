package jp.kurashina.commons.api.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ApiErrorDetail(String field, String detail) {

    public ApiErrorDetail {
        Objects.requireNonNull(detail, "detail must not be null");
    }

    public ApiErrorDetail(String detail) {
        this(null, detail);
    }
}
