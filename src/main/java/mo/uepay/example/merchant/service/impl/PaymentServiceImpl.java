package mo.uepay.example.merchant.service.impl;

import lombok.extern.slf4j.Slf4j;
import mo.uepay.example.merchant.config.ApplicationConfig;
import mo.uepay.example.merchant.dto.RequestDTO;
import mo.uepay.example.merchant.dto.ResponseDTO;
import mo.uepay.example.merchant.service.PaymentService;
import mo.uepay.example.merchant.util.HttpClientUtil;
import mo.uepay.example.merchant.util.JsonUtil;
import mo.uepay.example.merchant.util.SignUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * <pre>
 * *********************************************
 * Copyright uepay.mo.
 * All rights reserved.
 * Description: ${添加描述}
 * HISTORY:
 * *********************************************
 *  Version		Date		Author	   Desc
 *   v1.0     20-2-21       guobq      创建
 * *********************************************
 * </pre>
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private ApplicationConfig applicationConfig;


    /**
     * 统一下单获取支付参数
     *
     * @param orderNo
     * @param amount
     * @return
     */
    @Override
    public Map prePayOrder(String orderNo, String amount,String requestIp) {

        try {
            //接口中的金额参数单位为分：这里需要转化
            BigDecimal afterAmt =  new BigDecimal(amount).multiply(new BigDecimal(100)).setScale(0,BigDecimal.ROUND_HALF_EVEN);
            log.info("订单号：{},支付金额：{}",orderNo,afterAmt.toString());
            RequestDTO request = initUnifiedorderRequest(orderNo, afterAmt.toString(), requestIp);
            Map<String, String> result =  HttpClientUtil.doPostString(applicationConfig.getPaymentDomain()+"/wallet/unifiedorder",JsonUtil.toJson(request));

            if("success".equals(result.get("status"))) {
                ResponseDTO responseDTO = JsonUtil.toObject(result.get("result"),ResponseDTO.class);
                if (responseDTO != null && "true".equals(responseDTO.getResult())) {
                    log.error("prePayOrder success :{}", responseDTO.toString());
                    return uepayPayParamSign(responseDTO.getResults(),applicationConfig.getPayKey());
                } else {
                    log.error("prePayOrder fail :{}", responseDTO.toString());
                }
            }else{
                log.error("prePayOrder fail :{}", result.toString());
            }

        } catch (Exception e) {
            log.error("prePayOrder Exception :{}", ExceptionUtils.getStackTrace(e));
        }
        return null;

    }

    private RequestDTO initUnifiedorderRequest(String orderNo, String
            amount, String requestIp) {
        //自定义 body 内容
        String body = "xxx-收款-" + orderNo;

        /**
         * 2、組裝請求支付接口預下單接口
         */
        RequestDTO reqParamDTO = new RequestDTO();
        reqParamDTO.setAppSource("1");
        reqParamDTO.setAppVersion("1.4");
        reqParamDTO.setMerchantNo(applicationConfig.getMerchantNo());
        reqParamDTO.setRequestType("UNIFIEDORDER");
        reqParamDTO.setTradeType("UEPAY_JSAPI");

        SortedMap<String, String> arguments = new TreeMap<String, String>();
        arguments.put("terminal", "WEB");

        arguments.put("spbillCreateIp", requestIp);
        arguments.put("orderNo", orderNo);
        arguments.put("amt", amount);
        arguments.put("body", body);
        arguments.put("nonceStr", System.currentTimeMillis() + "UEPAY");
        arguments.put("notifyUrl", applicationConfig.getNotifyUrl());
        //TODO 根据本身业务组装 detail
        String detail = "{\"consignee\":\"澳門演示\",\"consignee_address\":\"address\",\"goods_detail\":[{\"goods_name\":\"西式炒飯\",\"quantity\":\"2\",\"price\":\"0.02\"}]}";
        arguments.put("detail", detail);
        reqParamDTO.setArguments(arguments);
        String sign = SignUtils.generateSign(reqParamDTO, applicationConfig.getPayKey());
        reqParamDTO.setClientSign(sign);

        return reqParamDTO;
    }

    private SortedMap<String, String> uepayPayParamSign(SortedMap<String, String> param, String payKey) {
        //uepay 下單成功後需要對喚起參數加簽
        String appid = applicationConfig.getMerchantNo();
        String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
        String nonceStr = String.valueOf(System.currentTimeMillis());
        SortedMap<String, String> payInfoMap = new TreeMap<String, String>();
        payInfoMap.put("appId", appid);
        payInfoMap.put("timeStamp", timestamp);
        payInfoMap.put("nonceStr", nonceStr);
        payInfoMap.put("prepayid", param.get("prepayid"));
        payInfoMap.put("signType", "MD5");
        //验签
        String paySign = SignUtils.generateSign(payInfoMap, payKey);
        payInfoMap.put("paySign", paySign);
        return payInfoMap;
    }
    /**
     * 获取 UID
     * @param userAgent
     * @param authCode
     * @return
     */
    @Override
    public String getUePayUid(String userAgent, String authCode) {
        String uid = null;
        try {
            RequestDTO request = initApplyToken(userAgent, authCode);
            Map<String, String> result =  HttpClientUtil.doPostString(applicationConfig.getPaymentDomain()+"/payment/gateway",JsonUtil.toJson(request));
            if("success".equals(result.get("status"))) {
                ResponseDTO responseDTO = JsonUtil.toObject(result.get("result"),ResponseDTO.class);
                if (responseDTO != null && "true".equals(responseDTO.getResult())) {
                    uid = responseDTO.getResults().get("openid");
                } else {
                    log.error("getAlipayUid fail :{}", responseDTO.toString());
                }
            }else{
                log.error("getAlipayUid fail :{}", result.toString());
            }
        } catch (Exception e) {
            log.error("getAlipayUid Exception :{}", ExceptionUtils.getStackTrace(e));
        }
        return uid;

    }

    private RequestDTO initApplyToken(String userAgent, String authCode) {
        RequestDTO reqParamDTO = new RequestDTO();
        reqParamDTO.setAppSource("1");
        reqParamDTO.setAppVersion("1.4");
        reqParamDTO.setMerchantNo(applicationConfig.getMerchantNo());
        reqParamDTO.setRequestType("OAUTH_APPLY_TOKEN");

        SortedMap<String, String> arguments = new TreeMap<String, String>();
        arguments.put("userAgent", userAgent);
        arguments.put("authCode", authCode);
        reqParamDTO.setArguments(arguments);
        String sign = SignUtils.generateSign(reqParamDTO, applicationConfig.getPayKey());
        reqParamDTO.setClientSign(sign);

        return reqParamDTO;
    }

    /**
     * 统一下单获取支付链接
     *
     * @param orderNo
     * @return
     */
    @Override
    public Map<String, String> queryPayOrder(String orderNo) {
        try {
            RequestDTO request = initQueryOrderReuest(orderNo);
            Map<String, String> result =  HttpClientUtil.doPostString(applicationConfig.getPaymentDomain()+"/payment/gateway",JsonUtil.toJson(request));
            if("success".equals(result.get("status"))) {
                ResponseDTO responseDTO = JsonUtil.toObject(result.get("result"),ResponseDTO.class);
                if (responseDTO != null && "true".equals(responseDTO.getResult())) {
                    return responseDTO.getResults();
                } else {
                    log.error("queryPayOrder fail :{}", responseDTO.toString());

                }
            }else{
                log.error("queryPayOrder fail :{}", result.toString());
            }
        } catch (Exception e) {
            log.error("queryPayOrder Exception :{}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }


    private RequestDTO initQueryOrderReuest(String orderNo) {
        RequestDTO reqParamDTO = new RequestDTO();
        reqParamDTO.setAppSource("1");
        reqParamDTO.setAppVersion("1.4");
        reqParamDTO.setMerchantNo(applicationConfig.getMerchantNo());
        reqParamDTO.setRequestType("QUERY");

        SortedMap<String, String> arguments = new TreeMap<String, String>();
        arguments.put("orderNo", orderNo);
        reqParamDTO.setArguments(arguments);
        String sign = SignUtils.generateSign(reqParamDTO, applicationConfig.getPayKey());
        reqParamDTO.setClientSign(sign);

        return reqParamDTO;
    }
}
