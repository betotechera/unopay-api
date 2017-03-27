package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.repository.UnovationJpaSpecificationExecutor;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.filter.UserFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface UserDetailRepository extends CrudRepository<UserDetail,String>, UnovationJpaSpecificationExecutor<UserDetail, UserFilter> {

    UserDetail findByEmail(String email);
    UserDetail findById(String id);
    Page<UserDetail> findByGroupsId(String id, Pageable pageable);
    Set<UserDetail> findByIdIn(Set<String> usersIds);

    Long countByPaymentRuleGroupId(String id);

    Long countByInstitutionId(String id);
}
