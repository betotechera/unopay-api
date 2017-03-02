package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.Authority;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority,String> {
}
