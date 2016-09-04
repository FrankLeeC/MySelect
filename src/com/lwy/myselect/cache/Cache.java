package com.lwy.myselect.cache;

/**
 * 抽象缓存，需要具体实现，必须FIFO,LRU实现
 * T：主键id值
 * Object：实体
 *
 * Created by frank lee on 2016/7/23.
 * Email: frankleecsz@gmail.com
 */
interface Cache<T,E> {
    void save(T t, E e);
    E find(T t);
    E remove(T t);
    void clear();
    int capacity();
    void close();
}
