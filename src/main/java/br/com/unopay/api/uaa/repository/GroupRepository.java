package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.filter.GroupFilter;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GroupRepository extends UnovationFilterRepository<Group,String, GroupFilter> {

    Group findById(String id);
    List<Group> findByMembersId(String id);
    Page<Group> findAll(Pageable pageable);
    Set<Group> findByIdIn(Set<String> ids);

    List<Group> findByUserTypeId(String userTypeId);
}
