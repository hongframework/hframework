package com.hframework.common.client.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.Map;

/**
 * HTTP请求工具类
 */
public class HttpPostUtils {

    public static String doHttpPost(String postUrl, Map<String, String> params) {

        HttpClient client = new HttpClient();
        UTF8PostMethod mPost = new UTF8PostMethod(postUrl);
        client.getParams().setParameter("http.socket.timeout", new Integer(600000));
        String[] keys = params.keySet().toArray(new String[0]);
        for (String key : keys) {
            mPost.addParameter(key, params.get(key));
        }
        //mPost.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        String responseTxt = "";
        mPost.getResponseCharSet();
        try {
            int responseCode = client.executeMethod(mPost);
            if (responseCode != HttpStatus.SC_OK) {
                responseTxt = "";
            } else {
                responseTxt = mPost.getResponseBodyAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mPost.releaseConnection();
                client.getHttpConnectionManager().closeIdleConnections(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseTxt;
    }

    static class UTF8PostMethod extends PostMethod {

        public UTF8PostMethod(String url) {
            super(url);
        }

        @Override
        public String getRequestCharSet() {
            return "UTF-8";
        }
    }

    public static String doHttpPostSubmit(String loginUrl, String postUrl, Map<String, String> params) {

        HttpClient client = new HttpClient();
        UTF8PostMethod login = new UTF8PostMethod(loginUrl);
        UTF8PostMethod mPost = new UTF8PostMethod(postUrl);
        client.getParams().setParameter("http.socket.timeout", new Integer(600000));
        String responseTxt = "";
        int responseCode = 0;
        try {
            //登录
            login.addParameter("username", "admin");
            login.addParameter("password", "admin");
            login.getResponseCharSet();
            responseCode = client.executeMethod(login);
            //提交
            String[] keys = params.keySet().toArray(new String[0]);
            for (String key : keys) {
                mPost.addParameter(key, params.get(key));
            }
            mPost.getResponseCharSet();

            responseCode = client.executeMethod(mPost);
            if (responseCode != HttpStatus.SC_OK) {
                responseTxt = "";
            } else {
                responseTxt = mPost.getResponseBodyAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mPost.releaseConnection();
                client.getHttpConnectionManager().closeIdleConnections(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return responseTxt;
    }

}
