package com.tong.dao.site.impl;

import com.alibaba.fastjson.JSON;
import com.tong.dao.site.BusPathDao;
import com.tong.dao.site.SiteMapSingle;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import com.tong.dao.site.dataPersistent.SaveSiteUtil;

import java.util.*;

/**
 *<功能：
 *     路径搜索
 * >
 *
 *@since: 1.2.0
 *@Author: tong
 *@Date: 2018年09月12日15:07:33
 *
 */
@Component
public class BusPathDaoImpl implements BusPathDao {


    Jedis jedis=new Jedis("localhost");
    /*
            储存 最终路线的
     */
    private LinkedList<Integer> resultBusNumList=new LinkedList<>();
    /*
     *      获取 存有每个站点信息的集合
     */
    private Map<String,HashSet<Integer>> siteNameMap;


    private String origin;
    private String end;

    /*
           先拿出起始点和结束点的 经过的公交信息 。
     */
    private Set<Integer> originSet=new HashSet<>();
    private Set<Integer> endSet=new HashSet<>();

    Logger log=Logger.getLogger(BusPathDaoImpl.class);
    /**
     *  路径搜索 ，在开始搜索之前判断是否为空，为空这进行爬取,非空则进行检查是否包含起始和终点
     * @param origin 起点
     * @param end 终点
     */
    @Override
    public LinkedList getBusPathInfo(String origin, String end) {
        siteNameMap= SiteMapSingle.getSiteNameMap();
        log.info("起始点信息:"+origin+"siteNameMap中为:"+ JSON.toJSON(siteNameMap.get(origin)));
        log.info("终点信息:"+end+"siteNameMap中为:"+ JSON.toJSON(siteNameMap.get(end)));
        this.origin=origin;
        this.end=end;


            /*
                判断是否包含 输入 的站点
             */
        if (!siteNameMap.containsKey(origin) && siteNameMap.containsKey(end) ){
//                不包含站点信息，先保存数据
            SaveSiteUtil.saveInRedis();

            try {
                throw new NullPointerException("地址输入有误，不包含站点信息");
            } catch (NullPointerException e) {
                e.printStackTrace();
            }finally {
                return null;
            }

        }else {
//                包含起始 终点，赋值
            originSet=siteNameMap.get(origin);
            endSet=siteNameMap.get(end);
            return pathSearch();
        }
    }

    /**
     * 视为其中有一次转车,把起始站所有的车次拿出,传入需要 遍历的 集合transferSet
     *  转一次车:
     *        遍历集合,得到1,2,3,4,5,6,7路公交,拿出 1的下一站名,获取下一站名的车次 8,9,10,11->与终点取交集
     *                                      拿出 2的下一站名,获取下一站名的车次 12,13,14,15->与终点取交集
     *  若都不符合,则为转两次车:
     *
     * @return LinkedList 包含路线的结果集,只包含busNum
     */
    private LinkedList<Integer> pathSearch() {
        System.out.println("开始匹配最佳路线站点");

        Map<Integer, Set<Integer>> transferMap = new HashMap<>();
        Set<Integer> transfer1Set=null;
        Set<Integer> transfer2Set=null;
        StringBuffer siteName;
//        下一站的车次集合(中介)
        Set<Integer> nextSiteSet;
        Set<Integer> nonstopSet=new HashSet<>();
        //取出其中的交集
         nonstopSet.addAll(CollectionUtils.intersection(originSet, endSet));
        if (nonstopSet.isEmpty()) {
        /*
             如果为空，那么则非直达，需要 遍历此站点车次
         */
            for (int n : originSet) {
//            下一个车次的下一站
                 siteName = new StringBuffer(getSiteName(n + "next", getNextScore(n + "next", origin)));
//             通过车次下一站名字，拿到其 站点车次集合
                  nextSiteSet = siteNameMap.get(siteName);
//            取其中交集
                transfer1Set = (HashSet<Integer>) CollectionUtils.intersection(nextSiteSet, endSet);
                if (!transfer1Set.isEmpty()) {
//                若不为空，则代表以此为转车点,返回最终线路
                    resultBusNumList.addAll((LinkedList<Integer>) CollectionUtils.intersection(originSet, transfer1Set));
                    resultBusNumList.addAll((LinkedList<Integer>) CollectionUtils.intersection(transfer1Set, endSet));
                    return resultBusNumList;
                }
//                若取不出交集，继续 第二次转车的循环

            }
 /*
                        第二次转车的循环

*/

            for (int n : originSet) {
                //            下一个车次的下一站
                siteName = new StringBuffer(getSiteName(n + "next", getNextScore(n + "next", origin)));
//             通过车次下一站名字，拿到其 站点车次集合
                nextSiteSet = siteNameMap.get(siteName);
                for (int t : nextSiteSet) {
//                      下一个车次的下一站
                        siteName = new StringBuffer(getSiteName(t + "next", getNextScore(t + "next", origin)));
//                      通过车次下一站名字，拿到其 站点车次集合
                       Set<Integer> nextSite2Set = siteNameMap.get(siteName);
//                      取其中交集
                        transfer2Set = (HashSet<Integer>) CollectionUtils.intersection(nextSite2Set, endSet);
                        if (!transfer2Set.isEmpty()) {
//                       若不为空，则代表以此为转车点,返回最终线路
                            resultBusNumList.addAll((LinkedList<Integer>) CollectionUtils.intersection(originSet, transfer1Set));
                            resultBusNumList.addAll((LinkedList<Integer>) CollectionUtils.intersection(transfer1Set, transfer2Set));
                            resultBusNumList.addAll((LinkedList<Integer>) CollectionUtils.intersection(transfer2Set, endSet));
                            return resultBusNumList;
                        } else {
                            System.out.println("垃圾程序员");
                        }
                    }
                    /*
                            第二次转车循环结束-------------------------------
                     */
            }


        } else {
            //直达
            resultBusNumList.addAll(nonstopSet);
            System.out.println("车子是直达");
        }
        return resultBusNumList;
    }


    /**
     *  获取此公交当前站点的下一站的 权重值（单位为1）
     * @param BusNum BusNumber eg. 6next 。 6路公交
     * @param siteName siteName
     * @return double score+1 权重值
     */
    private double getNextScore(String BusNum,String siteName){
       return jedis.zscore(BusNum,siteName)+1;
    }

    /**
     *  获取此公交的下一站的 名字
     * @param BusNum BusNumber eg. 6next 。 6路公交
     * @param score   权重值
     * @return String next siteName
     */
    private String getSiteName(String BusNum,double score){
       Set s=jedis.zrangeByScore(BusNum,score,score);
        Iterator it=s.iterator();
       return (String)it.next();
    }



    /**
     * 备份siteNameMap到redis
     *
     * @return true 保存成功
     */
    @Override
    public boolean saveSiteNameMapRedis() {
        return SaveSiteUtil.saveInRedis();
    }

    /**
     * 备份siteNameMap到本地资源
     *
     * @return true 保存成功
     */
    @Override
    public boolean saveSiteNameMapLocal() {
        return false;
    }


}
