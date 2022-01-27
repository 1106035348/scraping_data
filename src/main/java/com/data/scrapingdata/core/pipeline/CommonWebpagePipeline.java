package com.data.scrapingdata.core.pipeline;

import com.data.scrapingdata.core.Webpage;
import com.data.scrapingdata.utils.Sha256;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.scheduler.component.DuplicateRemover;

import java.nio.charset.StandardCharsets;

/**
 * @author YL
 * @date 15:22 2021/11/4
 */
@Component
public class CommonWebpagePipeline implements DuplicateRemover, Pipeline {

    private static final String DYNAMIC_FIELD = "dynamic_fields";
    private static int COUNT = 0;


    public static Webpage convertResultItemsWebpage(ResultItems resultItems) {
        Webpage webpage = new Webpage();
        webpage.setContent(resultItems.get("content"));
        webpage.setTitle(resultItems.get("title"));
        webpage.setUrl(resultItems.get("url"));
        webpage.setId(Sha256.hashAndToHexString(webpage.getUrl().getBytes(StandardCharsets.UTF_8)));
        webpage.setDomain(resultItems.get("domain"));
        webpage.setSpiderInfoId(resultItems.get("spiderInfoId"));
        webpage.setGathertime(resultItems.get("gatherTime"));
        webpage.setSpiderUUID(resultItems.get("spiderUUID"));
        webpage.setKeywords(resultItems.get("keywords"));
        webpage.setSummary(resultItems.get("summary"));
        webpage.setNamedEntity(resultItems.get("namedEntity"));
        webpage.setPublishTime(resultItems.get("publishTime"));
        webpage.setCategory(resultItems.get("category"));
        webpage.setRawHTML(resultItems.get("rawHTML"));
        webpage.setDynamicFields(resultItems.get(DYNAMIC_FIELD));
        webpage.setStaticFields(resultItems.get("staticField"));
        webpage.setAttachmentList(resultItems.get("attachmentList"));
        webpage.setImageList(resultItems.get("imageList"));
        webpage.setProcessTime(resultItems.get("processTime"));
        return webpage;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        System.err.println("这儿------------------------------------------------");
        Webpage webpage = convertResultItemsWebpage(resultItems);
        System.err.println(webpage.getTitle());
        //TODO 后续处理
    }

    @Override
    public boolean isDuplicate(Request request, Task task) {
        return false;
    }

    @Override
    public void resetDuplicateCheck(Task task) {

    }

    @Override
    public int getTotalRequestsCount(Task task) {
        return COUNT++;
    }




}
