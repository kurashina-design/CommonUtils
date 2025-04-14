package jp.kurashina.commons.cache.key;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UriCacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();

            // パスパラメータの取得
            Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String pathParamsPart = pathVariables != null ?
                    pathVariables.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .map(entry -> "{" + entry.getKey() + "=" + entry.getValue() + "}")
                            .collect(Collectors.joining("_")) : "";

            // クエリパラメータの取得
            Map<String, String[]> queryParams = request.getParameterMap();
            String queryParamsPart = queryParams.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .flatMap(entry -> Arrays.stream(entry.getValue()).map(value -> entry.getKey() + "=" + value))
                    .collect(Collectors.joining("&"));

            StringBuilder sb = new StringBuilder();
            if (!pathParamsPart.isEmpty()) {
                sb.append(pathParamsPart);
            }
            if (!queryParamsPart.isEmpty()) {
                if (sb.length() > 0) {
                    sb.append("_");
                }
                sb.append("(").append(queryParamsPart).append(")");
            }

            return sb.toString();
        }
        // リクエスト情報がない場合のフォールバック処理
        return target.getClass().getSimpleName() + "_" + method.getName() + "_" + Arrays.toString(params);
    }
}
