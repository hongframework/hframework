package com.hframework.common.client.http;


import com.hframework.common.util.message.JsonUtils;
import com.hframework.common.util.message.XmlUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP访问客户端
 */
public class OldHttpClient {

    private static Logger logger = Logger.getLogger(OldHttpClient.class);

    // 连接池里的最大连接数
    public static final int MAX_TOTAL_CONNECTIONS = 200;

    // 每个路由的默认最大连接数
    public static final int MAX_ROUTE_CONNECTIONS = 50;

    // 从连接池中取连接的超时时间
    public static Integer CONN_MANAGER_TIMETOUT = 1000 * 8;
    // 连接超时
    public static Integer HTTP_CONNECT_TIMEOUT = 1000 * 3;
    // 请求超时
    public static Integer HTTP_SO_TIMEOUT = 1000 * 6;

    private static org.apache.http.client.HttpClient customerHttpClient;

    public static org.apache.http.client.HttpClient getCustomerHttpClient() {

        if (customerHttpClient != null) return customerHttpClient;

        // 设置组件参数, HTTP协议的版本,1.1/1.0/0.9
        HttpParams httpParams = new BasicHttpParams();
        // 从连接池中取连接的超时时间
        ConnManagerParams.setTimeout(httpParams, CONN_MANAGER_TIMETOUT);
        // 设置连接超时时间
        HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_CONNECT_TIMEOUT);
        // 设置读取超时时间
        HttpConnectionParams.setSoTimeout(httpParams, HTTP_SO_TIMEOUT);
        // 使用线程安全的连接管理来创建 HttpClient
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));
        PoolingClientConnectionManager cm = new PoolingClientConnectionManager(schemeRegistry);
        // 设置最大连接数
        cm.setMaxTotal(MAX_TOTAL_CONNECTIONS);
//        设置每个路由默认最大连接数
        cm.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
//      // 设置代理和代理最大路由
//      HttpHost localhost = new HttpHost("locahost", 80);
//      cm.setMaxPerRoute(new HttpRoute(localhost), 50);
        // 设置代理
//        HttpHost proxy = new HttpHost("10.36.24.3", 60001);
//        httpParams.setParameter(ConnRoutePNames.DEFAULT_PROXY,  proxy);
//        ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(httpParams,schemeRegistry);
//        customerHttpClient  = new DefaultHttpClient(clientConnectionManager, httpParams);
        customerHttpClient = new DefaultHttpClient(cm, httpParams);
        return customerHttpClient;
    }








    /**
     * http执行get请求
     *
     * @param httpUrl
     * @param params
     * @return
     * @throws Exception
     */
    public static String doGet(String httpUrl, Map<String, String> params) throws Exception {
        logger.info("HTTP URL: " + httpUrl);
        if (params != null) {
            logger.info("HTTP Request params: " + params.toString());
        }
        org.apache.http.client.HttpClient httpClient = getCustomerHttpClient();
        if (params != null && params.size() > 0) {
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                nvps.add(new BasicNameValuePair(key, params.get(key)));
            }
            //对参数编码
            String strParams = URLEncodedUtils.format(nvps, HTTP.UTF_8);
            httpUrl += "?" + strParams;
        }
        HttpGet httpGet = new HttpGet(httpUrl);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        try {
            logger.info( "HTTP Status: " + httpResponse.getStatusLine().getStatusCode());
            logger.info("HTTP ReasonPhrase: " + httpResponse.getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            logger.error("Log error: " + e.toString());
        }
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = new String(EntityUtils.toByteArray(httpResponse.getEntity()));
            logger.info("HTTP 响应报文: " + result);
            return result;
        }
        return null;
    }

    /**
     * http执行post请求
     *
     * @param httpUrl
     * @param params
     * @return
     * @throws Exception
     */
    public static String doPost(String httpUrl, Map<String, String> params) throws Exception {
        logger.info("HTTP URL:" + httpUrl);
        //获得HTTP客户端
        org.apache.http.client.HttpClient httpClient = getCustomerHttpClient();
        try {
            //获得HttpPost对象
            HttpPost httpPost = new HttpPost(httpUrl);

            //将请求参数转换为NameValuePair集合对象
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (String key : params.keySet()) {
                nvps.add(new BasicNameValuePair(key, params.get(key)));
            }
            //设置表单提交编码为UTF-8
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            //执行Post请求
            HttpResponse response = httpClient.execute(httpPost);

            logger.info("HTTP Status: " + response.getStatusLine().getStatusCode());
            logger.info("HTTP ReasonPhrase: " + response.getStatusLine().getReasonPhrase());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = new String(EntityUtils.toByteArray(response.getEntity()));
                logger.info("HTTP 响应报文: " + result);
                return result;
            }
        } catch (Exception e) {
            logger.error("Log error: " + e.toString());
            throw e;
        } finally {
            httpClient.getConnectionManager().shutdown();
//            EntityUtils.consume(entity);
        }
        return null;
    }


    /**
     * http执行JSON消息Post请求
     * @param httpUrl
     * @return
     * @throws Exception
     */
    public static <T> String doJsonPost(String httpUrl, String jsonStr) throws Exception {
        logger.info("HTTP URL:" + httpUrl);
        org.apache.http.client.HttpClient httpClient = getCustomerHttpClient();

        try {
            logger.info("HTTP 报文:" + jsonStr);

            HttpPost httpPost = new HttpPost(httpUrl);
            // 将JSON信息放入POST
            StringEntity postEntity = new StringEntity(jsonStr, HTTP.UTF_8);
            httpPost.setEntity(postEntity);

            // 执行POST请求
            HttpResponse response = httpClient.execute(httpPost);
            logger.info("HTTP Status: " + response.getStatusLine().getStatusCode());
            logger.info("HTTP ReasonPhrase: " + response.getStatusLine().getReasonPhrase());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = new String(EntityUtils.toByteArray(response.getEntity()));
                logger.info("HTTP 响应报文: " + result);
                return result;
            }
        }catch (Exception e) {
            logger.error("Log error: " + e.toString());
            throw e;
        }finally {
            httpClient.getConnectionManager().shutdown();
//            EntityUtils.consume(entity);
        }

        return null;
    }

    /**
     * http执行JSON消息Post请求
     * @param httpUrl
     * @param reqData 请求数据对象
     * @return
     * @throws Exception
     */
    public static <T> String doJsonPost(String httpUrl, T reqData) throws Exception {
        logger.info("HTTP URL:" + httpUrl);
        org.apache.http.client.HttpClient httpClient = getCustomerHttpClient();

        try {
            String jsonStr = JsonUtils.writeValueAsString(reqData);
            logger.info("HTTP 报文:" + jsonStr);

            HttpPost httpPost = new HttpPost(httpUrl);
            // 将JSON信息放入POST
            StringEntity postEntity = new StringEntity(jsonStr, HTTP.UTF_8);
            httpPost.setEntity(postEntity);

            // 执行POST请求
            HttpResponse response = httpClient.execute(httpPost);
            logger.info("HTTP Status: " + response.getStatusLine().getStatusCode());
            logger.info("HTTP ReasonPhrase: " + response.getStatusLine().getReasonPhrase());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = new String(EntityUtils.toByteArray(response.getEntity()));
                logger.info("HTTP 响应报文: " + response.getStatusLine().getReasonPhrase());
                return result;
            }
        }catch (Exception e) {
            logger.error("Log error: " + e.toString());
            throw e;
        }finally {
            httpClient.getConnectionManager().shutdown();
//            EntityUtils.consume(entity);
        }

        return null;
    }

    /**
     * http执行JSON消息Post请求
     * @param httpUrl
     * @param reqData 请求数据对象
     * @return
     * @throws Exception
     */
    public static <T> String doXmlPost(String httpUrl, T reqData) throws Exception {
        logger.info("HTTP URL:" + httpUrl);
        org.apache.http.client.HttpClient httpClient = getCustomerHttpClient();

        try {
            String xmlStr = XmlUtils.writeValueAsString(reqData);
            logger.info("HTTP 报文:" + xmlStr);

            HttpPost httpPost = new HttpPost(httpUrl);
            // 将XML信息放入POST
            StringEntity postEntity = new StringEntity(xmlStr, HTTP.UTF_8);
            httpPost.setEntity(postEntity);

            // 执行POST请求
            HttpResponse response = httpClient.execute(httpPost);
            logger.info("HTTP Status: " + response.getStatusLine().getStatusCode());
            logger.info("HTTP ReasonPhrase: " + response.getStatusLine().getReasonPhrase());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = new String(EntityUtils.toByteArray(response.getEntity()));
                logger.info("HTTP 响应报文: " + response.getStatusLine().getReasonPhrase());
                return result;
            }
        }catch (Exception e) {
            logger.error("Log error: " + e.toString());
            throw e;
        }finally {
            httpClient.getConnectionManager().shutdown();
//            EntityUtils.consume(entity);
        }

        return null;
    }


    public static void postUpload(File file, String url, Map<String, String> params) throws Exception {
        org.apache.http.client.HttpClient httpclient = getCustomerHttpClient();
        HttpPost httppost = new HttpPost(url);
        System.out.println("executing request url " + url);
        MultipartEntity mpEntity = new MultipartEntity(); //文件传输
        // 添加上传文件
        ContentBody cbFile = new FileBody(file);
        mpEntity.addPart("file", cbFile); // <input type="file" name="userfile" />  对应的
        // 添加请求参数
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                StringBody stringBody = new StringBody(params.get(key));
                mpEntity.addPart(key, stringBody);
            }
        }

        httppost.setEntity(mpEntity);
        System.out.println("executing request " + httppost.getRequestLine());

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();

        System.out.println(response.getStatusLine());//通信Ok
        if (resEntity != null) {
            resEntity.getContent().close();
        }
        httpclient.getConnectionManager().shutdown();
    }

    public static void postDowload(String filePath, String url) throws Exception {

        org.apache.http.client.HttpClient httpclient = getCustomerHttpClient();
        HttpPost httppost = new HttpPost(url);
        System.out.println("executing request url " + url);
        HttpResponse resp = httpclient.execute(httppost);
        //判断访问状态是否正确执行
        if (HttpStatus.SC_OK == resp.getStatusLine().getStatusCode()) {
            HttpEntity entity = resp.getEntity();
            InputStream inputStream = entity.getContent();
            File file = new File(filePath);
            OutputStream outputStream = new FileOutputStream(file);
            byte buffer[] = new byte[4 * 1024];
            while ((inputStream.read(buffer)) != -1) {
                outputStream.write(buffer);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }
    }

    public static void main(String[] args) throws Exception {
        String url = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=ACCESS_TOKEN";
//        String access_token = WeiXinUtil.obtainAccessTokenTest().getToken();
        String access_token = "dsdsd";
        String media_id = "UI36NRFUpii8YOCSBXtY8I1TPmnNqdAJilJkPC40uis";
        url = url.replace("ACCESS_TOKEN", access_token);
        Map<String, String> map = new HashMap<String, String>();
        map.put("media_id", media_id);
//        String data = JacksonObjectMapper.getInstance().writeValueAsString(map);
//        HttpClient httpClient = getCustomerHttpClient();
//        try {
//            HttpPost httpPost = new HttpPost(url);
//            StringEntity entity = new StringEntity(data);
//            entity.setContentEncoding("UTF-8");
//            entity.setContentType("application/json");
//            httpPost.setEntity(entity);
//            HttpResponse response = httpClient.execute(httpPost);
//            String name = "E:/temp/guotest98.jpg";
//            File storeFile = new File(name);
//            FileOutputStream fileOutputStream = new FileOutputStream(storeFile);
//            FileOutputStream output = fileOutputStream;
//            output.write(EntityUtils.toByteArray(response.getEntity()));
//            output.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

}
