package jp.kurashina.commons.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Pagination<T> {

    private String q;

    private boolean empty;

    private int totalPages;

    private long totalElements;

    private int currentPageNumber;

    private Integer nextPageNumber;

    private Integer prevPageNumber;

    private int perPage;

    private long offset;

    private String sort;

    private String sortAbs;

    private Map<String, Sort.Direction> sortMap;

    private int currentNumberOfElements;

    private CurrentNumberOfElementsMap currentNumberOfElementsMap;

    private boolean hasPrevious;

    private boolean hasNext;

    private List<String> fields;

    private List<T> content;

    private Map<String, Object> options = new HashMap<>();
}
