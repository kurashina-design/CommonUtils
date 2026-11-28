package jp.kurashina.commons.annotation.support;

import jp.kurashina.commons.annotation.QueryFields;
import org.junit.jupiter.api.Test;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.ServletWebRequest;

import java.lang.reflect.Method;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryFieldsArgumentResolverTest {

    private final QueryFieldsArgumentResolver resolver = new QueryFieldsArgumentResolver();

    @SuppressWarnings("unused")
    void plain(@QueryFields List<String> fields) {
    }

    @SuppressWarnings("unused")
    void withDefault(@QueryFields(defaultValue = "id,name") List<String> fields) {
    }

    @SuppressWarnings("unused")
    void required(@QueryFields(required = true) List<String> fields) {
    }

    @SuppressWarnings("unused")
    void requiredWithDefault(@QueryFields(required = true, defaultValue = "id") List<String> fields) {
    }

    @SuppressWarnings("unused")
    void customName(@QueryFields("columns") List<String> fields) {
    }

    @SuppressWarnings("unused")
    void depthOne(@QueryFields(maxDepth = 1) List<String> fields) {
    }

    @SuppressWarnings("unused")
    void excluding(@QueryFields(excluding = {"secret", "children.assets"}) List<String> fields) {
    }

    @SuppressWarnings("unused")
    void notAnnotated(List<String> fields) {
    }

    private MethodParameter parameter(String methodName) throws Exception {
        Method method = QueryFieldsArgumentResolverTest.class.getDeclaredMethod(methodName, List.class);
        MethodParameter parameter = SynthesizingMethodParameter.forExecutable(method, 0);
        parameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
        return parameter;
    }

    @SuppressWarnings("unchecked")
    private List<String> resolve(String methodName, MockHttpServletRequest request) throws Exception {
        return (List<String>) resolver.resolveArgument(parameter(methodName), null, new ServletWebRequest(request), null);
    }

    private static MockHttpServletRequest request(String name, String value) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter(name, value);
        return request;
    }

    @Test
    void supportsOnlyAnnotatedParameters() throws Exception {
        assertThat(resolver.supportsParameter(parameter("plain"))).isTrue();
        assertThat(resolver.supportsParameter(parameter("notAnnotated"))).isFalse();
    }

    @Test
    void expandsBracketNotationAndSorts() throws Exception {
        assertThat(resolve("plain", request("fields", "name, id, children[id, name], user[id,profile[image,bio]]")))
                .containsExactly("children.id", "children.name", "id", "name",
                        "user.id", "user.profile.bio", "user.profile.image");
    }

    @Test
    void removesDuplicates() throws Exception {
        assertThat(resolve("plain", request("fields", "id,id,children[id],children.id")))
                .containsExactly("children.id", "id");
    }

    @Test
    void returnsNullWhenMissingOrEmptyAndNotRequired() throws Exception {
        assertThat(resolve("plain", new MockHttpServletRequest())).isNull();
        assertThat(resolve("plain", request("fields", ""))).isNull();
    }

    @Test
    void usesDefaultValueWhenMissing() throws Exception {
        assertThat(resolve("withDefault", new MockHttpServletRequest())).containsExactly("id", "name");
        assertThat(resolve("withDefault", request("fields", "code"))).containsExactly("code");
    }

    @Test
    void requiredWithoutValueThrows() {
        assertThatThrownBy(() -> resolve("required", new MockHttpServletRequest()))
                .isInstanceOf(MissingServletRequestParameterException.class);
    }

    @Test
    void defaultValueSatisfiesRequired() throws Exception {
        assertThat(resolve("requiredWithDefault", new MockHttpServletRequest())).containsExactly("id");
    }

    @Test
    void readsCustomParameterName() throws Exception {
        assertThat(resolve("customName", request("columns", "id"))).containsExactly("id");
        assertThat(resolve("customName", request("fields", "id"))).isNull();
    }

    @Test
    void maxDepthDropsDeeperPaths() throws Exception {
        assertThat(resolve("depthOne", request("fields", "id,children[id,assets[id]]")))
                .containsExactly("children.id", "id");
    }

    @Test
    void excludingDropsPathAndDescendants() throws Exception {
        assertThat(resolve("excluding", request("fields", "id,secret,children[id,assets[id]],secretary")))
                .containsExactly("children.id", "id", "secretary");
    }

    @Test
    void normalizeBracketFieldsHandlesPlainList() {
        assertThat(resolver.normalizeBracketFields("b,a")).containsExactly("a", "b");
    }
}
