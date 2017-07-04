package br.com.unopay.api.payment

import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.payment.RemittanceLayout.getBatchHeader
import static br.com.unopay.api.payment.RemittanceLayout.getBatchSegment
import static br.com.unopay.api.payment.RemittanceLayout.getBatchTrailer
import static br.com.unopay.api.payment.RemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.payment.RemittanceLayout.getRemittanceTrailer
import static br.com.unopay.api.payment.RemittanceRecord.SEPARATOR
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class WrappedRecordTest extends FixtureApplicationTest {

    def 'when create wrapped record without header should return error'(){
        when:
        new WrappedRecord()
                .createTrailer(new FilledRecord(remittanceHeader) {{
                fill('codigoBanco', "12")
                defaultFill('loteServico')
                defaultFill('tipoRegistro')
                defaultFill('febraban')
            }}).getRecord()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'HEADER_REQUIRED_ON_WRAPPED_RECORD'
    }

    def 'when create wrapped record without trailer should return error'(){
        when:
        new WrappedRecord().createHeader(new FilledRecord(remittanceHeader) {{
                fill('codigoBanco', "8")
                defaultFill('loteServico')
                defaultFill('tipoRegistro')
                defaultFill('febraban')
            }}).getRecord()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'TRAILER_REQUIRED_ON_WRAPPED_RECORD'
    }

    def 'should fill file header'(){
        when:
        String remittance = new WrappedRecord().createHeader(new FilledRecord(remittanceHeader) {{
                fill('codigoBanco', "75")
                defaultFill('loteServico')
                defaultFill('tipoRegistro')
                defaultFill('febraban')
        }}).createTrailer(new FilledRecord(remittanceTrailer)).getRecord()

        then:
        def expected = "07500000         "
        remittance.split(SEPARATOR).find() == expected
    }

    def 'should fill file trailer'(){
        when:
        String remittance = new WrappedRecord().createTrailer(new FilledRecord(remittanceTrailer) {{
                fill('codigoBanco', "888")
                defaultFill('loteServico')
                defaultFill('tipoRegistro')
                defaultFill('febraban')
            }})
                .createHeader(new FilledRecord(remittanceTrailer)).getRecord()

        then:
        def expected = "88800000         "
        remittance.split(SEPARATOR).last() == expected
    }

    def 'should fill file header and trailer'(){
        when:
        String remittance = new WrappedRecord()
                .createHeader(new FilledRecord(remittanceHeader) {{
                fill('codigoBanco', "15")
                defaultFill('loteServico')
                defaultFill('tipoRegistro')
                defaultFill('febraban')
            }})
                .createTrailer(new FilledRecord(remittanceTrailer) {{
                fill('codigoBanco', "15")
                defaultFill('loteServico')
                defaultFill('tipoRegistro')
                defaultFill('febraban')
            }}).getRecord()

        then:
        def expected = "01500000         "
        remittance.split(SEPARATOR).find() == expected
        remittance.split(SEPARATOR).last() == expected
    }

    def 'should fill file bach lines'(){
        when:
        String remittance = new WrappedRecord().addRecord(new FilledRecord(batchSegment) {{
                fill('codigoBanco', "5")
            }})
                .createTrailer(new FilledRecord(remittanceTrailer))
                .createHeader(new FilledRecord(remittanceTrailer)).getRecord()

        then:
        def expected = "005"
        remittance.split(SEPARATOR)[1]  == expected
    }

    def 'should fill file bach lines with header trailer'(){
        given:
        when:
        String remittance = new WrappedRecord()
                .createHeader(new FilledRecord(remittanceHeader) {{
                fill('codigoBanco', "8")
            }})
                .addRecord(new FilledRecord(batchSegment) {{
                fill('codigoBanco', "8")
            }})
                .addRecord(new FilledRecord(batchSegment) {{
                fill('codigoBanco', "8")
            }})
                .createTrailer(new FilledRecord(remittanceTrailer) {{
                fill('codigoBanco', "8")
            }}).getRecord()
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
                fill('codigoBanco', "5")
            }})
                .addRecord(new FilledRecord(batchSegment) {{
                fill('codigoBanco', "5")
            }})
                .addRecord(new FilledRecord(batchSegment) {{
                fill('codigoBanco', "5")
            }})
                .createTrailer(new FilledRecord(batchTrailer) {{
                fill('codigoBanco', "5")
            }})

        when:
        String remittance = new WrappedRecord()
                .createHeader(new FilledRecord(remittanceHeader) {{
                fill('codigoBanco', "5")
            }})
                .addRecord(batch)
                .createTrailer(new FilledRecord(remittanceTrailer) {{
                fill('codigoBanco', "5")
            }}).getRecord()

        then:
        def expected = "005"
        remittance.split(SEPARATOR).size() == 6
        remittance.split(SEPARATOR).find() == expected
        remittance.split(SEPARATOR).last() == expected
    }
}
