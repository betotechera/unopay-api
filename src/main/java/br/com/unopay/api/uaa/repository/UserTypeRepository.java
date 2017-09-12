package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.UserType;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface UserTypeRepository extends CrudRepository<UserType, String>{

    UserType findById(String id);

    List<UserType> findAll();

    UserType findByName(String name);
}
