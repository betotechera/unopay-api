package br.com.unopay.api.infra;

import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.lang.reflect.Field;
import java.util.stream.Stream;

import static br.com.unopay.api.uaa.exception.Errors.CANNOT_INVOKE_TYPE;

public class ReflectionHelper {

    public static Object invokeAttributeOfType(Class<?> clazz, Object object) {
        return Stream.of(object.getClass().getDeclaredFields())
                .filter(f -> f.getType() == clazz)
                .map(f -> getValue(object, f))
                .findFirst()
                .orElseThrow(()->
                        UnovationExceptions
                                .forbidden().withErrors(CANNOT_INVOKE_TYPE
                                .withOnlyArguments(clazz.getSimpleName(),
                                        object.getClass().getSimpleName())));
    }

    private static Object getValue(Object object, Field field) {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
