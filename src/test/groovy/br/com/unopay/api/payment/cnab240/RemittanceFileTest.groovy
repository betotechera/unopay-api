package br.com.unopay.api.payment.cnab240

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.payment.cnab240.filler.RecordColumnRule
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchHeader
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CODIGO_DOCUMENTO_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEGMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceRecord.SEPARATOR
import br.com.unopay.api.payment.model.PaymentRemittance
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class RemittanceFileTest extends FixtureApplicationTest {

    def 'should return segment line by field'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)
        def segmentValue = remittance.remittanceItems.find().establishment.documentNumber()
        when:
        RemittanceFile remittanceFile = new RemittanceFile(batchSegmentA, cnab240)
        String extracted = remittanceFile.findSegmentLine(CODIGO_DOCUMENTO_FAVORECIDO, segmentValue)

        then:
        cnab240.split(SEPARATOR)[3] == extracted
    }

    def 'given a cnab240 should return remittance header document number'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)

        when:
        RemittanceFile remittanceFile = new RemittanceFile(remittanceHeader, cnab240)
        String extracted = remittanceFile.extract(NUMERO_INSCRICAO_EMPRESA, 1)

        then:
        extracted.contains(remittance.issuer.person.document.number)
    }

    def 'given a cnab240 should return batch header bank'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)

        when:
        RemittanceFile remittanceFile = new RemittanceFile(batchHeader, cnab240)
        String extracted = remittanceFile.extract(BANCO_COMPENSACAO, 2)

        then:
        extracted.contains(String.valueOf(remittance.issuer.paymentAccount.bankAccount.bacenCode))
    }

    def 'given a cnab240 should return batch segment A bank agreement number'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)

        when:
        RemittanceFile remittanceFile = new RemittanceFile(batchSegmentA, cnab240)
        String extracted = remittanceFile.extract(SEGMENTO, 3)

        then:
        extracted == "A"
    }

    def 'given a cnab240 should return batch segment A ted code'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)

        when:
        RemittanceFile remittanceFile = new RemittanceFile(batchSegmentA, cnab240)
        String extracted = remittanceFile.extract(FINALIDADE_TED, 3)

        then:
        extracted == "12345"
    }


    def 'given a unknown key should return error'(){
        when:
        new RemittanceFile(new HashMap<String, RecordColumnRule>(), "").extract("key", 2)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RULE_COLUMN_REQUIRED'
    }

}
