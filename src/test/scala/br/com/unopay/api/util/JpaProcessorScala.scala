package br.com.unopay.api.util

import br.com.six2six.fixturefactory.processor.Processor
import br.com.unopay.api.bacen.model.Bank
import br.com.unopay.api.uaa.model.Authority
import javax.persistence.{Embeddable, EntityManager, Id, PersistenceContext}
import javax.transaction.Transactional
import org.apache.commons.lang3.text.WordUtils
import org.springframework.context.annotation.{Scope, ScopedProxyMode}
import org.springframework.stereotype.Component

import scala.collection.JavaConverters._

@Component
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
class JpaProcessorScala extends Processor{

  @PersistenceContext
  var entityManager: EntityManager = _

  @Override
  def execute(obj: Object): Unit = {
    if (obj.getClass.isAnnotationPresent(classOf[Embeddable])) return
    try {
      if (classOf[Bank] == obj.getClass) return
      if (classOf[Authority] == obj.getClass) return

      val field = obj.getClass.getDeclaredFields.find(it => it.isAnnotationPresent(classOf[Id]))

      val method = obj.getClass.getMethod("set" + WordUtils.capitalize(field.get.getName), classOf[String])

      if (method != null) {
        method.invoke(obj, null)
      }

      val fields = obj.getClass.getDeclaredFields

      if (fields.exists(it => {
        it.setAccessible(true); it.getName == "code"
      })) {
        val code = fields.find(it => it.getName == "code").orNull

        val createQuery = entityManager
          .createQuery(s"from ${obj.getClass.getSimpleName} where code = '${code.get(obj)}'")

        val result = createQuery.getResultList

        if (!result.isEmpty) {
          entityManager.merge(createQuery.getResultList.asScala.head.asInstanceOf[Object])
          return
        }
      }
      entityManager.persist(obj)
    } catch {
      case e: Exception => println("Got some other kind of exception" + e.getMessage)
      case rt: RuntimeException => println("Got some other kind of exception" + rt.getMessage)
      case _: Throwable => println("Got some other kind of exception")
    }
  }

}
