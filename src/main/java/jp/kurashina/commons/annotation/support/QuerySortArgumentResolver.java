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

        // パラメータが存在しない、または空の場合
        if (values == null || values.length == 0 || (values.length == 1 && values[0].isBlank())) {
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

        return Arrays.asList(values);
    }
}
