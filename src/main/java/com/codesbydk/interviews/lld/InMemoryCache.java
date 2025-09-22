package com.codesbydk.interviews.lld;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

import lombok.val;

public class InMemoryCache<K,V> {

    private Integer size;
    private Map<K, V> cache;
    private Queue<K> priorityQueue;

    public InMemoryCache(int size) {
        this.size = size;
        this.cache = new HashMap<>();
        this.priorityQueue = new LinkedBlockingDeque<>();

    }

    public void put(K key, V value) {

    }
    
    public V add(K key, V value) {
        System.out.println("Adding key:" + key + " with value:" + value + " to map");
        this.cache.put(key, value);
        priorityQueue.offer(key);
        return value;
    }

    



}
