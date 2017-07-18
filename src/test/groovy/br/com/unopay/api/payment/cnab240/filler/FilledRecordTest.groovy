package br.com.unopay.api.payment.cnab240.filler

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.payment.cnab240.Cnab240Generator
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA
import br.com.unopay.api.payment.model.PaymentRemittance
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class FilledRecordTest extends FixtureApplicationTest {

    def 'given a cnab240 should return document number'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)

        when:
        FilledRecord filledRecord = new FilledRecord(remittanceHeader)
        String extracted = filledRecord.extract(NUMERO_INSCRICAO_EMPRESA, cnab240)

        then:
        extracted.contains(remittance.issuer.person.document.number)
    }

    def 'given a cnab240 should return bank'(){
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        String cnab240 = new Cnab240Generator().generate(remittance, currentDate)

        when:
        FilledRecord filledRecord = new FilledRecord(remittanceHeader)
        String extracted = filledRecord.extract(BANCO_COMPENSACAO, cnab240)

        then:
        extracted.contains(String.valueOf(remittance.issuer.paymentAccount.bankAccount.bacenCode))
    }

    def 'given a unknown key should return error'(){
        when:
        new FilledRecord(new HashMap<String, RecordColumnRule>()).fill("key", "1")

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RULE_COLUMN_REQUIRED'
    }

    def 'given a unknown key should return error when fill default'(){
        when:
        new FilledRecord(new HashMap<String, RecordColumnRule>()).defaultFill("key")

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RULE_COLUMN_REQUIRED'
    }
}
