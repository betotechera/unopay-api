package br.com.unopay.api.billing.boleto.model

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.order.model.Order
import br.com.unopay.bootcommons.exception.UnprocessableEntityException
import org.joda.time.DateTime
import org.joda.time.DateTimeComparator
import org.joda.time.DateTimeFieldType

class BoletoStellaBuilderTest extends FixtureApplicationTest{

    def 'should map all required fields when build boleto'(){
        given:
        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        br.com.caelum.stella.boleto.Boleto boletoStella = new BoletoStellaBuilder()
                .issuer(order.getProduct().getIssuer())
                .number("12345")
                .expirationDays(1)
                .payer(order.person)
                .value(order.value)
                .build()

        then:
        boletoStella.getPagador().nome == order.person.name
        boletoStella.getPagador().documento == order.person.documentNumber()
        boletoStella.getPagador().getEndereco().bairro == order.person.address.district
        boletoStella.getPagador().getEndereco().cidade == order.person.address.city
        boletoStella.getPagador().getEndereco().logradouro == order.person.address.streetName
        boletoStella.getPagador().getEndereco().cep == order.person.address.zipCode
        boletoStella.getPagador().getEndereco().uf == order.person.address.state.name()

        def issuer = order.product.issuer
        boletoStella.getBeneficiario().nomeBeneficiario == issuer.person.name
        boletoStella.getBeneficiario().agencia == issuer.paymentAccount.bankAccount.agency
        boletoStella.getBeneficiario().digitoAgencia == issuer.paymentAccount.bankAccount.agencyDigit
        boletoStella.getBeneficiario().numeroConvenio == issuer.paymentAccount.bankAgreementNumberForDebit
        boletoStella.getBeneficiario().codigoBeneficiario == issuer.paymentAccount.beneficiaryCode
        boletoStella.getBeneficiario().digitoCodigoBeneficiario == issuer.paymentAccount.beneficiaryDigit
        boletoStella.getBeneficiario().carteira == issuer.paymentAccount.walletNumber


        boletoStella.getBeneficiario().getEndereco().bairro == issuer.person.address.district
        boletoStella.getBeneficiario().getEndereco().cidade == issuer.person.address.city
        boletoStella.getBeneficiario().getEndereco().logradouro == issuer.person.address.streetName
        boletoStella.getBeneficiario().getEndereco().cep == issuer.person.address.zipCode
        boletoStella.getBeneficiario().getEndereco().uf == issuer.person.address.state.name()

        boletoStella.valorBoleto == order.value
    }

    def 'should build boleto with valid expiration'(){
        given:
        Order order = Fixture.from(Order.class).gimme("valid")
        def expirationDays = (Math.random() * 1000).toInteger()
        when:
        br.com.caelum.stella.boleto.Boleto boletoStella = new BoletoStellaBuilder()
                .issuer(order.getProduct().getIssuer())
                .number("12345")
                .payer(order.person)
                .value(order.value)
                .expirationDays(expirationDays)
                .build()

        then:
        DateTimeComparator comparator = DateTimeComparator.getInstance(DateTimeFieldType.dayOfMonth())
        comparator.compare(boletoStella.datas.vencimento, DateTime.now().plusDays(expirationDays)) == 0

    }

    def 'when build without value should return error'(){
        given:
        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        new BoletoStellaBuilder()
                .issuer(order.getProduct().getIssuer())
                .number("12345")
                .expirationDays(1)
                .payer(order.person)
                .build()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'VALUE_REQUIRED'
    }

    def 'when build without client should return error'(){
        given:
        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        new BoletoStellaBuilder()
                .issuer(order.getProduct().getIssuer())
                .number("12345")
                .expirationDays(1)
                .value(order.value)
                .build()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'CLIENT_REQUIRED'
    }

    def 'when build without issuer should return error'(){
        given:
        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        new BoletoStellaBuilder()
                .number("12345")
                .expirationDays(1)
                .payer(order.person)
                .value(order.value)
                .build()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'ISSUER_REQUIRED'
    }

    def 'when build without number should return error'(){
        given:
        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        new BoletoStellaBuilder()
                .issuer(order.getProduct().getIssuer())
                .payer(order.person)
                .expirationDays(1)
                .value(order.value)
                .build()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'NUMBER_REQUIRED'
    }

    def 'when build without expiration days should return error'(){
        given:
        Order order = Fixture.from(Order.class).gimme("valid")

        when:
        new BoletoStellaBuilder()
                .issuer(order.getProduct().getIssuer())
                .payer(order.person)
                .number("1235")
                .value(order.value)
                .build()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'EXPIRATION_DAYS_REQUIRED'
    }
}
