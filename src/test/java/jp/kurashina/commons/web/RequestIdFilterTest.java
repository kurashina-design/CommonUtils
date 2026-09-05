package jp.kurashina.commons.web;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;

class RequestIdFilterTest {

    private final RequestIdFilter filter = new RequestIdFilter();

    @Test
    void propagatesIncomingRequestId() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(RequestIdUtils.HEADER_NAME, "upstream-id");
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(request.getAttribute(RequestIdUtils.ATTRIBUTE_NAME)).isEqualTo("upstream-id");
        assertThat(response.getHeader(RequestIdUtils.HEADER_NAME)).isEqualTo("upstream-id");
    }

    @Test
    void generatesRequestIdWhenHeaderIsMissing() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        String requestId = (String) request.getAttribute(RequestIdUtils.ATTRIBUTE_NAME);
        assertThat(requestId).isNotBlank();
        assertThat(response.getHeader(RequestIdUtils.HEADER_NAME)).isEqualTo(requestId);
    }
}
