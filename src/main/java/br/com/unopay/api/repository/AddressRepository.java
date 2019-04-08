package br.com.unopay.api.repository;

import br.com.unopay.api.model.Address;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<Address,String>{
    Optional<Address> findById(String id);
}

