package br.com.unopay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Updatable<T extends Updatable> {

    String[] IGNORED_FIELDS = { "version" };
    String FIELD_ID = "id";

    @JsonIgnore
    default String[] myNullFields(){
        Field[] attributes =  getClass().getDeclaredFields();
        Stream.of(attributes).forEach(f -> f.setAccessible(true));
        return Stream.of(attributes)
                .filter(field -> isReferenceWithoutIdValue(field, this) || isNullField(field, this))
                .map(Field::getName)
                .toArray(String[]::new);
    }

    @SneakyThrows
    static boolean isNullField(Field field, Object object){
        return field.get(object) == null;
    }

    @SneakyThrows
    static boolean isReferenceWithoutIdValue(Field field, Object sourceObject){
        Object reference = field.get(sourceObject);
        if(reference != null) {
            Class<?> referenceClass = reference.getClass();
            List<Field> result = Stream.of(referenceClass.getDeclaredFields())
                    .filter(f -> FIELD_ID.equals(f.getName()))
                    .collect(Collectors.toList());
            if(!result.isEmpty()) {
                result.get(0).setAccessible(true);
                boolean isNullId  = result.get(0).get(reference) == null;
                return isNullId;
            }
        }
        return false;
    }

    default void updateMe(T source){
        BeanUtils.copyProperties(source, this, ArrayUtils.addAll(source.myNullFields(), IGNORED_FIELDS));
    }
}
