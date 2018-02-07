package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.AuthorizedMember;
import br.com.unopay.api.bacen.repository.AuthorizedMemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthorizedMemberService {

    @Autowired
    AuthorizedMemberRepository repository;

    public AuthorizedMember create(AuthorizedMember authorizedMember) {
        authorizedMember.validateMe();
        return repository.save(authorizedMember);
    }
}
