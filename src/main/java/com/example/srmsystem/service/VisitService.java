package com.example.srmsystem.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class VisitService {

    // Используем ConcurrentHashMap для хранения количества посещений URL
    private final ConcurrentHashMap<String, Integer> visitCountMap = new ConcurrentHashMap<>();

    // Метод для увеличения счетчика посещений для конкретного URL
    public void incrementVisit(String url) {
        visitCountMap.merge(url, 1, Integer::sum);
    }

    // Метод для получения количества посещений для конкретного URL
    public int getVisitCount(String url) {
        return visitCountMap.getOrDefault(url, 0);
    }

    // Метод для очистки данных о посещениях
    public void clearVisitData() {
        visitCountMap.clear();
    }
}
