package br.com.unopay.api.pamcary.translate;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class KeyFieldValueResolver {

    private KeyFieldValueResolver(){}

    public static String getFieldValue(Field field, Object object){
        try {
            field.setAccessible(true);
            Object objectValue = field.get(object);
            if(objectValue != null && field.getType().isEnum()){
                return enumExtract(objectValue, field);
            }
            if(field.getType() == Date.class && field.isAnnotationPresent(KeyDate.class)){
                return formatDate(field, objectValue);
            }
            String methodResolver = field.getAnnotation(KeyField.class).methodResolver();
            if(!StringUtils.isEmpty(methodResolver)){
                return ReflectionHelper.invokeGetter(object, methodResolver);
            }
            return objectValue == null? null : String.valueOf(objectValue);
        } catch (IllegalAccessException e) {
            log.warn("could not get field value", e);
            return  null;
        }
    }

    private static String formatDate(Field field, Object objectValue) {
        String pattern = field.getAnnotation(KeyDate.class).pattern();
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(objectValue);
    }

    @SneakyThrows
    private static String enumExtract(Object object, Field field) {
        String methodName = field.getAnnotation(KeyEnumField.class).reverseMethodName();
        if(StringUtils.isEmpty(methodName)){
            return ((Enum) object).name();
        }
        return ReflectionHelper.invokeGetter(object, methodName);
    }
}
