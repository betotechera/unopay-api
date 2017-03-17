package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface GroupRepository extends CrudRepository<Group,String> {

    Group findById(String id);
    List<Group> findByMembersId(String id);
    Page<Group> findAll(Pageable pageable);
    Set<Group> findByIdIn(Set<String> ids);

}
