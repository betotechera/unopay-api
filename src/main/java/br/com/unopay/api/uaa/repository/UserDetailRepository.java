package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.filter.UserFilter;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserDetailRepository extends UnovationFilterRepository<UserDetail,String, UserFilter> {

    Optional<UserDetail> findByEmail(String email);
    UserDetail findById(String id);
    Page<UserDetail> findByGroupsId(String id, Pageable pageable);
    Set<UserDetail> findByIdIn(Set<String> usersIds);

    Long countByIssuerId(String id);

    Long countByInstitutionId(String id);

    int countByAccreditedNetworkId(String id);

    int countByEstablishmentId(String id);

    int countByHirerId(String id);

    int countByContractorId(String id);

    int countByPartnerId(String id);


}
