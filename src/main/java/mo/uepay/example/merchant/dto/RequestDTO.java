package mo.uepay.example.merchant.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.SortedMap;

@Setter
@Getter
@ToString
@EqualsAndHashCode
public class RequestDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tradeType;
    /**
     * 交易代碼
     */
    private String requestType;
    /**
     * 商戶號
     */
    private String merchantNo;
    /**
     * 參數
     */
    private SortedMap<String, String> arguments;
    /**
     * 請求方簽名
     */
    private String clientSign;
    /**
     * 版本號
     */
    private String appVersion;
    /**
     * 來源
     */
    private String appSource;



}
