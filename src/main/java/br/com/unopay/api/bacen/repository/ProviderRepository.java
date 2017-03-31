package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Provider;
import org.springframework.data.repository.CrudRepository;

public interface ProviderRepository extends CrudRepository<Provider,String> {
}
