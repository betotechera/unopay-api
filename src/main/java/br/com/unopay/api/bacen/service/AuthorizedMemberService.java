package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.AuthorizedMember;
import br.com.unopay.api.bacen.repository.AuthorizedMemberRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthorizedMemberService {

    @Autowired
    AuthorizedMemberRepository repository;

    public AuthorizedMember create(AuthorizedMember authorizedMember) {
        authorizedMember.validateMe();
        return repository.save(authorizedMember);
    }
    public AuthorizedMember findById(String id) {
        Optional<AuthorizedMember> authorizedMember = repository.findById(id);
        return authorizedMember.orElseThrow(()-> UnovationExceptions.notFound().withErrors(
                Errors.AUTHORIZED_MEMBER_NOT_FOUND));
    }
}
