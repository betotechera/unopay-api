package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.UserParams;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class UserByFields implements Specification<UserDetail>{

    private UserParams user;

    public UserByFields(UserParams user){
        this.user = user;
    }

    @Override
    public Predicate toPredicate(Root<UserDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        HashMap<String, String> simpleFields = new HashMap<String, String>(){{
            put("name", user.getName());
            put("email", user.getEmail());
        }};
        List<Predicate> predicates = simpleFields.entrySet().stream()
                                    .filter(pair -> pair.getValue() != null)
                                    .map(pair -> cb.equal(root.get(pair.getKey()), pair.getValue()))
                                    .collect(Collectors.toList());
        if( user.getGroupName() != null) {
            Join<UserDetail, Group> groups = root.join("groups");
            predicates.add(cb.equal(groups.get("name"), user.getGroupName()));
        }

        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }
}
