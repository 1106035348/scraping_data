package com.data.scrapingdata.utils;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 采用java.net.HttpURLConnection 实现基本http请求
 * 不依赖第三方库
 * Date: 2019/3/14 0014
 * Created by luoyingxiong
 */
public class HttpClientUtils {
    private enum RequestMethodEnum {
        GET("GET", false),//get 不带数据
        POST("POST", true),
        PUT("PUT", true),
        DELETE("DELETE", false)
        ;
        private String method;
        private boolean data;

        RequestMethodEnum(String method, boolean data) {
            this.method = method;
            this.data = data;
        }

        public String getMethod() {
            return method;
        }

        public boolean isData() {
            return data;
        }

        public static RequestMethodEnum getEnumObject(String method) {
            for (RequestMethodEnum requestMethodEnum : RequestMethodEnum.values()) {
                if (method.equalsIgnoreCase(requestMethodEnum.getMethod())) {
                    return requestMethodEnum;
                }
            }
            return null;
        }


    }

    /**
     * get请求
     *
     * @param url
     * @return
     */
    public static String get(String url) throws Exception {
        return get(url, null);
    }

    /**
     * get请求 自定义header
     *
     * @param url
     * @param headers
     * @return
     */
    public static String get(String url, Map<String, String> headers) throws Exception {
        String responce = null;
        Map<String, String> responceMap = request(url, RequestMethodEnum.GET.getMethod(), headers, "");
        if ("200".equalsIgnoreCase(responceMap.get("code"))) {
            responce = responceMap.get("result");
        }
        return responce;
    }

    /**
     * post 请求
     *
     * @param url
     * @param data
     * @return
     * @throws Exception
     */
    public static String post(String url, String data) throws Exception {
        return post(url, null, data);
    }

    /**
     * post 请求 带header
     *
     * @param url
     * @param headers
     * @param data
     * @return
     * @throws Exception
     */
    public static String post(String url, Map<String, String> headers, String data) throws Exception {
        String response = null;
        Map<String, String> responseMap = request(url, RequestMethodEnum.POST.getMethod(), headers, data);
        response = responseMap.get("result");
        return response;
    }

    /**
     * http基本请求
     *
     * @param url
     * @param method
     * @param headers
     * @param data
     * @return
     * @throws Exception
     */
    private static Map<String, String> request(String url, String method, Map<String, String> headers, String data) throws Exception {
        Map<String, String> response = new HashMap<>(6);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        //设置请求方式
        con.setRequestMethod(method);
        //header
        if (null != headers) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        con.setDoOutput(true);
        if (RequestMethodEnum.getEnumObject(method).isData()) {
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//            wr.writeBytes(data);
            wr.write(data.getBytes(StandardCharsets.UTF_8));
            wr.flush();
            wr.close();
        }
        int responseCode = con.getResponseCode();
        response.put("code", responseCode + "");
        try {
            BufferedReader in;
            if (responseCode >= 400) {
                in = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }
            String inputLine;
            StringBuffer responseBuffer = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                responseBuffer.append(inputLine);
            }
            in.close();
            response.put("result", responseBuffer.toString());
        } catch (Throwable throwable) {
            response.put("result", "");
        }
        return response;
    }
}
