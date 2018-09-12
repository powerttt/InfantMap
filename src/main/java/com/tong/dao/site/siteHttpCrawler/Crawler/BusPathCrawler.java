package com.tong.dao.site.siteHttpCrawler.Crawler;

import com.sun.deploy.util.SyncAccess;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;

/**
 * <功能：
 * 路线爬取,对公交路线 的信息 保存在redis 中,
 * 并将每个站点的url保存在 redis hash中供siteNameMap爬取
 * <p>
 * >
 *
 * @since: 1.2.0
 * @Author: tong
 * @Date: 2018年09月02日14:58:56
 */
public class BusPathCrawler extends CrawerBase {

      Logger log = Logger.getLogger(BusPathCrawler.class);
     Jedis jedis = new Jedis("localhost");


    /**
     * 网页响应的头文件信息
     */



    //   lock锁，保证namesMap储存的安全性

    /**
     * 获取到网页的document，分析的过程中调用savaInfo（）
     *
     * @param homeUrl 访问的url地址
     */
    public  void crawlerHtml(String homeUrl) {
        log.debug("crawlerHtml中地址>>>>>"+homeUrl);
        Document document = getUrl(homeUrl);
        if (document==null){
            return;
        }
        Elements element = document.select("div[id=con_site_1]").select("a");
        int i=1;
            /*
                公交路线 读取
             */
        for (Element ele : element) {
            String title = ele.select("a").text();
            String url = ele.select("a").attr("href");
//                调用保存信息函数，
            saveInfo(title, PATHURL + url);
        }

    }

    Lock lock=new ReentrantLock();

    /**
     * 保存信息，保存在map的是每个站点经过的busNum；
     * 同时也保存在Redis中的ZSet中，busNum为key，路线信息根据权重值储存
     *
     * @param title   busNum为key
     * @param markUrl 每个站点的地址
     * @throws IOException
     */
    public  void saveInfo(String title, String markUrl) {

//      创建busNum,使用正则转换为数字格式，存放到Redis中
        String busNum = null;
//      将busBum强转
        int busNumber = 0;
        matcher = PATTERN_BUSNUM.matcher(title);
        if (matcher.find()) {
            busNum = matcher.group();
        }

        Document doc = getUrl(markUrl);
//       解析doc文档
        Elements element = doc.select("div[class=bus_site_layer]");

        String[] score = element.select("div").select("i").text().split(" ");
        String[] names = element.select("div").select("a").text().split(" ");
        List<String> siteUrl = element.select("div").select("a").eachAttr("href");
       lock.lock();
        /*
         *           使用 busNumber储存到Redis当中
         */
        for (int i = 0; i < score.length; i++) {
            busNumber = Integer.valueOf(busNum);

            if (i != 0 && score[i].equals("1")) {
//                返程
                for (; i < score.length; i++) {
//                  返程
                    zadd(busNumber + "back", Double.valueOf(score[i]), names[i]);
                    //               添加到redis hash中,等待爬
                    rpush("siteUrl", siteUrl.get(i));
                }
                break;
            }
//                    正路
            zadd(busNumber + "next", Double.valueOf(score[i]), names[i]);
            //               添加到redis hash中,等待爬
           rpush("siteUrl", siteUrl.get(i));
        }

        lock.unlock();


    }


    /**
     * 不能将此方法放在循环中，需要拿出来
     *
     * @param key    String key
     * @param i      double score,
     * @param member Srting member
     */
    public  void zadd(String key, double i, String member) {
        jedis.zadd(key, i, member);

    }

    public  void rpush(String key, String member) {
        jedis.rpush("siteUrl", member);

    }


}
