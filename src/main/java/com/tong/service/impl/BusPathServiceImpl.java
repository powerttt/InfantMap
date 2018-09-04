package com.tong.service.impl;

import com.tong.dao.site.BusPathDao;
import com.tong.service.BusPathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class BusPathServiceImpl implements BusPathService {

    @Autowired
    BusPathDao busPathDao;

    /**
     * 路径搜索 ，在开始搜索之前判断是否为空，为空这进行爬取,非空则进行检查是否包含起始和终点
     *
     * @param origin 起点
     * @param end    终点
     */
    @Override
    public LinkedList getBusPathInfo(String origin, String end) {


        return busPathDao.getBusPathInfo(origin, end);
    }



}
