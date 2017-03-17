package br.com.unopay.api.uaa.service;

import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.oauth2.AuthUserContextHolder;
import br.com.unopay.api.uaa.repository.AuthorityRepository;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.bootcommons.exception.ConflictException;
import br.com.unopay.bootcommons.exception.NotFoundException;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import ch.qos.logback.core.net.SyslogOutputStream;
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

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Timed
public class UserDetailService implements UserDetailsService {

    private UserDetailRepository userDetailRepository;
    private AuthorityRepository authorityRepository;
    private PasswordEncoder passwordEncoder;
    private GroupService groupService;


    @Autowired
    public UserDetailService(UserDetailRepository userDetailRepository, AuthorityRepository authorityRepository, PasswordEncoder passwordEncoder, GroupService groupService) {
        this.userDetailRepository = userDetailRepository;
        this.authorityRepository = authorityRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupService = groupService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailService.class);

    public UserDetail create(UserDetail user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Set<Group> groups = groupService.loadKnownUserGroups(user);
            user.setGroups(groups);
            return this.userDetailRepository.save(user);
        } catch (RuntimeException e) {
            LOGGER.warn(String.format("user email already exists %s", user.toString()), e);
            throw new ConflictException(String.format("user email already exists %s", user.toString()));
        }
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new UsernameNotFoundException("bad credentials");
        }

        UserDetail user = this.userDetailRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("bad credentials");
        }

        List<SimpleGrantedAuthority> authorities = user.toGrantedAuthorities(groupService.findUserGroups(user.getId()));

        AuthUserContextHolder.setAuthUserId(user.getId());

        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                authorities);
    }

    public UserDetail getById(String id) {
        UserDetail user = this.userDetailRepository.findOne(id);
        if (user == null) {
            throw new NotFoundException("user not found");
        }
        return user;
    }

    public UserDetail update(UserDetail user) {

        UserDetail current = userDetailRepository.findOne(user.getId());
        if (current == null) {
            throw new NotFoundException("current user not found");
        }

        if (user.getPassword() != null) {
            current.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        if (user.getEmail() != null) {
            current.setEmail(user.getEmail());
        }

        try {
            return userDetailRepository.save(current);
        } catch (DataIntegrityViolationException e) {
            LOGGER.warn(String.format("user email already exists %s", user.toString()), e);
            throw new ConflictException(String.format("user email already exists %s", user.toString()));
        }

    }

    public UserDetail getByEmail(String email) {
        UserDetail user = this.userDetailRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("user not found");
        }
        return user;
    }

    public List<UserDetail> getByAuthority(String authority) {
        List<UserDetail> users = Collections.emptyList();
        if (users == null || users.isEmpty()) {
            throw new NotFoundException("users not found");
        }
        return users;
    }
}
