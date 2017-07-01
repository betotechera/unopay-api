package br.com.unopay.api.repository.filter;

import br.com.unopay.api.model.Period;
import br.com.unopay.bootcommons.exception.UnprocessableEntityException;
import com.google.common.base.Strings;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

public class Filter<T> implements Specification<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Filter.class);

    private Class<?> searchableType;
    private Object fields;
    private CriteriaBuilder cb;
    private Root<T> root;

    private static final int JOIN_LEVEL_ONE_SIZE = 2;
    private static final int JOIN_LEVEL_TWO_SIZE = 3;
    private static final int JOIN_LEVEL_TREE_SIZE = 4;
    private static final int SOURCE_FIELD_INDEX = 0;
    private static final int FIRST_JOIN_FIELD_INDEX = 1;
    private static final int SECOND_JOIN_FIELD_INDEX = 2;
    private static final int TREE_JOIN_FIELD_INDEX = 3;
    private static final int MINIMUM_JOIN_SIZE = 2;

    public Filter(Object fields){
        this.searchableType = fields.getClass();
        this.fields = fields;
    }

    private Filter(CriteriaBuilder cb, Root<T> root){
        this.cb = cb;
        this.root = root;
    }

    @Override
    public Predicate toPredicate(Root<T> rootParam, CriteriaQuery<?> query, CriteriaBuilder cbParam) {
        return new Filter<>(cbParam, rootParam).createValidPredicate(searchableFieldsToMap());
    }

    private Map<String, Object> searchableFieldsToMap()  {
        return Stream.of(searchableType.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(SearchableField.class))
                .filter(field -> Objects.nonNull(getFieldValue(field)))
                .collect(Collectors.toMap(this::getFieldName, this::getFieldValue));
    }

    private <T> Predicate createValidPredicate(Map<String, Object> simpleFields) {
        List<Predicate> predicates = simpleFields.entrySet().stream()
                .filter(entry -> Objects.nonNull(entry.getValue()))
                .map(this::createPredicate)
                .collect(Collectors.toList());
        return cb.and(predicates.toArray(new Predicate[predicates.size()]));
    }

    private Predicate createPredicate(Map.Entry<String, Object> pair){
        String[] split = pair.getKey().split("\\.");
        if(split.length >= MINIMUM_JOIN_SIZE){
            return createJoinPredicate(pair,split);
        }
        return addOperator(root.get(pair.getKey()), pair.getValue());
    }

    private <T> Predicate createJoinPredicate(Map.Entry<String, Object> pair, String... fields) {
        if(fields.length == JOIN_LEVEL_ONE_SIZE){
            Join<T, Object> join = root.join(fields[SOURCE_FIELD_INDEX]);
            return addOperator(join.get(fields[FIRST_JOIN_FIELD_INDEX]), pair.getValue());
        }
        if(fields.length == JOIN_LEVEL_TWO_SIZE){
            Join<T, Object> firstJoin = root.join(fields[SOURCE_FIELD_INDEX]);
            Join<T, Object> secondJoin = firstJoin.join(fields[FIRST_JOIN_FIELD_INDEX]);
            return addOperator(secondJoin.get(fields[SECOND_JOIN_FIELD_INDEX]), pair.getValue());
        }
        if(fields.length == JOIN_LEVEL_TREE_SIZE){
            Join<T, Object> firstJoin = root.join(fields[SOURCE_FIELD_INDEX]);
            Join<T, Object> secondJoin = firstJoin.join(fields[FIRST_JOIN_FIELD_INDEX]);
            Join<T, Object> treeJoin = secondJoin.join(fields[SECOND_JOIN_FIELD_INDEX]);
            return addOperator(treeJoin.get(fields[TREE_JOIN_FIELD_INDEX]), pair.getValue());
        }
        throw new UnprocessableEntityException(String.format("Invalid filter join length: %s",fields.length));
    }

    private Predicate addOperator(Path key, Object value) {
        if(value instanceof Period){
            return createPeriodBetween(key, value);
        }
        if(value instanceof Enum || value instanceof Integer){
            return cb.equal(key, value);
        }
        if(value instanceof Collection){
            return cb.isMember(value,key);
        }

        return cb.like(cb.lower(key), ("%" + value + "%").toLowerCase());
    }

    @SneakyThrows
    private <T> Predicate createPeriodBetween(Path key, Object value){
        Period period = (Period) value;
        if(period.getBegin() !=null && period.getEnd() == null)
           return cb.greaterThanOrEqualTo(key,period.getBegin());
        if(period.getEnd() !=null && period.getBegin() == null)
            return cb.lessThanOrEqualTo(key,period.getEnd());
        return cb.between(key,period.getBegin(),period.getEnd());
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
