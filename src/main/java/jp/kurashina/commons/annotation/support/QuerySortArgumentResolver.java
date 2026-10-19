package jp.kurashina.commons.annotation.support;

import jp.kurashina.commons.annotation.QuerySort;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class QuerySortArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(QuerySort.class) &&
                List.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        QuerySort querySort = parameter.getParameterAnnotation(QuerySort.class);
        String paramName = Objects.requireNonNull(querySort).name();
        String[] values = webRequest.getParameterValues(paramName);

        // リクエストの値も、デフォルト値と同様にカンマ区切りで複数指定できる（例: sort=id,-createdDate）。
        // sort=id&sort=-createdDate のようにパラメータを繰り返す形式も、従来どおり使える。両者は混在してもよい。
        List<String> sortKeys = (values == null)
                ? Collections.emptyList()
                : Arrays.stream(values)
                        .flatMap(value -> Arrays.stream(value.split(",")))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());

        // パラメータが存在しない、または分割後に有効なキーがない場合
        if (sortKeys.isEmpty()) {
            String defaultValue = querySort.defaultValue();
            
            // デフォルト値がある場合はそれを使用
            if (!defaultValue.isEmpty()) {
                return Arrays.stream(defaultValue.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
            }
            
            // デフォルト値もなく、必須指定の場合は例外を投げる
            if (querySort.required()) {
                throw new MissingServletRequestParameterException(
                        Objects.requireNonNull(parameter.getParameterName()),
                        parameter.getParameterType().getSimpleName());
            }

            return Collections.emptyList();
        }

        return sortKeys;
    }
}
