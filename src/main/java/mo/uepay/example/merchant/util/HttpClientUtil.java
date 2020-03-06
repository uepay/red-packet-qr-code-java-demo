package mo.uepay.example.merchant.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpClientUtil {
    // default charset 'utf-8'
    private static final String DEFAULT_CHARSET = "utf-8";

    /**
     * 链接超时 单位毫秒
     */
    private static final int CONNECT_TIMEOUT = 5 * 1000;
    /**
     * 响应超时 单位毫秒
     */
    private static final int SOCKET_TIMEOUT = 30 * 1000;


    private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows XP)";

    private static HttpClientConnectionManager connectionManager;
    private static CloseableHttpClient httpClient;

    // make constructor private
    private HttpClientUtil() {
    }


    static {
        initialize();
    }

    private static HttpClientConnectionManager createConnectionManager() {
        try {
            TrustManager[] tm = {new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }};
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslContext))
                    .build();

            return new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static RequestConfig buildRequestConfig() {
        return RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECT_TIMEOUT)
                .setSocketTimeout(SOCKET_TIMEOUT)
                .setCookieSpec(CookieSpecs.IGNORE_COOKIES).build();
    }

    private static CloseableHttpClient createClient() {
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setConnectionManager(connectionManager);
        builder.setRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
        builder.setDefaultRequestConfig(buildRequestConfig());
        builder.disableRedirectHandling();
        return builder.build();
    }

    /**
     * 间隔5秒关闭超时连接
     */
    private static void initialize() {
        connectionManager = createConnectionManager();
        httpClient = createClient();
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1,
                new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());
        executorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    if (connectionManager != null) {
                        connectionManager.closeExpiredConnections();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5, TimeUnit.SECONDS);
    }




    /**
     * 發送POST請求
     * @param url
     * @param paramStr
     * @return
     */
    public static final Map<String, String> doPostString(String url, String paramStr, String chatset) {
        log.info("url: {}" ,url);
        log.info("發送的數據：{}", paramStr);
        long t = System.currentTimeMillis();
        CloseableHttpResponse response = null;
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("status", "error");
        resultMap.put("result", "請求失敗");
        try {
            HttpPost httpPost = new HttpPost(url);
            HttpEntity entityReq = new StringEntity(paramStr, CharsetUtils.get("utf-8"));
            httpPost.setEntity(entityReq);
            httpPost.setHeader("Content-Type", "application/json;charset=UTF-8");
            response = httpClient.execute(httpPost);
            if(response==null){
                resultMap.put("result", "請求接口超时主动断开");
                return resultMap;
            }
            if (response.getStatusLine().getStatusCode() != 200) {
                return resultMap;
            }
            HttpEntity entity = response.getEntity();
            String result = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            if(chatset!=null){
                result = new String(result.getBytes(chatset), "utf-8");
            }else{
                result = new String(result.getBytes("iso8859-1"), "utf-8");
            }
            log.info("doPostString:{}"+result);

            resultMap.put("status", "success");
            resultMap.put("result", result);
            return resultMap;
        } catch (Exception e) {
            log.error("doPostString -{}"+ ExceptionUtils.getStackTrace(e));
            return resultMap;
        } finally {
            long end = System.currentTimeMillis() - t;
            log.info("doPostString 耗時："+end+" ms");
            try {
                if(response!=null){
                    response.close();
                }
            } catch (IOException e) {
                log.error("doPostString -{}"+ExceptionUtils.getStackTrace(e));
            }
        }
    }

    public static final Map<String, String> doPostString(String url, String paramStr) {
        return doPostString(url, paramStr, "utf-8");
    }



}