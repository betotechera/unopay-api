package br.com.unopay.api.market.repository;

import br.com.unopay.api.market.model.AuthorizedMemberCandidate;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface AuthorizedMemberCandidateRepository extends CrudRepository<AuthorizedMemberCandidate,String> {
    Optional<AuthorizedMemberCandidate> findById(String id);
    Optional<AuthorizedMemberCandidate> findByOrderId(String id);
}
