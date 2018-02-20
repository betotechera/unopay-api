package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.AuthorizedMember;
import br.com.unopay.api.bacen.model.filter.AuthorizedMemberFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface AuthorizedMemberRepository extends UnovationFilterRepository<AuthorizedMember,String, AuthorizedMemberFilter> {
    Optional<AuthorizedMember> findById(String id);
    Optional<AuthorizedMember> findByIdAndContractContractorId(String id, String contractorId);
    Optional<AuthorizedMember> findByIdAndContractHirerId(String id, String hirerId);
}
