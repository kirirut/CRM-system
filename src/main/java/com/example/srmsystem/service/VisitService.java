package com.example.srmsystem.service;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;


@Service
public class VisitService {


    private final ConcurrentHashMap<String, Integer> visitCountMap = new ConcurrentHashMap<>();


    public void incrementVisit(String url) {
        visitCountMap.merge(url, 1, Integer::sum);
    }


    public int getVisitCount(String url) {
        return visitCountMap.getOrDefault(url, 0);
    }


    public void clearVisitData() {
        visitCountMap.clear();
    }
}