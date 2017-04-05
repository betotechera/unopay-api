package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Service;
import org.springframework.data.repository.CrudRepository;

public interface ServiceRepository extends CrudRepository<Service,String> {
}
