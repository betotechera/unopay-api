package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Branch;
import br.com.unopay.api.bacen.model.filter.BranchFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import static br.com.unopay.api.uaa.exception.Errors.BRANCH_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;

public interface BranchRepository extends UnovationFilterRepository <Branch, String, BranchFilter>{

    int countByHeadOfficeId(String headOfficeId);

    default Branch findById(String id) {
        Branch branch = findOne(id);
        if(branch == null) {
            throw UnovationExceptions.notFound().withErrors(BRANCH_NOT_FOUND);
        }
        return branch;
    }

}
