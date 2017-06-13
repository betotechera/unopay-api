package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.Authority;
import java.util.List;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority,String> {

    List<Authority> findByGroupsId(String id);

    Set<Authority> findByNameIn(Set<String> authoritiesIds);
}
