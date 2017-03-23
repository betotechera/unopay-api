package br.com.unopay.api.repository;

import com.google.common.base.Strings;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Filter<T> implements Specification<T> {

    private Class<?> searchableType;
    private Object fields;

    public Filter(Object fields){
        this.searchableType = fields.getClass();
        this.fields = fields;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return create(root, cb, searchableFieldsToMap());
    }

    private Map<String, String> searchableFieldsToMap()  {
        return Stream.of(searchableType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(SearchableField.class))
                .filter(field -> Objects.nonNull(getFieldValue(field)))
                .collect(Collectors.toMap(this::getFieldName, this::getFieldValue));
    }

    private <T> Predicate create(Root<T> root, CriteriaBuilder cb, Map<String, String> simpleFields) {
        List<Predicate> predicates = simpleFields.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> createAndPredicate(entry, cb, root))
                .collect(Collectors.toList());
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private <T> Predicate createAndPredicate(Map.Entry<String, String> pair, CriteriaBuilder cb, Root<T> root){
        Pattern pattern = Pattern.compile("(\\w+)\\.(\\w+)");
        Matcher matcher = pattern.matcher(pair.getKey());
        if(matcher.matches()){
            Join<T, Object> groups = root.join(matcher.group(1));
            return cb.equal(groups.get(matcher.group(2)), pair.getValue());
        }
        return cb.equal(root.get(pair.getKey()), pair.getValue());
    }

    private String getFieldName(Field field){
        Annotation annotation = field.getAnnotation(SearchableField.class);
        SearchableField searchableField = (SearchableField) annotation;
        return Strings.isNullOrEmpty(searchableField.field()) ? field.getName() : searchableField.field();
    }

    private String getFieldValue(Field field){
        try {
            field.setAccessible(true);
            return (String) field.get(fields);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  null;
    }

}
