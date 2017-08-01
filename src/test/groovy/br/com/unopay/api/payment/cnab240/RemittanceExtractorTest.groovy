package br.com.unopay.api.payment.cnab240

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.payment.cnab240.filler.RecordColumnRule
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchHeader
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEGMENTO
import br.com.unopay.api.payment.model.PaymentRemittance
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class RemittanceExtractorTest extends FixtureApplicationTest {


    def 'given a cnab240 should return remittance header document number'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)

        when:
        RemittanceExtractor remittanceFile = new RemittanceExtractor(remittanceHeader, cnab240)
        String extracted = remittanceFile.extractOnLine(NUMERO_INSCRICAO_EMPRESA, 1)

        then:
        extracted.contains(remittance.payer.documentNumber)
    }

    def 'given a cnab240 should return batch header bank'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)

        when:
        RemittanceExtractor remittanceFile = new RemittanceExtractor(batchHeader, cnab240)
        String extracted = remittanceFile.extractOnLine(BANCO_COMPENSACAO, 2)

        then:
        extracted.contains("237")
    }

    def 'given a cnab240 should return batch segment A bank agreement number'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)

        when:
        RemittanceExtractor remittanceFile = new RemittanceExtractor(batchSegmentA, cnab240)
        String extracted = remittanceFile.extractOnLine(SEGMENTO, 3)

        then:
        extracted == "A"
    }

    def 'given a cnab240 should return batch segment A ted code'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)

        when:
        RemittanceExtractor remittanceFile = new RemittanceExtractor(batchSegmentA, cnab240)
        String extracted = remittanceFile.extractOnLine(FINALIDADE_TED, 3)

        then:
        extracted == "12345"
    }


    def 'given a unknown key should return error'(){
        when:
        new RemittanceExtractor(new HashMap<String, RecordColumnRule>(), "").extractOnLine("key", 2)

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RULE_COLUMN_REQUIRED'
    }

}
