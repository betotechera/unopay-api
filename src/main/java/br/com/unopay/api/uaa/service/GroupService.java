package br.com.unopay.api.uaa.service;

import br.com.unopay.api.uaa.model.Authority;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.repository.AuthorityRepository;
import br.com.unopay.api.uaa.repository.GroupRepository;
import br.com.unopay.api.uaa.repository.UserDetailRepository;
import br.com.unopay.bootcommons.exception.ConflictException;
import br.com.unopay.bootcommons.exception.NotFoundException;
import br.com.unopay.bootcommons.exception.UnprocessableEntityException;
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
        if(group.getName() == null) throw new UnprocessableEntityException("Name is required");
        try {
            return repository.save(group);
        }catch (DataIntegrityViolationException ex){
            throw new ConflictException("Group name already exists");
        }
    }

    public Group getById(String id) {
        return repository.findById(id);
    }

    public void delete(String id) {
        Group group = getById(id);
        if(group == null) throw new NotFoundException("Group not found");
        repository.delete(id);
    }

    public Page<Group> findAll(UnovationPageRequest pageRequest) {
        return repository.findAll(new PageRequest(pageRequest.getPageStartingAtZero(), pageRequest.getSize()));
    }

    @Transactional
    public void addMembers(String id, Set<String> memberIds) {
        Group group = repository.findById(id);
        if(id == null || group == null) throw new UnprocessableEntityException("Group required");
        List<UserDetail> users = StreamSupport.stream(userDetailRepository.findAll(memberIds).spliterator(), false).collect(Collectors.toList());
        if(users.isEmpty()) throw new UnprocessableEntityException("known members required");
        users.forEach(group::addToMyMembers);
        repository.save(group);
    }

    public Page<UserDetail> findMembers(String id, UnovationPageRequest pageRequest) {
        if(id == null) throw new UnprocessableEntityException("Group id required");
        return userDetailRepository.findByGroupsId(id, new PageRequest(pageRequest.getPageStartingAtZero(), pageRequest.getSize()));
    }

    @Transactional
    public void addAuthorities(String id, Set<String> authoritiesIds) {
        Group group = repository.findById(id);
        if(id == null || group == null) throw new UnprocessableEntityException("Group required");
        List<Authority> authorities = StreamSupport.stream(authorityRepository.findAll(authoritiesIds).spliterator(), false).collect(Collectors.toList());
        if(authorities.isEmpty()) throw new UnprocessableEntityException("known authorities required");
        authorities.forEach(group::addToMyAuthorities);
        repository.save(group);

    }

    public Page<Authority> findAuhtorities(String id, UnovationPageRequest pageRequest) {
        if(id == null) throw new UnprocessableEntityException("Group id required");
        return authorityRepository.findByGroupsId(id, new PageRequest(pageRequest.getPageStartingAtZero(), pageRequest.getSize()));
    }

    @Transactional
    public void associateUserWithGroups(String userId, Set<String> groupsIds) {
        UserDetail user = userDetailRepository.findById(userId);
        if(userId == null || user == null) throw new UnprocessableEntityException("User required");
        Set<Group> groups = getGroupsById(groupsIds);
        if(groups.isEmpty()) throw new UnprocessableEntityException("known groups required");
        groups.forEach(user::addToMyGroups);
        userDetailRepository.save(user);
    }

    private Set<Group> getGroupsById(Set<String> groupsIds) {
        return StreamSupport.stream(repository.findAll(groupsIds).spliterator(), false).collect(Collectors.toSet());
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
        if(userId == null) throw new UnprocessableEntityException("User id required");
        return repository.findByMembersId(userId);
    }
}
