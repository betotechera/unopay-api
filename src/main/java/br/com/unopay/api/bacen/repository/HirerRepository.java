package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HirerRepository extends UnovationFilterRepository<Hirer,String, HirerFilter> {

    Optional<Hirer> findById(String id);
    Optional<Hirer> findByPersonDocumentNumber(String documentNumber);
}
