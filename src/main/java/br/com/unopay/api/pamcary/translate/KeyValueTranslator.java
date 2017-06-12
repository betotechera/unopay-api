package br.com.unopay.api.pamcary.translate;

import br.com.unopay.api.pamcary.transactional.FieldTO;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

    private ConcurrentMap<String,Object> populateMap;

    public List<FieldTO> extractFields(Object object){
        Map<String, Object> translate = extract(object);
        return translate.entrySet().stream()
                .map( entry -> new FieldTO() {{ setKey(entry.getKey()); setValue(String.valueOf(entry.getValue()));}})
                .collect(Collectors.toList());
    }

    public <T> T populate(Class<T> klass, List<FieldTO> fieldTOS){
        Map<String, String> map = fieldTOS.stream().collect(Collectors.toMap(FieldTO::getKey, FieldTO::getValue));
        return populate(klass, map);
    }

    public Map<String, Object> extract(Object objectWithKeyAnnotation)  {
        return Stream.of(objectWithKeyAnnotation.getClass().getDeclaredFields())
                .filter(field -> isAnnotationPresent(field, objectWithKeyAnnotation))
                .map(field -> getMap(objectWithKeyAnnotation, field))
                .flatMap(m -> m.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey,Entry::getValue));
    }

    @SneakyThrows
    public <T> T populate(Class<T> klass, Map<String, String> map){
        T object = klass.newInstance();
        populateMap = new ConcurrentHashMap<>();
        map.entrySet().forEach(entry -> populateAnnotatedFields(object, entry));
        populateMap = null;
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
        if (field.isAnnotationPresent(KeyField.class) && containsKeyValue(entry, field)) {
            if(field.getType() != String.class) {
                populateComplexField(object, entry, field);
                return;
            }
            field.set(object, entry.getValue());
        }
        if(field.isAnnotationPresent(WithKeyFields.class)){
            populateReferencedField(object, entry, field);
        }
    }

    @SneakyThrows
    private void populateReferencedField(Object object, Entry entry, Field field) {
        if(field.getType() == List.class){
            populateList(object, entry, field);
            return;
        }
        Object reference = field.get(object);
        if(reference == null) {
            reference = field.getType().newInstance();
        }
        populateAnnotatedFields(reference, entry);
        invokeSetter(object, field, reference);
    }

    @SneakyThrows
    private void populateComplexField(Object object, Entry entry, Field field) {
        if(field.getType().isEnum() && field.isAnnotationPresent(KeyEnumField.class)){
            Object enumObj = getEnum(entry, field);
            field.set(object, enumObj);
            return;
        }
        if(!field.getType().isEnum()) {
            Method parseMethod = field.getType().getMethod("valueOf", String.class);
            field.set(object, parseMethod.invoke(field, entry.getValue()));
        }
        return;
    }

    @SneakyThrows
    private Object getEnum(Entry entry, Field field) {
        String methodName = field.getAnnotation(KeyEnumField.class).valueOfMethodName();
        Class methodParamType = field.getAnnotation(KeyEnumField.class).methodParamType();
        Method parseMethod = field.getType().getMethod(methodName, methodParamType);
        return parseMethod.invoke(null, entry.getValue());
    }

    private boolean containsKeyValue(Entry entry, Field field) {
        String key = entry.getKey().toString().replaceAll("\\d", "");
        return Objects.equals(getKey(field), key);
    }

    @SneakyThrows
    private void populateList(Object object,Entry entry, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(object);
        if(containsNumber(entry.getKey().toString())) {
            if (fieldValue == null) {
                invokeSetter(object, field, new ArrayList<>());
                fieldValue = field.get(object);
            }
            Class aClass = field.getAnnotation(WithKeyFields.class).listType();
            Object cachedObject = populateMap.get(getPopulateKey(entry, aClass));
            Boolean containsThisKey = Stream.of(aClass.getDeclaredFields())
                    .anyMatch( f -> f.isAnnotationPresent(KeyField.class) && containsKeyValue(entry, f));
            if(containsThisKey) {
                if (cachedObject == null) {
                    cachedObject = aClass.newInstance();
                    populateMap.put(getPopulateKey(entry, aClass), cachedObject);
                    ((List) fieldValue).add(cachedObject);
                }
                populateAnnotatedFields(cachedObject, entry);
            }
        }
    }

    private String getPopulateKey(Entry entry, Class klass) {
        Matcher matcher = getMatcher(entry.getKey().toString());
        matcher.find();
        String group =  matcher.group(0);
        return getMapKey(klass, group);
    }

    private String getMapKey(Class aClass, String group) {
        return group + aClass.getSimpleName();
    }

    private boolean containsNumber(String value){
        Matcher matcher = getMatcher(value);
        return matcher.find();
    }

    private Matcher getMatcher(String value) {
        Pattern pattern = Pattern.compile("\\d");
        return pattern.matcher(value);
    }

    @SneakyThrows
    private void invokeSetter(Object object, Field field, Object reference)  {
        String methodName = "set" + StringUtils.capitalize(field.getName());
        Method setMethod = object.getClass().getMethod(methodName, field.getType());
        setMethod.invoke(object, reference);
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
