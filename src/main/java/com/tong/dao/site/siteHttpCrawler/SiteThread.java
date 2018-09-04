package com.tong.dao.site.siteHttpCrawler;


import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.Queue;

/**
 * <功能：
 * 每个公交的站点信息，包含 siteName    siteName的 BusNumber
 * >
 *
 * @since: 1.3.0
 * @Author: tong
 * @return: HashMap<String:siteName   ,   ArrayList   <   Integer>>
 * 每个站点
 * @Date: 2018年08月30日16:39:48
 */
public class SiteThread {
    public static final String URL = "http://hangzhou.8684.cn";
    public static Queue<String> queue = new LinkedList<String>();

    static {
        //        添加元素
        queue.offer(URL + "/list1");
        queue.offer(URL + "/list2");
        queue.offer(URL + "/list3");
        queue.offer(URL + "/list4");
        queue.offer(URL + "/list5");
        queue.offer(URL + "/list6");
        queue.offer(URL + "/list7");
        queue.offer(URL + "/list8");
        queue.offer(URL + "/list9");

    }

    /**
     * 开始爬取公交信息
     */
    public static void crawlerAction() {
        SiteThread site = new SiteThread();
        while (true) {
            if (queue.isEmpty()) {
                break;
            }
            //           启用线程
            new Thread(
                    site.getRunnuble(queue.poll())
            ).start();

            new Thread(
                    site.getRunnuble(queue.poll())
            ).start();

            new Thread(
                    site.getRunnuble(queue.poll())
            ).start();

        }
    }

    SiteCrawler site = new SiteCrawler();

    public Runnable getRunnuble(String url) {
        return () -> {
            site = new SiteCrawler();
            if (url != null) {
                System.out.println("正在开始:" + url);
                site.getUrl(url);
            }
        };
    }


}
