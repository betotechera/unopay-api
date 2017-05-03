package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Branch;
import br.com.unopay.api.bacen.model.filter.BranchFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import static br.com.unopay.api.uaa.exception.Errors.BRANCH_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;

import java.util.Optional;

public interface BranchRepository extends UnovationFilterRepository <Branch, String, BranchFilter>{

    int countByHeadOfficeId(String headOfficeId);

     Optional<Branch> findById(String id);

}
