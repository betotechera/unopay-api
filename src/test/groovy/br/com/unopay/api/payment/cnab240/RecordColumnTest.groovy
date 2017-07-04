package br.com.unopay.api.payment.cnab240

import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import spock.lang.Specification

class RecordColumnTest extends Specification{

    def 'given a column without value should return value with default pad left'(){
        given:
        def column = new RecordColumn(new RecordColumnRule(1, 5))

        when:
        def value = column.getValue()

        then:
        value == '00000'
    }

    def 'given a column without value and with pad left should return value with defined pad left'(){
        given:
        def column = new RecordColumn(new RecordColumnRule(1, 5,padLeftType))

        when:
        def value = column.getValue()

        then:
        value == expected

        where:
        padLeftType       | expected
        LeftPadType.SPACE | '     '
        LeftPadType.ZERO  | '00000'
    }

    def 'when define value greater than length should return error'(){
        when:
        new RecordColumn(new RecordColumnRule(1, 2), "123")

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'REMITTANCE_COLUMN_LENGTH_NOT_MET'

    }

    def 'given a rule with default value should fill with this'(){
        given:
        def defaultValue = "1234"
        def column = new RecordColumn(new RecordColumnRule(1, 4, defaultValue))

        when:
        def value = column.getValue()

        then:
        value == defaultValue
    }
}
