package br.com.unopay.api.util

import spock.lang.Specification

class StringJoinerTest extends Specification {
    StringJoiner stringJoiner

    void setup() {
        stringJoiner = Spy(StringJoiner)
    }

    def "given a collection of strings then should join it properly with default delimiter"() {
        given:
            Collection<String> strings = ['a', 'b', 'c']
        when:
            String result = stringJoiner.join(strings)
        then:
            assert result == 'a,b,c'
    }

    def "given a collection of strings then should join it properly with custom delimiter"() {
        given:
            Collection<String> strings = ['a', 'b', 'c']
            String delimiter = ';'
        when:
            String result = stringJoiner.join(strings, delimiter)
        then:
            assert result == 'a;b;c'
    }

    def "given an empty collection of string then should return empty string"() {
        given:
            Collection<String> strings = []
        when:
            String result = stringJoiner.join(strings)
        then:
            assert result == ''
    }

    def "given an null collection of string then should return empty string"() {
        given:
            Collection<String> strings = null
        when:
            String result = stringJoiner.join(strings)
        then:
            assert result == ''
    }
}
