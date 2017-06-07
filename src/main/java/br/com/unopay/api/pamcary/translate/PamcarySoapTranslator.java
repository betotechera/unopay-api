package br.com.unopay.api.pamcary.translate;

import br.com.unopay.api.model.TravelDocument;
import br.com.unopay.api.pamcary.transactional.FieldTO;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PamcarySoapTranslator {

    public List<FieldTO> translate(TravelDocument travelDocument)  {
        return Stream.of(travelDocument.getClass().getDeclaredFields())
                .filter(field -> isAnnotationPresent(field, travelDocument))
                .map(field -> getFieldTO(travelDocument, field))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private boolean isAnnotationPresent(Field field, Object object) {
        boolean annotationPresent = field.isAnnotationPresent(PamcaryField.class);
        Optional<Pair<Object, List<Field>>> referencedFieldAnnotated = referencedFieldAnnotated(field, object);
        return annotationPresent || referencedFieldAnnotated.isPresent();
    }

    @SneakyThrows
    private Optional<Pair<Object, List<Field>>> referencedFieldAnnotated(Field field, Object object) {
        field.setAccessible(true);
        Object o = field.get(object);
        if(o != null){
            List<Field> fields = Stream.of(o.getClass().getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(PamcaryField.class)).collect(Collectors.toList());
            if(fields.isEmpty()){
                return Optional.empty();
            }
            return Optional.of(new ImmutablePair<>(o, fields));
        }
        return Optional.empty();
    }

    private List<FieldTO> getFieldTO(final Object object, final Field field) {
        List<FieldTO> fieldTOS = new ArrayList<>();
        Optional<Pair<Object, List<Field>>> referencedFieldAnnotated = referencedFieldAnnotated(field, object);
        if(!referencedFieldAnnotated.isPresent()) {
            String fieldValue = getFieldValue(field, object);
            if(fieldValue != null) {
                FieldTO fieldTO = new FieldTO();
                fieldTO.setKey(getKeys(field));
                fieldTO.setValue(fieldValue);
                fieldTOS.add(fieldTO);
            }
        }
        referencedFieldAnnotated.ifPresent(pair -> pair.getValue().forEach(f ->
            fieldTOS.addAll(getFieldTO(pair.getKey(), f))
        ));
        return fieldTOS;
    }

    private String getKeys(Field field){
        Annotation annotation = field.getAnnotation(PamcaryField.class);
        return  ((PamcaryField) annotation).key();
    }

    private String getFieldValue(Field field, Object object){
        try {
            field.setAccessible(true);

            Object objectValue = field.get(object);
            if(objectValue != null && field.getType().isEnum()){
                return ((Enum) objectValue).name();
            }
            return objectValue == null? null : String.valueOf(objectValue);
        } catch (IllegalAccessException e) {
            log.warn("could not get field value", e);
            return  null;
        }
    }
}
