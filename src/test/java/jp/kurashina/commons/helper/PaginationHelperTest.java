package jp.kurashina.commons.helper;

import jp.kurashina.commons.exception.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PaginationHelperTest {

    private final PaginationHelper<Object, Object> paginationHelper = new PaginationHelper<>();

    @Test
    void returnsPageableWithValidatedSort() {
        Pageable pageable = paginationHelper.getPageable(20, 1, List.of("id", "-createdDate"),
                Set.of("id", "createdDate"));

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getSort().getOrderFor("id")).extracting(Sort.Order::getDirection)
                .isEqualTo(Sort.Direction.ASC);
        assertThat(pageable.getSort().getOrderFor("createdDate")).extracting(Sort.Order::getDirection)
                .isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void rejectsInvalidSortKey() {
        assertThatThrownBy(() -> paginationHelper.getPageable(20, 1, List.of("bogus"), Set.of("id")))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Unknown sort field: bogus");
    }

    @Test
    void returnsUnsortedPageableForEmptySortList() {
        Pageable pageable = paginationHelper.getPageable(20, 1, Collections.emptyList(), Set.of("id"));

        assertThat(pageable.getPageNumber()).isZero();
        assertThat(pageable.getPageSize()).isEqualTo(20);
        assertThat(pageable.getSort()).isEqualTo(Sort.unsorted());
    }
}
