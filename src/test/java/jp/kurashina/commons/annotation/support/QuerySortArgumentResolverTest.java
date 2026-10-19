package jp.kurashina.commons.annotation.support;

import jp.kurashina.commons.annotation.QuerySort;
import org.junit.jupiter.api.Test;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuerySortArgumentResolverTest {

    private final QuerySortArgumentResolver resolver = new QuerySortArgumentResolver();

    @SuppressWarnings("unused")
    void withDefault(@QuerySort(defaultValue = "id,-name") List<String> sortList) {
    }

    @SuppressWarnings("unused")
    void withoutDefault(@QuerySort List<String> sortList) {
    }

    @SuppressWarnings("unused")
    void requiredWithoutDefault(@QuerySort(required = true) List<String> sortList) {
    }

    private MethodParameter parameter(String methodName) throws Exception {
        Method method = QuerySortArgumentResolverTest.class.getDeclaredMethod(methodName, List.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        return parameter;
    }

    @SuppressWarnings("unchecked")
    private List<String> resolve(String methodName, MockHttpServletRequest request) throws Exception {
        return (List<String>) resolver.resolveArgument(parameter(methodName), null, new ServletWebRequest(request), null);
    }

    @Test
    void splitsCommaSeparatedValue() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("sort", "id,-createdDate");

        assertThat(resolve("withDefault", request)).containsExactly("id", "-createdDate");
    }

    @Test
    void keepsRepeatedParameterForm() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("sort", "id");
        request.addParameter("sort", "-createdDate");

        assertThat(resolve("withDefault", request)).containsExactly("id", "-createdDate");
    }

    @Test
    void allowsMixingCommaAndRepeatedForms() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("sort", "a,b");
        request.addParameter("sort", "-c");

        assertThat(resolve("withDefault", request)).containsExactly("a", "b", "-c");
    }

    @Test
    void trimsWhitespaceAndDropsEmptyElements() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("sort", " id , ,-name,");

        assertThat(resolve("withDefault", request)).containsExactly("id", "-name");
    }

    @Test
    void usesDefaultValueWhenParameterIsMissingOrBlank() throws Exception {
        assertThat(resolve("withDefault", new MockHttpServletRequest())).containsExactly("id", "-name");

        MockHttpServletRequest blank = new MockHttpServletRequest();
        blank.addParameter("sort", " ");
        assertThat(resolve("withDefault", blank)).containsExactly("id", "-name");
    }

    @Test
    void returnsEmptyListWhenNoDefaultAndNotRequired() throws Exception {
        assertThat(resolve("withoutDefault", new MockHttpServletRequest())).isEmpty();
    }

    @Test
    void usesDefaultValueWhenOnlyDelimitersAreSpecified() throws Exception {
        for (String value : List.of(",", " , ", ",,")) {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.addParameter("sort", value);

            assertThat(resolve("withDefault", request)).containsExactly("id", "-name");
        }
    }

    @Test
    void usesDefaultValueWhenAllRepeatedValuesAreEmptyElements() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("sort", ",");
        request.addParameter("sort", ",");

        assertThat(resolve("withDefault", request)).containsExactly("id", "-name");
    }

    @Test
    void doesNotUseDefaultValueWhenRepeatedValuesContainAValidKey() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("sort", ",");
        request.addParameter("sort", "id");

        assertThat(resolve("withDefault", request)).containsExactly("id");
    }

    @Test
    void returnsEmptyListWhenOnlyDelimitersAreSpecifiedWithoutDefault() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("sort", ",");

        assertThat(resolve("withoutDefault", request)).isEmpty();
    }

    @Test
    void throwsWhenOnlyDelimitersAreSpecifiedAndParameterIsRequired() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("sort", ",");

        assertThatThrownBy(() -> resolve("requiredWithoutDefault", request))
                .isInstanceOfSatisfying(MissingServletRequestParameterException.class,
                        exception -> assertThat(exception.getParameterName()).isEqualTo("sortList"));
    }
}
