package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.UserDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserDetailRepository extends CrudRepository<UserDetail,String> {

    UserDetail findByEmail(String email);
    List<UserDetail> findByAuthoritiesOrderByEmail(String authority);
    Page<UserDetail> findByGroupsId(String id, Pageable pageable);
}
