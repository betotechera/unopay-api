package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.filter.InstitutionFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstitutionRepository extends UnovationFilterRepository<Institution,String, InstitutionFilter> {

    Optional<Institution> findById(String id);
}
