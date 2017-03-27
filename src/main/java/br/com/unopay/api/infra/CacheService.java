package br.com.unopay.api.infra;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Data
public class CacheService {

    @Autowired(required = false)
    private CacheManager cacheManager;

    public <T> T get(String name, Object key) {
        if(getCache(name) != null && getCache(name).get(key) != null) {
            return (T) getCache(name).get(key).get();
        }
        return null;
    }

    public void put(String name, Object key, Object value) {
        getCache(name).put(key, value);
    }

    public void evict(String name, Object key) {
        getCache(name).evict(key);
    }

    private Cache getCache(String name) {
        return cacheManager.getCache(name);
    }
}
