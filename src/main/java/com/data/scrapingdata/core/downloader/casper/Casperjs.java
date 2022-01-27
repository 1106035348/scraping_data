package com.data.scrapingdata.core.downloader.casper;


import com.alibaba.fastjson.JSONObject;
import com.data.scrapingdata.config.StaticValue;
import com.data.scrapingdata.core.Request;
import com.data.scrapingdata.utils.HttpClientUtils;
import org.assertj.core.util.Preconditions;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Casperjs
 */
@Component
public class Casperjs {

    @Resource
    private StaticValue staticValue;

    /**
     * 抓取html
     *
     * @param request 请求实体
     * @param url     casper url
     * @return
     * @throws IOException
     */
    private String gatherHtml(Request request, String url) throws Exception {
        Preconditions.checkArgument(request.getUrl().startsWith("http"), "url必须以http开头,当前url:%s", request.getUrl());
        Fetch fetch = new Fetch().setUrl(request.getUrl());
        String json = HttpClientUtils.post(url, JSONObject.toJSONString(fetch));
        json = new String(json.getBytes("iso8859-1"), StandardCharsets.UTF_8);
        return JSONObject.parseObject(json).getString("content");
    }

    /**
     * 抓取网页html
     *
     * @param request 请求实体
     * @return
     * @throws IOException
     */
    public String gatherHtml(Request request) throws Exception {
        return gatherHtml(request, staticValue.getAjaxDownloader() + "html");
    }


    public class Fetch {
        private String proxy = "";
        private int jsViewportWidth = 1024;
        private int jsViewportHeight = 1024;
        private boolean loadImages = false;
        private int timeout = 5;
        private String url;
        private String method = "get";
        private String data = "";
        private Map<String, String> headers;
        private String jsRunAt;
        private String jsScript;

        public String getProxy() {
            return proxy;
        }

        public Fetch setProxy(String proxy) {
            this.proxy = proxy;
            return this;
        }

        public int getJsViewportWidth() {
            return jsViewportWidth;
        }

        public Fetch setJsViewportWidth(int jsViewportWidth) {
            this.jsViewportWidth = jsViewportWidth;
            return this;
        }

        public int getJsViewportHeight() {
            return jsViewportHeight;
        }

        public Fetch setJsViewportHeight(int jsViewportHeight) {
            this.jsViewportHeight = jsViewportHeight;
            return this;
        }

        public boolean isLoadImages() {
            return loadImages;
        }

        public Fetch setLoadImages(boolean loadImages) {
            this.loadImages = loadImages;
            return this;
        }

        public int getTimeout() {
            return timeout;
        }

        public Fetch setTimeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public Fetch setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getMethod() {
            return method;
        }

        public Fetch setMethod(String method) {
            this.method = method;
            return this;
        }

        public String getData() {
            return data;
        }

        public Fetch setData(String data) {
            this.data = data;
            return this;
        }

        public Map<String, String> getHeaders() {
            return headers;
        }

        public Fetch setHeaders(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public String getJsRunAt() {
            return jsRunAt;
        }

        public Fetch setJsRunAt(String jsRunAt) {
            this.jsRunAt = jsRunAt;
            return this;
        }

        public String getJsScript() {
            return jsScript;
        }

        public Fetch setJsScript(String jsScript) {
            this.jsScript = jsScript;
            return this;
        }
    }
}
