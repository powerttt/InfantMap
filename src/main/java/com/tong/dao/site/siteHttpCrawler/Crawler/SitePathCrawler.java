package com.tong.dao.site.siteHttpCrawler.Crawler;

import com.alibaba.fastjson.JSON;
import com.tong.dao.site.SiteMapSingle;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import redis.clients.jedis.Jedis;

import java.io.Serializable;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *<功能：
 *      从redis hash 中拿取 每个站点的url信息,开启线程爬取,保存在siteNameMap中
 *   >
 *
 *@since: 1.0.0
 *@Author: tong
 *@Date:
 *
 */
public class SitePathCrawler extends CrawerBase implements Serializable {

    Logger log = Logger.getLogger(SitePathCrawler.class);

    Lock lock = new ReentrantLock();
    public  void sitePathCrawler(String url){
        lock.lock();
        Document document = getUrl(url);
        if (document==null){
            return;
        }
        String siteName=document.select("span").select("h1").text();
        String[] busPath=document.select("div[class=bus_i_link]").select("a").text().split(" ");
        for (int i = 0; i < busPath.length; i++) {
            HashSet<Integer> namesSet = new HashSet<Integer>();
            this.matcher=PATTERN_BUSNUM.matcher(busPath[i]);
            try {
                if (matcher.find()){
                    namesSet.add(Integer.valueOf(matcher.group()));
                    SiteMapSingle.getSiteNameMap().put(siteName, namesSet);
                }
            } catch (NumberFormatException e) {
                log.debug("java.lang.NumberFormatException: For input string: \"\""+siteName+"站点,"+busPath[i]+"存储失败,url地址为:"+url);
            }
        }
        lock.unlock();
    }
}
