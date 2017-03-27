package br.com.unopay.api.repository;

import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonFilter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<Address,String>{}

