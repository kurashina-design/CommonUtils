package jp.kurashina.commons.jackson;

import jp.kurashina.commons.api.error.ApiErrorDetail;
import jp.kurashina.commons.api.error.ApiErrorResponse;
import jp.kurashina.commons.api.error.ApiErrorResponseWriter;
import jp.kurashina.commons.resource.PaginationPathParameters;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Jackson3CompatibilityTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Test
    void honorsExistingJacksonAnnotations() {
        PaginationPathParameters parameters = new PaginationPathParameters();
        parameters.setPage(2);
        parameters.setPerPage(50);

        String json = objectMapper.writeValueAsString(parameters);

        assertThat(json).contains("\"page\":2", "\"per_page\":50")
                .doesNotContain("perPage");
    }

    @Test
    void writesApiErrorResponseAndOmitsEmptyProperties() throws Exception {
        ApiErrorResponse body = new ApiErrorResponse(
                400,
                "Invalid request",
                null,
                List.of(new ApiErrorDetail("name", "must not be blank")),
                null
        );
        MockHttpServletResponse response = new MockHttpServletResponse();

        new ApiErrorResponseWriter(objectMapper).write(response, body);

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getContentAsString()).contains(
                        "\"statusCode\":400",
                        "\"statusMessage\":\"Bad Request\"",
                        "\"detail\":\"Invalid request\"",
                        "\"field\":\"name\"")
                .doesNotContain("requestId", "\"data\"");
    }

    @Test
    void serializesIntegralBigDecimalWithoutDecimalPart() {
        SimpleModule module = new SimpleModule()
                .addSerializer(BigDecimal.class, new BigDecimalIntegerSerializer());
        ObjectMapper mapper = JsonMapper.builder().addModule(module).build();

        assertThat(mapper.writeValueAsString(new BigDecimal("123"))).isEqualTo("123");
        assertThat(mapper.writeValueAsString(new BigDecimal("123.45"))).isEqualTo("123.45");
    }
}
