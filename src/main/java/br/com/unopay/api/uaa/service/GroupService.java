package br.com.unopay.api.uaa.service;

import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.Authority;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.repository.AuthorityRepository;
import br.com.unopay.api.uaa.repository.GroupRepository;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.bootcommons.exception.*;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static br.com.unopay.api.uaa.exception.Errors.*;

@Service
public class GroupService {

    @Autowired
    private GroupRepository repository;

    @Autowired
    private UserDetailRepository userDetailRepository;

    @Autowired
    private UserDetailRepository groupMemberRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    public Group create(Group group) {
        if(group.getName() == null)  throw UnovationExceptions.unprocessableEntity().withErrors(USER_REQUIRED);
        try {
            return repository.save(group);
        }catch (DataIntegrityViolationException ex){
            throw UnovationExceptions.conflict().withErrors(Errors.GROUP_NAME_ALREADY_EXISTS).withArguments(group.getName());
        }
    }

    public Group getById(String id) {
        Group group =  repository.findById(id);
        if(group == null) throw UnovationExceptions.notFound();
        return group;
    }

    public void delete(String id) {
        getById(id);
        Page<UserDetail> members = findMembers(id, new UnovationPageRequest());
        if(members.getContent() != null && !members.getContent().isEmpty())
            throw UnovationExceptions.conflict().withErrors(GROUP_WITH_MEMBERS);
        repository.delete(id);
    }

    public Page<Group> findAll(UnovationPageRequest pageRequest) {
        return repository.findAll(new PageRequest(pageRequest.getPageStartingAtZero(), pageRequest.getSize()));
    }

    @Transactional
    public void addMembers(String id, Set<String> memberIds) {
        Group group = repository.findById(id);
        if(id == null || group == null) throw UnovationExceptions.unprocessableEntity().withErrors(GROUP_REQUIRED);
        List<UserDetail> users = StreamSupport.stream(userDetailRepository.findAll(memberIds).spliterator(), false).collect(Collectors.toList());
        if(users.isEmpty()) throw UnovationExceptions.unprocessableEntity().withErrors(KNOWN_MEMBERS_REQUIRED);
        users.forEach(group::addToMyMembers);
        repository.save(group);
    }

    public Page<UserDetail> findMembers(String id, UnovationPageRequest pageRequest) {
        if(id == null) throw UnovationExceptions.unprocessableEntity().withErrors(GROUP_REQUIRED);
        return userDetailRepository.findByGroupsId(id, new PageRequest(pageRequest.getPageStartingAtZero(), pageRequest.getSize()));
    }

    @Transactional
    public void addAuthorities(String id, Set<String> authoritiesIds) {
        Group group = repository.findById(id);
        if(id == null || group == null) throw UnovationExceptions.unprocessableEntity().withErrors(GROUP_REQUIRED);
        List<Authority> authorities = StreamSupport.stream(authorityRepository.findAll(authoritiesIds).spliterator(), false).collect(Collectors.toList());
        if(authorities.isEmpty()) throw  UnovationExceptions.unprocessableEntity().withErrors(KNOWN_AUTHORITIES_REQUIRED);
        authorities.forEach(group::addToMyAuthorities);
        repository.save(group);

    }

    public Page<Authority> findAuthorities(String id, UnovationPageRequest pageRequest) {
        if(id == null) throw  UnovationExceptions.unprocessableEntity().withErrors(GROUP_REQUIRED);
        return authorityRepository.findByGroupsId(id, new PageRequest(pageRequest.getPageStartingAtZero(), pageRequest.getSize()));
    }

    @Transactional
    public void associateUserWithGroups(String userId, Set<String> groupsIds) {
        UserDetail user = userDetailRepository.findById(userId);
        if(userId == null || user == null) throw UnovationExceptions.unprocessableEntity().withErrors(USER_REQUIRED);
        Set<Group> groups = getGroupsById(groupsIds);
        if(groups.isEmpty()) throw  UnovationExceptions.unprocessableEntity().withErrors(KNOWN_GROUP_REQUIRED);
        groups.forEach(user::addToMyGroups);
        userDetailRepository.save(user);
    }

    private Set<Group> getGroupsById(Set<String> groupsIds) {
        return repository.findByIdIn(groupsIds);
    }

    public Set<Group> loadKnownUserGroups(UserDetail user){
        if(user.getGroups() != null && !user.getGroups().isEmpty()){
            Set<String> groupIds = user.getGroups().stream()
                                        .map(Group::getId)
                                        .filter(Objects::nonNull)
                                        .collect(Collectors.toSet());
           return getGroupsById(groupIds);
        }
        return Collections.emptySet();
    }

    public List<Group> findUserGroups(String userId) {
        if(userId == null) throw UnovationExceptions.unprocessableEntity().withErrors(USER_REQUIRED);
        return repository.findByMembersId(userId);
    }
}
