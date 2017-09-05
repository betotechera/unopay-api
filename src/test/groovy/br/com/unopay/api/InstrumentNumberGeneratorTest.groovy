package br.com.unopay.api

import org.springframework.beans.factory.annotation.Autowired

class InstrumentNumberGeneratorTest extends SpockApplicationTests {

    @Autowired
    InstrumentNumberGenerator generator

    void setup() {
        generator.bin = '5000'
        generator.length = 20
    }


    def "should generate a instrument number from bin and length"() {
        when:
            String result = generator.generate()
        then:
            result.startsWith('5000')
            result.length() == 20

    }
}
