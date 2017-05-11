package com.hframework.common.util.message;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;

/**
 * User: zhangqh6
 * Date: 2016/1/19 16:26:26
 */
public class JsonUtils {

    private static ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
    }

    /**
     * Json内容转化为对象
     * @param content
     * @param valueType
     * @param <T>
     * @return
     * @throws IOException
     */
    public static  <T> T  readValue(String content, Class... valueType) throws IOException {
        return objectMapper.readValue(content,objectMapper.getTypeFactory().constructParametricType(valueType[0],valueType[1]));
    }

    /**
     * Json内容转化为对象
     * @param content
     * @param valueType
     * @param <T>
     * @return
     * @throws IOException
     */
    public static  <T> T  readValue(String content, Class<T> valueType) throws IOException {
        return objectMapper.readValue(content,valueType);
    }

    public static  <T> String writeValueAsString(T t) throws IOException {
        return objectMapper.writeValueAsString(t);
    }


    public static void main(String[] args) throws IOException {
        String rootClassPath = Thread.currentThread().getContextClassLoader ().getResource("").getPath();
        String jsonString = readFile(rootClassPath + "test.json");
        System.out.println(jsonString);
//        Menu menu = readValue(jsonString, Menu.class);
//        System.out.println(writeValueAsString(menu));
    }

    private static String readFile(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
        String retStr = "";
        String str = br.readLine();
        while(str != null) {
            retStr += str;
            str = br.readLine();
        }
        return retStr;
    }

}
