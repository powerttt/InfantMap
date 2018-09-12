package com.tong.dao.site.siteHttpCrawler;

import com.tong.dao.site.siteHttpCrawler.Crawler.CrawerBase;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import com.tong.dao.site.SiteMapSingle;
import com.tong.dao.site.dataPersistent.SaveSiteUtil;

import javax.annotation.PostConstruct;

@Component
public class InitData {

    Logger log=Logger.getLogger(InitData.class);

    /**
     * 初始化数据
     */
    @PostConstruct
    public  void InitSiteNameMap() throws InterruptedException {
        Jedis jedis=new Jedis("localhost");

        if (jedis.get("siteNameMap".getBytes()) == null) {
//                如果redis中没有 siteNameMap 的序列化数组，则爬取
           log.info("\n\n\t\tredis没有备份,马上爬取...\n\n");
            CrawerBase.crawlerAction("path");
            Thread.sleep(1000);
            if (jedis.dbSize()<100){
                Thread.sleep(1000);
            }
            CrawerBase.crawlerAction("site");


        } else {
//                有持久化，读取，注入到集合中
            log.info("\n\n\t\tredis备份,正在恢复...\n\n");
            byte[] objBytes = jedis.get("siteNameMap".getBytes());
            SiteMapSingle.setSiteNameMap(SaveSiteUtil.readInRedis(objBytes));
        }
        jedis.close();
    }
}
