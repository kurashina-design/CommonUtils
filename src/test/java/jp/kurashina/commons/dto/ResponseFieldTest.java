package jp.kurashina.commons.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseFieldTest {

    @Test
    void splitsRootAndNestedFields() {
        ResponseField field = new ResponseField(List.of("id", "name", "children.id", "children.name"));

        assertThat(field.getFields()).containsExactlyInAnyOrder("id", "name", "children");
        assertThat(field.getRootFields()).containsExactlyInAnyOrder("id", "name");
        assertThat(field.getNestedFieldKeySet()).containsExactly("children");
        assertThat(field.getNestedField("children").getFields()).containsExactlyInAnyOrder("id", "name");
        assertThat(field.getNestedField("children").getRootFields()).containsExactlyInAnyOrder("id", "name");
        assertThat(field.getFieldList()).containsExactly("id", "name", "children.id", "children.name");
    }

    @Test
    void buildsDeeplyNestedTree() {
        ResponseField field = new ResponseField(List.of("user.profile.image"));

        ResponseField user = field.getNestedField("user");
        assertThat(user.getFields()).containsExactly("profile");
        assertThat(user.getRootFields()).isEmpty();
        assertThat(user.getNestedField("profile").getFields()).containsExactly("image");
        assertThat(user.getNestedField("profile").getRootFields()).containsExactly("image");
    }

    @Test
    void emptyOrNullListProducesEmptyField() {
        assertThat(new ResponseField(List.of()).isEmpty()).isTrue();
        assertThat(new ResponseField((List<String>) null).isEmpty()).isTrue();
        assertThat(new ResponseField().isNotEmpty()).isFalse();
    }

    @Test
    void containsKeyChecksFieldsAndNestedKeys() {
        ResponseField field = new ResponseField(List.of("id", "children.id"));

        assertThat(field.containsKey("id")).isTrue();
        assertThat(field.containsKey("children")).isTrue();
        assertThat(field.containsKey("name")).isFalse();
        assertThat(field.containsAllKeys("id", "children")).isTrue();
        assertThat(field.containsAllKeys("id", "name")).isFalse();
    }

    @Test
    void containsAllUsesTopLevelFields() {
        ResponseField field = new ResponseField(List.of("id", "name", "children.id"));

        assertThat(field.containsAll("id", "name")).isTrue();
        assertThat(field.containsAll("id", "missing")).isFalse();
        assertThat(new ResponseField().containsAll("id")).isFalse();
    }

    @Test
    void removeFieldDropsItEverywhere() {
        ResponseField field = new ResponseField(List.of("id", "children.id"));

        field.removeField("children");
        field.removeField("id");

        assertThat(field.getFields()).isEmpty();
        assertThat(field.getRootFields()).isEmpty();
        assertThat(field.getNestedFields()).isEmpty();
        assertThat(field.isEmpty()).isTrue();
    }

    @Test
    void addNestedFieldCreatesChildOnDemand() {
        ResponseField field = new ResponseField();

        field.addNestedField("owner", "name");
        field.addNestedField("owner", "address.city");

        ResponseField owner = field.getNestedField("owner");
        assertThat(owner.getFields()).containsExactlyInAnyOrder("name", "address");
        assertThat(owner.getNestedField("address").getFields()).containsExactly("city");
        assertThat(field.isNotEmpty()).isTrue();
    }

    @Test
    void copyConstructorKeepsSameContent() {
        ResponseField original = new ResponseField(List.of("id", "children.id"));

        ResponseField copy = new ResponseField(original);

        assertThat(copy.getFields()).isEqualTo(original.getFields());
        assertThat(copy.getRootFields()).isEqualTo(original.getRootFields());
        assertThat(copy.getNestedFields()).isEqualTo(original.getNestedFields());
    }
}
