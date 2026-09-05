package jp.kurashina.commons.api.error;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApiErrorResponseFactoryTest {

    @Test
    void derivesStatusMessageAndKeepsSpecific4xxDetail() {
        ApiErrorResponse response = ApiErrorResponseFactory.create(
                HttpStatus.BAD_REQUEST,
                "Email already exists",
                "request-1",
                List.of(new ApiErrorDetail("email", "already exists")),
                Map.of("code", "EMAIL_EXISTS")
        );

        assertThat(response.getStatusCode()).isEqualTo(400);
        assertThat(response.getStatusMessage()).isEqualTo("Bad Request");
        assertThat(response.getDetail()).isEqualTo("Email already exists");
    }

    @Test
    void replaces5xxDetailWithFixedMessage() {
        ApiErrorResponse response = ApiErrorResponseFactory.create(
                HttpStatus.INTERNAL_SERVER_ERROR, "SQL connection failed", "request-2"
        );

        assertThat(response.getDetail()).isEqualTo(ApiErrorResponseFactory.INTERNAL_SERVER_ERROR_DETAIL);
    }

    @Test
    void rejectsReservedDataKeys() {
        assertThatThrownBy(() -> ApiErrorResponseFactory.create(
                HttpStatus.BAD_REQUEST, "invalid", null, null, Map.of("requestId", "duplicate")
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
