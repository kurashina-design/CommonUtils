package jp.kurashina.commons.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;

@Component
@Slf4j
public class SortUtils {

    public static boolean isEmpty(Sort sort) {
        if (sort == null) {
            return true;
        }
        return sort.isEmpty();
    }

    public static boolean isNotEmpty(Sort sort) {
        return !isEmpty(sort);
    }

    public static Sort getSort(List<String> sortList, Set<String> sortableFields) {
        if (CollectionUtils.isNotEmpty(sortList)) {
            if (sortList.size() == 1) {
                String sortKey = sortList.get(0);
                if (sortableFields.contains(sortKey)) {
                    return Sort.by(getOrderByConvertingPrefix(sortList.get(0)));
                } else {
                    log.error("\"" + sortKey + "\" not allowed sort key.");
                }
            } else {
                List<Sort.Order> orderList = new ArrayList<>();
                sortList.forEach(sortKey -> {
                    if (sortableFields.contains(sortKey)) {
                        orderList.add(getOrderByConvertingPrefix(sortKey));
                    } else {
                        log.error("\"" + sortKey + "\" not allowed sort key.");
                    }
                });
                return Sort.by(orderList);
            }
        }
        return null;
    }

    public static Sort getSort(List<String> sortList, List<String> defaultSortList, Set<String> sortableFields) {
        if (CollectionUtils.isNotEmpty(sortList)) {
            if (sortList.size() == 1) {
                String sortKey = sortList.get(0);
                if (sortableFields.contains(sortKey)) {
                    return Sort.by(getOrderByConvertingPrefix(sortList.get(0)));
                } else {
                    log.error("\"" + sortKey + "\" not allowed sort key.");
                }
            } else {
                List<Sort.Order> orderList = new ArrayList<>();
                sortList.forEach(sortKey -> {
                    if (sortableFields.contains(sortKey)) {
                        orderList.add(getOrderByConvertingPrefix(sortKey));
                    } else {
                        log.error("\"" + sortKey + "\" not allowed sort key.");
                    }
                });
                return Sort.by(orderList);
            }
        } else {
            return getSort(defaultSortList, sortableFields);
        }
        return null;
    }

    public static Sort getSort(List<String> sortList) {
        if (CollectionUtils.isNotEmpty(sortList)) {
            if (sortList.size() == 1) {
                return Sort.by(getOrderByConvertingPrefix(sortList.get(0)));
            } else {
                List<Sort.Order> orderList = new ArrayList<>();
                sortList.forEach(s -> {
                    orderList.add(getOrderByConvertingPrefix(s));
                });
                return Sort.by(orderList);
            }
        }
        return null;
    }

    public static Sort getSort(List<String> sortList, List<String> defaultSortList) {
        if (CollectionUtils.isNotEmpty(sortList)) {
            if (sortList.size() == 1) {
                return Sort.by(getOrderByConvertingPrefix(sortList.get(0)));
            } else {
                List<Sort.Order> orderList = new ArrayList<>();
                sortList.forEach(s -> {
                    orderList.add(getOrderByConvertingPrefix(s));
                });
                return Sort.by(orderList);
            }
        } else {
            return getSort(defaultSortList);
        }
    }

    public static Sort.Order getOrderByConvertingPrefix(String sortParam) {
        if (StringUtils.startsWith(sortParam, "-")) {
            return Sort.Order.desc(StringUtils.removeStart(sortParam, "-"));
        } else {
            return Sort.Order.asc(sortParam);
        }
    }

    public static Sort desc(String sortParam) {
        if (sortParam.startsWith("-")) {
            return Sort.by(Sort.Direction.DESC, StringUtils.removeStart(sortParam, "-"));
        } else {
            return Sort.by(Sort.Direction.DESC, sortParam);
        }
    }

    public static Sort asc(String sortParam) {
        if (sortParam.startsWith("-")) {
            return Sort.by(Sort.Direction.ASC, StringUtils.removeStart(sortParam, "-"));
        } else {
            return Sort.by(Sort.Direction.ASC, sortParam);
        }
    }

    public static String toString(Sort sort) {
        if (SortUtils.isNotEmpty(sort)) {
            return sort.toString().replace(": ", "");
        }
        return "";
    }

    public static String toString(List<String> sortList) {
        StringJoiner joiner = new StringJoiner(",");
        for (String sort : sortList) {
            if (sort.startsWith("-")) {
                joiner.add(StringUtils.removeStart(sort, "-") + " DESC");
            } else {
                joiner.add(sort + " ASC");
            }
        }
        return joiner.toString();
    }

}
