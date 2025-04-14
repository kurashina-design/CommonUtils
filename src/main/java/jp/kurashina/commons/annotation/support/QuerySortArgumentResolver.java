package jp.kurashina.commons.annotation.support;

import jp.kurashina.commons.annotation.QuerySort;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    ) {
        QuerySort querySort = parameter.getParameterAnnotation(QuerySort.class);
        String paramName = querySort.name();
        String[] values = webRequest.getParameterValues(paramName);

        if (values == null || values.length == 0 || (values.length == 1 && values[0].isBlank())) {
            String defaultValue = querySort.defaultValue();
            if (!defaultValue.isEmpty()) {
                return Collections.singletonList(defaultValue);
            } else {
                return Collections.emptyList();
            }
        }

        return Arrays.asList(values);
    }
}
