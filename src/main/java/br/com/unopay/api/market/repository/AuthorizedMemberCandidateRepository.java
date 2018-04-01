package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.AuthorizedMemberCandidate;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface AuthorizedMemberCandidateRepository extends CrudRepository<AuthorizedMemberCandidate,String> {
    Optional<AuthorizedMemberCandidate> findById(String id);
    Set<AuthorizedMemberCandidate> findByOrderId(String id);
}
