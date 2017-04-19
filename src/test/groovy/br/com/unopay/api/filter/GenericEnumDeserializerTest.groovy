package br.com.unopay.api.filter

import br.com.unopay.api.bacen.model.BankAccountType
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class GenericEnumDeserializerTest  extends Specification {


    def 'should be deserializer'(){
        def deserializer = new GenericEnumDeserializer<BankAccountType>()
        JsonFactory jsonFactory = new JsonFactory()
        JsonParser jp = jsonFactory.createParser('{ "code" : "CURRENT" }')
        jp.setCodec(new ObjectMapper())
        when:
        def result = deserializer.deserialize(jp, null)

        then:
        BankAccountType.CURRENT == result

    }
}
