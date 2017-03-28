package br.com.unopay.api.infra;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

@Service
@Data
public class CacheService {

    @Autowired(required = false)
    private CacheManager cacheManager;

    public <T> T get(String name, Object key) {
        Cache cache = getCache(name);
        if(cache != null && cache.get(key) != null) {
            return (T) cache.get(key).get();
        }
        return null;
    }

    public void put(String name, Object key, Object value) {
        Cache cache = getCache(name);
        if(cache != null) {
            cache.put(key, value);
        }
    }

    public void evict(String name, Object key) {
        Cache cache = getCache(name);
        if(cache != null) {
            cache.evict(key);
        }
    }

    private Cache getCache(String name) {
        return cacheManager != null ? cacheManager.getCache(name) : null;
    }
}
