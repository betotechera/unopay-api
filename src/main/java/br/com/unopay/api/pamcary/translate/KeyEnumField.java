package br.com.unopay.api.pamcary.translate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface KeyEnumField {

    String valueOfMethodName() default "valueOf";

    Class methodParamType() default String.class;


}
