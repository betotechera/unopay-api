package br.com.unopay.api.uaa.service;

import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.repository.GroupRepository;
import br.com.unopay.bootcommons.exception.NotFoundException;
import br.com.unopay.bootcommons.exception.UnprocessableEntityException;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

    @Autowired
    private GroupRepository repository;

    public Group create(Group group) {
        if(group.getName() == null) throw new UnprocessableEntityException("Name is required");
        return repository.save(group);
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
        return  repository.findAll(new PageRequest(pageRequest.getPageStartingAtZero(), pageRequest.getSize()));
    }
}
