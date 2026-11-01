package jp.kurashina.commons.jackson;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class BigDecimalIntegerSerializerTest {

    private final JsonMapper jsonMapper = JsonMapper.builder()
            .addModule(new SimpleModule()
                    .addSerializer(BigDecimal.class, new BigDecimalIntegerSerializer()))
            .build();

    @Test
    void writesWholeNumberWithoutDecimalPart() {
        assertThat(jsonMapper.writeValueAsString(new BigDecimal("42"))).isEqualTo("42");
        assertThat(jsonMapper.writeValueAsString(new BigDecimal("42.00"))).isEqualTo("42.00");
    }

    @Test
    void preservesFractionalNumber() {
        assertThat(jsonMapper.writeValueAsString(new BigDecimal("42.5"))).isEqualTo("42.5");
    }
}
