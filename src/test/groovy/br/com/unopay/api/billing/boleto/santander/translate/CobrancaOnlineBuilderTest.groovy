package br.com.unopay.api.billing.boleto.santander.translate

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.boleto.santander.cobrancaonline.dl.TicketRequest
import br.com.unopay.api.order.model.Order
import br.com.unopay.api.util.Rounder
import org.joda.time.DateTime

class CobrancaOnlineBuilderTest extends FixtureApplicationTest{

    def 'should build entries with valid data'(){
        given:
        Order order = Fixture.from(Order.class).gimme("valid")
        def yourNumber = '12345'
        def payer = order.person
        def paymentAccount = order.product.issuer.paymentAccount
        def expirationDays = 5
        def value = Math.random() * 100
        when:
        Set<TicketRequest.Dados.Entry> entries = new CobrancaOlnineBuilder()
                .payer(payer)
                .value(value)
                .yourNumber(yourNumber)
                .paymentBankAccount(paymentAccount)
                .expirationDays(expirationDays)
                .build()

        then:
        entries.find { it.key == 'PAGADOR.NOME' }.value == payer.name
        entries.find { it.key == 'PAGADOR.TP-DOC' }.value == payer.documentType()
        entries.find { it.key == 'PAGADOR.NUM-DOC' }.value == payer.documentNumber()
        entries.find { it.key == 'PAGADOR.BAIRRO' }.value == payer.address.district
        entries.find { it.key == 'PAGADOR.CIDADE' }.value == payer.address.city
        entries.find { it.key == 'PAGADOR.ENDER' }.value == payer.address.streetName
        entries.find { it.key == 'PAGADOR.CEP' }.value == payer.address.zipCode
        entries.find { it.key == 'PAGADOR.UF' }.value == payer.address.state.name()

        entries.find { it.key == 'CONVENIO.COD-CONVENIO' }.value == paymentAccount.bankAgreementNumberForDebit
        entries.find { it.key == 'CONVENIO.COD-BANCO' }.value == paymentAccount.bankAccount.bacenCode().toString()

        entries.find { it.key == 'TITULO.SEU-NUMERO' }.value == yourNumber
        entries.find { it.key == 'TITULO.DT-VENCTO' }.value == new DateTime()
                .plusDays(expirationDays).toDate().format("ddMMyyyy")
        entries.find { it.key == 'TITULO.DT-EMISSAO' }.value == new Date().format("ddMMyyyy")
        entries.find { it.key == 'TITULO.ESPECIE' }.value == '99'
        entries.find { it.key == 'TITULO.VL-NOMINAL' }.value == Rounder.roundToString(value).replace(".", "")
        entries.find { it.key == 'TITULO.TP-DESC' }.value == '0'
        entries.find { it.key == 'TITULO.TP-PROTESTO' }.value == '3'
        entries.find { it.key == 'TITULO.QT-DIAS-BAIXA' }.value == '2'
    }
}
