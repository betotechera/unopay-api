package br.com.unopay.api.util

import br.com.unopay.api.UnopayApiScalaApplicationTest

import scala.collection.JavaConverters._

class StringJoinerTest extends UnopayApiScalaApplicationTest {
    
    "given a collection of strings then" should "join it properly with default delimiter" in {
        val strings = List("a", "b", "c")
        val result = StringJoiner.join(strings.asJava)
        result should be ("a,b,c")
    }

    "given a collection of strings then" should "join it properly with custom delimiter" in {
        val strings = List("a", "b", "c")
        val delimiter = ";"
        val result = StringJoiner.join(strings.asJava, delimiter)
        result should be ("a;b;c")
    }

    "given an empty collection of string then" should "return empty string" in {
        val strings = List()
        val result = StringJoiner.join(strings.asJava)
        result should be ("")
    }

    "given an null collection of string then" should "return empty string" in {
        val strings = null
        val result = StringJoiner.join(strings)
        result should be ("")
    }
}
