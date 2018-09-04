package com.tong.dao.site.siteHttpCrawler;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;
import com.tong.dao.site.SiteMapSingle;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <功能：
 * 网络爬虫类 ，对网站的公交信息进行保存
 * 每个站点site的信息为siteNameMap的key，则value是一个set，保存此站点经过的公交编号
 * >
 *
 * @since: 1.0.0
 * @Author: tong
 * @Date: 2018年09月02日14:58:56
 */
public class SiteCrawler {
    /**
     * 网页响应的头文件信息
     */
    public static Map<String, String> headerMap = new HashMap<String, String>();

    static {
        headerMap.put("Host", "www.w3school.com.cn");
        headerMap.put("User-Agent", " Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36");
        headerMap.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headerMap.put("Accept-Language", "zh-cn,zh;q=0.5");
        headerMap.put("Accept-Encoding", "gzip, deflate");
        headerMap.put("Cache-Control", "max-age=0");
        headerMap.put("Connection", "keep-alive");
    }

    public static final Pattern PATTERN_BUSNUM = Pattern.compile("[0-9]{0,4}");
    Matcher matcher = null;
    Jedis jedis = new Jedis("localhost");
    //   lock锁，保证siteNameMap储存的安全性
    Lock lock = new ReentrantLock();

    /**
     * 获取到网页的document，分析的过程中调用savaInfo（）
     *
     * @param homeUrl 访问的url地址
     */
    public void getUrl(String homeUrl) {

        SiteCrawler SiteCrawler = new SiteCrawler();
        Connection connect = Jsoup.connect(homeUrl); //获取请求连接
        //      添加头信息
        Connection connHeader = connect.headers(headerMap);

        try {
//      使用get()请求页面内容
            Document document = connHeader.get();
            Elements element = document.select("div[id=con_site_1]").select("a");
            /*
                正反路线的保存
             */
            for (Element ele : element) {
                String title = ele.select("a").text();
                String url = ele.select("a").attr("href");
//                调用保存信息函数，
                SiteCrawler.saveInfo(title, SiteThread.URL + url);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    /**
     * 保存信息，保存在map的是每个站点经过的busNum；
     * 同时也保存在Redis中的ZSet中，busNum为key，路线信息根据权重值储存
     *
     * @param title   busNum为key
     * @param markUrl 每个站点的地址
     * @throws IOException
     */
    public void saveInfo(String title, String markUrl) throws IOException {
        /*
             开始上锁
 */
        lock.lock();
//      创建busNum,使用正则转换为数字格式，存放到Redis中
        String busNum = null;
//      将busBum强转
        int busNumber = 0;
        matcher = PATTERN_BUSNUM.matcher(title);
        if (matcher.find()) {
            busNum = matcher.group();
        }
//        新建连接地址
        Connection connection = Jsoup.connect(markUrl);
//          添加头文件信息
        Connection connHeader = connection.headers(headerMap);
//      使用get()请求页面内容，注入document
        Document doc = connHeader.get();

//       解析doc文档
        Elements elements = doc.select("div[id=bus_line]").select("div[class=bus_site_layer]");
        for (Element element : elements) {
            String[] names = element.select("a").text().split(" ");

            for (int i = 0; i < names.length; i++) {
                /*
                 *         使用 siteName储存到map当中,判断是否重复
                 */
                busNumber = Integer.valueOf(busNum);
                if (SiteMapSingle.getSiteNameMap().containsKey(names[i])) {
//                    map中包含，添加busNumber信息
                    SiteMapSingle.getSiteNameMap().get(names[i]).add(busNumber);
                } else {
//                    集合中没有储存过，添加新的公交站点到 busNumSet
                    HashSet<Integer> siteNameSet = new HashSet<Integer>();
                    siteNameSet.add(busNumber);
                    SiteMapSingle.getSiteNameMap().put(names[i], siteNameSet);}

                /*
                 *           使用 busNumber储存到Redis当中,判断是否重复
                 */
                if (jedis.zrange(busNum+ "next", 0, -1).isEmpty()) {
//                zset为空，没有数据，添加 后缀 next
                    for (int j = 0; j < names.length; j++) {
                        zadd(busNum + "next", j, names[j]);
                    }
                } else {
//                zset有数据，返程,添加后缀 back
                    for (int j = 0; j < names.length; j++) {
                        zadd(busNum + "back", j, names[j]);
                    }
                }
           /*
              解锁
           */
                lock.unlock();
            }

        }
    }

    /**
     * 不能将此方法放在循环中，需要拿出来
     *
     * @param key    String key
     * @param i      double score,
     * @param member Srting member
     */
    public void zadd(String key, double i, String member) {
        jedis.zadd(key, i, member);

    }

}
