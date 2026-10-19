package jp.kurashina.commons.util;

import jp.kurashina.commons.exception.BadRequestException;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SortUtilsTest {

    private static final Set<String> SORTABLE_FIELDS = Set.of("id", "directory.name");
    private static final String INVALID_FORMAT_MESSAGE =
            "Invalid sort format. Use fieldName or -fieldName.";

    @Test
    void acceptsValidKeysIncludingNestedPaths() {
        assertThatCode(() -> SortUtils.validateSortKeys(
                List.of("id", "-id", "directory.name", "-directory.name"), SORTABLE_FIELDS))
                .doesNotThrowAnyException();
    }

    @Test
    void acceptsNullOrEmptySortList() {
        assertThatCode(() -> SortUtils.validateSortKeys(null, SORTABLE_FIELDS))
                .doesNotThrowAnyException();
        assertThatCode(() -> SortUtils.validateSortKeys(Collections.emptyList(), SORTABLE_FIELDS))
                .doesNotThrowAnyException();
    }

    @Test
    void rejectsUnknownAscendingAndDescendingKeysWithoutPrefixInMessage() {
        for (String sortKey : List.of("bogus", "-bogus")) {
            assertThatThrownBy(() -> SortUtils.validateSortKeys(List.of(sortKey), SORTABLE_FIELDS))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage("Unknown sort field: bogus");
        }
    }

    @Test
    void rejectsUnknownNestedPath() {
        assertThatThrownBy(() -> SortUtils.validateSortKeys(List.of("directory.bogus"), SORTABLE_FIELDS))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Unknown sort field: directory.bogus");
    }

    @Test
    void treatsFieldNamesAsCaseSensitive() {
        assertThatThrownBy(() -> SortUtils.validateSortKeys(List.of("ID"), SORTABLE_FIELDS))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Unknown sort field: ID");
    }

    @Test
    void rejectsInvalidFormatsWithoutReflectingInputInMessage() {
        List<String> invalidKeys = Arrays.asList("id;drop table", "--id", "id desc", " ", null);

        for (String sortKey : invalidKeys) {
            assertThatThrownBy(() -> SortUtils.validateSortKeys(
                    Collections.singletonList(sortKey), SORTABLE_FIELDS))
                    .isInstanceOf(BadRequestException.class)
                    .hasMessage(INVALID_FORMAT_MESSAGE)
                    .hasMessageNotContaining("drop");
        }
    }

    @Test
    void rejectsEveryKeyWhenAllowedFieldsAreEmpty() {
        assertThatThrownBy(() -> SortUtils.validateSortKeys(List.of("id"), Collections.emptySet()))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Unknown sort field: id");
    }

    @Test
    void rejectsNullAllowedFields() {
        assertThatThrownBy(() -> SortUtils.validateSortKeys(List.of("id"), null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsInvalidKeyAfterValidKey() {
        assertThatThrownBy(() -> SortUtils.validateSortKeys(List.of("id", "bogus"), SORTABLE_FIELDS))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Unknown sort field: bogus");
    }
}
