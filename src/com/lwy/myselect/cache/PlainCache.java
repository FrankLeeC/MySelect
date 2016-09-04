package com.lwy.myselect.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * plain implementation of cache
 * 普通的cache实现，没有任何算法包装，用于委派模式的最终执行者
 *
 * Created by frank lee on 2016/7/23.
 * Email: frankleecsz@gmail.com
 */
public class PlainCache<T,E> implements Cache<T,E> {

    private int capacity;
    private Map<T,E> cache;
    private boolean cleared = false;

    public PlainCache(int capacity){
        this.capacity = capacity;
        cache = new HashMap<>(capacity);
    }

    @Override
    public void save(T t, E e) {
        cache.put(t,e);
    }

    @Override
    public E find(T t) {
        return cache.get(t);
    }

    @Override
    public E remove(T t) {
        return cache.remove(t);
    }

    @Override
    public void clear() {
        if(!cleared){
            cache.clear();
            cleared = true;
        }
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public void close() {
        clear();
    }
}
