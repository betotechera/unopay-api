package br.com.unopay.api.filter

import br.com.unopay.api.bacen.model.BankAccountType
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import spock.lang.Specification

class GenericEnumSerializerTest extends Specification {

    def 'should be serializer with code'(){
        Writer jsonWriter = new StringWriter()
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(jsonWriter)
        SerializerProvider serializerProvider = new ObjectMapper().getSerializerProvider()
        def serializer = new GenericEnumSerializer<BankAccountType>(){}

        when:
        serializer.serialize(BankAccountType.CURRENT, jsonGenerator, serializerProvider)
        jsonGenerator.flush()

        then:
        jsonWriter.toString() == '{"code":"CURRENT","description":"Corrente"}'

    }

}
