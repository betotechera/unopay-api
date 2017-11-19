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
        def ourNumber = '0'
        def yourNumber = '12345'
        def issuer = order.product.issuer
        def expirationDays = 5
        def value = Math.random() * 100
        when:
        Set<TicketRequest.Dados.Entry> entries = new CobrancaOlnineBuilder()
                .issuer(issuer)
                .ourNumber(ourNumber)
                .yourNumber(yourNumber)
                .value(value)
                .expirationDays(expirationDays)
                .build()

        then:
        entries.find { it.key == 'PAGADOR.NOME' }.value == issuer.person.name
        entries.find { it.key == 'PAGADOR.TP-DOC' }.value == issuer.person.documentType()
        entries.find { it.key == 'PAGADOR.NUM-DOC' }.value == issuer.person.documentNumber()
        entries.find { it.key == 'PAGADOR.BAIRRO' }.value == issuer.person.address.district
        entries.find { it.key == 'PAGADOR.CIDADE' }.value == issuer.person.address.city
        entries.find { it.key == 'PAGADOR.ENDER' }.value == issuer.person.address.streetName
        entries.find { it.key == 'PAGADOR.CEP' }.value == issuer.person.address.zipCode
        entries.find { it.key == 'PAGADOR.UF' }.value == issuer.person.address.state.name()

        entries.find { it.key == 'CONVENIO.COD-CONVENIO' }.value == issuer.paymentAccount.bankAgreementNumberForDebit
        entries.find { it.key == 'CONVENIO.COD-BANCO' }.value == issuer.paymentAccount.bankAccount.bacenCode().toString()

        entries.find { it.key == 'TITULO.NOSSO-NUMERO' }.value == ourNumber
        entries.find { it.key == 'TITULO.SEU-NUMERO' }.value == yourNumber
        entries.find { it.key == 'TITULO.DT-VENCTO' }.value == new DateTime().plusDays(expirationDays).toDate().format("dd/MM/yyyy")
        entries.find { it.key == 'TITULO.DT-EMISSAO' }.value == new Date().format("dd/MM/yyyy")
        entries.find { it.key == 'TITULO.ESPECIE' }.value == '99'
        entries.find { it.key == 'TITULO.VL-NOMINAL' }.value == Rounder.roundToString(value)
        entries.find { it.key == 'TITULO.TP-DESC' }.value == '0'
        entries.find { it.key == 'TITULO.TP-PROTESTO' }.value == '3'
        entries.find { it.key == 'TITULO.QT-DIAS-BAIXA' }.value == '2'
    }
}
