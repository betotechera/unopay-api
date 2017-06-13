package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Service;
import br.com.unopay.api.bacen.model.filter.ServiceFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface ServiceRepository extends UnovationFilterRepository<Service,String,ServiceFilter>{
    int countByCode(Integer code);

    int countByName(String name);

    Optional<Service> findById(String id);
}
