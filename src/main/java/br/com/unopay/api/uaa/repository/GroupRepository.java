package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface GroupRepository extends CrudRepository<Group,String> {

    Group findById(String id);

    Page<Group> findAll(Pageable pageable);
}
