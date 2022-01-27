package com.data.scrapingdata.task;

import com.data.scrapingdata.core.SpiderInfo;
import com.data.scrapingdata.core.spider.CommonSpider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.proxy.Proxy;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YL
 * @date 18:10 2021/11/4
 */
@Component
public class GithubScheduler {

    @Autowired
    private CommonSpider commonSpider;

    @Scheduled(cron = "*/5 * * * * ?")
    public void run1() {
        SpiderInfo spiderInfo = new SpiderInfo();
        List<String> startURL = new ArrayList<>();
        startURL.add("https://github.com/ethereum/go-ethereum/releases");
        startURL.add("https://github.com/bitcoin/bitcoin/releases");
        spiderInfo.setStartURL(startURL)
                .setConsumer("view")
                .setSiteName("github")
                .setDomain("github.com")
                .setContentXPath("//div[@data-test-selector='body-content']/tidyText()")
                .setTitleXPath("//h1[@class='d-inline mr-3']/text()")
                .setUrlReg("https://github\\.com/.*/.*/releases/tag/.*")
                .setPublishTimeXPath("//*[@datetime]/@datetime")
                .setLang("zh")
                .setCountry("CN")
                .setSleep(10000)
                .setThread(10)
                .setPublishTimeFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Spider spider = commonSpider.makeSpider(spiderInfo);
        spider.run();
    }

    //@Scheduled(cron = "*/5 * * * * ?")
    public void run2() {
        SpiderInfo spiderInfo = new SpiderInfo();
        List<String> startURL = new ArrayList<>();
        startURL.add("https://github.com/bitcoin/bitcoin/releases");
        spiderInfo.setStartURL(startURL)
                .setConsumer("view")
                .setSiteName("github")
                .setDomain("github.com")
                .setContentXPath("//div[@data-test-selector='body-content']/tidyText()")
                .setTitleXPath("//h1[@class='d-inline mr-3']/text()")
                .setUrlReg("https://github\\.com/.*/.*/releases/tag/.*")
                .setPublishTimeXPath("//*[@datetime]/@datetime")
                .setLang("zh")
                .setCountry("CN")
                .setSleep(10000)
                .setThread(2)
                .setPublishTimeFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Spider spider = commonSpider.makeSpider(spiderInfo);
        spider.run();
    }

    //@Scheduled(cron = "*/5 * * * * ?")
    public void run3() {
        SpiderInfo spiderInfo = new SpiderInfo();
        List<String> startURL = new ArrayList<>();
        startURL.add("https://www.feixiaohao.co/exchange/notice/");
        spiderInfo.setStartURL(startURL)
                .setConsumer("view")
                .setSiteName("feixiaohao")
                .setDomain("feixiaohao.co")
                .setContentXPath("//div[@data-test-selector='body-content']/tidyText()")
                .setTitleXPath("//h1[@class='d-inline mr-3']/text()")
                .setUrlReg("https://github\\.com/.*/.*/releases/tag/.*")
                .setPublishTimeXPath("//*[@datetime]/@datetime")
                .setLang("zh")
                .setCountry("CN")
                .setSleep(10000)
                .setThread(2)
                .setAjaxSite(true)
                .setPublishTimeFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Spider spider = commonSpider.makeSpider(spiderInfo);
        spider.run();
    }

    //@Scheduled(cron = "*/30 * * * * ?")
    public void json0() {
        SpiderInfo spiderInfo = new SpiderInfo();
        List<String> startURL = new ArrayList<>();
        startURL.add("https://api.github.com/repos/ethereum/go-ethereum/releases");
        startURL.add("https://api.github.com/repos/bitcoin/bitcoin/releases");
        spiderInfo.setStartURL(startURL)
                .setConsumer("json")
                .setSiteName("github")
                .setDomain("github.com")
                .setUrlReg("^https://api.github\\.com/repos/.*/.*/releases$")
                .setUrlJson("$[*].url")
                .setContentJson("$.body")
                .setTitleJson("$.name")
                .setPublishTimeJson("$.published_at")
                .setLang("zh")
                .setCountry("CN")
                .setThread(10)
                .setPublishTimeFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        List<Proxy> list = new ArrayList<>();
        Proxy proxy = new Proxy("localhost", 10809);
        list.add(proxy);
        spiderInfo.setProxies(list);

        Spider spider = commonSpider.makeSpider(spiderInfo);
        spider.run();
    }

    //@Scheduled(cron = "*/30 * * * * ?")
    public void json1() {
        SpiderInfo spiderInfo = new SpiderInfo();
        List<String> startURL = new ArrayList<>();
        startURL.add("https://dncapi.fxhapp.com/api/v3/exchange/news?page=1&per_page=10&webp=1");
        spiderInfo.setStartURL(startURL)
                .setConsumer("json")
                .setSiteName("fxh")
                .setDomain("dncapi.fxhapp.com")
                .setUrlReg("^$")
                .setUrlJson("$[*].url")
                .setContentJson("$.body")
                .setTitleJson("$.name")
                .setPublishTimeJson("$.published_at")
                .setLang("zh")
                .setCountry("CN")
                .setPublishTimeFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        List<Proxy> list = new ArrayList<>();
        Proxy proxy = new Proxy("localhost", 10809);
        list.add(proxy);
        spiderInfo.setProxies(list);

        Spider spider = commonSpider.makeSpider(spiderInfo);
        spider.run();
    }
}
