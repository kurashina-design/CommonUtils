package jp.kurashina.commons.resource;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CountResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -4737209473695734432L;

    private long count;
    private Map<String, Boolean> criteria = new HashMap<>();

}
