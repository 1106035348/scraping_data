package com.data.scrapingdata.core.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * @author YL
 * @date 14:30 2022/1/17
 */
@Slf4j
public class ConsumerBase {
    public static List<String> ignoredUrls;
    public static final LinkedList<Pair<String, SimpleDateFormat>> DATE_PATTERN = new LinkedList<>();
    public static final String DYNAMIC_FIELD = "dynamic_fields";


    static {
        try {
            ignoredUrls = FileUtils.readLines(new File(ViewConsumer.class.getClassLoader().getResource("ignoredUrls.txt").getFile()), StandardCharsets.UTF_8);
            log.info("加载普通网页爬虫url忽略名单成功,忽略名单:{}", ignoredUrls);
            try {
                String[] datePatternFile = FileUtils.readFileToString(
                        new File(ViewConsumer.class.getClassLoader().getResource("datePattern.txt").getFile()), "utf8").replace("\r", "").split("=====\r?\n");
                String[] dateList = datePatternFile[0].split("\n");
                String[] timeList = datePatternFile[1].split("\n");
                for (String date : dateList) {
                    String[] dateEntry = date.split("##");
                    String dateReg = dateEntry[0];
                    String dateFormat = dateEntry[1];
                    log.debug("正在编译日期正则{},format:{}", dateReg, dateFormat);
                    DATE_PATTERN.add(Pair.of(dateReg, new SimpleDateFormat(dateFormat)));
                    for (String time : timeList) {
                        String[] timeEntry = time.split("##");
                        String timeReg = timeEntry[0];
                        String timeFormat = timeEntry[1];
                        //日期与时间中间有空格
                        log.debug("正在编译日期正则{},format:{}", dateReg + " " + timeReg, dateFormat + " " + timeFormat);
                        DATE_PATTERN.add(Pair.of(dateReg + " " + timeReg, new SimpleDateFormat(dateFormat + " " + timeFormat)));
                        //日期与时间中间无空格
                        log.debug("正在编译日期正则{},format:{}", dateReg + timeReg, dateFormat + timeFormat);
                        DATE_PATTERN.add(Pair.of(dateReg + timeReg, new SimpleDateFormat(dateFormat + timeFormat)));
                    }
                }
                DATE_PATTERN.sort((o1, o2) -> o2.getLeft().length() - o1.getLeft().length());
                log.info("日期匹配式加载完成");
            } catch (IOException e) {
                log.error("加载日期匹配式失败，{}", e.getLocalizedMessage());
            }
        } catch (IOException e) {
            log.error("加载普通网页爬虫url忽略名单失败", e);
        }
    }
}
