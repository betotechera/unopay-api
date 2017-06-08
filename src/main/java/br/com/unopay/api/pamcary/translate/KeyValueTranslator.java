package br.com.unopay.api.pamcary.translate;

import br.com.unopay.api.model.TravelDocument;
import br.com.unopay.api.pamcary.transactional.FieldTO;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KeyValueTranslator {

    public List<FieldTO> translateToFieldTO(Object object){
        Map<String, Object> translate = translate(object);
        return translate.entrySet().stream()
                .map( entry -> new FieldTO() {{ setKey(entry.getKey()); setValue(String.valueOf(entry.getValue()));}})
                .collect(Collectors.toList());
    }

    public TravelDocument translateTravelDocument(List<FieldTO> fieldTOS){
        Map<String, String> map = fieldTOS.stream().collect(Collectors.toMap(FieldTO::getKey, FieldTO::getValue));
        return translate(new TravelDocument(), map);
    }

    public Map<String, Object> translate(Object objectWithKeyAnnotation)  {
        return Stream.of(objectWithKeyAnnotation.getClass().getDeclaredFields())
                .filter(field -> isAnnotationPresent(field, objectWithKeyAnnotation))
                .map(field -> getMap(objectWithKeyAnnotation, field))
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey,Entry::getValue));
    }

    public <T> T translate(T object, Map<String, String> map){
        map.entrySet().forEach(entry -> populateAnnotatedFields(object, entry));
        return object;
    }

    private void populateAnnotatedFields(Object object, Entry entry) {
        Stream.of(object.getClass().getDeclaredFields())
                .filter(field -> isAnnotationPresent(field, object))
                .forEach(field -> populateField(object, entry, field));
    }

    @SneakyThrows
    private void populateField(Object object, Entry entry, Field field) {
        field.setAccessible(true);
        if (!field.isAnnotationPresent(WithKeyFields.class) && Objects.equals(getKey(field), entry.getKey())) {
            if(field.getType() != String.class) {
                Method parseMethod = field.getType().getMethod("valueOf", String.class);
                field.set(object, parseMethod.invoke(field, entry.getValue()));
                return;
            }
            field.set(object, entry.getValue());
        }
        if(field.isAnnotationPresent(WithKeyFields.class)){
            Object reference = field.getType().newInstance();
            populateAnnotatedFields(reference, entry);
            String methodName = "set" + StringUtils.capitalize(field.getName());
            Method setMethod = object.getClass().getMethod(methodName, field.getType());
            setMethod.invoke(object, reference);
        }
    }

    private boolean isAnnotationPresent(Field field, Object object) {
        boolean annotationFieldPresent = field.isAnnotationPresent(KeyField.class);
        Optional<Pair<Object, List<Field>>> referencedFieldAnnotated = referencedFieldAnnotated(field, object);
        boolean annotationReferencePresent = field.isAnnotationPresent(WithKeyFields.class);
        return annotationFieldPresent || annotationReferencePresent || referencedFieldAnnotated.isPresent();
    }

    @SneakyThrows
    private Optional<Pair<Object, List<Field>>> referencedFieldAnnotated(Field field, Object object) {
        field.setAccessible(true);
        Object o = field.get(object);
        if(o != null){
            List<Field> fields = Stream.of(o.getClass().getDeclaredFields())
                    .filter(f -> f.isAnnotationPresent(KeyField.class)).collect(Collectors.toList());
            if(fields.isEmpty()){
                return Optional.empty();
            }
            return Optional.of(new ImmutablePair<>(o, fields));
        }
        return Optional.empty();
    }

    private Map<String, Object> getMap(final Object object, final Field field) {
        Map<String, Object> map = new HashMap<>();
        Optional<Pair<Object, List<Field>>> referencedFieldAnnotated = referencedFieldAnnotated(field, object);
        if(!referencedFieldAnnotated.isPresent()) {
            String fieldValue = getFieldValue(field, object);
            if(fieldValue != null) {
                map.put(getKey(field), fieldValue);
            }
        }
        referencedFieldAnnotated.ifPresent(pair -> pair.getValue().forEach(f ->
            map.putAll(getMap(pair.getKey(), f))
        ));
        return map;
    }

    private String getKey(Field field){
        Annotation annotation = field.getAnnotation(KeyField.class);
        return  ((KeyField) annotation).key();
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
