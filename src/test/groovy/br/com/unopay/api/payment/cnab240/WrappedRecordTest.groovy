package br.com.unopay.api.payment.cnab240

import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.CODIGO_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.INICIO_FEBRABAN
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.LOTE_SERVICO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.TIPO_REGISTRO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchHeader
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchSegment
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchTrailer
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceTrailer
import static br.com.unopay.api.payment.cnab240.RemittanceRecord.SEPARATOR
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class WrappedRecordTest extends FixtureApplicationTest {

    def 'when create wrapped record without header should return error'(){
        when:
        new WrappedRecord()
                .createTrailer(new FilledRecord(remittanceHeader) {{
                fill(CODIGO_BANCO, "12")
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
            }}).build()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'HEADER_REQUIRED_ON_WRAPPED_RECORD'
    }

    def 'when create wrapped record without trailer should return error'(){
        when:
        new WrappedRecord().createHeader(new FilledRecord(remittanceHeader) {{
                fill(CODIGO_BANCO, "8")
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
            }}).build()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'TRAILER_REQUIRED_ON_WRAPPED_RECORD'
    }

    def 'should fill file header'(){
        when:
        String remittance = new WrappedRecord().createHeader(new FilledRecord(remittanceHeader) {{
                fill(CODIGO_BANCO, "75")
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
        }}).createTrailer(new FilledRecord(remittanceTrailer)).build()

        then:
        def expected = "07500000         "
        remittance.split(SEPARATOR).find() == expected
    }

    def 'should fill file trailer'(){
        when:
        String remittance = new WrappedRecord().createTrailer(new FilledRecord(remittanceTrailer) {{
                fill(CODIGO_BANCO, "888")
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
            }})
                .createHeader(new FilledRecord(remittanceTrailer)).build()

        then:
        def expected = "88800000         "
        remittance.split(SEPARATOR).last() == expected
    }

    def 'should fill file header and trailer'(){
        when:
        String remittance = new WrappedRecord()
                .createHeader(new FilledRecord(remittanceHeader) {{
                fill(CODIGO_BANCO, "15")
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
            }})
                .createTrailer(new FilledRecord(remittanceTrailer) {{
                fill(CODIGO_BANCO, "15")
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
            }}).build()

        then:
        def expected = "01500000         "
        remittance.split(SEPARATOR).find() == expected
        remittance.split(SEPARATOR).last() == expected
    }

    def 'should fill file bach lines'(){
        when:
        String remittance = new WrappedRecord().addRecord(new FilledRecord(batchSegment) {{
                fill(CODIGO_BANCO, "5")
            }})
                .createTrailer(new FilledRecord(remittanceTrailer))
                .createHeader(new FilledRecord(remittanceTrailer)).build()

        then:
        def expected = "005"
        remittance.split(SEPARATOR)[1]  == expected
    }

    def 'should fill file bach lines with header trailer'(){
        given:
        when:
        String remittance = new WrappedRecord()
                .createHeader(new FilledRecord(remittanceHeader) {{
                fill(CODIGO_BANCO, "8")
            }})
                .addRecord(new FilledRecord(batchSegment) {{
                fill(CODIGO_BANCO, "8")
            }})
                .addRecord(new FilledRecord(batchSegment) {{
                fill(CODIGO_BANCO, "8")
            }})
                .createTrailer(new FilledRecord(remittanceTrailer) {{
                fill(CODIGO_BANCO, "8")
            }}).build()
        then:
        def expected = "008"
        remittance.split(SEPARATOR).size() == 4
        remittance.split(SEPARATOR).find() == expected
        remittance.split(SEPARATOR).last() == expected
    }

    def 'should fill file remittance with bach lines with header and trailer'(){
        given:
        WrappedRecord batch = new WrappedRecord()
                .createHeader(new FilledRecord(batchHeader) {{
                fill(CODIGO_BANCO, "5")
            }})
                .addRecord(new FilledRecord(batchSegment) {{
                fill(CODIGO_BANCO, "5")
            }})
                .addRecord(new FilledRecord(batchSegment) {{
                fill(CODIGO_BANCO, "5")
            }})
                .createTrailer(new FilledRecord(batchTrailer) {{
                fill(CODIGO_BANCO, "5")
            }})

        when:
        String remittance = new WrappedRecord()
                .createHeader(new FilledRecord(remittanceHeader) {{
                fill(CODIGO_BANCO, "5")
            }})
                .addRecord(batch)
                .createTrailer(new FilledRecord(remittanceTrailer) {{
                fill(CODIGO_BANCO, "5")
            }}).build()

        then:
        def expected = "005"
        remittance.split(SEPARATOR).size() == 6
        remittance.split(SEPARATOR).find() == expected
        remittance.split(SEPARATOR).last() == expected
    }
}
