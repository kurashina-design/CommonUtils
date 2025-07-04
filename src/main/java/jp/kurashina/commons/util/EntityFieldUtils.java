package jp.kurashina.commons.util;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class EntityFieldUtils {

    private static final Pattern UPPER = Pattern.compile("[A-Z0-9_]+");
    private static final Set<String> EXCLUDING_FIELDS = Set.of("serialVersionUID");

    public static Set<String> getSortableFiels(Class<?> clazz, Set<String> excludes) {
        if (CollectionUtils.isEmpty(excludes)) {
            return getSortableFiels(clazz);
        }
        Set<String> sortableFields = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (!UPPER.matcher(field.getName()).matches() && !EXCLUDING_FIELDS.contains(field.getName()) && !field.getName().contains("$") && !excludes.contains(field.getName())) {
                boolean hasRelations = false;
                List<Annotation> annotations = Arrays.stream(field.getDeclaredAnnotations()).toList();
                if (CollectionUtils.isNotEmpty(annotations)) {
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof OneToOne) {
                            hasRelations = true;
                            break;
                        } else if (annotation instanceof OneToMany) {
                            hasRelations = true;
                            break;
                        } else if (annotation instanceof ManyToOne) {
                            hasRelations = true;
                            break;
                        } else if (annotation instanceof ManyToMany) {
                            hasRelations = true;
                            break;
                        }
                    }
                }
                if (!hasRelations) {
                    sortableFields.add(field.getName());

                }
            }
        }
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            try {
                Field superSortableField = superClazz.getDeclaredField("SORTABLE_FIELDS");
                Set<String> superSortableFieldValue = (HashSet<String>) superSortableField.get(superClazz);
                if (CollectionUtils.isNotEmpty(superSortableFieldValue)) {
                    sortableFields.addAll(superSortableFieldValue);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
        return sortableFields;
    }

    public static Set<String> getSortableFiels(Class<?> clazz) {
        Set<String> sortableFields = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (!UPPER.matcher(field.getName()).matches() && !EXCLUDING_FIELDS.contains(field.getName()) && !field.getName().contains("$")) {
                boolean hasRelations = false;
                List<Annotation> annotations = Arrays.stream(field.getDeclaredAnnotations()).toList();
                if (CollectionUtils.isNotEmpty(annotations)) {
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof OneToOne) {
                            hasRelations = true;
                            break;
                        } else if (annotation instanceof OneToMany) {
                            hasRelations = true;
                            break;
                        } else if (annotation instanceof ManyToOne) {
                            hasRelations = true;
                            break;
                        } else if (annotation instanceof ManyToMany) {
                            hasRelations = true;
                            break;
                        }
                    }
                }
                if (!hasRelations) {
                    sortableFields.add(field.getName());

                }
            }
        }
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            try {
                Field superSortableField = superClazz.getDeclaredField("SORTABLE_FIELDS");
                Set<String> superSortableFieldValue = (HashSet<String>) superSortableField.get(superClazz);
                if (CollectionUtils.isNotEmpty(superSortableFieldValue)) {
                    sortableFields.addAll(superSortableFieldValue);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
        return sortableFields;
    }

    public static Set<String> getSearchableFields(Class<?> clazz, Set<String> excludes) {
        if (CollectionUtils.isEmpty(excludes)) {
            return getSearchableFields(clazz);
        }
        Set<String> searchableFields = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (!UPPER.matcher(field.getName()).matches() && !EXCLUDING_FIELDS.contains(field.getName()) && !field.getName().contains("$") && !excludes.contains(field.getName())) {
                if (field.getType().getSimpleName().equals("String")) {
                    searchableFields.add(field.getName());
                }
            }
        }
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            try {
                Field superSearchableField = superClazz.getDeclaredField("SEARCHABLE_FIELDS");
                Set<String> superSearchableFieldValue = (HashSet<String>) superSearchableField.get(superClazz);
                if (CollectionUtils.isNotEmpty(superSearchableFieldValue)) {
                    searchableFields.addAll(superSearchableFieldValue);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
        return searchableFields;
    }

    public static Set<String> getSearchableFields(Class<?> clazz) {
        Set<String> searchableFields = new HashSet<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (!UPPER.matcher(field.getName()).matches() && !EXCLUDING_FIELDS.contains(field.getName()) && !field.getName().contains("$")) {
                if (field.getType().getSimpleName().equals("String")) {
                    searchableFields.add(field.getName());
                }
            }
        }
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            try {
                Field superSearchableField = superClazz.getDeclaredField("SEARCHABLE_FIELDS");
                Set<String> superSearchableFieldValue = (HashSet<String>) superSearchableField.get(superClazz);
                if (CollectionUtils.isNotEmpty(superSearchableFieldValue)) {
                    searchableFields.addAll(superSearchableFieldValue);
                }
            } catch (NoSuchFieldException | IllegalAccessException ignored) {}
        }
        return searchableFields;
    }

    public static Set<String> getCloneFields(Set<String> sourceFields, Set<String> excludes) {
        Set<String> newFields = new HashSet<>(sourceFields);
        excludes.forEach(newFields::remove);
        return newFields;
    }

    public static void printEntityFields(Object obj) {
        if (obj == null) {
            System.out.println("null");
            return;
        }

        String toStringResult = obj.toString();
        String[] lines = toStringResult.split(","); // Windows, Mac, Linux の改行コードに対応

        System.out.println("--- " + obj.getClass().getSimpleName() + ".toString() の出力 ---");
        for (String line : lines) {
            System.out.println(line.trim());
        }
        System.out.println("--------------------------------------------------");
    }

}
