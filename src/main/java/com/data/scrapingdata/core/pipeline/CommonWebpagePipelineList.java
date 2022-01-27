package com.data.scrapingdata.core.pipeline;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author YL
 * @date 12:02 2021/11/5
 */
@Component
public class CommonWebpagePipelineList implements ApplicationContextAware {

    @Getter
    private List<Pipeline> pipelineList = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Pipeline> beansOfType = applicationContext.getBeansOfType(Pipeline.class);
        pipelineList = new ArrayList<>(beansOfType.values());
    }

}
