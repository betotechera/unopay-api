package br.com.unopay.api.payment

import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.payment.RemittanceGenerator.SEPARATOR

class RemittanceGeneratorTest extends FixtureApplicationTest {

    def 'should create file header'(){
        given:
        def generator = new RemittanceGenerator()
        def header = new RemittanceFileHeader() {{
            add('codigoBanco',"5")
            add('loteServico',null)
            add('tipoRegistro',null)
            add('febraban',null)
        }}

        when:
        String remittance = generator.addHeader(header).build()

        then:
        def expected = "00500000         "
        remittance.split(SEPARATOR).find() == expected
    }

    def 'should create file trailer'(){
        given:
        def generator = new RemittanceGenerator()
        def trailer = new RemittanceFileTrailer() {{
            add('codigoBanco',"5")
            add('loteServico',null)
            add('tipoRegistro',null)
            add('febraban',null)
        }}
        when:
        String remittance = generator.addTrailer(trailer).build()

        then:
        def expected = "00500000         "
        remittance.split(SEPARATOR).last() == expected
    }

    def 'should create file header and trailer'(){
        given:
        def generator = new RemittanceGenerator()
        def header = new RemittanceFileHeader() {{
            add('codigoBanco',"5")
            add('loteServico',null)
            add('tipoRegistro',null)
            add('febraban',null)
        }}
        def trailer = new RemittanceFileTrailer() {{
            add('codigoBanco',"5")
            add('loteServico',null)
            add('tipoRegistro',null)
            add('febraban',null)
        }}
        when:
        String remittance = generator
                .addHeader(header)
                .addTrailer(trailer).build()

        then:
        def expected = "00500000         "
        remittance.split(SEPARATOR).find() == expected
        remittance.split(SEPARATOR).last() == expected
    }

    def 'should create file bach lines'(){
        given:
        def generator = new RemittanceGenerator()
        def batch = new RemittanceBatch(){{ add('codigoBanco',"5") }}

        when:
        String remittance = generator
                .addBatch(batch).build()

        then:
        def expected = "005"
        remittance.split(SEPARATOR)[1]  == expected
    }

    def 'should create file bach lines with header trailer'(){
        given:
        def generator = new RemittanceGenerator()
        def header = new RemittanceFileHeader(){{ add('codigoBanco',"5") }}
        def batch1 = new RemittanceBatch(){{ add('codigoBanco',"5") }}
        def batch2 = new RemittanceBatch(){{ add('codigoBanco',"5") }}
        def trailer = new RemittanceFileTrailer(){{ add('codigoBanco',"5") }}
        when:
        String remittance = generator
                .addHeader(header)
                .addBatch(batch1)
                .addBatch(batch2)
                .addTrailer(trailer).build()

        then:
        def expected = "005"
        remittance.split(SEPARATOR).size() == 4
        remittance.split(SEPARATOR).find()  == expected
        remittance.split(SEPARATOR).last()  == expected
    }
}
