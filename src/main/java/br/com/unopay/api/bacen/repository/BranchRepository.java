package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Branch;
import br.com.unopay.api.bacen.model.filter.BranchFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;

import java.util.List;

import static br.com.unopay.api.uaa.exception.Errors.BRANCH_NOT_FOUND;

public interface BranchRepository extends UnovationFilterRepository <Branch, String, BranchFilter>{

    List<Branch> findByHeadOfficeId(String headOfficeId);

    default Branch findById(String id) {
        Branch branch = findOne(id);
        if(branch == null) throw UnovationExceptions.notFound().withErrors(BRANCH_NOT_FOUND);
        return branch;
    }

}