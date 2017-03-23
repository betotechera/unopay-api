package br.com.unopay.api.uaa.service;

import br.com.unopay.api.repository.Filter;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.UserFilter;
import br.com.unopay.api.uaa.model.UserType;
import br.com.unopay.api.uaa.oauth2.AuthUserContextHolder;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.api.uaa.repository.UserTypeRepository;
import br.com.unopay.bootcommons.exception.ConflictException;
import br.com.unopay.bootcommons.exception.NotFoundException;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static br.com.unopay.api.uaa.exception.Errors.USER_TYPE_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.USER_TYPE_REQUIRED;

@Service
@Timed
public class UserDetailService implements UserDetailsService {

    private UserDetailRepository userDetailRepository;
    private UserTypeRepository userTypeRepository;
    private PasswordEncoder passwordEncoder;
    private GroupService groupService;


    @Autowired
    public UserDetailService(UserDetailRepository userDetailRepository, UserTypeRepository userTypeRepository, PasswordEncoder passwordEncoder, GroupService groupService) {
        this.userDetailRepository = userDetailRepository;
        this.userTypeRepository = userTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupService = groupService;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailService.class);

    public UserDetail create(UserDetail user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            validateUserType(user);
            Set<Group> groups = groupService.loadKnownUserGroups(user);
            user.setGroups(groups);
            return this.userDetailRepository.save(user);
        } catch (DataIntegrityViolationException e) {
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

    private void validateUserType(UserDetail user) {
        if(user.getType() == null) throw UnovationExceptions.unprocessableEntity().withErrors(USER_TYPE_REQUIRED);
        UserType type = userTypeRepository.findById(user.getType().getId());
        if(type == null) throw UnovationExceptions.unprocessableEntity().withErrors(USER_TYPE_NOT_FOUND);
    }

    public Page<UserDetail> findByFilter(UserFilter userFilter, UnovationPageRequest pageable) {
        return userDetailRepository.findAll(userFilter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
