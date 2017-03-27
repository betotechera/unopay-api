package br.com.unopay.api.repository;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonFilter;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends CrudRepository<Person,String>, UnovationJpaSpecificationExecutor<Person, PersonFilter> {}

