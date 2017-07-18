package br.com.unopay.api.payment.cnab240.filler

import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchHeader
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchTrailer
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceTrailer
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceRecord.SEPARATOR
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class WrappedRecordTest extends FixtureApplicationTest {

    def 'when create wrapped record without header should return error'(){
        when:
        new WrappedRecord()
                .createTrailer(new FilledRecord(remittanceHeader) {{
                fill(BANCO_COMPENSACAO, "12")
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
                fill(BANCO_COMPENSACAO, "8")
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
                fill(BANCO_COMPENSACAO, "75")
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
        }}).createTrailer(new FilledRecord(remittanceTrailer)).build()

        then:
        def expected = "07500000         "
        remittance.split(SEPARATOR).find() == expected
    }

    def 'should replace unaccepted number characters in number column'(){
        when:
        def forFill = chars
        String remittance = new WrappedRecord().createHeader(new FilledRecord(remittanceHeader) {{
            fill(BANCO_COMPENSACAO, forFill)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
        }}).createTrailer(new FilledRecord(remittanceTrailer)).build()

        then:
        def expected = "05500000         "
        remittance.split(SEPARATOR).find() == expected

        where:
        chars|_
        "5.5"|_
        "5/5"|_
        "5-5"|_
    }

    def 'should not replace unaccepted number characters in alpha column'(){
        when:
        def forFill = chars
        String remittance = new WrappedRecord().createHeader(new FilledRecord(remittanceHeader) {{
            fill(BANCO_COMPENSACAO, "55")
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            fill(INICIO_FEBRABAN, forFill)
        }}).createTrailer(new FilledRecord(remittanceTrailer)).build()

        then:
        def expected = "05500000      ${forFill}"
        remittance.split(SEPARATOR).find() == expected

        where:
        chars|_
        "5.5"|_
        "5/5"|_
        "5-5"|_
    }

    def 'should fill file trailer'(){
        when:
        String remittance = new WrappedRecord().createTrailer(new FilledRecord(remittanceTrailer) {{
                fill(BANCO_COMPENSACAO, "888")
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
            }})
                .createHeader(new FilledRecord(remittanceTrailer)).build()

        then:
        def expected = "88899999         "
        remittance.split(SEPARATOR).last() == expected
    }

    def 'should fill file header and trailer'(){
        when:
        String remittance = new WrappedRecord()
                .createHeader(new FilledRecord(remittanceHeader) {{
                fill(BANCO_COMPENSACAO, "15")
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
            }})
                .createTrailer(new FilledRecord(remittanceTrailer) {{
                fill(BANCO_COMPENSACAO, "15")
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
            }}).build()

        then:
        remittance.split(SEPARATOR).find() == "01500000         "
        remittance.split(SEPARATOR).last() == "01599999         "
    }

    def 'should fill file bach lines'(){
        when:
        String remittance = new WrappedRecord().addRecord(new FilledRecord(batchSegmentA) {{
                fill(BANCO_COMPENSACAO, "5")
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
                fill(BANCO_COMPENSACAO, "8")
            }})
                .addRecord(new FilledRecord(batchSegmentA) {{
                fill(BANCO_COMPENSACAO, "8")
            }})
                .addRecord(new FilledRecord(batchSegmentA) {{
                fill(BANCO_COMPENSACAO, "8")
            }})
                .createTrailer(new FilledRecord(remittanceTrailer) {{
                fill(BANCO_COMPENSACAO, "8")
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
                fill(BANCO_COMPENSACAO, "5")
            }})
                .addRecord(new FilledRecord(batchSegmentA) {{
                fill(BANCO_COMPENSACAO, "5")
            }})
                .addRecord(new FilledRecord(batchSegmentA) {{
                fill(BANCO_COMPENSACAO, "5")
            }})
                .createTrailer(new FilledRecord(batchTrailer) {{
                fill(BANCO_COMPENSACAO, "5")
            }})

        when:
        String remittance = new WrappedRecord()
                .createHeader(new FilledRecord(remittanceHeader) {{
                fill(BANCO_COMPENSACAO, "5")
            }})
                .addRecord(batch)
                .createTrailer(new FilledRecord(remittanceTrailer) {{
                fill(BANCO_COMPENSACAO, "5")
            }}).build()

        then:
        def expected = "005"
        remittance.split(SEPARATOR).size() == 6
        remittance.split(SEPARATOR).find() == expected
        remittance.split(SEPARATOR).last() == expected
    }
}
