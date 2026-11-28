package jp.kurashina.commons.util;

import jp.kurashina.commons.dto.ResponseField;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseFieldUtilsTest {

    @Test
    void buildReturnsNullForEmptyInput() {
        assertThat(ResponseFieldUtils.build(null)).isNull();
        assertThat(ResponseFieldUtils.build(List.of())).isNull();
    }

    @Test
    void buildCreatesFieldTree() {
        ResponseField field = ResponseFieldUtils.build(List.of("id", "children.name"));

        assertThat(field).isNotNull();
        assertThat(field.getRootFields()).containsExactly("id");
        assertThat(field.getNestedField("children").getFields()).containsExactly("name");
    }

    @Test
    void isEmptyTreatsNullAsEmpty() {
        assertThat(ResponseFieldUtils.isEmpty(null)).isTrue();
        assertThat(ResponseFieldUtils.isEmpty(new ResponseField())).isTrue();
        assertThat(ResponseFieldUtils.isNotEmpty(null)).isFalse();
        assertThat(ResponseFieldUtils.isNotEmpty(ResponseFieldUtils.build(List.of("id")))).isTrue();
    }

    @Test
    void containsAllChecksTopLevelFields() {
        ResponseField field = ResponseFieldUtils.build(List.of("id", "name", "children.id"));

        assertThat(ResponseFieldUtils.containsAll(field, "id", "children")).isTrue();
        assertThat(ResponseFieldUtils.containsAll(field, "id", "missing")).isFalse();
    }
}
