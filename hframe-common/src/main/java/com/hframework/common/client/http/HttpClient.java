package com.hframework.common.client.http;


import com.hframework.common.util.message.JsonUtils;
import com.hframework.common.util.message.XmlUtils;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP访问客户端
 */
public class HttpClient {

    private static Logger logger = Logger.getLogger(HttpClient.class);

    // 连接超时
    public static Integer HTTP_CONNECT_TIMEOUT = 1000 * 3;
    // 请求超时
    public static Integer HTTP_SO_TIMEOUT = 1000 * 5;


    private static URI getUri(String httpUrl, Map<String, String> params) throws URISyntaxException {
        if (params != null) {
            logger.info("HTTP Request params: " + params.toString());
        }

        URIBuilder uriBuilder = new URIBuilder(httpUrl);
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (params.values() != null) {
                uriBuilder.addParameter(entry.getKey(), entry.getValue());
            }
        }

        URI uri = uriBuilder.build();
        System.out.println(uri);
        logger.info("HTTP URI: " + uri);
        return uri;
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
        String ret = null;
        try {
            ret = Request.Get(getUri(httpUrl, params))
                    .connectTimeout(HTTP_CONNECT_TIMEOUT)
                    .socketTimeout(HTTP_SO_TIMEOUT)
                    .setHeader("Content-Type", "charset=UTF-8")
                    .execute()
                    .returnContent().asString(Charset.forName("utf-8"));
        } catch (IOException e) {
            logger.warn("request failed.|{}", e);
        }
        return ret;
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
        String ret = null;
        try {
            ret = Request.Post(getUri(httpUrl, params))
                    .connectTimeout(HTTP_CONNECT_TIMEOUT)
                    .socketTimeout(HTTP_SO_TIMEOUT)
                    .setHeader("Content-Type", "charset=UTF-8")
                    .execute()
                    .returnContent().asString(Charset.forName("utf-8"));
        } catch (IOException e) {
            logger.warn("request failed.|{}", e);
        }
        logger.info("HTTP 响应报文: " + ret);
        System.out.println(ret);
        return ret;
    }


    /**
     * http执行JSON消息Post请求
     * @param httpUrl
     * @param jsonStr 请求数据对象
     * @return
     * @throws Exception
     */
    public static <T> String doJsonPost(String httpUrl, String jsonStr) throws Exception {
        logger.info("HTTP 报文:" + jsonStr);
        String ret = null;
        try {
            ret = Request.Post(httpUrl)
                    .connectTimeout(HTTP_CONNECT_TIMEOUT)
                    .socketTimeout(HTTP_SO_TIMEOUT)
                    .bodyString(jsonStr, ContentType.APPLICATION_JSON)
                    .setHeader("Content-Type", "charset=UTF-8")
                    .execute()
                    .returnContent().asString(Charset.forName("utf-8"));
        } catch (IOException e) {
            logger.warn("request failed.|{}", e);
        }
        logger.info("HTTP 响应报文: " + ret);
        return ret;
    }

    /**
     * http执行JSON消息Post请求
     * @param httpUrl
     * @param reqData 请求数据对象
     * @return
     * @throws Exception
     */
    public static <T> String doJsonPost(String httpUrl, T reqData) throws Exception {
        String jsonStr = JsonUtils.writeValueAsString(reqData);
        logger.info("HTTP 报文:" + jsonStr);
        String ret = null;
        try {
            ret = Request.Post(httpUrl)
                    .connectTimeout(HTTP_CONNECT_TIMEOUT)
                    .socketTimeout(HTTP_SO_TIMEOUT)
                    .bodyString(jsonStr, ContentType.APPLICATION_JSON)
                    .setHeader("Content-Type", "charset=UTF-8")
                    .execute()
                    .returnContent().asString(Charset.forName("utf-8"));
        } catch (IOException e) {
            logger.warn("request failed.|{}", e);
        }
        logger.info("HTTP 响应报文: " + ret);
        return ret;

    }

    /**
     * http执行JSON消息Post请求
     * @param httpUrl
     * @param reqData 请求数据对象
     * @return
     * @throws Exception
     */
    public static <T> String doXmlPost(String httpUrl, T reqData) throws Exception {
        String xmlStr = XmlUtils.writeValueAsString(reqData);
        logger.info("HTTP 报文:" + xmlStr);
        String ret = null;
        try {
            ret = Request.Post(httpUrl)
                    .connectTimeout(HTTP_CONNECT_TIMEOUT)
                    .socketTimeout(HTTP_SO_TIMEOUT)
                    .bodyString(xmlStr, ContentType.APPLICATION_XML)
                    .setHeader("Content-Type", "charset=UTF-8")
                    .execute()
                    .returnContent().asString(Charset.forName("utf-8"));
        } catch (IOException e) {
            logger.warn("request failed.|{}", e);
        }
        logger.info("HTTP 响应报文: " + ret);
        return ret;
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
