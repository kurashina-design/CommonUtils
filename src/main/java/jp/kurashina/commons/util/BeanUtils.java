package jp.kurashina.commons.util;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class BeanUtils {

    public static void copyProperties(Object source, Object target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target);
    }

    public static void copyProperties(Object source, Object target, String... ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties);
    }

    public static void copyProperties(Object source, Object target, Set<String> ignoreProperties) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, ignoreProperties.toArray(new String[0]));
    }

    public static void copySpecificProperties(Object source, Object target, Collection<String> specificProperties, Set<String> ignoreProperties) {
        BeanWrapper sourceBean = PropertyAccessorFactory.forBeanPropertyAccess(source);
        BeanWrapper targetBean = PropertyAccessorFactory.forBeanPropertyAccess(target);
        if (CollectionUtils.isNotEmpty(ignoreProperties)) {
            for (String property : specificProperties) {
                if (!ignoreProperties.contains(property)) {
                    try {
                        targetBean.setPropertyValue(property, sourceBean.getPropertyValue(property));
                    } catch (Exception ignored) {}
                }
            }
        } else {
            for (String property : specificProperties) {
                try {
                    targetBean.setPropertyValue(property, sourceBean.getPropertyValue(property));
                } catch (Exception ignored) {}
            }
        }
    }

    public static void copySpecificProperties(Object source, Object target, Collection<String> specificProperties) {
        if (CollectionUtils.isEmpty(specificProperties)) {
            org.springframework.beans.BeanUtils.copyProperties(source, target);
        } else {
            BeanWrapper sourceBean = PropertyAccessorFactory.forBeanPropertyAccess(source);
            BeanWrapper targetBean = PropertyAccessorFactory.forBeanPropertyAccess(target);
            for (String property : specificProperties) {
                try {
                    targetBean.setPropertyValue(property, sourceBean.getPropertyValue(property));
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static Map<String, Set<String>> fieldListToMap(Collection<String> fieldList) {
        if (CollectionUtils.isEmpty(fieldList)) {
            return null;
        }
        Map<String, Set<String>> fieldSet = new HashMap<>();
        for (String field : fieldList) {
            String entityName;
            String fieldName;
            if (!field.contains(".")) {
                entityName = "";
                fieldName = field;
                fieldSetToMapInner(fieldSet, entityName, fieldName);
            } else {
                String[] fieldArr = field.split("\\.");
                entityName = fieldArr[0];
                fieldName = fieldArr[1];
                fieldSetToMapInner(fieldSet, entityName, fieldName);
            }
        }
        return fieldSet;
    }

    private static void fieldSetToMapInner(Map<String, Set<String>> fieldMap, String entityName, String fieldName) {
        Set<String> fields;
        if (fieldMap.get(entityName) != null) {
            fields = fieldMap.get(entityName);
        } else {
            fields = new HashSet<>();
        }
        fields.add(fieldName);
        fieldMap.put(entityName, fields);
    }

    public static Set<String> generateSortKeySet(Set<String> setOfSortKeySources) {
        Set<String> sortKeySet = new HashSet<>(setOfSortKeySources);
        setOfSortKeySources.forEach(sortKeySource -> sortKeySet.add("-" + sortKeySource));
        return sortKeySet;
    }
}
