package br.com.unopay.api.util

import java.util

import scala.collection.JavaConverters._

object StringJoinerS {
    val DEFAULT_DELIMITER : String = ","

    def join(strings: util.Collection[String] ): String = {
        join(strings, DEFAULT_DELIMITER)
    }
     def join(strings: util.Collection[String], delimiter: String) : String = {
        if (strings != null && !strings.isEmpty) {
            strings.asScala.mkString(delimiter)
        } else {
            ""
        }
    }
}