package jp.kurashina.commons.jackson;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;

import java.math.BigDecimal;

public class BigDecimalIntegerSerializer extends ValueSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializationContext context) {
        if (value != null && value.scale() <= 0) {
            gen.writeNumber(value.intValue());
        } else {
            gen.writeNumber(value);
        }
    }

}
