package br.com.unopay.api.uaa.service;

import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserType;
import br.com.unopay.api.uaa.repository.GroupRepository;
import br.com.unopay.api.uaa.repository.UserTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserTypeService {

    private UserTypeRepository repository;
    private GroupRepository groupRepository;

    @Autowired
    public UserTypeService(UserTypeRepository repository, GroupRepository groupRepository, GroupService groupService) {
        this.repository = repository;
        this.groupRepository = groupRepository;
    }

    public List<UserType> findAll(){
        return repository.findAll();
    }

    public List<Group> findUserTypeGroups(String userTypeId) {
        return groupRepository.findByUserTypeId(userTypeId);
    }
}
