package com.tong.controller;

import com.alibaba.fastjson.JSON;
import com.tong.dao.site.SiteMapSingle;
import com.tong.service.impl.BusPathServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.LinkedList;

/**
 *<功能：
 * 公交站点  路径搜索 控制响应类       >
 *
 *@since: 1.0.0
 *@Author: tong
 *@Date:
 *
 */
@Controller
@RequestMapping("/search")
public class BusPathSearch {
    @Autowired
    @Qualifier("busPathServiceImpl")
    BusPathServiceImpl busPathService;


    @RequestMapping(value="/getBusInfo",method = RequestMethod.GET)
    public String getBusPathInfo(@RequestParam(value = "origin",required = false,defaultValue ="" )String origin,
                               @RequestParam(value="end",required = false,defaultValue ="")String end){
        if (!(origin==null && origin.isEmpty() && end==null && end.isEmpty())){
            LinkedList<Integer> resultList=busPathService.getBusPathInfo(origin,end);
            System.out.println("车次信息:"+JSON.toJSON(resultList));
            return "";
        }else {
            return "";
        }

    }

    @RequestMapping("/siteNameMap")
    public void getSiteNameMapDataInfo(){
        System.out.println("查看siteNameMap信息");
        SiteMapSingle.getSiteNameMapDataInfo();
    }





}


















