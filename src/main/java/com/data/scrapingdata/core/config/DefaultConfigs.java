package com.data.scrapingdata.core.config;

import com.data.scrapingdata.core.consumer.JsonConsumer;
import com.data.scrapingdata.core.consumer.PageConsumer;
import com.data.scrapingdata.core.consumer.ViewConsumer;
import com.data.scrapingdata.core.nlp.HANLPExtractor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author YL
 * @date 18:59 2021/11/25
 */
@Configuration
public class DefaultConfigs {

    @Bean
    public HANLPExtractor HANLPExtractor() {
        return new HANLPExtractor();
    }

    @Bean
    public PageConsumer jsonConsumer() {
        return new JsonConsumer();
    }

    @Bean
    public PageConsumer viewConsumer() {
        return new ViewConsumer();
    }
}
