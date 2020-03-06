package mo.uepay.example.merchant.controller;


import lombok.extern.slf4j.Slf4j;
import mo.uepay.example.merchant.config.ApplicationConfig;
import mo.uepay.example.merchant.service.PaymentService;
import mo.uepay.example.merchant.util.JsonUtil;
import mo.uepay.example.merchant.util.SignUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * @author guobq
 * @desc 商户端集成支付功能
 * @date 2020/3/3 16:24
 **/
@RestController
@RequestMapping("/api/common")
@Slf4j
public class CommonController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ApplicationConfig applicationConfig;

    /**
     * 支付回调地址 接收上游接口支付成功的结果通知
     *
     * @param params
     * @return
     */
    @PostMapping(value = "/payment/notify")
    public String merchantNotify(@RequestBody Map<String, String> params) {
        try {
            if (params == null) {
                return "error";
            }
            log.info("notify  params: {}", JsonUtil.toJson(params));
            SortedMap<String, String> signMap = new TreeMap<>();
            signMap.putAll(params);
            String notifySign = signMap.remove("sign");
            //验签处理
            String sign = SignUtils.generateSign(signMap, applicationConfig.getPayKey());
            if (!Objects.equals(sign, notifySign)) {
                log.error("验签失败！！！！！");
                return "error";
            }
            String payMethod = params.get("payMethod");
            String orderNo = params.get("orderNo");
            String tradeType = params.get("tradeType");
            String tradeTime = params.get("tradeTime");
            Date payTime = new DateTime(Long.valueOf(tradeTime)).toDate();


            //TODO 处理业务  处理完成并无异常  返回   "success";

            return "success";
        } catch (Exception e) {
            log.error("merchantNotify-ex:{}", ExceptionUtils.getStackTrace(e));
            return "error";
        }
    }

    @RequestMapping(value = "/redirect/code")
    public ModelAndView redirect(HttpServletRequest request, @RequestParam(value = "auth_code", required = false) String authCode) {

        //前端地址
        String targetUrl = "/api/common/payhtml";

        String userAgent = request.getHeader("user-agent");

        if (StringUtils.isEmpty(userAgent)) {
            log.error("userAgent must not null");
            ModelAndView model = new ModelAndView("404.html");
            return model;
        }

        if (StringUtils.isNotEmpty(authCode)) {
            String uid = paymentService.getUePayUid(userAgent, authCode);
            if (StringUtils.isNotEmpty(uid)) {
                String appendStr = "key=" + uid;
                if (StringUtils.contains(targetUrl, "?")) {
                    targetUrl = targetUrl + "&" + appendStr;
                } else {
                    targetUrl = targetUrl + "?" + appendStr;
                }
            } else {
                log.error("getUePayUid error");
            }
        }
        ModelAndView model = new ModelAndView("redirect:" + targetUrl);
        return model;
    }

    @RequestMapping(value = "/payhtml")
    public ModelAndView payhtml(@RequestParam(value = "key", required = false) String key) {
        ModelAndView model = new ModelAndView("pay-demo");
        if (StringUtils.isNotEmpty(key)) {
            log.info(key);
            model.addObject("uid", key);
        }
        return model;
    }


}
