package jp.kurashina.commons.helper;

import jp.kurashina.commons.dto.ResponseField;
import jp.kurashina.commons.resource.CurrentNumberOfElementsMap;
import jp.kurashina.commons.resource.Pagination;
import jp.kurashina.commons.util.CollectionUtils;
import jp.kurashina.commons.util.ResponseFieldUtils;
import jp.kurashina.commons.util.SortUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

@Component
@Getter
@Setter
public class PaginationHelper<E,R> {

    private int defaultPerPage = 20;

    public Pagination<R> get(Page<E> page, String query, ResponseField responseField, List<R> contentList) {
        Pagination<R> pagination = new Pagination<>();
        pagination.setEmpty(page.isEmpty());
        pagination.setTotalPages(page.getTotalPages());
        pagination.setTotalElements(page.getTotalElements());
        pagination.setCurrentPageNumber(page.getNumber() + 1);
        pagination.setPerPage(page.getSize());
        pagination.setOffset(page.getPageable().getOffset());
        pagination.setSort(getSortStr(page.getSort()));
        pagination.setSortAbs(getSortAbs(page.getSort()));
        pagination.setSortMap(getSortMap(page.getSort()));
        pagination.setCurrentNumberOfElements(page.getNumberOfElements());
        pagination.setCurrentNumberOfElementsMap(getCurrentNumberOfElementsMap(pagination.getOffset(), pagination.getCurrentNumberOfElements(), pagination.isEmpty()));
        pagination.setHasPrevious(page.hasPrevious());
        pagination.setHasNext(page.hasNext());

        if (pagination.isHasPrevious()) {
            pagination.setPrevPageNumber(pagination.getCurrentPageNumber() - 1);
        }

        if (pagination.isHasNext()) {
            pagination.setNextPageNumber(pagination.getCurrentPageNumber() + 1);
        }

        int lastPageNumber = (int) (pagination.getTotalElements() / pagination.getPerPage());
        if (pagination.getCurrentPageNumber() > lastPageNumber) {
            pagination.setPrevPageNumber(lastPageNumber);
        }

        if (ResponseFieldUtils.isNotEmpty(responseField)) {
            pagination.setFields(responseField.getFieldList());
        }
        if (StringUtils.isNotEmpty(query)) {
            pagination.setQ(query);
        }
        pagination.setContent(contentList);
        return pagination;
    };

    public Pagination<R> get(Page<E> page, long directoryId, String query, ResponseField responseField, List<R> contentList) {
        Pagination<R> pagination = get(page, query, responseField, contentList);
        pagination.getOptions().put("directoryId", directoryId);
        return pagination;
    }

    public CurrentNumberOfElementsMap getCurrentNumberOfElementsMap(long offset, int currentNumberOfElements, boolean empty) {
        CurrentNumberOfElementsMap currentNumberOfElementsMap = new CurrentNumberOfElementsMap();
        if (empty) {
            currentNumberOfElementsMap.setStart(0L);
            currentNumberOfElementsMap.setEnd(0L);
        } else {
            currentNumberOfElementsMap.setStart(offset + 1);
            currentNumberOfElementsMap.setEnd(offset + currentNumberOfElements);
        }
        return currentNumberOfElementsMap;
    }

    public Map<String, Sort.Direction> getSortMap(Sort sort) {
        Map<String, Sort.Direction> sortMap = new HashMap<>();
        for (Sort.Order order : sort.toList()) {
            sortMap.put(order.getProperty(), order.getDirection());
        }
        return sortMap;
    }

    public String getSortAbs(List<String> sortList) {
        if (CollectionUtils.isNotEmpty(sortList)) {
            StringJoiner joiner = new StringJoiner(",");
            for (String sort : sortList) {
                if (sort.startsWith("-")) {
                    joiner.add(sort.replaceFirst("-", ""));
                } else {
                    joiner.add(sort);
                }
            }
            return joiner.toString();
        } else {
            return "";
        }
    }

    public String getSortStr(Sort sort) {
        if (SortUtils.isNotEmpty(sort)) {
            StringJoiner joiner = new StringJoiner(",");
            for (Sort.Order order : sort) {
                if (order.isDescending()) {
                    joiner.add("-" + order.getProperty());
                } else {
                    joiner.add(order.getProperty());
                }
            }
            return joiner.toString();
        }
        return "";
    }

    public String getSortAbs(Sort sort) {
        if (SortUtils.isNotEmpty(sort)) {
            StringJoiner joiner = new StringJoiner(",");
            for (Sort.Order order : sort) {
                joiner.add(order.getProperty());
            }
            return joiner.toString();
        }
        return "";
    }

    public Pageable getPageable(Integer perPage, Integer page, List<String> sortList) {
        if (perPage == null) {
            perPage = defaultPerPage;
        }
        if (page == null) {
            page = 0;
        } else {
            page = page - 1;
        }
        if (CollectionUtils.isNotEmpty(sortList)) {
            return PageRequest.of(page, perPage, Objects.requireNonNull(SortUtils.getSort(sortList)));
        } else {
            return PageRequest.of(page, perPage);
        }
    }

    public Pageable replaceSort(Pageable pageable, Sort newSort) {
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), newSort);
    }

    public Pageable getPageable(Integer perPage, Integer page, Sort sort) {
        if (perPage == null) {
            perPage = defaultPerPage;
        }
        if (page == null) {
            page = 0;
        } else {
            page = page - 1;
        }
        if (SortUtils.isNotEmpty(sort)) {
            return PageRequest.of(page, perPage, sort);
        } else {
            return PageRequest.of(page, perPage);
        }
    }

    @SuppressWarnings({ "unchecked "})
    public void setOffspring(Collection<E> entityCollection, E entity, String offspringFieldName) {
        try {
            Class<?> clazz = entity.getClass();
            Field offspringField = clazz.getDeclaredField(offspringFieldName);
            offspringField.setAccessible(true);
            Collection<E> collection = (Collection<E>) offspringField.get(entity);
            if (CollectionUtils.isNotEmpty(collection)) {
                for (E e : collection) {
                    entityCollection.add(e);
                    setOffspring(entityCollection, e, offspringFieldName);
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings({ "unchecked "})
    public <I> List<E> getParentOrientedSortedList(List<E> entityList, String idFieldName, String parentIdFieldName, String childrenFieldName) {
        Map<I, E> entityMap = new LinkedHashMap<>();
        entityList.forEach(entity -> {
            Class<?> clazz = entity.getClass();
            try {
                Field idField = clazz.getDeclaredField(idFieldName);
                idField.setAccessible(true);
                entityMap.put((I) idField.get(entity), entity);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        entityList.forEach(entity -> {
            Class<?> clazz = entity.getClass();
            try {
                Field parentIdField = clazz.getDeclaredField(parentIdFieldName);
                parentIdField.setAccessible(true);
                if (parentIdField.get(entity) != null) {
                    E parent = entityMap.get((I) parentIdField.get(entity));
                    Field childrenField = clazz.getDeclaredField(childrenFieldName);
                    childrenField.setAccessible(true);
                    Collection<E> children = (Collection<E>) childrenField.get(parent);
                    children.add(entity);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        List<E> newEntityList = new ArrayList<>();
        for (E entity : entityList) {
            Class<?> clazz = entity.getClass();
            try {
                Field parentIdField = clazz.getDeclaredField(parentIdFieldName);
                parentIdField.setAccessible(true);
                if (parentIdField.get(entity) == null) {
                    newEntityList.add(entity);
                    setOffspring(newEntityList, entity, childrenFieldName);
                }
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return newEntityList;
    }

}
