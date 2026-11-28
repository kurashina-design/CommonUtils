package jp.kurashina.commons.util;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CollectionUtilsTest {

    @Test
    void emptyChecksHandleNull() {
        assertThat(CollectionUtils.isEmpty(null)).isTrue();
        assertThat(CollectionUtils.isEmpty(List.of())).isTrue();
        assertThat(CollectionUtils.isEmpty(List.of("a"))).isFalse();
        assertThat(CollectionUtils.isNotEmpty(null)).isFalse();
        assertThat(CollectionUtils.isNotEmpty(List.of("a"))).isTrue();
    }

    @Test
    void containsAllRequiresEveryElement() {
        List<String> values = List.of("a", "b", "c");

        assertThat(CollectionUtils.containsAll(values, "a", "c")).isTrue();
        assertThat(CollectionUtils.containsAll(values, "a", "z")).isFalse();
        assertThat(CollectionUtils.containsAll(values)).isTrue();
    }

    @Test
    void isEqualCollectionIgnoresOrderButCountsDuplicates() {
        assertThat(CollectionUtils.isEqualCollection(List.of("a", "b"), List.of("b", "a"))).isTrue();
        assertThat(CollectionUtils.isEqualCollection(List.of("a", "a"), List.of("a"))).isFalse();
    }

    @Test
    void concatSetsReturnsNewUnion() {
        Set<String> one = Set.of("a", "b");
        Set<String> another = Set.of("b", "c");

        Set<String> result = CollectionUtils.concat(one, another);

        assertThat(result).containsExactlyInAnyOrder("a", "b", "c");
        assertThat(one).containsExactlyInAnyOrder("a", "b");
    }

    @Test
    void concatListsKeepsOrderAndDuplicates() {
        List<String> one = List.of("a", "b");
        List<String> another = List.of("b", "c");

        List<String> result = CollectionUtils.concat(one, another);

        assertThat(result).containsExactly("a", "b", "b", "c");
        assertThat(one).containsExactly("a", "b");
    }

    @Test
    void removeAllReturnsNewCollectionWithoutRemovedElements() {
        List<String> source = new ArrayList<>(List.of("a", "b", "c"));

        assertThat(CollectionUtils.removeAll(source, List.of("b"))).containsExactly("a", "c");
        assertThat(source).containsExactly("a", "b", "c");
    }

    @Test
    void getRandomElementReturnsAMember() {
        List<String> values = List.of("a", "b", "c");

        assertThat(CollectionUtils.getRandomElement(values)).isIn(values);
        assertThat(CollectionUtils.getRandomElement(List.of("only"))).isEqualTo("only");
    }
}
