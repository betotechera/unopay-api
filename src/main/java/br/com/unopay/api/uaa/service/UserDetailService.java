package br.com.unopay.api.uaa.service;

import br.com.unopay.api.bacen.service.AccreditedNetworkService;
import br.com.unopay.api.bacen.service.InstitutionService;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.infra.PasswordTokenService;
import br.com.unopay.api.uaa.model.*;  // NOSONAR
import br.com.unopay.api.uaa.model.filter.UserFilter;
import br.com.unopay.api.uaa.oauth2.AuthUserContextHolder;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.api.uaa.repository.UserTypeRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

import static br.com.unopay.api.uaa.exception.Errors.*;  // NOSONAR

@Service
@Timed
@Getter @Setter
@Slf4j
public class UserDetailService implements UserDetailsService {

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private IssuerService issuerService;

    @Autowired
    private AccreditedNetworkService accreditedNetworkService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private UserTypeRepository userTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private GroupService groupService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private PasswordTokenService passwordTokenService;

    public UserDetail create(UserDetail user) {
        try {
            if(user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            validateUserType(user);
            Set<Group> groups = groupService.loadKnownUserGroups(user);
            user.setGroups(groups);
            UserDetail created =  this.userDetailRepository.save(user);
            notificationService.sendNewPassword(created);
            return created;
        } catch (DataIntegrityViolationException e) {
            log.warn(String.format("user already exists %s", user.toString()), e);
            throw UnovationExceptions.conflict()
                    .withErrors(Errors.USER_EMAIL_ALREADY_EXISTS)
                    .withArguments(user.getEmail());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new UsernameNotFoundException("bad credentials");
        }
        UserDetail user = this.userDetailRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("bad credentials");
        }
        List<SimpleGrantedAuthority> authorities = user.toGrantedAuthorities(groupService.findUserGroups(user.getId()));
        AuthUserContextHolder.setAuthUserId(user.getId());
        return new User(username, user.getPassword(), authorities);
    }

    public UserDetail getById(String id) {
        UserDetail user = this.userDetailRepository.findById(id);
        if (user == null) {
            throw UnovationExceptions.notFound().withErrors(USER_NOT_FOUND);
        }
        return user;
    }

    public UserDetail update(UserDetail user) {
        UserDetail current = userDetailRepository.findById(user.getId());
        if (current == null) {
            throw UnovationExceptions.notFound().withErrors(USER_NOT_FOUND);
        }
        if (user.getPassword() != null) {
            current.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        current.updateModel(user);
        try {
            return userDetailRepository.save(current);
        } catch (DataIntegrityViolationException e) {
            log.warn(String.format("user email already exists %s", user.toString()), e);
            throw UnovationExceptions.conflict().withErrors(Errors.USER_EMAIL_ALREADY_EXISTS).withArguments(user.getEmail());
        }
    }

    public UserDetail getByEmail(String email) {
        UserDetail user = this.userDetailRepository.findByEmail(email);
        if (user == null) {
            throw UnovationExceptions.notFound().withErrors(USER_NOT_FOUND);
        }
        return user;
    }

    public Page<UserDetail> findByFilter(UserFilter userFilter, UnovationPageRequest pageable) {
        return userDetailRepository.findAll(userFilter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public void delete(String id) {
        getById(id);
        userDetailRepository.delete(id);
    }

    @Transactional(rollbackOn = Throwable.class)
    public void updatePasswordByToken(NewPassword newPassword) {
        String token = newPassword.getToken();
        String userId = passwordTokenService.getUserIdByToken(token);
        UserDetail user = getById(userId);
        updatePasswordByUser(user,newPassword);
        passwordTokenService.remove(token);
    }

    public void resetPasswordById(String userId) {
        UserDetail user = getById(userId);
        notificationService.sendNewPassword(user);
    }
    public void resetPasswordByEmail(String email) {
        UserDetail user = getByEmail(email);
        notificationService.sendNewPassword(user, EventType.PASSWORD_RESET);
    }

    @Transactional(rollbackOn = Throwable.class)
    public void updatePasswordByEmail(String email, NewPassword newPassword) {
        UserDetail user = getByEmail(email);
        updatePasswordByUser(user, newPassword);
    }

    private void updatePasswordByUser( UserDetail user, NewPassword newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
        userDetailRepository.save(user);
    }

    private void validateUserType(UserDetail user) {
        if(user.getType() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(USER_TYPE_REQUIRED);
        }

        UserType type = userTypeRepository.findById(user.getType().getId());
        if(type == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(USER_TYPE_NOT_FOUND);
        }

        if(type.getName().equals(UserTypeNames.INSTITUTION)) {
            validateInstitution(user);
        }
        if(type.getName().equals(UserTypeNames.ACCREDITED_NETWORK)) {
            validateAccreditedNetwork(user);
        }
        if(type.getName().equals(UserTypeNames.ISSUER)) {
            validateIssuer(user);
        }
    }

    private void validateIssuer(UserDetail user) {
        if(user.getIssuer() == null || user.getIssuer().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.USER_TYPE_MUST_SET_AN_ISSUER);
        } else {
           issuerService.findById(user.getIssuer().getId());
        }
    }

    private void validateAccreditedNetwork(UserDetail user) {
        if(user.getAccreditedNetwork() == null || user.getAccreditedNetwork().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.USER_TYPE_MUST_SET_AN_ACCREDITED_NETWORK);
        } else {
            accreditedNetworkService.getById(user.getAccreditedNetwork().getId());
        }
    }

    private void validateInstitution(UserDetail user) {
        if(user.getInstitution() == null || user.getInstitution().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.USER_TYPE_MUST_SET_AN_INSTITUTION);
        } else {
            institutionService.getById(user.getInstitution().getId());
        }

    }

}
