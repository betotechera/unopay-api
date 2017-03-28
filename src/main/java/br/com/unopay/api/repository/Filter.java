package br.com.unopay.api.repository;

import br.com.unopay.bootcommons.exception.UnovationError;
import br.com.unopay.bootcommons.exception.UnovationErrors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.exception.UnprocessableEntityException;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(Filter.class);

    private Class<?> searchableType;
    private Object fields;

    public Filter(Object fields){
        this.searchableType = fields.getClass();
        this.fields = fields;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return createValidPredicate(root, cb, searchableFieldsToMap());
    }

    private Map<String, Object> searchableFieldsToMap()  {
        return Stream.of(searchableType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(SearchableField.class))
                .filter(field -> Objects.nonNull(getFieldValue(field)))
                .collect(Collectors.toMap(this::getFieldName, this::getFieldValue));
    }

    private <T> Predicate createValidPredicate(Root<T> root, CriteriaBuilder cb, Map<String, Object> simpleFields) {
        List<Predicate> predicates = simpleFields.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(entry -> createPredicate(entry, cb, root))
                .collect(Collectors.toList());
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private <T> Predicate createPredicate(Map.Entry<String, Object> pair, CriteriaBuilder cb, Root<T> root){
        String[] split = pair.getKey().split("\\.");

        if(split.length > 1){
            return createJoinPredicate(pair, cb, root,split);
        }
        return cb.equal(root.get(pair.getKey()), pair.getValue());
    }

    private <T> Predicate createJoinPredicate(Map.Entry<String, Object> pair, CriteriaBuilder cb, Root<T> root, String... fields) {
        if(fields.length == 2){
            Join<T, Object> groups = root.join(fields[0]);
            return cb.equal(groups.get(fields[1]), pair.getValue());
        }
        if(fields.length == 3){
            Join<T, Object> first = root.join(fields[0]);
            Join<T, Object> second = first.join(fields[1]);
            return cb.equal(second.get(fields[2]), pair.getValue());
        }
        throw new UnprocessableEntityException("Invalid filter join length: "+fields.length);

    }

    private String getFieldName(Field field){
        Annotation annotation = field.getAnnotation(SearchableField.class);
        SearchableField searchableField = (SearchableField) annotation;
        return Strings.isNullOrEmpty(searchableField.field()) ? field.getName() : searchableField.field();
    }

    private Object getFieldValue(Field field){
        try {
            field.setAccessible(true);
            return field.get(fields);
        } catch (IllegalAccessException e) {
            LOGGER.warn("could not get field value", e);
            return  null;
        }
    }
}
