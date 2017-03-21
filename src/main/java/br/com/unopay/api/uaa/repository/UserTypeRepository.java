package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.UserType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserTypeRepository extends CrudRepository<UserType, String>{

    UserType findById(String id);

    List<UserType> findAll();
}
