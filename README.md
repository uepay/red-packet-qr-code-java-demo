# 集成Uepay電子錢包支付--商户端後台

### 实现接口功能有：

 1. 获取UID接口
 1. 预下单接口
 1. 查询订单接口
 1. 处理回调通知并验签
 
### 部署环境
  1.  Jdk1.8+
  1.  maven3.5+
  1.  IDEA 或 Eclipse

### 配置文件 application.yml
 ``` 
    merchant:
      merchantNo: "xx" #请联系极易付提供
      payKey: "xx" #请联系极易付提供
      notifyUrl: "http://本机ip:8111/api/common/payment/notify" #商户接收回调通知地址
    uepay:
      payment:
        domain: https://fat.uepay.mo  #测试域名
      oauth2:
        url: https://gztest1.uepay.mo/api/oauth2/redirect #测试认证域名
 ``` 
   
    
### 演示过程（需要使用极易用测试钱包测试版 APP ，请联系极易付提供）
    
    访问地址：https://gztest1.uepay.mo/api/oauth2/redirect?merchant_id=000350001&redirect_uri=http://本机ip:8111/api/common/redirect/code
   
    项目启动后，替换本机IP ，把在访问地址转成二维码，使用UePay钱包APP扫一扫功能，完成支付体验。

### 效果图
![h5唤起支付](https://github.com/uepay/red-packet-qr-code-java-demo/blob/master/src/main/resources/static/1.jpg)
![钱包支付界面](https://github.com/uepay/red-packet-qr-code-java-demo/blob/master/src/main/resources/static/2.jpg)


### 
[在线二维码转换](https://cli.im)

