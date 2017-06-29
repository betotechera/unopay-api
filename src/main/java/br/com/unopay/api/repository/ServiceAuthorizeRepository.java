package br.com.unopay.api.repository;

import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.ServiceAuthorizeFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.Query;

public interface ServiceAuthorizeRepository extends
        UnovationFilterRepository<ServiceAuthorize,String, ServiceAuthorizeFilter> {

    Optional<ServiceAuthorize> findById(String id);
    List<ServiceAuthorize> findAll();

    @Query("SELECT s FROM ServiceAuthorize s WHERE s.establishment.id = ?1 and  " +
            " s.authorizationDateTime < CURRENT_DATE and s.batchClosingDateTime is null order by s.contract.hirer")
    Stream<ServiceAuthorize> findByEstablishmentIdForProcessBatchClosing(String establishmentId);
}
