package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.filter.HirerFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HirerRepository extends UnovationFilterRepository<Hirer,String, HirerFilter> {}
