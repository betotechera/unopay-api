package br.com.unopay.api.http

import br.com.unopay.api.bacen.model.Scope
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
        def serializer = new GenericEnumSerializer<Scope>(){}

        when:
        serializer.serialize(Scope.DOMESTIC, jsonGenerator, serializerProvider)
        jsonGenerator.flush()

        then:
        jsonWriter.toString() == '{"code":"DOMESTIC","description":"Domestico"}'

    }

}
