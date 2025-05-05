package com.example.srmsystem.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

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
