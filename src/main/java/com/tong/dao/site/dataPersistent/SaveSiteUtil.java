package com.tong.dao.site.dataPersistent;

import com.tong.dao.site.SiteMapSingle;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


/**
 *<功能：
 *  本地持久化保存Map集合         >
 *
 *@since: 1.2.0
 *@Author: tong
 *@Date: 2018年09月12日15:06:47
 *
 */
public class SaveSiteUtil implements Serializable {
    public static ObjectOutputStream oos=null;
    public static ByteArrayOutputStream baos=null;
    public static ObjectInputStream ois=null;
    public static ByteArrayInputStream bais=null;

    /**
     *      以字节方式保存在redis缓存当中
     * @return 是否保存在redis当中
     */
    public static boolean saveInRedis(){

        Jedis jedis=new Jedis("localhost");
        byte[] objByte=null;
        try {
            baos=new ByteArrayOutputStream();
            oos=new ObjectOutputStream(baos);
//           将对象转换为字节数组
            oos.writeObject(SiteMapSingle.getSiteNameMap());
            objByte=baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return "ok".equalsIgnoreCase(jedis.set("siteNameMap".getBytes(),objByte))?true:false;
    }

    /**
     * 从redis 字符串中读取对象
     * @param objByte 对象数组
     * @return 反序列化得到的对象
     */
    public static Map<String, HashSet<Integer>> readInRedis(byte[] objByte){
        Jedis jedis=new Jedis("localhost");
        Map<String, HashSet<Integer>> o=null;
        try {
            bais=new ByteArrayInputStream(objByte);
            ois=new ObjectInputStream(bais);
            o=(HashMap<String, HashSet<Integer>>)ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return o;
    }

    /**
     *  从本地json数据  格式中恢复数据
     *     此方法暂时不用,Json数据转出时不能将set正确拿出>
     */
    public static <HashSetSet> void readRecover() {

        try (Reader reader = new FileReader("/home/tongning/siteNameMap-Json")) {
            StringBuffer sb = new StringBuffer();
            char[] buffer = new char[1024 * 210];
            int len = 0;
            while (-1 != (len = reader.read(buffer))) {
                sb.append(buffer);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *   保存字节在本地
     * @return
     */
    public boolean saveInLocal(){

        return false;
    }



}
