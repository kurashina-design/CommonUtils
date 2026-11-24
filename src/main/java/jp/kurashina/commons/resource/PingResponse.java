package jp.kurashina.commons.resource;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PingResponse extends AbstractResponse {

    private final String status = "ok";

}
