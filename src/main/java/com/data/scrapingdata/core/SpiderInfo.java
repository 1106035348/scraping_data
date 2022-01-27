package com.data.scrapingdata.core;

import lombok.*;
import lombok.experimental.Accessors;
import us.codecraft.webmagic.proxy.Proxy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 网页抽取模板
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Accessors(chain = true)
public class SpiderInfo {
    /**
     * 使用多少抓取线程
     */
    private int thread = 1;
    /**
     * 失败的网页重试次数
     */
    private int retry = 2;
    /**
     * 抓取每个网页睡眠时间
     */
    private int sleep = 0;
    /**
     * 最大抓取网页数量,0代表不限制
     */
    private int maxPageGather = 10;
    /**
     * HTTP链接超时时间
     */
    private int timeout = 5000;
    /**
     * 网站权重
     */
    private int priority;
    /**
     * 是否只抓取首页
     */
    private boolean gatherFirstPage = false;
    /**
     * 抓取模板id
     */
    private String id;
    /**
     * 网站名称
     */
    private String siteName;
    /**
     * 域名
     */
    private String domain;
    /**
     * 起始链接
     */
    private List<String> startURL;
    /**
     * 正文正则表达式
     */
    private String contentReg;
    /**
     * 正文Xpath
     */
    private String contentXPath;
    /**
     * 正文Json
     */
    private String contentJson;
    /**
     * 标题正则
     */
    private String titleReg;
    /**
     * 标题xpath
     */
    private String titleXPath;
    /**
     * 标题json
     */
    private String titleJson;
    /**
     * 分类信息正则
     */
    private String categoryReg;
    /**
     * 分类信息XPath
     */
    private String categoryXPath;
    /**
     * 分类信息XPath
     */
    private String categoryJson;
    /**
     * 默认分类
     */
    private String defaultCategory;
    /**
     * url正则
     */
    private String urlReg;
    /**
     * url Json
     */
    private String urlJson;
    /**
     * 编码
     */
    private String charset;
    /**
     * 发布时间xpath
     */
    private String publishTimeXPath;
    /**
     * 发布时间json
     */
    private String publishTimeJson;
    /**
     * 发布时间正则
     */
    private String publishTimeReg;
    /**
     * 发布时间模板
     */
    private String publishTimeFormat;
    /**
     * 回调url
     */
    private List<String> callbackURL;
    /**
     * 是否进行nlp处理
     */
    private boolean doNLP = true;
    /**
     * 是否由json解析
     */
    private String consumer;
    /**
     * 网页必须有标题
     */
    private boolean needTitle = false;
    /**
     * 网页必须有正文
     */
    private boolean needContent = false;
    /**
     * 网页必须有发布时间
     */
    private boolean needPublishTime = false;
    /**
     * 动态字段列表
     */
    private List<FieldConfig> dynamicFields = new LinkedList<>();
    /**
     * 静态字段
     */
    private List<StaticField> staticFields = new LinkedList<>();
    /**
     * 语言,用于配置发布时间
     */
    private String lang;
    /**
     * 国家,用于配置发布时间
     */
    private String country;
    /**
     * User Agent
     */
    private String userAgent = "Mozilla/5.0 (Windows NT 5.2) AppleWebKit/534.30 (KHTML, like Gecko) Chrome/12.0.742.122 Safari/534.30";
    /**
     * 是否保存网页快照,默认保存
     */
    private boolean saveCapture = true;
    /**
     * 是否是ajax网站,如果是则使用casperjs下载器
     */
    private boolean ajaxSite = false;
    /**
     * 自动探测发布时间
     */
    private boolean autoDetectPublishDate = false;


    private List<Proxy> proxies = new ArrayList<>();


    public class StaticField {
        private String name;
        private String value;

        public StaticField(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public StaticField setName(String name) {
            this.name = name;
            return this;
        }

        public String getValue() {
            return value;
        }

        public StaticField setValue(String value) {
            this.value = value;
            return this;
        }
    }

    public class FieldConfig {
        private String regex;
        private String xpath;
        private String json;
        private String name;
        private boolean need = false;

        public FieldConfig(String regex, String xpath, String json, String name, boolean need) {
            this.regex = regex;
            this.xpath = xpath;
            this.json = json;
            this.name = name;
            this.need = need;
        }

        public FieldConfig() {
        }

        public String getRegex() {
            return regex;
        }

        public FieldConfig setRegex(String regex) {
            this.regex = regex;
            return this;
        }

        public String getXpath() {
            return xpath;
        }

        public FieldConfig setXpath(String xpath) {
            this.xpath = xpath;
            return this;
        }

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }

        public String getName() {
            return name;
        }

        public FieldConfig setName(String name) {
            this.name = name;
            return this;
        }

        public boolean isNeed() {
            return need;
        }

        public FieldConfig setNeed(boolean need) {
            this.need = need;
            return this;
        }
    }
}
