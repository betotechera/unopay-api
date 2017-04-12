package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Partner;
import br.com.unopay.api.bacen.model.filter.PartnerFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends UnovationFilterRepository<Partner,String, PartnerFilter> {}
