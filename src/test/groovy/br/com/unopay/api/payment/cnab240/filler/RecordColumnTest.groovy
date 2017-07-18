package br.com.unopay.api.payment.cnab240.filler

import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import spock.lang.Specification

class RecordColumnTest extends Specification{

    def 'given a column without value should return value with default pad left'(){
        given:
        def column = new RecordColumn(new RecordColumnRule(1,1,1, 5, ColumnType.ALPHA))

        when:
        def value = column.getValue()

        then:
        value == '     '
    }

    def 'given a column without value and with pad left should return value with defined pad left'(){
        given:
        def column = new RecordColumn(new RecordColumnRule(1,1,1, 5,columnType))

        when:
        def value = column.getValue()

        then:
        value == expected

        where:
        columnType       | expected
        ColumnType.ALPHA | '     '
        ColumnType.NUMBER  | '00000'
    }

    def 'when define value greater than length should return error'(){
        when:
        new RecordColumn(new RecordColumnRule(1,1,1, 2, ColumnType.NUMBER), "123")

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'REMITTANCE_COLUMN_LENGTH_NOT_MET'

    }

    def 'given a null rule should return error'(){
        when:
        new RecordColumn(null, "1")

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RULE_COLUMN_REQUIRED'
    }

    def 'given a column without value should fill with default left pad value'(){
        given:
        def column = new RecordColumn(new RecordColumnRule(1,1,1, 2, ColumnType.ALPHA), null)
        when:
        def value = column.getValue()

        then:
        value == '  '

    }

    def 'given a rule with default value should fill with this'(){
        given:
        def defaultValue = "1234"
        def column = new RecordColumn(new RecordColumnRule(1,1,1, 4, defaultValue, ColumnType.NUMBER))

        when:
        def value = column.getValue()

        then:
        value == defaultValue
    }
}
