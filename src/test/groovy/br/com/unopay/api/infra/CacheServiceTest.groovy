package br.com.unopay.api.infra

import br.com.unopay.api.SpockApplicationTests
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager

class CacheServiceTest extends SpockApplicationTests {

    CacheService cacheService = new CacheService()
    CacheManager cacheManager = Mock(CacheManager)
    Cache cache = Mock(Cache)
    void setup(){
        cacheManager.getCache(_) >> cache
        cacheService.cacheManager = cacheManager
    }

    def "given a valid cache should return"() {
        given:
        String name = 'cachename'
        String key = 'token'
        cache.get(key) >> 'value'

        when:
        def result = cacheService.get(name, key)
        then:
        assert result != null
    }

    def "given a invalid cache should not return"() {
        given:
        String name = 'cachename'
        String key = 'token'
        cache.get(key) >> 'value'

        when:
        def result = cacheService.get(name, 'invalid')
        then:
        assert result == null
    }
}
