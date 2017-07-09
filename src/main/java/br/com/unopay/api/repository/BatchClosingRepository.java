package br.com.unopay.api.repository;

import br.com.unopay.api.model.BatchClosing;
import br.com.unopay.api.model.BatchClosingSituation;
import br.com.unopay.api.model.filter.BatchClosingFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BatchClosingRepository extends UnovationFilterRepository<BatchClosing,String, BatchClosingFilter> {

    Set<BatchClosing> findByEstablishmentId(String establishmentId);

    Optional<BatchClosing> findFirstByEstablishmentIdAndHirerIdAndSituation(String establishmentId,
                                                                            String hirerId,
                                                                            BatchClosingSituation situation);

    Optional<BatchClosing> findById(String id);

    Optional<BatchClosing> findByEstablishmentIdAndSituation(String establishmentId, BatchClosingSituation situation);

    Set<BatchClosing> findByIssuerIdAndSituation(String hirerId, BatchClosingSituation situation);

    List<BatchClosing> findAll();

}
