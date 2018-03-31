package br.com.unopay.api.market.service;

import br.com.unopay.api.market.model.AuthorizedMemberCandidate;
import br.com.unopay.api.market.repository.AuthorizedMemberCandidateRepository;
import br.com.unopay.api.order.service.OrderService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthorizedMemberCandidateService {

    private AuthorizedMemberCandidateRepository repository;
    private OrderService orderService;

    @Autowired
    public AuthorizedMemberCandidateService(AuthorizedMemberCandidateRepository repository,
                                            OrderService orderService) {
        this.repository = repository;
        this.orderService = orderService;
    }

    public AuthorizedMemberCandidate create(AuthorizedMemberCandidate authorizedMember) {
        authorizedMember.validateMe();
        validateReferences(authorizedMember);
        return save(authorizedMember);
    }

    private AuthorizedMemberCandidate save(AuthorizedMemberCandidate authorizedMember) {
        return repository.save(authorizedMember);
    }

    private void validateReferences(AuthorizedMemberCandidate authorizedMember) {
        authorizedMember.setOrder(orderService.findById(authorizedMember.getOrder().getId()));
    }

    public AuthorizedMemberCandidate findById(String id) {
        Optional<AuthorizedMemberCandidate> authorizedMember = repository.findById(id);
        return authorizedMember.orElseThrow(()-> UnovationExceptions.notFound().withErrors(
                Errors.AUTHORIZED_MEMBER_NOT_FOUND));
    }

    public void update(String id, AuthorizedMemberCandidate authorizedMember) {
        authorizedMember.validateMe();
        AuthorizedMemberCandidate current = findById(id);
        update(current, authorizedMember);
    }

    private void update(AuthorizedMemberCandidate current, AuthorizedMemberCandidate authorizedMember) {
        current.updateMe(authorizedMember);
        validateReferences(current);
        save(current);
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

}
