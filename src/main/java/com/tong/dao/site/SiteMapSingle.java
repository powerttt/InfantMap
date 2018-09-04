package com.tong.dao.site;

import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;
import com.tong.dao.site.dataPersistent.SaveSiteUtil;
import redis.clients.jedis.Jedis;
import com.tong.dao.site.siteHttpCrawler.InitData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * <功能：
 * >
 *
 * @since: 1.0.0
 * @Author: tong
 * @Date:
 */
@Component
public class SiteMapSingle {
    /**
     * 存放每个 站点的 名字name 和 公交num 当出现重复的名字时，打开value-->HashSet 存放公交num
     */
    private static Map<String, HashSet<Integer>> siteNameMap = new HashMap<String, HashSet<Integer>>();

    public static Map<String, HashSet<Integer>> getSiteNameMap() {
//        SaveSiteUtil save = new SaveSiteUtil();
//        Jedis jedis = new Jedis("localHost");
//        if (siteNameMap.isEmpty()) {
//            //siteNameMap为空的情况,重新调用初始化化数据方法
//                throw new NullPointerException("siteNameMap运行过程中为空");
//        }
        return siteNameMap;
    }

    public static void setSiteNameMap(Map<String, HashSet<Integer>> siteNameMap) {
        SiteMapSingle.siteNameMap = siteNameMap;
    }

    private SiteMapSingle() {
    }

    public static void getSiteNameMapDataInfo(){
        System.out.println("SiteNameMap:\n"+ JSON.toJSON(siteNameMap) +"\n");

    }
}
