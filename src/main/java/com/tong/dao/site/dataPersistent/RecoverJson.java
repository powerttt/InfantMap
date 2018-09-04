package com.tong.dao.site.dataPersistent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.tong.dao.site.SiteMapSingle;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *<功能：
 *  从本地json数据  格式中恢复数据         >
 *
 *@since: 1.0.0
 *@Author: tong
 *@Date:
 *
 */
public class RecoverJson {

    public static void main(String[] args) {
        readRecover();
    }

    public static <HashSetSet> void readRecover() {

        try(Reader reader =new FileReader("/home/tongning/siteNameMap-Json")) {
            StringBuffer sb = new StringBuffer();

            char[] buffer = new char[1024*210];
            int len = 0;
            while (-1 != (len = reader.read(buffer))) {
               sb.append(buffer);
            }
            Map<String, JSONArray> siteNameMap= (Map<String, JSONArray>) JSON.parse(sb.toString());
//            SiteMapSingle.setSiteNameMap(siteNameMap);
                        SiteMapSingle.setSiteNameMap((Map<String, HashSet<Integer>>) JSON.parse(sb.toString()));

            JSONArray s=siteNameMap.get("古荡");
            HashSet<Integer>  set=new HashSet<>();
            set.addAll((HashSet<Integer>)JSON.toJavaObject(s,HashSet.class));
            System.out.println(set);
            System.out.println(s);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
}

}
