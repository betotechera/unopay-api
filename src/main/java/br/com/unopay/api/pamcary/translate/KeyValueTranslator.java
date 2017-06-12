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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
                .filter(this::isAnnotationPresent)
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
                .filter(this::isAnnotationPresent)
                .forEach(field -> populateField(object, entry, field));
    }

    @SneakyThrows
    private void populateField(Object object, Entry entry, Field field) {
        field.setAccessible(true);
        if (field.isAnnotationPresent(KeyField.class) && containsKeyValue(entry, field, object)) {
            if(field.getType() != String.class) {
                populateComplexField(object, entry, field);
                return;
            }
            field.set(object, entry.getValue());
        }
        if(isReferencedField(field)){
            populateReferencedField(object, entry, field);
        }
    }

    private boolean isReferencedField(Field field) {
        return field.isAnnotationPresent(KeyFieldReference.class) ||
                field.isAnnotationPresent(KeyFieldListReference.class);
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

    @SneakyThrows
    private boolean containsKeyValue(Entry entry, Field field, Object object) {
        String key = entry.getKey().toString().replaceAll("\\d", "");
        return Objects.equals(getKey(field, object), key);
    }

    @SneakyThrows
    private void populateList(Object object,Entry entry, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(object);
        if(containsNumber(entry.getKey().toString())) {
            if (fieldValue == null) {
                invokeSetter(object, field, new ArrayList<>());
                fieldValue = field.get(object);
            }
            Class aClass = getListType(field);
            Object cachedObject = populateMap.get(getPopulateKey(entry, aClass));
            if (cachedObject == null) {
                cachedObject = aClass.newInstance();
            }
            Object finalCachedObject = cachedObject;
            Boolean containsThisKey = Stream.of(aClass.getDeclaredFields())
                    .anyMatch( f -> f.isAnnotationPresent(KeyField.class) && containsKeyValue(entry, f, finalCachedObject));
            if(containsThisKey) {
               if(populateMap.get(getPopulateKey(entry, aClass)) == null ) {
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

    private boolean isAnnotationPresent(Field field) {
        return field.isAnnotationPresent(KeyField.class) || isReferencedField(field);
    }

    @SneakyThrows
    private Map<String, Object> getMap(final Object object, final Field field) {
        Map<String, Object> map = new HashMap<>();
        if(field.isAnnotationPresent(KeyFieldListReference.class)) {
            return extractList(object, field, map);
        }
        if(field.isAnnotationPresent(KeyFieldReference.class)) {
            field.setAccessible(true);
            Object o = field.get(object);
            Stream.of(o.getClass().getDeclaredFields()).forEach(f->
                    map.putAll(getMap(o,f))
            );
            return map;
        }
        String fieldValue = getFieldValue(field, object);
        if(fieldValue != null) {
            map.put(getKey(field, object), fieldValue);
        }
        return map;
    }

    private Map<String, Object> extractList(Object object, Field field, Map<String, Object> map) throws IllegalAccessException {
        if(field.getType() == List.class){
            field.setAccessible(true);
            List<Object> list = (List<Object>) field.get(object);
            if(list == null){
                return map;
            }
            final int[] count = {1};
            list.forEach(e ->{
                    Stream.of(e.getClass().getDeclaredFields()).filter(this::isAnnotationPresent).forEach(f-> {
                        String keyField = f.getAnnotation(KeyField.class).field();
                        map.put(getKeyBase(field).key() + count[0] + "." + keyField, getFieldValue(f,e));
                    });
                   count[0]++;
            });
            map.put(getKeyBase(field).key() + ".qtde", list.size());
        }
        return map;
    }

    private KeyBase getKeyBase(Field field) {
        return (KeyBase) getListType(field).getAnnotation(KeyBase.class);
    }

    private Class getListType(Field field) {
        return field.getAnnotation(KeyFieldListReference.class).listType();
    }

    private String getKey(Field field, Object object){
        KeyField keyField = field.getAnnotation(KeyField.class);
        KeyBase keyBase = object.getClass().getAnnotation(KeyBase.class);
        return  String.format("%s.%s",keyBase.key(),keyField.field());
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
