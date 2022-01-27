package com.data.scrapingdata.core.consumer;


import com.data.scrapingdata.core.SpiderInfo;
import us.codecraft.webmagic.Page;

/**
 * PageConsumer
 */
@FunctionalInterface
public interface PageConsumer {

    /**
     * 数据解析
     * @author YL
     * @date 2021/11/8 11:12
     * @param page
     * @param info
     * @return void
     */
    void accept(Page page, SpiderInfo info);
}
