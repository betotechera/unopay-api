package br.com.unopay.api.uaa.service;

import br.com.unopay.api.uaa.model.Authority;
import br.com.unopay.api.uaa.repository.AuthorityRepository;
import static com.google.common.collect.Lists.newArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityService {

    private AuthorityRepository authorityRepository;

    public AuthorityService() {}

    @Autowired
    public AuthorityService(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    public List<Authority> getAll() {
        return newArrayList(authorityRepository.findAll());
    }
}
