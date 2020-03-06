package mo.uepay.example.merchant.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.SortedMap;

@Slf4j
@Setter
@Getter
@ToString
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 交易代碼
     */
    private String requestType;

    private String tradeType;
    /**
     * 返回结果
     */
    private String message;
    /**
     * 英文返回结果描述
     */
    private String enMessage;
    /**
     * 结果 false : 失敗 true :成功
     */
    private String result = "false";
    /**
     * 返回业务内容
     */
    private SortedMap<String, String> results;
    /**
     * 服务端签名
     */
    private String serverSign;
    /**
     * 返回編碼
     */
    private String retCode;


}
