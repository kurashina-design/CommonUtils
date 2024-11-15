package jp.kurashina.commons.dto;

import jp.kurashina.commons.util.CollectionUtils;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

@Data
public class ResponseField implements Serializable {

    @Serial
    private static final long serialVersionUID = -8594377996552366427L;

    public ResponseField() {}

    public ResponseField(List<String> fieldList) {
        if (CollectionUtils.isNotEmpty(fieldList)) {
            for (String field : fieldList) {
                if (!field.contains(".")) {
                    addField(field);
                    addRootField(field);
                } else {
                    String[] splitFields = getSplitFields(field);
                    addNestedField(splitFields[0], splitFields[1]);
                    addField(splitFields[0]);
                }
            }
            this.fieldList = fieldList;
        }
    }

    public ResponseField(ResponseField responseField) {
        this.fields = responseField.getFields();
        this.rootFields = responseField.getRootFields();
        this.nestedFields = responseField.getNestedFields();
    }

    private Set<String> fields = new HashSet<>();
    private Set<String> rootFields = new HashSet<>();
    private Map<String, ResponseField> nestedFields = new HashMap<>();
    private List<String> fieldList = new ArrayList<>();

    private String[] getSplitFields(String field) {
        int index = field.indexOf(".");
        String[] splitFields = new String[2];
        splitFields[0] = field.substring(0, index);
        splitFields[1] = field.substring((index + 1));
        return splitFields;
    }

    public void addNestedField(String fieldKey, String field) {
        ResponseField resourceField = this.nestedFields.get(fieldKey);
        if (resourceField == null) {
            resourceField = new ResponseField();
            this.nestedFields.put(fieldKey, resourceField);
        }
        if (!field.contains(".")) {
            resourceField.addField(field);
            resourceField.addRootField(field);
        } else {
            String[] splitFields = getSplitFields(field);
            resourceField.addNestedField(splitFields[0], splitFields[1]);
            resourceField.addField(splitFields[0]);
        }
    }

    public ResponseField getNestedField(String fieldKey) {
        return nestedFields.get(fieldKey);
    }

    public Set<String> getNestedFieldKeySet() {
        return this.nestedFields.keySet();
    }

    public void addField(String field) {
        this.fields.add(field);
    }

    public void addRootField(String rootField) {
        this.rootFields.add(rootField);
    }

    public void removeField(String fieldKey) {
        fields.remove(fieldKey);
        rootFields.remove(fieldKey);
        nestedFields.remove(fieldKey);
    }

    public boolean isEmpty() {
        return fields.isEmpty() && nestedFields.isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean containsKey(String fieldKey) {
        if (getFields().contains(fieldKey)) {
            return true;
        }
        return getNestedFields().containsKey(fieldKey);
    }

    public boolean containsAllKeys(String... args) {
        for (String arg : args) {
            if (!containsKey(arg)) {
                return false;
            }
        }
        return true;
    }

    public boolean containsAll(String... args) {
        if (CollectionUtils.isEmpty(this.fields)) return false;
        return CollectionUtils.containsAll((Collection<?>) this.fields, args);
    }
}