package org.foximager.db;

import java.util.List;

public class IDCache {
    private final String 
            cacheName;
    
    private List<Long>
            cache = null;
    
    private int 
            cursor = 0, 
            size   = 0;
    
    public IDCache(String cacheName) {
        this.cacheName = cacheName;  
    }

    public void load(List<Long> list) {
        cache = list;
        size = list.size();
    }
    
    public long nextID() {
        if (size < cursor) cursor++;
        return cache.get(cursor);
    }
    
    public long prevID() {
        if (cursor > 0) cursor--;
        return cache.get(cursor);
    }
    
    public void toFirst() {
        cursor = 0;
    }
    
    public void toLast() {
        cursor = size;
    }
    
    public List<Long> getPage(int start, int stop) {
        return cache.subList((start < 0) ? 0 : start, (stop > size) ? size : stop);
    }
    
    public List<Long> getNextPage(int pageSize) {
        int 
                start = cursor, 
                stop  = cursor + pageSize;
        if (size < cursor) cursor += pageSize;
        return getPage(start, stop);
    } 
    
    public List<Long> getPrevPage(int pageSize) {
        int 
                start = cursor - pageSize, 
                stop  = cursor;
        if (cursor > 0) cursor -= pageSize;
        return getPage(start, stop);
    }

    public String getCacheName() {
        return cacheName;
    }
}
