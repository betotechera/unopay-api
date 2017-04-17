package br.com.unopay.api.util

import spock.lang.Specification

class StringJoinerTest extends Specification {

    def "given a collection of strings then should join it properly with default delimiter"() {
        given:
            Collection<String> strings = ['a', 'b', 'c']
        when:
            String result = StringJoiner.join(strings)
        then:
            assert result == 'a,b,c'
    }

    def "given a collection of strings then should join it properly with custom delimiter"() {
        given:
            Collection<String> strings = ['a', 'b', 'c']
            String delimiter = ';'
        when:
            String result = StringJoiner.join(strings, delimiter)
        then:
            assert result == 'a;b;c'
    }

    def "given an empty collection of string then should return empty string"() {
        given:
            Collection<String> strings = []
        when:
            String result = StringJoiner.join(strings)
        then:
            assert result == ''
    }

    def "given an null collection of string then should return empty string"() {
        given:
            Collection<String> strings = null
        when:
            String result = StringJoiner.join(strings)
        then:
            assert result == ''
    }
}
