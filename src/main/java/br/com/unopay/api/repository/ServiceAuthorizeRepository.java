package br.com.unopay.api.repository;

import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.ServiceAuthorizeFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;

public interface ServiceAuthorizeRepository extends
        UnovationFilterRepository<ServiceAuthorize,String, ServiceAuthorizeFilter> {

    Optional<ServiceAuthorize> findById(String id);
    List<ServiceAuthorize> findAll();
}
