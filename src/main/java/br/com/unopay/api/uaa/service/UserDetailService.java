package br.com.unopay.api.uaa.service;

import br.com.unopay.api.bacen.service.AccreditedNetworkService;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.notification.engine.MailValidator;
import br.com.unopay.api.notification.model.EventType;
import br.com.unopay.api.notification.service.NotificationService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.infra.PasswordTokenService;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.NewPassword;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.UserReferencesValidator;
import br.com.unopay.api.uaa.model.UserType;
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

@Service
@Timed
@Getter
@Setter
@Slf4j
public class UserDetailService implements UserDetailsService {

    public static final String CONTRACTOR = "CONTRATADO";
    public static final String CONTRACTOR_ROLE = "ROLE_CONTRACTOR";

    private UserDetailRepository userDetailRepository;
    private UserTypeService userTypeService;
    private PasswordEncoder passwordEncoder;
    private GroupService groupService;
    private NotificationService notificationService;
    private PasswordTokenService passwordTokenService;
    private AccreditedNetworkService accreditedNetworkService;
    private MailValidator mailValidator;
    private ContractorService contractorService;
    private UserReferencesValidator userReferencesValidator;
    private UserTypeRepository userTypeRepository;

    @Autowired
    public UserDetailService(UserDetailRepository userDetailRepository,
                             PasswordEncoder passwordEncoder,
                             GroupService groupService,
                             NotificationService notificationService,
                             PasswordTokenService passwordTokenService,
                             UserTypeService userTypeService,
                             MailValidator mailValidator,
                             ContractorService contractorService,
                             UserReferencesValidator userReferencesValidator,
                             UserTypeRepository userTypeRepository) {
        this.userDetailRepository = userDetailRepository;
        this.passwordEncoder = passwordEncoder;
        this.groupService = groupService;
        this.notificationService = notificationService;
        this.passwordTokenService = passwordTokenService;
        this.userTypeService = userTypeService;
        this.mailValidator = mailValidator;
        this.contractorService = contractorService;
        this.userReferencesValidator = userReferencesValidator;
        this.userTypeRepository = userTypeRepository;
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
            userTypeService.validateUserType(user);
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
        current.updateMe(user);
        userReferencesValidator.defineValidReferences(current);
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

    public boolean hasIssuer(String id) {
        return  userDetailRepository.countByIssuerId(id) > 0;
    }

    public boolean hasNetwork(String id) {
        return userDetailRepository.countByAccreditedNetworkId(id) > 0;
    }

    public boolean hasEstablishment(String id) {
        return userDetailRepository.countByEstablishmentId(id) > 0;
    }

    public boolean hasHirer(String id) {
        return userDetailRepository.countByHirerId(id) > 0;
    }

    public boolean hasInstitution(String id) {
        return userDetailRepository.countByInstitutionId(id) > 0;
    }

    public boolean hasPartner(String id) {
        return userDetailRepository.countByPartnerId(id) > 0;
    }

    private void updatePasswordByUser( UserDetail user, NewPassword newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword.getPassword()));
        userDetailRepository.save(user);
    }

}
