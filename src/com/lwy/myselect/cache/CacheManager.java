package com.lwy.myselect.cache;

/** cache manager
 * 缓存管理器
 *
 * Created by frank lee on 2016/7/22.
 * Email: frankleecsz@gmail.com
 */
public interface CacheManager {
    <T,E> void save(String className,T t,E e);
    <T,E> E find(String className,T t);
//    void registerKeyStrategy(String className, String keyStrategy);
    void registerCacheStrategy(String className, String cacheStrategy);
    <T,E> void closeCache(String className);
    void close();
    boolean contains(String className);
}
