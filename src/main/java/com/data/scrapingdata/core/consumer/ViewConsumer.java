package com.data.scrapingdata.core.consumer;

import com.data.scrapingdata.core.SpiderInfo;
import com.data.scrapingdata.core.nlp.NLPExtractor;
import com.data.scrapingdata.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.utils.UrlUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YL
 * @date 16:54 2021/11/5
 */
@Slf4j
public class ViewConsumer extends ConsumerBase implements PageConsumer {

    private static final String IFRAME = "iframe";
    private static NLPExtractor nlpExtractor;

    @Override
    public void accept(Page page, SpiderInfo info) {
        try {
            nlpExtractor = (NLPExtractor) SpringUtils.getBean("HANLPExtractor");
            long start = System.currentTimeMillis();
            //本页是否是startUrls里面的页面
            final boolean startPage = info.getStartURL().contains(page.getUrl().get());
            //判断本网站是否只抽取入口页,和当前页面是不是入口页
            //step1、链接发现
            if (!info.isGatherFirstPage() || (info.isGatherFirstPage() && startPage)) {
                List<String> links = new ArrayList<>();
                if (StringUtils.isNotBlank(info.getUrlReg())) {
                    //url正则式不为空
                    List<String> regex = page.getHtml().links().regex(info.getUrlReg()).all();
                    links = regex.stream().map(s -> {
                        int indexOfSharp = s.indexOf("#");
                        return s.substring(0, indexOfSharp == -1 ? s.length() : indexOfSharp);
                    }).collect(Collectors.toList());
                } else {//url正则式为空则抽取本域名下的所有连接,并使用黑名单对链接进行过滤
                    links = page.getHtml().links().regex("https?://" + info.getDomain().replace(".", "\\.") + "/.*").all().stream().map(s -> {
                                int indexOfSharp = s.indexOf("#");
                                return s.substring(0, indexOfSharp == -1 ? s.length() : indexOfSharp);
                            })
                            .filter(s -> {
                                for (String ignoredPostfix : ignoredUrls) {
                                    if (s.toLowerCase().endsWith(ignoredPostfix)) {
                                        return false;
                                    }
                                }
                                return true;
                            }).collect(Collectors.toList());
                }
                //如果页面包含iframe则也进行抽取
                for (Element iframe : page.getHtml().getDocument().getElementsByTag(IFRAME)) {
                    final String src = iframe.attr("src");
                    //iframe抽取规则遵循设定的url正则
                    if (StringUtils.isNotBlank(info.getUrlReg()) && src.matches(info.getUrlReg())) {
                        links.add(src);
                    }
                    //如无url正则,则遵循同源策略
                    else if (StringUtils.isBlank(info.getUrlReg()) && UrlUtils.getDomain(src).equals(info.getDomain())) {
                        links.add(src);
                    }
                }

                if (!links.isEmpty()) {
                    page.addTargetRequests(links);
                }
            }
            //去掉startUrl页面
            if (startPage) {
                page.setSkip(true);
            }
            page.putField("url", page.getUrl().get());
            page.putField("domain", info.getDomain());
            page.putField("spiderInfoId", info.getId());
            page.putField("gatherTime", new Date());
            page.putField("spiderInfo", info);
            if (info.isSaveCapture()) {
                page.putField("rawHTML", page.getHtml().get());
            }
            //转换静态字段
            if (info.getStaticFields() != null && info.getStaticFields().size() > 0) {
                Map<String, String> staticFieldList = new HashMap<>(10);
                for (SpiderInfo.StaticField staticField : info.getStaticFields()) {
                    staticFieldList.put(staticField.getName(), staticField.getValue());
                }
                page.putField("staticField", staticFieldList);
            }
            ///////////////////////////////////////////////////////
            String content;
            if (!StringUtils.isBlank(info.getContentXPath())) {
                //如果有正文的XPath的话优先使用XPath
                StringBuilder buffer = new StringBuilder();
                page.getHtml().xpath(info.getContentXPath()).all().forEach(buffer::append);
                content = buffer.toString();
            } else if (!StringUtils.isBlank(info.getContentReg())) {
                //没有正文XPath
                StringBuilder buffer = new StringBuilder();
                page.getHtml().regex(info.getContentReg()).all().forEach(buffer::append);
                content = buffer.toString();
            } else {
                //如果没有正文的相关规则则使用智能提取
                Document clone = page.getHtml().getDocument().clone();
                clone.getElementsByTag("p").append("***");
                clone.getElementsByTag("br").append("***");
                clone.getElementsByTag("script").remove();
                //移除不可见元素
                clone.getElementsByAttributeValueContaining("style", "display:none").remove();
                content = new Html(clone).smartContent().get();
            }
            content = content.replaceAll("<script([\\s\\S]*?)</script>", "");
            content = content.replaceAll("<style([\\s\\S]*?)</style>", "");

            page.putField("content", content);
            if (info.isNeedContent() && StringUtils.isBlank(content)) {
                //if the content is blank ,skip it!
                page.setSkip(true);
                return;
            }
            //抽取标题
            String title = null;
            if (!StringUtils.isBlank(info.getTitleXPath())) {
                //提取网页标题
                title = page.getHtml().xpath(info.getTitleXPath()).get();
            } else if (!StringUtils.isBlank(info.getTitleReg())) {
                title = page.getHtml().regex(info.getTitleReg()).get();
            } else {
                //如果不写默认是title
                title = page.getHtml().getDocument().title();
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
                if (fieldName.equals("imgs")) {
                    fieldData = page.getHtml().xpath(conf.getXpath()).all().toString();
                } else if (!StringUtils.isBlank(conf.getXpath())) {
                    //提取
                    fieldData = page.getHtml().xpath(conf.getXpath()).get();
                } else if (!StringUtils.isBlank(conf.getRegex())) {
                    fieldData = page.getHtml().regex(conf.getRegex()).get();
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
            if (!StringUtils.isBlank(info.getCategoryXPath())) {
                //提取网页分类
                category = page.getHtml().xpath(info.getCategoryXPath()).get();
            } else if (!StringUtils.isBlank(info.getCategoryReg())) {
                category = page.getHtml().regex(info.getCategoryReg()).get();
            }
            if (StringUtils.isNotBlank(category)) {
                page.putField("category", category);
            } else {
                page.putField("category", info.getDefaultCategory());
            }

            //抽取发布时间
            String publishTime = null;
            if (!StringUtils.isBlank(info.getPublishTimeXPath())) {
                //文章发布时间规则
                publishTime = page.getHtml().xpath(info.getPublishTimeXPath()).get();
            } else if (!StringUtils.isBlank(info.getPublishTimeReg())) {
                publishTime = page.getHtml().regex(info.getPublishTimeReg()).get();
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
                        return;
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
        } catch (Exception e) {
            log.error("处理网页出错", e);
        }
    }
}
