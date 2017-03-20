package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.UserDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface UserDetailRepository extends CrudRepository<UserDetail,String> {

    UserDetail findByEmail(String email);
    UserDetail findById(String id);
    Page<UserDetail> findByGroupsId(String id, Pageable pageable);
    Set<UserDetail> findByIdIn(Set<String> usersIds);
}
