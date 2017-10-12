package com.hframework.common.client.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
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
 */
public class HttpClientUtils {

    // 从连接池中取连接的超时时间
    public final static Integer CONN_MANAGER_TIMETOUT = 10000; //10s
    // 连接超时
    public final static Integer HTTP_CONNECT_TIMEOUT = 10000; //10s
    // 请求超时
    public final static Integer HTTP_SO_TIMEOUT = 10000; //10s

    private static Logger logger = Logger.getLogger(HttpClientUtils.class);

    private static HttpClient customerHttpClient;

    public static HttpClient getCustomerHttpClient() {

        if (customerHttpClient != null) return customerHttpClient;

        HttpParams httpParams = new BasicHttpParams();
        // 从连接池中取连接的超时时间
        ConnManagerParams.setTimeout(httpParams, CONN_MANAGER_TIMETOUT);
        // 连接超时
        HttpConnectionParams.setConnectionTimeout(httpParams, HTTP_CONNECT_TIMEOUT);
        // 请求超时
        HttpConnectionParams.setSoTimeout(httpParams, HTTP_SO_TIMEOUT);
        // 使用线程安全的连接管理来创建 HttpClient
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        ClientConnectionManager clientConnectionManager = new ThreadSafeClientConnManager(httpParams, schemeRegistry);
        customerHttpClient = new DefaultHttpClient(clientConnectionManager, httpParams);
        return customerHttpClient;
    }

    public static String get(String httpUrl, List<BasicNameValuePair> params) throws Throwable {
        if (params != null) {
            logger.info("HTTP Request params: " + params.toString());
        }
        HttpClient httpClient = getCustomerHttpClient();
        if (params != null && params.size() > 0) {
            //对参数编码
            String strParams = URLEncodedUtils.format(params, HTTP.UTF_8);
            httpUrl += "?" + strParams;
        }
        HttpGet httpGet = new HttpGet(httpUrl);
        HttpResponse httpResponse = httpClient.execute(httpGet);

        try {
            //LogUtils.i(TAG, "HTTP Entity" + EntityUtils.toString(httpResponse.getEntity(), "无实体"));
            logger.info("HTTP Url: " + httpUrl);
            logger.info("HTTP Status: " + httpResponse.getStatusLine().getStatusCode());
            logger.info("HTTP ReasonPhrase: " + httpResponse.getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            logger.error("Log error: " + e.toString());
        }

        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
        }
        return null;
    }

    public static String post(String httpUrl, List<BasicNameValuePair> params) throws Throwable {
        HttpClient httpClient = getCustomerHttpClient();
        HttpPost httpPost = new HttpPost(httpUrl);
        if (params != null && params.size() > 0) {
            httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8)); //将参数填入POST Entity中
        }
        HttpResponse httpResponse = httpClient.execute(httpPost);

        try {
            //LogUtils.i(TAG, "HTTP Entity" + EntityUtils.toString(httpResponse.getEntity(), "无实体"));
            logger.info("HTTP Url: " + httpUrl);
            logger.info("HTTP Status: " + httpResponse.getStatusLine().getStatusCode());
            logger.info("HTTP ReasonPhrase: " + httpResponse.getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            logger.error("Log error: " + e.toString());
        }

        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            return EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
        }
        return null;
    }


    public static void postUpload(File file, String url, Map<String, String> params) throws Throwable {
        HttpClient httpclient = getCustomerHttpClient();
        HttpPost httppost = new HttpPost(url);
        logger.info("executing request url " + url);
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
        logger.info("executing request " + httppost.getRequestLine());

        HttpResponse response = httpclient.execute(httppost);
        HttpEntity resEntity = response.getEntity();

        System.out.println(response.getStatusLine());//通信Ok
        if (resEntity != null) {
            resEntity.getContent().close();
        }
        httpclient.getConnectionManager().shutdown();
    }

    public static void postDowload(String filePath, String url) throws Throwable {
        HttpClient httpclient = getCustomerHttpClient();
        HttpPost httppost = new HttpPost(url);
        logger.info("executing request url " + url);
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

    public static HttpResponse executePost(String httpUrl, List<BasicNameValuePair> params) throws Throwable {
        HttpClient httpClient = getCustomerHttpClient();
        HttpPost httpPost = new HttpPost(httpUrl);
        if (params != null && params.size() > 0) {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8")); //将参数填入POST Entity中
        }

        HttpResponse httpResponse = httpClient.execute(httpPost);

        try {
            System.out.println("HTTP Status: " + httpResponse.getStatusLine().getStatusCode());
            System.out.println("HTTP ReasonPhrase: " + httpResponse.getStatusLine().getReasonPhrase());
        } catch (Exception e) {
            System.out.println("Log error: " + e.toString());
        }

        return httpResponse;
    }

    public static InputStream postImage(String httpUrl, List<BasicNameValuePair> params) throws Throwable {

        HttpResponse httpResponse = executePost(httpUrl, params);

        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            //将返回内容转换为bitmap
            InputStream inputStream = httpResponse.getEntity().getContent();
            return inputStream;
        }
        return null;
    }

    public static void main(String[] args) throws Throwable {
        while(true) {
            String msg =null;/*  HttpPostUtils.doHttpPost("https://cmcoins.boc.cn/CoinSeller/_bfwajax.do?_locale=zh_CN", new HashMap<String, String>() {{
                put("json", "{\"method\":\"PsnProvincialInstitution\",\"params\":{\"productId\":\"HSB20170101\",\"province\":\"北京市\"},\"header\":{\"agent\":\"WEB15\",\"version\":\"1.0\",\"device\":\"\",\"platform\":\"Win32\",\"plugins\":\"\",\"page\":\"\",\"local\":\"zh_CN\",\"ext\":\"\"}}");
            }}); */

            JSONObject jsonObject = JSONObject.parseObject(msg);
            System.out.println(jsonObject.getJSONArray("result").size());
            if(jsonObject.getJSONArray("result").size() > 0) {
                HttpClientUtils.post("http://****.****.com/api/alarm/push", new ArrayList<BasicNameValuePair>() {{
                    add(new BasicNameValuePair("type", "manis"));
                    add(new BasicNameValuePair("title", "manis阻塞：" + "****_test"));
                    add(new BasicNameValuePair("content", "info"));
                }});
                System.out.println(jsonObject.getString("result"));
                break ;
            }
            Thread.sleep(1000L);
        }

    }
}
