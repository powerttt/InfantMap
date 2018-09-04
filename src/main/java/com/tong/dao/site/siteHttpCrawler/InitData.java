package com.tong.dao.site.siteHttpCrawler;

import com.tong.dao.site.dataPersistent.RecoverJson;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import com.tong.dao.site.SiteMapSingle;
import com.tong.dao.site.dataPersistent.SaveSiteUtil;

import javax.annotation.PostConstruct;
import java.io.File;

@Component
public class InitData {


    /**
     * 初始化数据
     */
    @PostConstruct
    public static void InitSiteNameMap(){
        //判断本地json备份是否存在
        if (new File("/home/tongning/siteNameMap-Json").exists()){
            RecoverJson.readRecover();
        }else {
        Jedis jedis=new Jedis("localhost");
        if (jedis.get("siteNameMap".getBytes()) == null) {
//                如果redis中没有 siteNameMap 的序列化数组，则爬取
            System.out.println("哎呦,redis没有备份,马上爬取");
            SiteThread.crawlerAction();
        } else {
//                有持久化，读取，注入到集合中
            System.out.println("哈哈,我就知道redis备份了,正在恢复...");
            byte[] objBytes = jedis.get("siteNameMap".getBytes());
            SiteMapSingle.setSiteNameMap(SaveSiteUtil.readInRedis(objBytes));
            jedis.close();
        }
        }

    }
}
