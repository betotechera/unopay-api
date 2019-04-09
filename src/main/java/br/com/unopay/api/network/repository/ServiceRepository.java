package br.com.unopay.api.network.repository;

import br.com.unopay.api.network.model.Service;
import br.com.unopay.api.network.model.filter.ServiceFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface ServiceRepository extends UnovationFilterRepository<Service,String,ServiceFilter> {
    int countByCode(Integer code);

    int countByName(String name);

    Optional<Service> findById(String id);

    Optional<Service> findByIdAndEstablishmentsId(String id, String establishmentId);
}
