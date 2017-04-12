package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Hired;
import br.com.unopay.api.bacen.model.filter.HiredFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HiredRepository extends UnovationFilterRepository<Hired,String, HiredFilter> {}
