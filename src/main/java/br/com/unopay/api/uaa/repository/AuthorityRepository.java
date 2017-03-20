package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.Authority;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AuthorityRepository extends CrudRepository<Authority,String> {

    List<Authority> findByGroupsId(String id);
}
