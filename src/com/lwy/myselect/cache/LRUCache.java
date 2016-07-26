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
        change(t);
        delegate.save(t,e);
    }

    @Override
    public E find(T t) {
        change(t);
        return delegate.find(t);
    }

    /**
     * 把查找的或者保存的放到第一个，并且移除其原本所在位置
     * 例如：原来在index 5,获取时移到index 0，并将index 5的那个移除
     * @param t t
     */
    private void change(T t){
        synchronized (lock){
            list.set(0,t);
            Iterator<T> iterator = list.iterator();
            while(iterator.hasNext()){
                if(iterator.next() == t){
                    iterator.remove();
                }
            }
        }
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
        delegate.close();
    }
}
