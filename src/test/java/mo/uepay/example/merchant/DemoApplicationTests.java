package mo.uepay.example.merchant;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import mo.uepay.example.merchant.dto.RequestDTO;
import mo.uepay.example.merchant.util.HttpClientUtil;
import mo.uepay.example.merchant.util.SignUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Slf4j
@SpringBootTest
class DemoApplicationTests {
    /**
     * 商户号
     */
    private final String merchantNo = "xxx";
    /**
     * 密钥
     */
    private final String payKey = "xxx";
    private final String url = "https://xxxxxxxxx";

    @Test
    void unifiedorder() {
        log.info("test");
        RequestDTO reqParamDTO = new RequestDTO();
        reqParamDTO.setAppSource("4");
        reqParamDTO.setAppVersion("1.3");
        reqParamDTO.setMerchantNo(merchantNo);
        reqParamDTO.setRequestType("UNIFIEDORDER");
        reqParamDTO.setTradeType("APP");
        SortedMap<String, String> arguments = new TreeMap<String, String>();
        arguments.put("orderNo", System.currentTimeMillis() + "");
        arguments.put("payMethod", "wxapp");
        arguments.put("amt", "10");
        arguments.put("notifyUrl", "http://www.uepay.mo");
        arguments.put("attach", "attach");
        //detail 内容是json 字符串
        arguments.put("detail", "{ \"goods_detail\":[ {  \"goods_name\":\"iPhone6s 16G\", \"quantity\":1,\"price\":555 } ],   \"consignee\":\"收貨人\" , \"consignee_address\":\"收貨地址\" }");
        arguments.put("terminal", "100000");
        arguments.put("body", "支付");
        reqParamDTO.setArguments(arguments);
        String sign = SignUtils.generateSign(reqParamDTO, payKey);
        reqParamDTO.setClientSign(sign);
        Gson gson = new Gson();

        Map<String, String> result = HttpClientUtil.doPostString(url, gson.toJson(reqParamDTO));
        log.info(result.get("result"));
    }

    @Test
    void query() {
        log.info("test");
        RequestDTO reqParamDTO = new RequestDTO();
        reqParamDTO.setAppSource("1");
        reqParamDTO.setAppVersion("1.3");
        reqParamDTO.setMerchantNo(merchantNo);
        reqParamDTO.setRequestType("QUERY");
        reqParamDTO.setTradeType("APP");
        SortedMap<String, String> arguments = new TreeMap<String, String>();
        arguments.put("tranNo", "201900091823123");
        reqParamDTO.setArguments(arguments);
        String sign = SignUtils.generateSign(reqParamDTO, payKey);
        reqParamDTO.setClientSign(sign);
        Gson gson = new Gson();

        Map<String, String> result = HttpClientUtil.doPostString(url, gson.toJson(reqParamDTO));
        log.info(result.get("result"));
    }

}
