package br.com.unopay.api.uaa.service;

import br.com.unopay.api.bacen.service.AccreditedNetworkService;
import br.com.unopay.api.bacen.service.InstitutionService;
import br.com.unopay.api.bacen.service.IssuerService;
import br.com.unopay.api.notification.engine.MailValidator;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.infra.PasswordTokenService;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.NewPassword;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.UserType;
import br.com.unopay.api.uaa.model.UserTypeNames;
import br.com.unopay.api.uaa.model.filter.UserFilter;
import br.com.unopay.api.uaa.oauth2.AuthUserContextHolder;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.api.uaa.repository.UserTypeRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import br.com.unopay.bootcommons.stopwatch.annotation.Timed;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
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

import static br.com.unopay.api.uaa.exception.Errors.USER_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.USER_TYPE_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.USER_TYPE_REQUIRED;

@Service
@Timed
@Getter
@Setter
@Slf4j
public class UserDetailService implements UserDetailsService {

    public static final String CONTRACTOR = "CONTRATADO";
    public static final String CONTRACTOR_ROLE = "ROLE_CONTRACTOR";

    private UserDetailRepository userDetailRepository;
    private IssuerService issuerService;
    private AccreditedNetworkService accreditedNetworkService;
    private InstitutionService institutionService;
    private UserTypeRepository userTypeRepository;
    private PasswordEncoder passwordEncoder;
    private GroupService groupService;
    private NotificationService notificationService;
    private PasswordTokenService passwordTokenService;
    private MailValidator mailValidator;

    @Autowired
    public UserDetailService(UserDetailRepository userDetailRepository,
                             IssuerService issuerService,
                             AccreditedNetworkService accreditedNetworkService,
                             InstitutionService institutionService,
                             UserTypeRepository userTypeRepository,
                             PasswordEncoder passwordEncoder,
                             GroupService groupService,
                             NotificationService notificationService,
                             PasswordTokenService passwordTokenService,
                             MailValidator mailValidator) {
        this.userDetailRepository = userDetailRepository;
        this.issuerService = issuerService;
        this.accreditedNetworkService = accreditedNetworkService;
        this.institutionService = institutionService;
        this.userTypeRepository = userTypeRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupService = groupService;
        this.notificationService = notificationService;
        this.passwordTokenService = passwordTokenService;
        this.mailValidator = mailValidator;
    }

    public UserDetailService(){}

    public UserDetail create(UserDetail user) {
        try {
            checkUser(user);
            if(user.getPassword() != null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            UserType userType = userTypeRepository.findByName(CONTRACTOR);
            if(user.getType() == null) {
                user.setType(userType);
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
                    .withErrors(Errors.USER_EMAIL_ALREADY_EXISTS.withOnlyArgument(user.getEmail()));
        }
    }

    private void checkUser(UserDetail user) {
        Optional<UserDetail> byEmailOptional = getByEmailOptional(user.getEmail());
        this.mailValidator.check(user.getEmail());
        byEmailOptional.ifPresent((ThrowingConsumer)-> { throw UnovationExceptions.conflict()
                .withErrors(Errors.USER_EMAIL_ALREADY_EXISTS.withOnlyArgument(user.getEmail()));
        });
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            throw new UsernameNotFoundException("bad credentials");
        }
        Optional<UserDetail> userOptional = getByEmailOptional(email);
        UserDetail user = userOptional.orElseThrow(() -> new UsernameNotFoundException("bad credentials"));

        List<SimpleGrantedAuthority> authorities = user.toGrantedAuthorities(groupService.findUserGroups(user.getId()));
        if(authorities.isEmpty()){
            authorities = Collections.singletonList(new SimpleGrantedAuthority(CONTRACTOR_ROLE));
        }
        AuthUserContextHolder.setAuthUserId(user.getId());
        return new User(email, user.getPassword(), authorities);
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
            throw UnovationExceptions.conflict().withErrors(Errors.USER_EMAIL_ALREADY_EXISTS)
                    .withArguments(user.getEmail());
        }
    }

    public UserDetail getByEmail(String email) {
        Optional<UserDetail> user = getByEmailOptional(email);
         return user.orElseThrow(()-> UnovationExceptions.notFound().withErrors(USER_NOT_FOUND));
    }

    public Optional<UserDetail> getByEmailOptional(String email) {
        return this.userDetailRepository.findByEmailIgnoreCase(email);
    }

    public Page<UserDetail> findByFilter(UserFilter userFilter, UnovationPageRequest pageable) {
        PageRequest page = new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize());
        return userDetailRepository.findAll(userFilter, page);
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
        notificationService.sendNewPassword(user, EventType.PASSWORD_RESET);
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
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.USER_TYPE_MUST_SET_AN_ACCREDITED_NETWORK);
        } else {
            accreditedNetworkService.getById(user.getAccreditedNetwork().getId());
        }
    }

    private void validateInstitution(UserDetail user) {
        if(user.getInstitution() == null || user.getInstitution().getId() == null) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.USER_TYPE_MUST_SET_AN_INSTITUTION);
        } else {
            institutionService.getById(user.getInstitution().getId());
        }

    }


}
