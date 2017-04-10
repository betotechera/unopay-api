package br.com.unopay.api.bacen.service;

import br.com.unopay.api.model.BrandFlag;
import br.com.unopay.api.repository.BrandFlagRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

import static br.com.unopay.api.config.CacheConfig.BRAND_FLAGS;
import static br.com.unopay.api.uaa.exception.Errors.BRAND_FLAG_NOT_FOUND;

@Service
public class BrandFlagService {

    @Autowired
    private BrandFlagRepository repository;

    public BrandFlag findById(String id){
        BrandFlag brandFlag = repository.findOne(id);
        if(brandFlag == null) throw UnovationExceptions.notFound().withErrors(BRAND_FLAG_NOT_FOUND);
        return brandFlag;
    }
    @Cacheable(key="#key",value = BRAND_FLAGS)
    public List<BrandFlag> findAll(String key){
        return repository.findAll();
    }
}
