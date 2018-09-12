package com.tong.dao.site.siteHttpCrawler.Crawler;


import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <功能：
 * 爬去网页基类,抽出共有的网页头请求方法,返回 Document        >
 *
 * @since: 1.0.0
 * @Author: tong
 * @Date:
 */
public class CrawerBase {
    public static final Pattern PATTERN_BUSNUM = Pattern.compile("[0-9]{0,4}");
    public Matcher matcher = null;

    static Logger log = Logger.getLogger(CrawerBase.class);

    public static Map<String, String> headerMap = new HashMap<>();

    public static final String PATHURL = "http://hangzhou.8684.cn";
    public static Queue<String> queue = new LinkedList<String>();

    static {
        headerMap.put("Host", "www.w3school.com.cn");
        headerMap.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
        headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headerMap.put("Accept-Language", "zh-cn,zh;q=0.5");
        headerMap.put("Accept-Encoding", "gzip, deflate");
        headerMap.put("Cache-Control", "max-age=0");
        headerMap.put("Connection", "keep-alive");
    }

    protected static Document getUrl(String homeUrl) {
        BusPathCrawler SiteCrawler = new BusPathCrawler();
        //获取请求连接
        Connection connect = Jsoup.connect(homeUrl);
        //      添加头信息
        Connection connHeader = connect.headers(headerMap);
        Document document = null;
//      使用get()请求页面内容
        try {
            document = connHeader.get();
            return document;
        } catch (IOException e) {
            e.printStackTrace();
            log.debug("html读取失败");
            return null;
        }
    }

    static {
        //        添加元素
        queue.offer("/list1");
        queue.offer("/list2");
        queue.offer("/list3");
        queue.offer("/list4");
        queue.offer("/list5");
        queue.offer("/list6");
        queue.offer("/list7");
        queue.offer("/list8");
        queue.offer("/list9");

    }

    /**
     * 开始爬取公交信息
     */
    public static void crawlerAction(String choose) {
        Jedis jedisR=new Jedis("localhost");
        Jedis jedisR2=new Jedis("localhost");
        Jedis jedisL=new Jedis("localhost");
        Jedis jedisL2=new Jedis("localhost");
         BusPathCrawler pathCrawler = new BusPathCrawler();
         SitePathCrawler siteCrawler = new SitePathCrawler();

        Runnable runnableL=()->{
            siteCrawler.sitePathCrawler(PATHURL + jedisL.lpop("siteUrl"));
        };
        Runnable runnableL2=()->{
            siteCrawler.sitePathCrawler(PATHURL + jedisL2.lpop("siteUrl"));
        };
        Runnable runnableR=()->{
            siteCrawler.sitePathCrawler(PATHURL + jedisR.lpop("siteUrl"));
        };
        Runnable runnableR2=()->{
            siteCrawler.sitePathCrawler(PATHURL + jedisR2.lpop("siteUrl"));
        };
        Runnable queueUrl=()->{
            pathCrawler.crawlerHtml(PATHURL +   queue.poll());
        };


        if ("path".equalsIgnoreCase(choose)){
            for (int i = 0; i <3 ; i++) {
                ThreadManager.getInstance().execute(queueUrl);
            }
        }else {
            try {
                while (true){
                    if (jedisR.llen("siteUrl")==0L){
                        log.debug("\n\n\t\t========================\n\n\t\t剩余长度为0\n\n\t\t========================\n");
                        return;
                    }
                    ThreadManager.getInstance().execute(runnableL);
                    ThreadManager.getInstance().execute(runnableL2);
                    ThreadManager.getInstance().execute(runnableR);
                    ThreadManager.getInstance().execute(runnableR2);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


//


    }




    static Jedis jedisR = new Jedis("localhost");

    public static String rpop() {
        log.debug("rpop执行");
         return jedisR.rpop("siteUrl");

    }

    static Jedis jedisL = new Jedis("localhost");

    public static String lpop() {
        log.debug("lpop执行");
        return jedisL.lpop("siteUrl");


    }


}
