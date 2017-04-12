package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.HirerBranch;
import br.com.unopay.api.bacen.model.filter.HirerBranchFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HirerBranchRepository extends UnovationFilterRepository<HirerBranch,String, HirerBranchFilter> {}
