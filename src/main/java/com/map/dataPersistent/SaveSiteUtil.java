package com.map.dataPersistent;

import redis.clients.jedis.Jedis;

import java.io.*;


/**
 *<功能：
 *  本地持久化保存Map集合         >
 *
 *@since: 1.0.0
 *@Author: tong
 *@Date: 2018年08月30日17:42:38
 *
 */
public class SaveSiteUtil implements Serializable {
    public static ObjectOutputStream oos=null;
    public static ByteArrayOutputStream baos=null;
    public static ObjectInputStream ois=null;
    public static ByteArrayInputStream bais=null;

    /**
     *      以字节方式保存在redis缓存当中
     * @param object 序列化后的对象
     * @param objName 序列化后的对象名
     * @return 是否保存在redis当中
     */
    public static boolean saveInRedis(Object object,String objName){
        Jedis jedis=new Jedis("localhost");
        byte[] objByte=null;
        try {
            baos=new ByteArrayOutputStream();
            oos=new ObjectOutputStream(baos);
//           将对象转换为字节数组
            oos.writeObject(object);
            objByte=baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            jedis.close();
        }
        return "ok".equalsIgnoreCase(jedis.set(objName.getBytes(),objByte))?true:false;
    }

    /**
     * 从redis 字符串中读取对象
     * @param objByte 对象数组
     * @return 反序列化得到的对象
     */
    public static Object readInRedis(byte[] objByte){
        Jedis jedis=new Jedis("localhost");
        Object o=null;
        try {
            bais=new ByteArrayInputStream(objByte);
            ois=new ObjectInputStream(bais);
            o=ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return o;
    }


}
