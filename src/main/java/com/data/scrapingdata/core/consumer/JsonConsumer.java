package com.data.scrapingdata.core.consumer;

import com.data.scrapingdata.core.SpiderInfo;
import com.data.scrapingdata.core.nlp.NLPExtractor;
import com.data.scrapingdata.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import us.codecraft.webmagic.Page;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author YL
 * @date 16:22 2021/11/25
 */
@Slf4j
public class JsonConsumer extends ConsumerBase implements PageConsumer {


    private static NLPExtractor nlpExtractor;

    @Override
    public void accept(Page page, SpiderInfo info) {
        nlpExtractor = SpringUtils.getBean(NLPExtractor.class);
        final boolean startPage = info.getStartURL().contains(page.getUrl().get());
        //去掉startUrl页面
        if (startPage) {
            page.setSkip(true);
        }
        if (StringUtils.isNotBlank(info.getUrlReg())) {
            if (page.getUrl().regex(info.getUrlReg()).match()) {
                List<String> all = page.getJson().jsonPath(info.getUrlJson()).all();
                if (CollectionUtils.isNotEmpty(all)) {
                    page.addTargetRequests(all);
                }
            } else {
                long start = System.currentTimeMillis();
                page.putField("url", page.getUrl().get());
                page.putField("domain", info.getDomain());
                page.putField("spiderInfoId", info.getId());
                page.putField("gatherTime", new Date());
                page.putField("spiderInfo", info);
                if (info.isSaveCapture()) {
                    page.putField("rawHTML", page.getJson().get());
                }
                //内容
                String content = null;
                if (StringUtils.isNotBlank(info.getContentJson())) {
                    content = page.getJson().jsonPath(info.getContentJson()).get();
                }
                page.putField("content", content);
                if (info.isNeedContent() && StringUtils.isBlank(content)) {
                    //if the content is blank ,skip it!
                    page.setSkip(true);
                    return;
                }
                //标题
                String title = null;
                if (StringUtils.isNotBlank(info.getTitleJson())) {
                    title = page.getJson().jsonPath(info.getTitleJson()).get();
                }
                page.putField("title", title);
                if (info.isNeedTitle() && StringUtils.isBlank(title)) {
                    //if the title is blank ,skip it!
                    page.setSkip(true);
                    return;
                }

                //抽取动态字段
                Map<String, Object> dynamicFields = new HashMap<>(10);
                for (SpiderInfo.FieldConfig conf : info.getDynamicFields()) {
                    String fieldName = conf.getName();
                    String fieldData = null;
                    if (!StringUtils.isBlank(conf.getJson())) {
                        //提取
                        fieldData = page.getJson().jsonPath(conf.getJson()).get();
                    }
                    dynamicFields.put(fieldName, fieldData);
                    if (conf.isNeed() && StringUtils.isBlank(fieldData)) {
                        //if the field data is blank ,skip it!
                        page.setSkip(true);
                        return;
                    }
                }
                page.putField(DYNAMIC_FIELD, dynamicFields);

                //抽取分类
                String category = null;
                if (!StringUtils.isBlank(info.getCategoryJson())) {
                    category = page.getJson().jsonPath(info.getCategoryJson()).get();
                }
                if (StringUtils.isNotBlank(category)) {
                    page.putField("category", category);
                } else {
                    page.putField("category", info.getDefaultCategory());
                }

                //抽取发布时间
                String publishTime = null;
                if (!StringUtils.isBlank(info.getPublishTimeJson())) {
                    //文章发布时间规则
                    publishTime = page.getJson().jsonPath(info.getPublishTimeJson()).get();
                }

                Date publishDate = null;
                SimpleDateFormat simpleDateFormat = null;
                //获取SimpleDateFormat时间匹配模板,首先检测爬虫模板指定的,如果为空则自动探测
                if (StringUtils.isNotBlank(info.getPublishTimeFormat())) {
                    //使用爬虫模板指定的时间匹配模板
                    if (StringUtils.isNotBlank(info.getLang())) {
                        simpleDateFormat = new SimpleDateFormat(info.getPublishTimeFormat(), new Locale(info.getLang(), info.getCountry()));
                    } else {
                        simpleDateFormat = new SimpleDateFormat(info.getPublishTimeFormat());
                    }
                } else if (StringUtils.isBlank(publishTime) && info.isAutoDetectPublishDate()) {
                    //如果没有使用爬虫模板抽取到文章发布时间,或者选择了自动抽时间,则进行自动发布时间探测
                    for (Pair<String, SimpleDateFormat> formatEntry : DATE_PATTERN) {
                        publishTime = page.getHtml().regex(formatEntry.getKey(), 0).get();
                        //如果探测到了时间就退出探测
                        if (StringUtils.isNotBlank(publishTime)) {
                            simpleDateFormat = formatEntry.getValue();
                            break;
                        }
                    }
                }
                //解析发布时间成date类型
                if (simpleDateFormat != null && StringUtils.isNotBlank(publishTime)) {
                    try {
                        publishDate = simpleDateFormat.parse(publishTime);
                        //如果时间没有包含年份,则默认使用当前年
                        if (!simpleDateFormat.toPattern().contains("yyyy")) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(publishDate);
                            calendar.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                            publishDate = calendar.getTime();
                        }
                        page.putField("publishTime", publishDate);
                    } catch (ParseException e) {
                        log.error("解析文章发布时间出错,source:" + publishTime + ",format:" + simpleDateFormat.toPattern());
                        if (info.isNeedPublishTime()) {
                            //if the publishTime is blank ,skip it!
                            page.setSkip(true);
                        }
                    }
                } else if (info.isNeedPublishTime()) {
                    //if the publishTime is blank ,skip it!
                    page.setSkip(true);
                    return;
                }

                ///////////////////////////////////////////////////////
                if (info.isDoNLP()) {
                    //判断本网站是否需要进行自然语言处理
                    //进行nlp处理之前先去除标签
                    content = content.replace("</p>", "***");
                    content = content.replace("<BR>", "***");
                    content = content.replaceAll("<([\\s\\S]*?)>", "");
                    content = content.replace("***", "<br/>");
                    content = content.replace("\n", "<br/>");
                    content = content.replaceAll("(\\<br/\\>\\s*){2,}", "<br/> ");
                    content = content.replaceAll("(&nbsp;\\s*)+", " ");
                    String contentWithoutHtml = content.replaceAll("<br/>", "");
                    try {
                        //抽取关键词,10个词
                        page.putField("keywords", nlpExtractor.extractKeywords(contentWithoutHtml));
                        //抽取摘要,5句话
                        page.putField("summary", nlpExtractor.extractSummary(contentWithoutHtml));
                        //抽取命名实体
                        page.putField("namedEntity", nlpExtractor.extractNamedEntity(contentWithoutHtml));
                    } catch (Exception e) {
                        log.error("对网页进行NLP处理失败,{}", e.getLocalizedMessage());
                    }
                }

                //本页面处理时长
                page.putField("processTime", System.currentTimeMillis() - start);
            }
        }
    }
}
