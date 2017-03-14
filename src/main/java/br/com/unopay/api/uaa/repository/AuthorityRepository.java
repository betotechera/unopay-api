package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.Authority;
import br.com.unopay.api.uaa.model.UserDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface AuthorityRepository extends CrudRepository<Authority,String> {

    Page<Authority> findByGroupsId(String id, Pageable pageable);
}
