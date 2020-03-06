package mo.uepay.example.merchant.controller;


import lombok.extern.slf4j.Slf4j;
import mo.uepay.example.merchant.service.PaymentService;
import mo.uepay.example.merchant.util.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;
import java.util.HashMap;
import java.util.Map;


/**
 * @author guobq
 * @desc 商户端集成支付功能
 * @date 2020/3/3 16:24
 **/
@RestController
@RequestMapping("/api/order")
@Slf4j
public class OrderController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 发起支付，生产业务订单并向极易付预下单，返回支付参数
     * 根据自身业务定义，这里只是模拟支付流程demo
     *
     * @param params
     * @return
     */
    @RequestMapping(value = "/preorder", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public Map<String, String> preorder(HttpServletRequest request
            , @RequestBody(required = false) Map<String, String> params) {

        Map<String, String> temp = new HashMap<>();
        /**
         * 1、獲取頁面傳參
         * deme 中的 amt 单位为元
         */

        String amt = params.get("amt");
        /**
         * 2、處理頁面發送參數，組織請求支付接口數據
         */
        //支付金額amt驗證
        if (StringUtils.isEmpty(amt)) {
            temp.put("flag", "01");
            temp.put("msg", "請輸入支付金額");
            return temp;
        }

        /**
         * 3、初始化业务订单，并保存
         */
        //TODO ....... 业务代码

        /**
         * 4、向极易付发起预下单
         */
        //调用方需要保存商户订单号-在商户号下，唯一性
        String orderNo = "Demo-" + System.currentTimeMillis();
        //下单接口金额单位分 所以要转化
        Map result = paymentService.prePayOrder(orderNo, amt, IpUtils.getIpFromRequest(request));
        if (result != null) {
            temp.put("orderNo",orderNo);
            temp.putAll(result);
        } else {
            temp.put("flag", "02");
            temp.put("msg", "预下单失败");
        }
        /**
         * 处理返回支付参数
         */
        return temp;
    }

    @RequestMapping(value = "/query/{orderNo}")
    public Map<String, String> queryOrder(@PathVariable(value = "orderNo") String orderNo) {
        Map<String, String> temp = new HashMap<>();

        /**
         * 1、先查询业务订单，
         */
        //TODO ....... 业务代码
        /**
         * 2.若支付状态不确实 例如是支付中，调用支付系统查询接口
         */
        Map result = paymentService.queryPayOrder(orderNo);
        /**
         * 3、更新业务订单
         */
        //TODO ....... 业务代码
        if (result==null) {
            temp.put("flag", "01");
            temp.put("msg", "order not exist");
            return temp;
        }
        return result;
    }


}
