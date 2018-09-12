package com.tong.dao.site.dataPersistent;

import redis.clients.jedis.Jedis;

/**
 *<功能：
 *  清除 Map
 *      redis 数据         >
 *
 *@since: 1.0.0
 *@Author: tong
 *@Date:
 *
 */
public class ClearData {

    public void clearSiteNameMapAndRedis(){
    }

    /**
     * 清除redis中的siteNameMap备份
     */
    public void clearSiteNameBack(){
        Jedis jedis=new Jedis("localhost");
        jedis.expire("siteNameMap",0);
        jedis.close();
    }




}
