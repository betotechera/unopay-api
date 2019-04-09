package br.com.unopay.api.network.repository;

import br.com.unopay.api.network.model.Partner;
import br.com.unopay.api.network.model.filter.PartnerFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends UnovationFilterRepository<Partner,String, PartnerFilter> {

    Optional<Partner> findById(String id);
}
