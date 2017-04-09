package br.com.unopay.api.repository;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends UnovationFilterRepository<Person,String, PersonFilter> {}

