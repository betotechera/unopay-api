package br.com.unopay.api.http

import br.com.unopay.api.bacen.model.BankAccountType
import br.com.unopay.bootcommons.exception.BadRequestException
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.ObjectMapper
import spock.lang.Specification

class GenericEnumDeserializerTest  extends Specification {

    def 'should be deserializer with code'(){
        def deserializer = new GenericEnumDeserializer<BankAccountType>(BankAccountType.class)
        JsonFactory jsonFactory = new JsonFactory()
        JsonParser jp = jsonFactory.createParser('{"code":"CURRENT","description":"Corrente"}')
        def mapper = new ObjectMapper()
        jp.setCodec(mapper)
        DeserializationContext context = mapper.getDeserializationContext()

        when:
        def result = deserializer.deserialize(jp, context)

        then:
        BankAccountType.CURRENT == result

    }

    def 'should be deserializer without code'(){
        def deserializer = new GenericEnumDeserializer<BankAccountType>(BankAccountType.class)
        JsonFactory jsonFactory = new JsonFactory()
        JsonParser jp = jsonFactory.createParser('"CURRENT"')
        jp.setCodec(new ObjectMapper())
        when:
        def result = deserializer.deserialize(jp, null)

        then:
        BankAccountType.CURRENT == result

    }

    def 'should not be deserializer unknown code'(){
        def deserializer = new GenericEnumDeserializer<BankAccountType>(BankAccountType.class)
        JsonFactory jsonFactory = new JsonFactory()
        JsonParser jp = jsonFactory.createParser("{\"code\":\"$value\",\"description\":\"Corrente\"}")
        def mapper = new ObjectMapper()
        jp.setCodec(mapper)
        DeserializationContext context = mapper.getDeserializationContext()

        when:
        def result = deserializer.deserialize(jp, context)

        then:
        !result

        where:
        _ | value
        _ | ""
        _ | "ENUM"

    }

    def 'should not be deserializer unknown value'(){
        def deserializer = new GenericEnumDeserializer<BankAccountType>(BankAccountType.class)
        JsonFactory jsonFactory = new JsonFactory()
        JsonParser jp = jsonFactory.createParser("\"$value\"")
        def mapper = new ObjectMapper()
        jp.setCodec(mapper)

        when:
        def result = deserializer.deserialize(jp, null)

        then:
        !result

        where:
        _ | value
        _ | ""
        _ | "ENUM"

    }
}
