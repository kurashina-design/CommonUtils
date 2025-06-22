package jp.kurashina.commons.resource;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class CountResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -7167133282248957103L;

    private int count;
    private Map<String, Boolean> criteria = new HashMap<>();

}
