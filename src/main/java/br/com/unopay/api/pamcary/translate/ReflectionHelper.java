package br.com.unopay.api.pamcary.translate;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

public class ReflectionHelper {

    public static Stream<Field> getDeclaredFields(Object object) {
        return Stream.of(object.getClass().getDeclaredFields());
    }

    @SneakyThrows
    public static String invokeGetter(Object object, String methodName) {
        Method parseMethod = object.getClass().getMethod(methodName);
        return (String) parseMethod.invoke(object);
    }

    @SneakyThrows
    public static void invokeSetter(Object object, Field field, Object reference)  {
        String methodName = "set" + StringUtils.capitalize(field.getName());
        Method setMethod = object.getClass().getMethod(methodName, field.getType());
        setMethod.invoke(object, reference);
    }
}
