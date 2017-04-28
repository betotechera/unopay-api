package br.com.unopay.api.repository.filter;

import br.com.unopay.api.model.Period;
import br.com.unopay.bootcommons.exception.UnprocessableEntityException;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*; // NOSONAR
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Filter<T> implements Specification<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Filter.class);

    private Class<?> searchableType;
    private Object fields;
    private CriteriaBuilder cb;
    private Root<T> root;

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
        if(split.length > 1){
            return createJoinPredicate(pair,split);
        }
        return addOperator(root.get(pair.getKey()), pair.getValue());
    }

    private <T> Predicate createJoinPredicate(Map.Entry<String, Object> pair, String... fields) {
        if(fields.length == 2){
            Join<T, Object> groups = root.join(fields[0]);
            return addOperator(groups.get(fields[1]), pair.getValue());
        }
        if(fields.length == 3){
            Join<T, Object> first = root.join(fields[0]);
            Join<T, Object> second = first.join(fields[1]);
            return addOperator(second.get(fields[2]), pair.getValue());
        }
        throw new UnprocessableEntityException("Invalid filter join length: "+fields.length);

    }

    private Predicate addOperator(Path key, Object value) {
        if(value instanceof Period){
            return createPeriodBetween(key, value);
        }
        if(value instanceof Enum){
            return cb.equal(key, value);
        }
        return cb.like(key, "%" + value + "%");
    }

    @SneakyThrows
    private <T> Predicate createPeriodBetween(Path key, Object value){
        Period period = (Period) value;
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
