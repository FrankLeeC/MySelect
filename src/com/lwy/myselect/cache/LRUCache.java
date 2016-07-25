package com.lwy.myselect.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**LRU cache
 * 当前时间之前最近没使用的
 * Created by frank lee on 2016/7/23.
 */
public class LRUCache<T,E> implements Cache<T,E>, Runnable {

    private Cache<T,E> delegate;
    private int capacity;
    private List<T> list = new ArrayList<>();
    private final Object lock = new Object();
    private final int interval = 10;
    private volatile boolean run = true;
    private boolean cleared = false;

    public LRUCache (Cache<T,E> delegate){
        this.delegate = delegate;
        this.capacity = delegate.capacity();
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void save(T t, E e) {
        synchronized (lock){
            list.set(0,t);
            Iterator<T> iterator = list.iterator();
            while(iterator.hasNext()){
                if(iterator.next() == t){
                    iterator.remove();
                }
            }
        }
        delegate.save(t,e);
    }

    @Override
    public E find(T t) {
        synchronized (lock){
            list.set(0,t);
            Iterator<T> iterator = list.iterator();
            while(iterator.hasNext()){
                if(iterator.next() == t){
                    iterator.remove();
                }
            }
        }
        return delegate.find(t);
    }

    @Override
    public E remove(T t) {
        synchronized (lock){
            list.remove(t);
        }
        return delegate.remove(t);
    }

    @Override
    public void clear() {
        if(!cleared) {
            synchronized (lock) {
                list.clear();
                delegate.clear();
            }
            cleared = true;
        }
    }

    @Override
    public int capacity() {
        return capacity;
    }

    @Override
    public void run() {
        while(run){
            synchronized (lock) {
                list.remove(list.size() - 1);
            }
            try {
                Thread.sleep(interval*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close(){
        clear();
        run = false;
    }
}
