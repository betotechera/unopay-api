package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.InstitutionFilter;
import br.com.unopay.api.repository.UnovationFilterRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends UnovationFilterRepository<Institution,String, InstitutionFilter> {}
