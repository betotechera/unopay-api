package br.com.unopay.api.repository;

import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.filter.BatchClosingFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.Optional;
import java.util.Set;

public interface BatchClosingRepository extends UnovationFilterRepository<BatchClosing,String, BatchClosingFilter> {

    Set<BatchClosing> findByEstablishmentId(String establishmentId);

    Optional<BatchClosing> findFirstByEstablishmentIdAndHirerId(String establishmentId, String hirerId);

    Optional<BatchClosing> findById(String id);

}
