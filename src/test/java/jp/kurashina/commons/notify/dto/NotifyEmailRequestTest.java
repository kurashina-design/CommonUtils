package jp.kurashina.commons.notify.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class NotifyEmailRequestTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void mapsJavaNamesToNotifyApiJsonNames() throws Exception {
        NotifyEmailRequest request = new NotifyEmailRequest();
        request.setFromAddress(new NotifyEmailAddress("sender@example.com", "Sender"));
        request.setTo(List.of(new NotifyEmailAddress("recipient@example.com", null)));
        request.setReplyTo(new NotifyEmailAddress("reply@example.com", null));
        request.setSubject("Subject");
        request.setText("Body");

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(request));

        assertEquals("sender@example.com", json.get("from").get("email").asText());
        assertEquals("reply@example.com", json.get("replyTo").get("email").asText());
        assertFalse(json.has("fromAddress"));
        assertFalse(json.has("reply_to"));
        assertFalse(json.has("cc"));
    }
}
