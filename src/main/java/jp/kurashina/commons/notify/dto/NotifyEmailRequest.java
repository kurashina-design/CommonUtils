package jp.kurashina.commons.notify.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class NotifyEmailRequest {

    @JsonProperty("from")
    private NotifyEmailAddress fromAddress;

    private List<NotifyEmailAddress> to;
    private List<NotifyEmailAddress> cc;
    private List<NotifyEmailAddress> bcc;

    @JsonProperty("replyTo")
    private NotifyEmailAddress replyTo;

    private String subject;
    private String text;
    private String html;
}
