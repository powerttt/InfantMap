import com.tong.dao.site.siteHttpCrawler.Crawler.BusPathCrawler;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class redisCrawler {




    @Test
    public void crawler() throws IOException {
        Connection connect = Jsoup.connect("http://hangzhou.8684.cn/x_db663b1a"); //获取请求连接
        //      添加头信息
        Connection connHeader = connect.headers(BusPathCrawler.headerMap);

        try {
//      使用get()请求页面内容
            Document document = connHeader.get();
            Elements element = document.select("div[class=bus_site_layer]");
//            System.out.println(element.select("div").select("i").text());
//            System.out.println(element.select("div").select("a").text());

            List<String> siteUrl=element.select("div").select("a").eachAttr("href");
            String[] score=element.select("div").select("i").text().split(" ");
            String[] siteName=element.select("div").select("a").text().split(" ");

    for (int i = 0; i <score.length ; i++) {
//            返程
          if (i!=0 && score[i].equals("1")){
              for (; i < score.length ; i++) {
                  System.out.println("48back\t"+score[i]+siteName[i]);
                  System.out.println("url: "+siteUrl.get(i));
              }
              break;
          }
          System.out.println("48next\t"+score[i]+siteName[i]);
        System.out.println("url: "+siteUrl.get(i));

      }



//            Jedis jedis=new Jedis("localhost");
//
//            BusPathCrawler siteCrawler=new BusPathCrawler();
//
//
//
//            for (int i = 0; i <score.length ; i++) {
////                返程
//                if (i!=0 && score[i].equals("1")){
//                    for (; i < score.length ; i++) {
//                        System.out.println("48back\t"+score[i]+siteName[i]);
//                        siteCrawler.zadd("48back",Double.valueOf(score[i]),siteName[i]);
//                    }
//                    break;
//                }
//                System.out.println("48next\t"+score[i]+siteName[i]);
//                siteCrawler.zadd("48next",Double.valueOf(score[i]),siteName[i]);
//            }
//
//            jedis.close();








        } catch (IOException e) {
            e.printStackTrace();
        }

    }



//        for (int i = 0; i < names.length; i++){
//
//
//            /*
//             *           使用 busNumber储存到Redis当中,判断是否重复
//             */
//            if (jedis.zrange(busNum + "next", 0, -1).isEmpty()) {
////                zset为空，没有数据，添加 后缀 next
//                for (int j = 0; j < names.length; j++) {
//                    zadd(busNum + "next", j, names[j]);
//                }
//            } else {
////                zset有数据，返程,添加后缀 back
//                for (int j = 0; j < names.length; j++) {
//                    zadd(busNum + "back", j, names[j]);
//                }
//            }
//        }

    }




