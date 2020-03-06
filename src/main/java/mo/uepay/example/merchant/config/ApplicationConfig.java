package mo.uepay.example.merchant.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Setter
@Getter
@ToString
@Configuration
public class ApplicationConfig {


    /**
     * appid
     */
    @Value(value = "${merchant.appid}")
    private String appid;
    /**
     * 商户编号
     */
    @Value(value = "${merchant.merchantNo}")
    private String merchantNo;
    /**
     * 商户密钥
     */
    @Value(value = "${merchant.payKey}")
    private String payKey;
    /**
     * 商户接收回调通知地址
     */
    @Value(value = "${merchant.notifyUrl}")
    private String notifyUrl;
    /**
     * 钱包UePay授权地址
     */
    @Value(value = "${uepay.oauth2.url}")
    private String oauth2Url;
    /**
     * api url
     */
    @Value(value = "${uepay.payment.domain}")
    private String paymentDomain;

    public String getOauth2Url(String redirectUrl) {
        //https://gztest1.uepay.mo/api/oauth2/redirect?merchant_id=000350001&redirect_uri=https%3A%2F%2Fgztest1.uepay.mo%2Fapi%2Fpayment%2Ftest
        return String.format("%s?merchant_id=%s&redirect_uri=%s",this.getOauth2Url() , this.getMerchantNo(), redirectUrl);
    }

}
