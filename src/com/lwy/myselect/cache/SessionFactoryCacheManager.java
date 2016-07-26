package com.lwy.myselect.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * SessionFactory Cache
 * Created by frank lee on 2016/7/23.
 */
public class SessionFactoryCacheManager implements CacheManager {

    private int DEFAULT_CAPACITY = 100;

    private int CAPACITY = DEFAULT_CAPACITY;

    /**
     * this map holds className-AbstractCache
     * different class can use different AbstractCache implementation
     * 这个map只有多个 className-AbstractCache
     * 不同的类可以使用不同的缓存实现
     */
    private ConcurrentHashMap<String,Cache<?,?>> caches = new ConcurrentHashMap<>();

    /**
     * this map holds cache strategy, different class can use different strategy
     * 这个map保存类使用的缓存策略，不同的类可以使用不同的策略
     */
    private Map<String,String> cacheStrategy = new HashMap<>();


    private <T,E> Cache<T,E> getCache(String className){
        Cache<T,E> cache = (Cache<T, E>) caches.get(className);
        if(cache == null){
            Cache<T,E> plain = new PlainCache<>(CAPACITY);
            String strategy = getStrategy(className);
            switch (strategy) {
                case Strategy.FIFO:
                    cache = new FIFOCache<>(plain);
                    break;
                case Strategy.LFU:
                    cache = new LFUCache<>(plain);
                    break;
                case Strategy.LRU:
                    cache = new LRUCache<>(plain);
                    break;
                default:
                    cache = new FIFOCache<>(plain);
                    break;
            }
            Cache<T,E> temp = (Cache<T, E>) caches.putIfAbsent(className,cache);
            return temp == null ? cache : temp;
        }
        return cache;
    }

    /**
     * 具体是否缓存，取决于是否注册了这个类的缓存策略，如没有，则默认没有加入缓存
     * @param className class name
     * @param t t
     * @param e e
     * @param <T> T
     * @param <E> E
     */
    @Override
    public <T,E> void save(String className, T t, E e) {
        if(cacheStrategy.containsKey(className)){
            Cache<T,E> cache = getCache(className);
            cache.save(t,e);
        }
    }

    @Override
    public <T,E> E find(String className, T t) {
        Cache<T,E> cache = getCache(className);
        return cache.find(t);
    }

    @Override
    public void registerStrategy(String className, String strategy) {
        cacheStrategy.put(className,strategy);
    }

    private String getStrategy(String className){
        String strategy = cacheStrategy.get(className);
        return strategy == null ? Strategy.FIFO : strategy;
    }

    @Override
    public <T,E> void closeCache(String className){
        Cache<T,E> cache = getCache(className);
        cache.close();
    }

    @Override
    public void close(){
        Collection<Cache<?, ?>> cacheCollection = caches.values();
        Iterator<Cache<?,?>> iterator = cacheCollection.iterator();
        while(iterator.hasNext()){
            iterator.next().close();
        }
    }

    @Override
    public boolean contains(String className) {
        return cacheStrategy.containsKey(className);
    }
}
