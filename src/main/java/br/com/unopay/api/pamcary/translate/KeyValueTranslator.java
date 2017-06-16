package br.com.unopay.api.pamcary.translate;

import br.com.unopay.api.pamcary.transactional.FieldTO;
import static br.com.unopay.api.uaa.exception.Errors.BASE_KEY_REQUIRED;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KeyValueTranslator {

    private ConcurrentMap<String,Object> listObjectsCache;

    public KeyValueTranslator(){}

    public List<FieldTO> extractFields(Object object){
        Map<String, Object> translate = extract(object);
        return translate.entrySet().stream()
                .map( entry -> new FieldTO(entry.getKey(),String.valueOf(entry.getValue())))
                .collect(Collectors.toList());
    }

    public <T> T populate(Class<T> klass, List<FieldTO> fieldTOS){
        Map<String, String> map = fieldTOS.stream().collect(Collectors.toMap(FieldTO::getKey, FieldTO::getValue));
        return populate(klass, map);
    }

    public Map<String, Object> extract(Object objectWithKeyAnnotation)  {
        return ReflectionHelper.getDeclaredFields(objectWithKeyAnnotation)
                .filter(this::isAnnotationPresent)
                .map(field -> extractMap(objectWithKeyAnnotation, field))
                .flatMap(m -> m.entrySet().stream())
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Entry::getKey,Entry::getValue));
    }

    @SneakyThrows
    public <T> T populate(Class<T> klass, Map<String, String> map){
        T object = klass.newInstance();
        listObjectsCache = new ConcurrentHashMap<>();
        map.entrySet().forEach(entry -> populateAnnotatedFields(object, entry));
        listObjectsCache = null;
        return object;
    }

    private void populateAnnotatedFields(Object object, Entry entry) {
        ReflectionHelper.getDeclaredFields(object)
                .filter(this::isAnnotationPresent)
                .forEach(field -> populateField(object, entry, field));
    }

    @SneakyThrows
    private void populateField(Object object, Entry entry, Field field) {
        field.setAccessible(true);
        if (field.isAnnotationPresent(KeyField.class) && containsKeyValue(entry.getKey(), field, object)) {
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
        Object reference = getObject(object, field);
        populateAnnotatedFields(reference, entry);
        ReflectionHelper.invokeSetter(object, field, reference);
    }

    private Object getObject(Object object, Field field) throws IllegalAccessException, InstantiationException {
        Object reference = field.get(object);
        if(reference == null) {
            reference = field.getType().newInstance();
        }
        return reference;
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
    private boolean containsKeyValue(Object entryKey, Field field, Object object) {
        String key = entryKey.toString().replaceAll("\\d", "");
        String baseField = field.getAnnotation(KeyField.class).baseField();
        return Objects.equals(getKey(baseField, object), key);
    }

    @SneakyThrows
    private void populateList(Object object,Entry entry, Field field) throws IllegalAccessException {
        if(containsNumber(entry.getKey())) {
            List list = getList(object, field);
            Class aClass = getListType(field);
            Object listObject = getListObject(entry, aClass);
            if(objectContainsKey(listObject, entry.getKey())) {
               if(notCached(entry.getKey(), listObject)) {
                   listObjectsCache.put(getListObjectKey(entry, aClass), listObject);
                   list.add(listObject);
               }
               populateAnnotatedFields(listObject, entry);
            }
        }
    }

    private List getList(Object object, Field field) throws IllegalAccessException {
        Object fieldValue = field.get(object);
        if (fieldValue == null) {
            ReflectionHelper.invokeSetter(object, field, new ArrayList<>());
            fieldValue = field.get(object);
        }
        return ((List) fieldValue);
    }

    @SneakyThrows
    private Object getListObject(Entry entry, Class aClass) {
        Object cachedObject = listObjectsCache.get(getListObjectKey(entry, aClass));
        if (cachedObject == null) {
            cachedObject = aClass.newInstance();
        }
        return cachedObject;
    }

    private boolean objectContainsKey(final Object object, Object entryKey) {
        return ReflectionHelper.getDeclaredFields(object)
                .anyMatch(f -> f.isAnnotationPresent(KeyField.class) && containsKeyValue(entryKey, f, object));
    }

    private boolean notCached(Object entryKey, Object object) {
        Class aClass = object.getClass();
        return listObjectsCache.get(getListObjectKey(entryKey, aClass)) == null;
    }

    private String getListObjectKey(Object entryKey, Class klass) {
        Matcher matcher = getMatcher(entryKey.toString());
        matcher.find();
        String group =  matcher.group(0);
        return getMapKey(klass, group);
    }

    private String getMapKey(Class aClass, String group) {
        return group + aClass.getSimpleName();
    }

    private boolean containsNumber(Object entryKey){
        Matcher matcher = getMatcher(entryKey.toString());
        return matcher.find();
    }

    private Matcher getMatcher(String value) {
        Pattern pattern = Pattern.compile("\\d");
        return pattern.matcher(value);
    }

    private boolean isAnnotationPresent(Field field) {
        return field.isAnnotationPresent(KeyField.class) || isReferencedField(field);
    }

    @SneakyThrows
    private Map<String, Object> extractMap(final Object object, final Field field) {
        Map<String, Object> map = new HashMap<>();
        if(field.isAnnotationPresent(KeyFieldListReference.class)) {
            return extractList(object, field, map);
        }
        if(field.isAnnotationPresent(KeyFieldReference.class)) {
            return extractReference(object, field, map);
        }
        if(field.isAnnotationPresent(KeyField.class)) {
            extractKey(object, field, map);
        }
        return map;
    }

    private void extractKey(Object object, Field field, Map<String, Object> map) {
        String fieldValue = KeyFieldValueResolver.getFieldValue(field, object);
        if (fieldValue != null) {
            String baseField = getField(field);
            map.put(getKey(baseField, object), fieldValue);
        }
    }

    @SneakyThrows
    private Map<String, Object> extractReference(Object object, Field field, Map<String, Object> map)  {
        field.setAccessible(true);
        Object reference = field.get(object);
        ReflectionHelper.getDeclaredFields(reference)
                .filter(this::isAnnotationPresent).forEach(referField-> map.putAll(extractMap(reference,referField)));
        return map;
    }

    @SneakyThrows
    private Map<String, Object> extractList(Object object, Field field, Map<String, Object> map)  {
        if(field.getType() == List.class){
            field.setAccessible(true);
            List<Object> list = (List<Object>) field.get(object);
            if(list == null){
                return map;
            }
            extractWithIndexedKeys(field, map, list);
            map.put(getKeyBase(field).key() + ".qtde", list.size());
        }
        return map;
    }

    private void extractWithIndexedKeys(Field field, Map<String, Object> map, List<Object> list) {
        final int[] count = {1};
        list.forEach(object -> {
            ReflectionHelper.getDeclaredFields(object).filter(this::isAnnotationPresent).forEach(objField -> {
                String keyField = getField(objField);
                String indexedKey = String.format("%s%s.%s", getKeyBase(field).key(), count[0], keyField);
                map.put(indexedKey, KeyFieldValueResolver.getFieldValue(objField, object));
            });
            count[0]++;
        });
    }

    private String getField(Field objField) {
        KeyField annotation = objField.getAnnotation(KeyField.class);
        String reverseField = annotation.reverseField();
        return StringUtils.isEmpty(reverseField) ? annotation.baseField() : reverseField;
    }

    private KeyBase getKeyBase(Field field) {
        return (KeyBase) getListType(field).getAnnotation(KeyBase.class);
    }

    private Class getListType(Field field) {
        return field.getAnnotation(KeyFieldListReference.class).listType();
    }

    private String getKey(String baseField, Object object){
        KeyBase keyBase = object.getClass().getAnnotation(KeyBase.class);
        if(keyBase == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(BASE_KEY_REQUIRED);
        }
        return  String.format("%s.%s",keyBase.key(),baseField);
    }
}
