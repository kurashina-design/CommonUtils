package jp.kurashina.commons.util;

import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class BeanUtilsTest {

    @Data
    static class Person {
        private Long id;
        private String name;
        private String email;
        private Integer age;
    }

    private static Person person(Long id, String name, String email, Integer age) {
        Person person = new Person();
        person.setId(id);
        person.setName(name);
        person.setEmail(email);
        person.setAge(age);
        return person;
    }

    @Test
    void copyPropertiesCopiesEverything() {
        Person target = new Person();

        BeanUtils.copyProperties(person(1L, "Alice", "a@example.com", 30), target);

        assertThat(target).isEqualTo(person(1L, "Alice", "a@example.com", 30));
    }

    @Test
    void copyPropertiesSkipsIgnoredNames() {
        Person target = person(9L, "Old", "old@example.com", 9);

        BeanUtils.copyProperties(person(1L, "Alice", "a@example.com", 30), target, "id", "email");

        assertThat(target).isEqualTo(person(9L, "Alice", "old@example.com", 30));
    }

    @Test
    void copyPropertiesSkipsIgnoredSet() {
        Person target = person(9L, "Old", "old@example.com", 9);

        BeanUtils.copyProperties(person(1L, "Alice", "a@example.com", 30), target, Set.of("age"));

        assertThat(target).isEqualTo(person(1L, "Alice", "a@example.com", 9));
    }

    @Test
    void copyPatchPropertiesKeepsTargetValuesForNullSourceValues() {
        Person target = person(9L, "Old", "old@example.com", 9);

        BeanUtils.copyPatchProperties(person(null, "New", null, 40), target);

        assertThat(target).isEqualTo(person(9L, "New", "old@example.com", 40));
    }

    @Test
    void copySpecificPropertiesCopiesOnlyListedNames() {
        Person target = person(9L, "Old", "old@example.com", 9);

        BeanUtils.copySpecificProperties(person(1L, "Alice", "a@example.com", 30), target, List.of("name", "age"));

        assertThat(target).isEqualTo(person(9L, "Alice", "old@example.com", 30));
    }

    @Test
    void copySpecificPropertiesAppliesIgnoreSetToListedNames() {
        Person target = person(9L, "Old", "old@example.com", 9);

        BeanUtils.copySpecificProperties(person(1L, "Alice", "a@example.com", 30), target,
                List.of("name", "age"), Set.of("age"));

        assertThat(target).isEqualTo(person(9L, "Alice", "old@example.com", 9));
    }

    @Test
    void copySpecificPropertiesWithNullListCopiesNothing() {
        Person target = person(9L, "Old", "old@example.com", 9);

        BeanUtils.copySpecificProperties(person(1L, "Alice", "a@example.com", 30), target, null);

        assertThat(target).isEqualTo(person(9L, "Old", "old@example.com", 9));
    }

    @Test
    void fieldListToMapGroupsByRoot() {
        Map<String, Set<String>> map = BeanUtils.fieldListToMap(List.of("id", "user.name", "user.profile.image"));

        assertThat(map).containsOnlyKeys("id", "user");
        assertThat(map.get("id")).isEmpty();
        assertThat(map.get("user")).containsExactlyInAnyOrder("name", "profile.image");
    }

    @Test
    void fieldListToMapReturnsEmptyMapForNull() {
        assertThat(BeanUtils.fieldListToMap(null)).isEmpty();
    }

    @Test
    void generateSortKeySetAddsDescendingVariants() {
        assertThat(BeanUtils.generateSortKeySet(Set.of("id", "name")))
                .containsExactlyInAnyOrder("id", "-id", "name", "-name");
    }
}
