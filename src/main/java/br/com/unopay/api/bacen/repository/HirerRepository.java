package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface HirerRepository extends UnovationFilterRepository<Hirer,String, HirerFilter> {

    Optional<Hirer> findById(String id);
    Optional<Hirer> findByPersonDocumentNumber(String documentNumber);
}
