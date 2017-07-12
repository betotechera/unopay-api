package br.com.unopay.api.payment.cnab240

import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import spock.lang.Specification

class FilledRecordTest extends Specification {

    def 'given a unknown key should return error'(){
        when:
        new FilledRecord(new HashMap<String, RecordColumnRule>()).fill("key", "1")

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RULE_COLUMN_REQUIRED'
    }

    def 'given a unknown key should return error when fill default'(){
        when:
        new FilledRecord(new HashMap<String, RecordColumnRule>()).defaultFill("key")

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RULE_COLUMN_REQUIRED'
    }
}
