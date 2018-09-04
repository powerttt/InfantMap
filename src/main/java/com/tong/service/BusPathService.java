package com.tong.service;

import org.springframework.stereotype.Service;

import java.util.LinkedList;
@Service
public interface BusPathService {

    /**
     *  路径搜索 ，在开始搜索之前判断是否为空，为空这进行爬取,非空则进行检查是否包含起始和终点
     * @param origin 起点
     * @param end 终点
     */
    public LinkedList getBusPathInfo(String origin, String end);

}
