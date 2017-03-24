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

    @Autowired
    private UserTypeRepository repository;
    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;

    public List<UserType> findAll(){
        return repository.findAll();
    }

    public List<Group> findUserTypeGroups(String userTypeId) {
        return groupRepository.findByUserTypeId(userTypeId);
    }
}
