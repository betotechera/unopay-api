package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Service;
import br.com.unopay.api.bacen.model.ServiceFilter;
import br.com.unopay.api.repository.UnovationFilterRepository;
import org.springframework.data.repository.CrudRepository;

public interface ServiceRepository extends UnovationFilterRepository<Service,String,ServiceFilter>{
}
