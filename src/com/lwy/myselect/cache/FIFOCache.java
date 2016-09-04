package com.lwy.myselect.cache;

import java.util.Deque;
import java.util.LinkedList;

/**
 * fifo cache
 * 先进先出算法实现
 *
 * Created by frank lee on 2016/7/23.
 * Email: frankleecsz@gmail.com
 */
public class FIFOCache<T,E> implements Cache<T,E>{

    private Deque<T> deque = new LinkedList<>();
    private Cache<T,E> delegate;
    private int capacity;  //总容量
    private int count = 0;  //当前数量
    private boolean cleared = false;

    public FIFOCache(Cache<T,E> delegate){
        this.delegate = delegate;
        this.capacity = delegate.capacity();
    }

    @Override
    public void save(T t, E e) {
        boolean big = check();
        if(big) {
            T tr = deque.getFirst();
            remove(tr);
        }
        deque.addLast(t);
        delegate.save(t,e);
        count++;
    }

    private boolean check(){
        return count >= capacity;
    }

    @Override
    public E find(T t) {
        return delegate.find(t);
    }

    @Override
    public E remove(T t) {
        deque.remove(t);
        count--;
        return delegate.remove(t);
    }

    @Override
    public void clear() {
        if(!cleared) {
            deque.clear();
            count = 0;
            delegate.clear();
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
        delegate.close();
    }
}
