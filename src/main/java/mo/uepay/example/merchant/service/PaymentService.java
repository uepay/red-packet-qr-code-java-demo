package mo.uepay.example.merchant.service;

import java.util.Map;

/**
 * <pre>
 * *********************************************
 * Copyright uepay.mo.
 * All rights reserved.
 * Description: ${添加描述}
 * HISTORY:
 * *********************************************
 *  Version		Date		Author	   Desc
 *   v1.0     20-3-2       guobq      创建
 * *********************************************
 * </pre>
 */

public interface PaymentService {

    /**
     * 获取 UID
     * @param userAgent
     * @param authCode
     * @return
     */
     String getUePayUid(String userAgent, String authCode);

    /**
     * 统一下下单
     * @param orderNo
     * @param amount
     * @param requestIp
     * @return
     */
     Map prePayOrder(String orderNo, String amount,String requestIp);


    /**
     * 查询订单
     * @param orderNo
     * @return
     */
    Map<String,String> queryPayOrder(String orderNo);
}
