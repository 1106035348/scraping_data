package com.data.scrapingdata.core.processor;


import com.data.scrapingdata.core.SpiderInfo;
import com.data.scrapingdata.core.consumer.PageConsumer;
import com.data.scrapingdata.utils.SpringUtils;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author YL
 * @date 10:23 2021/11/8
 */
public class MyPageProcessor implements PageProcessor {

    private final Site site;
    private final SpiderInfo info;
    private PageConsumer pageConsumer;


    public MyPageProcessor(SpiderInfo info) {
        this.site = Site.me().setDomain(info.getDomain()).setTimeOut(info.getTimeout())
                .setRetryTimes(info.getRetry()).setSleepTime(info.getSleep())
                .setCharset(StringUtils.isBlank(info.getCharset()) ? null : info.getCharset())
                .setUserAgent(info.getUserAgent());
        this.info = info;
        pageConsumer = (PageConsumer) SpringUtils.getBean(info.getConsumer() + "Consumer");
    }

    @Override
    public void process(Page page) {
        pageConsumer.accept(page, info);
    }

    @Override
    public Site getSite() {
        return site;
    }
}
