package br.com.unopay.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public interface Updatable<T extends Updatable> {

    @JsonIgnore
    default String[] myNullFields(){
        Field[] attributes =  getClass().getDeclaredFields();
        return Stream.of(attributes).filter(f -> isNullField(f, this)).map(Field::getName).toArray(String[]::new);
    }

    @SneakyThrows
    static boolean isNullField(Field field, Object object){
        field.setAccessible(true);
        return field.get(object) == null;
    }

    default void updateMe(T source){
        BeanUtils.copyProperties(source, this, source.myNullFields());
    }
}
