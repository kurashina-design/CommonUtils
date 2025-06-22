package jp.kurashina.commons.resource;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class CountResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 8363659727962237726L;

    private long count;
    private Map<String, Boolean> criteria = new HashMap<>();

}
