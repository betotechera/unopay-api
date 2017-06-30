package br.com.unopay.api

import br.com.six2six.fixturefactory.processor.Processor
import br.com.unopay.api.bacen.model.Bank
import br.com.unopay.api.uaa.model.Authority
import groovy.util.logging.Slf4j
import org.apache.commons.lang3.text.WordUtils
import org.springframework.context.annotation.Scope
import org.springframework.context.annotation.ScopedProxyMode
import org.springframework.stereotype.Component

import javax.persistence.Embeddable
import javax.persistence.EntityManager
import javax.persistence.Id
import javax.persistence.PersistenceContext
import javax.transaction.Transactional

@Slf4j
@Component
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
class JpaProcessor implements Processor{

    @PersistenceContext
    EntityManager entityManager

    @Override
    void execute(Object object) {
        if(object.getClass().isAnnotationPresent(Embeddable.class)) return
        try {
            if(Bank.class == object.class) return
            if(Authority.class == object.class) return

            def field = object.getClass().getDeclaredFields().find { it.isAnnotationPresent(Id)}
            def method = object.class.getMethod("set" + WordUtils.capitalize(field.getName()), String.class)
            if(method != null){
                method.invoke(object, (Object[])[null])
            }
            def fields = object.class.getDeclaredFields()
            if(fields.any { it.setAccessible(true); it.name == 'code' }) {
                def code = fields.find { it.name == 'code' }
                def createQuery = entityManager
                        .createQuery("from ${object.class.getSimpleName()} where code = '${code.get(object)}'")
                def result = createQuery.getResultList()
                if(result) {
                    object = createQuery.getResultList().find()
                    entityManager.merge(object)
                    return
                }
            }
            entityManager.persist(object)
        } catch (Exception e) {
            log.error("Jpa processor error with class={} message={}", object.getClass().getName(), e.getMessage())
        }

    }
}
