package com.xf_mingsu.common;

import org.apache.commons.io.IOUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpClientUtil {
    private static final Logger logger= LoggerFactory.getLogger(HttpClientUtil.class);
    private static PoolingHttpClientConnectionManager connMgr;
    private static RequestConfig requestConfig;
    private static final int MAX_TIMEOUT =10000;//默认十秒超时

    static {
        // 设置连接池
        connMgr = new PoolingHttpClientConnectionManager();
        // 设置连接池大小
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

        RequestConfig.Builder configBuilder = RequestConfig.custom();
        // 设置连接超时
        configBuilder.setConnectTimeout(MAX_TIMEOUT);
        // 设置读取超时
        configBuilder.setSocketTimeout(MAX_TIMEOUT);
        // 设置从连接池获取连接实例的超时
        configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
        // 在提交请求之前 测试连接是否可用
        configBuilder.setStaleConnectionCheckEnabled(true);
        //解决Cookie rejected 问题
        configBuilder.setCookieSpec(CookieSpecs.IGNORE_COOKIES);
        requestConfig = configBuilder.build();
    }

    /**
     * 发送 GET 请求（HTTP），不带输入数据
     * @param url
     * @return
     */
    public static String doGet(String url) {
        return doGet(url, new HashMap<String, Object>());
    }

    /**
     * 发送 GET 请求（HTTP），K-V形式
     * @param url
     * @param params
     * @return
     */
    public static String doGet(String url, Map<String, Object> params) {
        String apiUrl = url;
        StringBuffer param = new StringBuffer();
        int i = 0;
        for (String key : params.keySet()) {
            if (i == 0)
                param.append("?");
            else
                param.append("&");
            param.append(key).append("=").append(params.get(key));
            i++;
        }
        apiUrl += param;
        String result = null;
        HttpClient httpclient = new DefaultHttpClient();
        try {
            logger.debug("[http工具类]请求参数:",apiUrl);
            HttpGet httpGet = new HttpGet(apiUrl);
            HttpResponse response = httpclient.execute(httpGet);
            int statusCode = response.getStatusLine().getStatusCode();

            logger.debug("[http工具类]响应状态:",statusCode);

            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                result = IOUtils.toString(instream, "UTF-8");
            }
            logger.debug("[http工具类]响应信息:",result);
        } catch (IOException e) {
            logger.error("[http工具类]请求发生异常:",e);
        }
        return result;
    }

    /**
     * 发送 POST 请求（HTTP），不带输入数据
     * @param apiUrl
     * @return
     */
    public static String doPost(String apiUrl) {
        return doPost(apiUrl, new HashMap<String, Object>());
    }

    /**
     * 发送 POST 请求（HTTP），K-V形式
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPost(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            logger.info("[http工具类]请求地址:{}请求参数:{}",apiUrl,params);
            response = httpClient.execute(httpPost);
            if(302==response.getStatusLine().getStatusCode()){
                Header header = response.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD 中的
               String newUri = header.getValue(); // 这就是跳转后的地址，再向这个地址发出新申请，以便得到跳转后的信息是啥。
              return doPost(newUri,params);
            }
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
            logger.info("[http工具类]响应内容:{}",httpStr);
        } catch (IOException e) {
            logger.error("[http工具类]请求发生IO异常:",e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("[http工具类]请求发生IO异常1:",e);
                }
            }
        }
        return httpStr;
    }


    /**
     * 发送 POST 请求（HTTP），K-V形式
     * @param apiUrl API接口URL
     * @param reqParams 请求参数
     * @return
     */
    public static String doPostHtml(String apiUrl, String reqParams) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity=new StringEntity(reqParams,"utf-8");
            stringEntity.setContentType("text/xml;charset=utf-8");
            httpPost.setEntity(stringEntity);
            logger.info("[http工具类]请求地址:{}请求参数:{}",apiUrl,reqParams);
            response = httpClient.execute(httpPost);
            if(302==response.getStatusLine().getStatusCode()){
                Header header = response.getFirstHeader("location"); // 跳转的目标地址是在 HTTP-HEAD 中的
                String newUri = header.getValue(); // 这就是跳转后的地址，再向这个地址发出新申请，以便得到跳转后的信息是啥。
                return doPost(newUri,reqParams);
            }
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
            logger.info("[http工具类]响应内容:{}",httpStr);
        } catch (IOException e) {
            logger.error("[http工具类]请求发生IO异常:",e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("[http工具类]请求发生IO异常1:",e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 POST 请求（HTTP），JSON形式
     * @param apiUrl
     * @param json json对象
     * @return
     */
    public static String doPost(String apiUrl, Object json) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(),"UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            logger.debug("[http工具类]请求地址:请求参数:",apiUrl,json.toString());
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            logger.debug("[http工具类]响应状态码:",response.getStatusLine().getStatusCode());
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            logger.error("[http工具类]请求发生IO异常:",e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("[http工具类]请求发生IO异常1:",e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     * @param apiUrl API接口URL
     * @param params 参数map
     * @return
     */
    public static String doPostSSL(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(customSSLConnection()).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;
        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList<NameValuePair>(params.size());
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                NameValuePair pair = new BasicNameValuePair(entry.getKey(), entry
                        .getValue().toString());
                pairList.add(pair);
            }
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            logger.error("[http工具类]请求发生IO异常:",e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("[http工具类]请求发生IO异常1:",e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式
     * @param apiUrl API接口URL
     * @param json JSON对象
     * @return
     */
    public static String doPostSSL(String apiUrl, String json) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(customSSLConnection()).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;
        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json,"UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            logger.error("[http工具类]请求发生IO异常:",e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("[http工具类]请求发生IO异常1:",e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 创建SSL安全连接
     *
     * @return
     */
    /*private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new X509HostnameVerifier() {

                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }

                @Override
                public void verify(String host, SSLSocket ssl) throws IOException {
                }

                @Override
                public void verify(String host, X509Certificate cert) throws SSLException {
                }

                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
                }
            });
        } catch (GeneralSecurityException e) {
            logger.error("[http工具类]SSL发生异常:",e);
        }
        return sslsf;
    }*/


    /**
     * 创建SSL安全连接
     *
     * @return
     */
    /*private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf=new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (GeneralSecurityException e) {
            logger.error("[http工具类]SSL发生异常:",e);
        }
        return sslsf;
    }*/

    private static SSLConnectionSocketFactory customSSLConnection() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslcontext = SSLContexts.custom()
                    .loadTrustMaterial(new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                            return true;
                        }
                    })
                    .build();
            HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
            sslsf = new SSLConnectionSocketFactory(sslcontext,allowAllHosts);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[http工具类]SSL发生异常:",e);
        }
        return sslsf;
    }


    private static SSLConnectionSocketFactory customKQSSLConnection(String keyPath,String password) {
        SSLConnectionSocketFactory sslsf = null;
        try {
            KeyStore keyStore=KeyStore.getInstance(KeyStore.getDefaultType());
            //InputStream inputStream=Thread.currentThread().getContextClassLoader().getResourceAsStream(keyPath);
            //System.out.println("TTT文件地址为:"+inputStream1);
            String path=HttpClientUtil.class.getResource(keyPath).toURI().getPath();
            //System.out.println("文件地址为:"+path);
            InputStream inputStream=new FileInputStream(new File(path));
            keyStore.load(inputStream,password.toCharArray());
            SSLContextBuilder custom = SSLContexts.custom();
            custom.loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            }).loadKeyMaterial(keyStore,password.toCharArray());
            SSLContext sslcontext = custom.build();
            //HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
            sslsf = new SSLConnectionSocketFactory(sslcontext);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[http工具类]SSL发生异常:",e);
        }
        return sslsf;
    }



    /**
     * 发送 SSL POST 请求（HTTPS），JSON形式(划款http请求)
     * @param apiUrl API接口URL
     * @param json JSON对象
     * @return
     */
    public static String doPostSSLForPay(String apiUrl, String json,String keyPath,String password,String auth) {
        System.setProperty("jsse.enableSNIExtension", "false");
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(customKQSSLConnection(keyPath,password)).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;
        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json,"UTF-8");//解决中文乱码问题
            stringEntity.setContentEncoding("UTF-8");
            //stringEntity.setContentType("text/html");
            BasicHeader header=new BasicHeader("Authorization",auth);
            stringEntity.setContentType(header);
            //httpPost.setHeader("Authorization",auth);
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            logger.error("[http工具类]请求发生IO异常:",e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("[http工具类]请求发生IO异常1:",e);
                }
            }
        }
        return httpStr;
    }

    /**
     * 发送 SSL POST 请求（HTTPS），K-V形式
     * @param apiUrl API接口URL
     * @param pairList 参数map
     * @return
     */
    public static String doPostSSLForFuYou(String apiUrl, List<NameValuePair> pairList) {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(customSSLConnection()).build();
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;
        String httpStr = null;
        try {
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("utf-8")));
            response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                return null;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null) {
                return null;
            }
            httpStr = EntityUtils.toString(entity, "utf-8");
        } catch (Exception e) {
            logger.error("[http工具类]请求发生IO异常:",e);
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    logger.error("[http工具类]请求发生IO异常1:",e);
                }
            }
        }
        return httpStr;
    }
    /**
     * 测试方法
     * @param args
     */
    public static void main(String[] args) throws Exception {
        /*Map params= Maps.newHashMap();
        params.put("orderNo", "12345788");
        params.put("bizCode", "aaa");
        params.put("auditResult", "01");
        params.put("auditComment", "测试");
        HttpClientUtil.doPost("http://10.103.20.26:9323/risk/notify.html",params);*/
        //InputStream path=Thread.currentThread().getContextClassLoader().getResourceAsStream("/certificates/81211174511000190.jks");
        //String path=HttpClientUtil.class.getResource("/certificates/81211174511000190.jks").toURI().getPath();
        String path=Thread.currentThread().getContextClassLoader().getResource("/certificates/81211174511000190.jks").getPath();
        System.out.println(path);

        String path1=HttpClientUtil.class.getResource("/certificates/81211174511000190.jks").getPath();
        System.out.println(path1);
    }
}
