package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.UserDetail;
import org.springframework.data.repository.CrudRepository;

public interface UserDetailRepository extends CrudRepository<UserDetail,String> {

    UserDetail findByEmail(String email);
}
