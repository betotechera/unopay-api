package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.UserParams;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashMap;

public class UserByFields implements Specification<UserDetail>{

    private UserParams user;
    private SimplePredicateCreator predicateCreator;

    public UserByFields(UserParams user, SimplePredicateCreator predicateCreator){
        this.user = user;
        this.predicateCreator = predicateCreator;
    }

    @Override
    public Predicate toPredicate(Root<UserDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        HashMap<String, String> simpleFields = new HashMap<String, String>(){{
            put("name", user.getName());
            put("email", user.getEmail());
            put("groups.name", user.getGroupName());
        }};
        return predicateCreator.create(root, cb, simpleFields);
    }
}
