package br.com.unopay.api.uaa.service;

import br.com.unopay.api.uaa.model.Authority;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.oauth2.AuthUserContextHolder;
import br.com.unopay.api.uaa.repository.AuthorityRepository;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserDetailService implements UserDetailsService {

    private UserDetailRepository userDetailRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserDetailService(UserDetailRepository userDetailRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder) {
        this.userDetailRepository = userDetailRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailService.class);

    public UserDetail create(UserDetail user) {

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setAuthorities(getExistingAuthorities(user.getAuthorities()));
            return this.userDetailRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            LOGGER.warn(String.format("user email already exists %s", user.toString()), e);
            throw new RuntimeException("user email already exists"); //TODO change to Conflict exception
        }
    }

    private Set<String> getExistingAuthorities(Set<String> authorities) {
        if (authorities == null || authorities.isEmpty()) {
            return null;
        }
        Iterable<Authority> all = authorityRepository.findAll(authorities);
        return StreamSupport.stream(all.spliterator(), false)
                .map(Authority::getName)
                .collect(Collectors.toSet());
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new UsernameNotFoundException("bad credentials");
        }

        UserDetail user = this.userDetailRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("bad credentials");
        }

        List<SimpleGrantedAuthority> authorities = user.getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        AuthUserContextHolder.setAuthUserId(user.getId());

        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                authorities);
    }
}
