package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.AuthorizedMember;
import br.com.unopay.api.bacen.model.filter.AuthorizedMemberFilter;
import br.com.unopay.api.bacen.repository.AuthorizedMemberRepository;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthorizedMemberService {

    @Autowired
    AuthorizedMemberRepository repository;

    @Autowired
    PaymentInstrumentService paymentInstrumentService;

    public AuthorizedMember create(AuthorizedMember authorizedMember) {
        return save(authorizedMember);
    }

    private AuthorizedMember save(AuthorizedMember authorizedMember) {
        authorizedMember.validateMe();
        validateReferences(authorizedMember);
        return repository.save(authorizedMember);
    }

    private void validateReferences(AuthorizedMember authorizedMember) {
        authorizedMember.setPaymentInstrument(paymentInstrumentService.findById(authorizedMember.paymentInstrumentId()));
    }

    public AuthorizedMember findById(String id) {
        Optional<AuthorizedMember> authorizedMember = repository.findById(id);
        return authorizedMember.orElseThrow(()-> UnovationExceptions.notFound().withErrors(
                Errors.AUTHORIZED_MEMBER_NOT_FOUND));
    }

    public void update(String id, AuthorizedMember authorizedMember) {
        AuthorizedMember current = findById(id);
        current.updateMe(authorizedMember);
        save(authorizedMember);
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<AuthorizedMember> findByFilter(AuthorizedMemberFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
