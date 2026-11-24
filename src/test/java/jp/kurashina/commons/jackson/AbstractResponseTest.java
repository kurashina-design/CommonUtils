package jp.kurashina.commons.jackson;

import jp.kurashina.commons.resource.AbstractResponse;
import jp.kurashina.commons.resource.PingResponse;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractResponseTest {

    private final ObjectMapper objectMapper = JsonMapper.builder().build();

    @Test
    void omitsEmptyValuesAndKeepsZeroFalseAndNonEmptyValues() {
        TestResponse response = new TestResponse();

        String json = objectMapper.writeValueAsString(response);

        assertThat(json)
                .contains("\"zero\":0", "\"disabled\":false", "\"value\":\"x\"")
                .doesNotContain("nullValue", "emptyString", "emptyList", "emptyMap");
    }

    @Test
    void serializesPingResponse() {
        assertThat(objectMapper.writeValueAsString(new PingResponse()))
                .isEqualTo("{\"status\":\"ok\"}");
    }

    private static class TestResponse extends AbstractResponse {

        public String getNullValue() {
            return null;
        }

        public String getEmptyString() {
            return "";
        }

        public List<String> getEmptyList() {
            return Collections.emptyList();
        }

        public Map<String, String> getEmptyMap() {
            return Collections.emptyMap();
        }

        public int getZero() {
            return 0;
        }

        public boolean isDisabled() {
            return false;
        }

        public String getValue() {
            return "x";
        }
    }
}
