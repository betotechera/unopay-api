package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.GroupMember;
import org.springframework.data.repository.CrudRepository;

public interface GroupMemberRepository extends CrudRepository<GroupMember,String> {
}
