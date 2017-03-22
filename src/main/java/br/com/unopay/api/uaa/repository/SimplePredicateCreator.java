package br.com.unopay.api.uaa.repository;

import br.com.unopay.api.uaa.model.UserDetail;
import org.springframework.stereotype.Component;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class SimplePredicateCreator {

    public <T> Predicate create(Root<T> root, CriteriaBuilder cb, HashMap<String, String> simpleFields) {
        List<Predicate> predicates = simpleFields.entrySet().stream()
                .filter(pair -> pair.getValue() != null)
                .map(pair -> create(pair, cb, root))
                .collect(Collectors.toList());
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private <T> Predicate create(Map.Entry<String, String> pair, CriteriaBuilder cb, Root<T> root){
        Pattern r = Pattern.compile("(\\w+)\\.(\\w+)");
        Matcher m = r.matcher(pair.getKey());
        if(m.matches()){
            Join<T, Object> groups = root.join(m.group(1));
            return cb.equal(groups.get(m.group(2)), pair.getValue());
        }
        return cb.equal(root.get(pair.getKey()), pair.getValue());
    }
}
