package br.com.unopay.api.repository;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.filter.PersonFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends UnovationFilterRepository<Person,String, PersonFilter> {

    Optional<Person> findById(String id);
}

