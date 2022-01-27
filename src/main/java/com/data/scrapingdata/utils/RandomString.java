package com.data.scrapingdata.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;

/**
 * Description: 随机字符串相关工具类
 * Date: 2019/7/1 16:56
 * Created by luoyingxiong
 */
public class RandomString {
    private static final Logger logger = LoggerFactory.getLogger(RandomString.class);

    /**
     * @param length
     * @return
     * @Description 根据指定长度生成字母和数字的随机数
     * 0~9的ASCII为48~57
     * A~Z的ASCII为65~90
     * a~z的ASCII为97~122
     */
    public static String genUUID(int length) {
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        SecureRandom randomData = new SecureRandom();
        int data;
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(3);
            //目的是随机选择生成数字，大小写字母
            switch (index) {
                case 0:
                    data = randomData.nextInt(10);//仅仅会生成0~9
                    sb.append(data);
                    break;
                case 1:
                    data = randomData.nextInt(26) + 65;//保证只会产生65~90之间的整数
                    sb.append((char) data);
                    break;
                case 2:
                    data = randomData.nextInt(26) + 97;//保证只会产生97~122之间的整数
                    sb.append((char) data);
                    break;
                default:
                    break;
            }
        }
        return sb.toString();
    }

    public static void saveKeys(String path, StringBuffer sb) throws IOException {
        File file = new File(path);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(sb);
            pw.flush();
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            logger.error("[saveKeys]===>", e);
        }
    }
}
