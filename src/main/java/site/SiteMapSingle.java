package site;

import site.dataPersistent.SaveSiteUtil;
import redis.clients.jedis.Jedis;
import site.siteHttpCrawler.SiteThread;

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
public class SiteMapSingle {
    /**
     * 存放每个 站点的 名字name 和 公交num 当出现重复的名字时，打开value-->HashSet 存放公交num
     */
    private static Map<String, HashSet<Integer>> siteNameMap = new HashMap<String, HashSet<Integer>>();

    public static Map<String, HashSet<Integer>> getSiteNameMap() {
        SaveSiteUtil save = new SaveSiteUtil();
        Jedis jedis = new Jedis("localHost");
        if (siteNameMap.isEmpty()) {
            System.out.println("siteNameMap单利模式中为空");
            if (jedis.get("siteNameMap".getBytes()) == null) {
//                如果redis中没有 siteNameMap 的序列化数组，则爬取
                System.out.println("siteNameMap单利模式中发现redis中 没 有保存,开始爬取");
                SiteThread.crawlerAction();
            } else {
//                有持久化，读取，注入到集合中
                System.out.println("siteNameMap单利模式中发现redis中有保存,正在恢复");
                byte[] objBytes = jedis.get("siteNameMap".getBytes());
                siteNameMap = (HashMap<String, HashSet<Integer>>) save.readInRedis(objBytes);
                SiteThread.crawlerAction();
                jedis.close();
            }
        }
        return siteNameMap;
    }

    public static void setSiteNameMap(Map<String, HashSet<Integer>> siteNameMap) {
        SiteMapSingle.siteNameMap = siteNameMap;
    }

    private SiteMapSingle() {
    }
}
