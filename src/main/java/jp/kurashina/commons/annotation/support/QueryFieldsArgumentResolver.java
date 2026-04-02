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
 * {@link QueryFields} アノテーションが付与されたコントローラーの引数を解析し、
 * 正規化およびフィルタリングされたフィールドリストを生成するリゾルバー。
 * <p>
 * 以下の順序で処理を行います：
 * </p>
 * <ol>
 * <li>リクエストパラメータから文字列を取得（未指定時はデフォルト値）</li>
 * <li>ブラケット記法（{@code a[b,c]}）をドット記法（{@code a.b, a.c}）に展開</li>
 * <li>{@code maxDepth} による深度（ドット数）制限の適用</li>
 * <li>{@code excluding} による特定パスの除外（ワイルドカード対応）</li>
 * </ol>
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

        // 1. 正規化
        List<String> fields = normalizeBracketFields(value);

        // 2. 深度制限
        int maxDepth = annotation.maxDepth();
        if (maxDepth < 99) {
            fields = fields.stream()
                    .filter(f -> countDots(f) <= maxDepth)
                    .collect(Collectors.toList());
        }

        // 3. 除外設定
        String[] excluding = annotation.excluding();
        if (excluding.length > 0) {
            fields = fields.stream()
                    .filter(f -> Arrays.stream(excluding).noneMatch(e -> {
                        boolean isWildcard = e.endsWith(".*");
                        String base = isWildcard ? e.substring(0, e.length() - 2) : e;
                        return f.equals(base) || f.startsWith(base + ".");
                    }))
                    .collect(Collectors.toList());
        }

        return fields;
    }

    /**
     * フィールド名に含まれるドット（.）の数をカウントします。
     *
     * @param field フィールド名
     * @return ドットの数
     */
    private long countDots(String field) {
        if (field == null) return 0;
        return field.chars().filter(ch -> ch == '.').count();
    }

    /**
     * ブラケット記法を含む文字列を、フラットなドット記法のリストに正規化します。
     * <p>入力例: {@code id,user[id,profile[image]]}</p>
     * <p>出力例: {@code ["id", "user.id", "user.profile.image"]}</p>
     *
     * @param source リクエストされたフィールド指定文字列
     * @return 正規化され、ソートされたフィールドリスト
     */
    public List<String> normalizeBracketFields(String source) {
        source = source.replaceAll("\\s+", "");
        String normalizedSource = replaceCommasWithinBrackets(source);
        List<String> splittedFields = Arrays.stream(normalizedSource.split(","))
                .map(s -> s.replace("𐐷", ","))
                .toList();

        Set<String> fieldSet = new HashSet<>();
        for (String field : splittedFields) {
            processField(field, fieldSet);
        }
        return fieldSet.stream().sorted().collect(Collectors.toList());
    }

    /**
     * ブラケット内のカンマを一時的に特殊文字に置換します。
     *
     * @param source 変換対象の文字列
     * @return 変換後の文字列
     */
    private String replaceCommasWithinBrackets(String source) {
        StringBuilder builder = new StringBuilder();
        int depth = 0;
        for (char c : source.toCharArray()) {
            if (c == '[') depth++;
            else if (c == ']') depth--;
            if (c == ',' && depth > 0) builder.append("𐐷");
            else builder.append(c);
        }
        return builder.toString();
    }

    /**
     * 単一またはネストされたフィールドを解析してフィールドリストに追加します。
     *
     * @param field 処理対象のフィールド文字列
     * @param fieldList 結果を格納するセット
     */
    private void processField(String field, Set<String> fieldList) {
        if (field.contains("[")) {
            processNestedField(field, "", fieldList);
        } else {
            fieldList.add(field);
        }
    }

    /**
     * 再帰的にネストされたブラケット構造を解析し、フィールドパスを構築します。
     *
     * @param field 処理対象のフィールド文字列
     * @param prefix 現在のプレフィックスパス
     * @param fieldList 結果を格納するセット
     */
    private void processNestedField(String field, String prefix, Set<String> fieldList) {
        Pattern pattern = Pattern.compile("^([^\\s\\[\\]]+)\\[(.+)\\]$");
        Matcher matcher = pattern.matcher(field);
        if (matcher.find()) {
            String root = matcher.group(1);
            String subFieldsRaw = matcher.group(2);
            String currentPrefix = prefix.isEmpty() ? root : prefix + "." + root;
            String[] subFields = replaceCommasWithinBrackets(subFieldsRaw).split(",");
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
