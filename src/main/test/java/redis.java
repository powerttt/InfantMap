import org.junit.Test;
import redis.clients.jedis.Jedis;

import static com.tong.dao.site.siteHttpCrawler.Crawler.CrawerBase.PATHURL;

public class redis {

    @Test
    public void hash(){
        Jedis jedis=new Jedis("localhost");


        for (int i = 0; i <100 ; i++) {

            System.out.println(PATHURL + jedis.lpop("siteUrl"));

        }

        jedis.close();
    }

    public void test(String t){
        System.out.println(t);
    }

    @Test
    public void demo1(){
        Jedis jedis=new Jedis("localhost");
        System.out.println( jedis.llen("siteUrl"));
    }
}
