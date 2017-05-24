package br.com.unopay.api

import br.com.six2six.fixturefactory.processor.Processor
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.text.WordUtils
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.persistence.Embeddable
import javax.persistence.EntityManager
import javax.persistence.Id
import javax.persistence.PersistenceContext

@Slf4j
@Component
class JpaProcessor implements Processor{

    @PersistenceContext
    EntityManager entityManager

    @PostConstruct
    void setup(){
        entityManager = entityManager.getEntityManagerFactory().createEntityManager()
    }
    @Override
    void execute(Object result) {
        if(result.getClass().isAnnotationPresent(Embeddable.class)) return
        try {
            def field = result.getClass().getDeclaredFields().find { it.isAnnotationPresent(Id)}
            def method = result.class.getMethod("set" + WordUtils.capitalize(field.getName()), String.class)
            if(method != null){
                method.invoke(result, (Object[])[null])
            }
            entityManager.persist(result)
        } catch (NoSuchMethodException | SecurityException e) {
            log.error("Jpa processor error with class={} message={}", result.getClass().getName(), e)
        }

    }
}
