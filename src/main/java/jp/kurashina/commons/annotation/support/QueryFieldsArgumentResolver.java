package jp.kurashina.commons.annotation.support;

import jp.kurashina.commons.annotation.QueryFields;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryFieldsArgumentResolver implements HandlerMethodArgumentResolver {

    public List<String> normalizeBracketFields(String source) {
        // 前処理: 空白を削除
        source = source.replaceAll("\\s+", "");

        // カスタム区切り文字を使ってフィールドを分割
        String normalizedSource = replaceCommasWithinBrackets(source);
        List<String> splittedFields = Arrays.stream(normalizedSource.split(","))
                .map(s -> s.replace("𐐷", ","))
                .toList();

        // フィールドを解析してセットに追加
        Set<String> fieldList = new HashSet<>();
        for (String field : splittedFields) {
            processField(field, fieldList);
        }

        // 結果をソートして返す
        return fieldList.stream().toList();
    }

    private String replaceCommasWithinBrackets(String source) {
        StringBuilder builder = new StringBuilder();
        boolean insideBrackets = false;

        for (char c : source.toCharArray()) {
            if (c == '[') {
                insideBrackets = true;
            } else if (c == ']') {
                insideBrackets = false;
            }

            if (insideBrackets && c == ',') {
                builder.append("𐐷"); // カスタム区切り文字に置き換え
            } else {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    private void processField(String field, Set<String> fieldList) {
        if (!field.contains("[") && !field.contains("]")) {
            // 単純なフィールドの場合
            fieldList.add(field);
        } else {
            // パターンマッチングでフィールドを解析
            if (!processNestedField(field, fieldList)) {
                processSimpleField(field, fieldList);
            }
        }
    }

    private boolean processNestedField(String field, Set<String> fieldList) {
        Pattern nestedPattern = Pattern.compile("(.*)\\[(.*)\\]\\[(.*)\\]");
        Matcher matcher = nestedPattern.matcher(field);

        if (matcher.find()) {
            String root = matcher.group(1);
            String child = matcher.group(2);
            String[] childFields = matcher.group(3).split(",");

            for (String childField : childFields) {
                fieldList.add(root + "." + child + "." + childField);
            }
            return true;
        }
        return false;
    }

    private void processSimpleField(String field, Set<String> fieldList) {
        Pattern simplePattern = Pattern.compile("(.*)\\[(.*)\\]");
        Matcher matcher = simplePattern.matcher(field);

        if (matcher.matches()) {
            String root = matcher.group(1);
            String[] fields = matcher.group(2).split(",");

            for (String subField : fields) {
                fieldList.add(root + "." + subField);
            }
        }
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(QueryFields.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        QueryFields annotation = parameter.getParameterAnnotation(QueryFields.class);
        String name = Objects.requireNonNull(annotation).name();
        if (name == null || name.isEmpty()) {
            name = "fields";
        }

        String value = webRequest.getParameter(name);
        if (value != null && value.isEmpty()) {
            value = null;
        }

        String defaultValue = annotation.defaultValue() == null || annotation.defaultValue().isEmpty() ? null : annotation.defaultValue();
        boolean required = annotation.required();
        if (defaultValue != null) {
            required = false;
        }

        if (value == null && defaultValue != null) {
            value = defaultValue;
        }

        if (value == null) {
            if (required) {
                throw new MissingServletRequestParameterException(
                        Objects.requireNonNull(parameter.getParameterName()),
                        parameter.getParameterType().getSimpleName());
            }
            return null;
        }

        return normalizeBracketFields(value);
    }

}