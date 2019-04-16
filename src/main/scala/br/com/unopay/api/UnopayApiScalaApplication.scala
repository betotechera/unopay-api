package br.com.unopay.api

import br.com.unopay.bootcommons.CommonsAutoConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity

@EnableCaching
@SpringBootApplication(scanBasePackageClasses = Array(classOf[UnopayScala], classOf[CommonsAutoConfig]))
@EnableGlobalMethodSecurity(securedEnabled = true, proxyTargetClass = true, prePostEnabled = true)
class UnopayScala

object UnopayApiScalaApplication{
  def main(args: Array[String]) {
    SpringApplication.run(classOf[UnopayScala], args:_*)
  }
}
