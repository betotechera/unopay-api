package br.com.unopay.api.bacen.service;

import static br.com.unopay.api.config.CacheConfig.BRAND_FLAGS;
import br.com.unopay.api.model.BrandFlag;
import br.com.unopay.api.repository.BrandFlagRepository;
import static br.com.unopay.api.uaa.exception.Errors.BRAND_FLAG_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BrandFlagService {

    private BrandFlagRepository repository;

    public BrandFlagService(){}

    @Autowired
    public BrandFlagService(BrandFlagRepository repository) {
        this.repository = repository;
    }

    public BrandFlag findById(String id){
        Optional<BrandFlag> brandFlag = repository.findById(id);
        return brandFlag.orElseThrow(()->UnovationExceptions.notFound().withErrors(BRAND_FLAG_NOT_FOUND));
    }
    @Cacheable(value = BRAND_FLAGS,key="#key")
    public List<BrandFlag> findAll(String key){
        return repository.findAll();
    }
}
