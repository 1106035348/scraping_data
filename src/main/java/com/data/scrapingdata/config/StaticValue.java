package com.data.scrapingdata.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author YL
 * @date 10:10 2021/11/4
 */
@Data
@Configuration
public class StaticValue {

    @Value("${hotcoin.task.delete.delay}")
    private int taskDeleteDelay;

    @Value("${hotcoin.task.delete.period}")
    private int taskDeletePeriod;
    /**
     * 普通网页下载器队列最大长度限制
     */
    @Value("${hotcoin.limitOfCommonWebpageDownloadQueue}")
    private int limitOfCommonWebpageDownloadQueue;

    @Value("${hotcoin.ajaxDownloader}")
    private String ajaxDownloader;
}
