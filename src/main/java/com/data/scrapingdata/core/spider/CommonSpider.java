package com.data.scrapingdata.core.spider;

import com.data.scrapingdata.core.SpiderInfo;
import com.data.scrapingdata.core.downloader.casper.CasperjsDownloader;
import com.data.scrapingdata.core.pipeline.CommonWebpagePipelineList;
import com.data.scrapingdata.core.processor.MyPageProcessor;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.SimpleProxyProvider;

import javax.annotation.Resource;

/**
 * @author YL
 * @date 16:32 2021/11/24
 */
@Component
public class CommonSpider {

    @Resource
    private CasperjsDownloader casperjsDownloader;
    @Resource
    private CommonWebpagePipelineList commonWebpagePipelineList;

    /**
     * 生成爬虫模板
     *
     * @param spiderInfo
     * @return com.hotcoin.data.scraping.core.spider.MySpider
     * @author YL
     * @date 2021/11/24 16:33
     */
    public Spider makeSpider(SpiderInfo spiderInfo) {
        Spider spider = new Spider(new MyPageProcessor(spiderInfo))
                .thread(spiderInfo.getThread())
                .startUrls(spiderInfo.getStartURL());
        //添加输出通道
        commonWebpagePipelineList.getPipelineList().forEach(spider::addPipeline);
        //添加代理
        if (!spiderInfo.getProxies().isEmpty()) {
            //设置抓取代理IP与接口
            HttpClientDownloader httpClientDownloader = new HttpClientDownloader();
            httpClientDownloader.setProxyProvider(new SimpleProxyProvider(spiderInfo.getProxies()));
            spider.setDownloader(httpClientDownloader);
        }
        if (spiderInfo.isAjaxSite()) {
            spider.setDownloader(casperjsDownloader);
        }
        return spider;
    }
}
