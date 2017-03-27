package br.com.unopay.api.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired(required = false)
    private CacheManager cacheManager;

    public <T> T get(String name, Object key) {
        return (T) cacheManager.getCache(name).get(key).get();
    }

    public void put(String name, Object key, Object value) {
        cacheManager.getCache(name).put(key, value);
    }

    public void evict(String name, Object key) {
        cacheManager.getCache(name).evict(key);
    }
}
