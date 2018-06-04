package br.com.unopay.api.util

import org.slf4j.LoggerFactory

trait Logging {
  lazy val log = LoggerFactory.getLogger(getClass.getName)
}
