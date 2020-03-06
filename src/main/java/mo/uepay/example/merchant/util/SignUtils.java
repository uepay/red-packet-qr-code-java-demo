package mo.uepay.example.merchant.util;

import lombok.extern.slf4j.Slf4j;
import mo.uepay.example.merchant.dto.RequestDTO;
import org.springframework.util.StringUtils;

import java.util.*;

@Slf4j
public class SignUtils {

    private SignUtils() {
    }


    /**
     * 生成驗簽
     *
     * @param signkey
     * @return
     */
    public static final String generateSign(RequestDTO reqParamDTO, String signkey) {

        SortedMap<String, String> signMap = new TreeMap<String, String>();
        signMap.putAll(reqParamDTO.getArguments());
        signMap.put("appSource", reqParamDTO.getAppSource());
        signMap.put("appVersion", reqParamDTO.getAppVersion());
        signMap.put("merchantNo", reqParamDTO.getMerchantNo());
        if (!StringUtils.isEmpty(reqParamDTO.getTradeType())) {
            signMap.put("tradeType", reqParamDTO.getTradeType());
        }
        signMap.put("requestType", reqParamDTO.getRequestType());
        return SignUtils.generateSign(signMap, signkey);
    }

    /**
     * 生成驗簽
     *
     * @param paramsMap
     * @param signkey
     * @return
     */
    public static final String generateSign(SortedMap<String, String> paramsMap, String signkey) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<String, String>> es = paramsMap.entrySet();
        Iterator<Map.Entry<String, String>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            String key = entry.getKey();
            String value = entry.getValue();
            if (!StringUtils.isEmpty(value) && !key.equals("sign")) {
                sb.append(key + "=" + value + "&");
            }
        }
        sb.append("key=" + signkey);
        String sign = Md5Encrypt.md5(sb.toString(), "UTF-8").toUpperCase();
        log.info("generateSign params：{} \n generateSign sign：{}", sb.toString(), sign);
        return sign;
    }


}
