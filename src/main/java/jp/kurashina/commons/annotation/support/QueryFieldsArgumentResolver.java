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
import java.util.stream.Collectors;

/**
 * {@link QueryFields} アノテーションが付与されたメソッド引数を解決するリゾルバー。
 * ブラケット記法をドット記法に正規化し、深度制限および除外設定を適用します。
 */
public class QueryFieldsArgumentResolver implements HandlerMethodArgumentResolver {

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

        // 1. ブラケット記法を正規化してドット記法のリストに変換
        List<String> fields = normalizeBracketFields(value);

        // 2. 深度制限 (maxDepth) の適用
        // ドットの数を数えて、許可された階層を超えるフィールドを除去する
        int maxDepth = annotation.maxDepth();
        if (maxDepth < 99) {
            fields = fields.stream()
                    .filter(f -> countDots(f) <= maxDepth)
                    .collect(Collectors.toList());
        }

        // 3. 除外設定 (excluding) の適用
        String[] excluding = annotation.excluding();
        if (excluding.length > 0) {
            fields = fields.stream()
                    .filter(f -> Arrays.stream(excluding).noneMatch(e -> f.equals(e) || f.startsWith(e + ".")))
                    .collect(Collectors.toList());
        }

        return fields;
    }

    /**
     * 文字列内のドットの数をカウントします。
     */
    private long countDots(String field) {
        if (field == null) return 0;
        return field.chars().filter(ch -> ch == '.').count();
    }

    /**
     * ブラケット記法の文字列をドット記法のリストに正規化します。
     */
    public List<String> normalizeBracketFields(String source) {
        // 前処理: 空白を削除
        source = source.replaceAll("\\s+", "");

        // ブラケット内のカンマを一時的に置換して分割
        String normalizedSource = replaceCommasWithinBrackets(source);
        List<String> splittedFields = Arrays.stream(normalizedSource.split(","))
                .map(s -> s.replace("𐐷", ","))
                .toList();

        Set<String> fieldSet = new HashSet<>();
        for (String field : splittedFields) {
            processField(field, fieldSet);
        }

        // 結果をソートして返す（決定論的なレスポンスのため）
        return fieldSet.stream().sorted().collect(Collectors.toList());
    }

    private String replaceCommasWithinBrackets(String source) {
        StringBuilder builder = new StringBuilder();
        boolean insideBrackets = false;
        int depth = 0;

        for (char c : source.toCharArray()) {
            if (c == '[') depth++;
            else if (c == ']') depth--;

            insideBrackets = (depth > 0);

            if (c == ',' && insideBrackets) {
                builder.append("𐐷");
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    private void processField(String field, Set<String> fieldList) {
        if (field.contains("[")) {
            processNestedField(field, "", fieldList);
        } else {
            fieldList.add(field);
        }
    }

    private void processNestedField(String field, String prefix, Set<String> fieldList) {
        Pattern pattern = Pattern.compile("^([^\\s\\[\\]]+)\\[(.+)\\]$");
        Matcher matcher = pattern.matcher(field);

        if (matcher.find()) {
            String root = matcher.group(1);
            String subFieldsRaw = matcher.group(2);

            String currentPrefix = prefix.isEmpty() ? root : prefix + "." + root;

            // ネストのルート自体もフィールドとして追加（必要に応じて）
            // fieldList.add(currentPrefix);

            String normalizedSubFields = replaceCommasWithinBrackets(subFieldsRaw);
            String[] subFields = normalizedSubFields.split(",");

            for (String subField : subFields) {
                String cleanSubField = subField.replace("𐐷", ",");
                if (cleanSubField.contains("[")) {
                    processNestedField(cleanSubField, currentPrefix, fieldList);
                } else {
                    fieldList.add(currentPrefix + "." + cleanSubField);
                }
            }
        } else {
            fieldList.add(prefix.isEmpty() ? field : prefix + "." + field);
        }
    }
}