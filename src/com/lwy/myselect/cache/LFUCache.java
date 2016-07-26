package com.lwy.myselect.cache;

import java.util.*;

/**LFUCache
 * 近期最少使用
 * 淘汰一定时期内被访问次数最少的对象
 * Created by frank lee on 2016/7/23.
 */
public class LFUCache<T,E> implements Cache<T,E>, Runnable {

    private Cache<T,E> delegate;
    private int capacity;
    private int interval = 10;
    private volatile boolean run = true;
    private final Object lock = new Object();
    private boolean cleared = false;

    /**
     * key:T t
     * value:该对象被获取次数在list中所在的位置
     * 比如：T 为 Integer 的id，5， value为3，则表示在list中位置3的地方保存的是该对象被访问的次数
     */
    private Map<T,Integer> map = new HashMap<>();

    /**
     * 表示访问的次数
     * 先确定该对象的保存位置，然后再把位置存到map中
     * 替换时，不要直接用remove方法，因为后面的元素会移动一格过来，导致与map中的位置记录不符合，
     * 所以应该使用set(position,value)方法直接设置新加入的元素
     */
    private List<Integer> list = new ArrayList<>();

    public LFUCache(Cache<T,E> delegate){
        this.delegate = delegate;
        this.capacity = delegate.capacity();
    }

    @Override
    public void save(T t, E e) {
        synchronized (lock){
            int position = list.size();
            map.put(t,position);
            list.add(0);
        }
        delegate.save(t,e);
    }

    /**
     * return index of min value
     * 获取list中的最小值，返回位置index
     * @return index of min value
     */
    private int findLeastUsed(){
        int c = list.get(0);
        int position = 0;
        for(int i=0;i<list.size();i++) {
            if (list.get(i) < c){
                c = list.get(i);
                position = i;
            }
        }
        return position;
    }

    @Override
    public E find(T t) {
        synchronized (lock){
            int position = map.get(t);
            int time = list.get(position)+1;
            list.set(position,time);
        }
        return delegate.find(t);
    }

    @Override
    public E remove(T t) {
        synchronized (lock){
            int position = map.get(t);
            list.remove(position);
            map.remove(t);
        }
        return delegate.remove(t);
    }

    @Override
    public void clear() {
        if(!cleared) {
            synchronized (lock) {
                map.clear();
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
    public void close() {
        clear();
        run = false;
        delegate.close();
    }

    @Override
    public void run() {
        while(run){
            synchronized (lock){
                T temp = null;
                int position = findLeastUsed();
                Set<T> set = map.keySet();
                for(T key:set){
                    Integer index = map.get(key);
                    if(position == index){
                        temp = key;
                        break;
                    }
                }
                map.remove(temp);
                list.remove(position);
            }
            try {
                Thread.sleep(interval*1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
