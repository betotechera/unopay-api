package br.com.unopay.api.repository;

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
                .filter(f -> f.isAnnotationPresent(SearchableField.class))
                .filter(f -> Objects.nonNull(getFieldValue(f)))
                .collect(Collectors.toMap(this::getField, this::getFieldValue));
    }

    private <T> Predicate create(Root<T> root, CriteriaBuilder cb, Map<String, String> simpleFields) {
        List<Predicate> predicates = simpleFields.entrySet().stream()
                .filter(pair -> pair.getValue() != null)
                .map(pair -> createAndPredicate(pair, cb, root))
                .collect(Collectors.toList());
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private <T> Predicate createAndPredicate(Map.Entry<String, String> pair, CriteriaBuilder cb, Root<T> root){
        Pattern r = Pattern.compile("(\\w+)\\.(\\w+)");
        Matcher m = r.matcher(pair.getKey());
        if(m.matches()){
            Join<T, Object> groups = root.join(m.group(1));
            return cb.equal(groups.get(m.group(2)), pair.getValue());
        }
        return cb.equal(root.get(pair.getKey()), pair.getValue());
    }

    private String getField(Field field){
        Annotation annotation = field.getAnnotation(SearchableField.class);
        SearchableField searchableField = (SearchableField) annotation;
        return  Objects.equals(searchableField.field(), "") ? field.getName() : searchableField.field();
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
