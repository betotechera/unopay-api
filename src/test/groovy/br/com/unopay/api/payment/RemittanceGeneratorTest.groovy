package br.com.unopay.api.payment

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.bacen.model.Issuer
import org.apache.commons.lang3.StringUtils

class RemittanceGeneratorTest extends FixtureApplicationTest {

    def 'should create file header'(){
        given:
        def generator = new RemittanceGenerator()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def account = issuer.paymentAccount
        def header = new RemittanceFileHeader() {{ add('codigoBanco',account.bankAccount.bacenCode.toString()) }}

        when:
        String remittance = generator.addHeader(header).build()

        then:
        def expected = "${StringUtils.leftPad(account.bankAccount.bacenCode.toString(),3,'0')}00000         "
        remittance.split("/n").find() == expected
    }

    def 'should create file trailer'(){
        given:
        def generator = new RemittanceGenerator()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def account = issuer.paymentAccount
        def trailer = new RemittanceFileTrailer(){{ add('codigoBanco',account.bankAccount.bacenCode.toString()) }}
        when:
        String remittance = generator.addTrailer(trailer).build()

        then:
        def expected = "${StringUtils.leftPad(account.bankAccount.bacenCode.toString(),3,'0')}00000         "
        remittance.split("/n").last() == expected
    }

    def 'should create file header and trailer'(){
        given:
        def generator = new RemittanceGenerator()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def account = issuer.paymentAccount
        def header = new RemittanceFileHeader(){{ add('codigoBanco',account.bankAccount.bacenCode.toString()) }}
        def trailer = new RemittanceFileTrailer(){{ add('codigoBanco',account.bankAccount.bacenCode.toString()) }}
        when:
        String remittance = generator
                .addHeader(header)
                .addTrailer(trailer).build()

        then:
        def expected = "${StringUtils.leftPad(account.bankAccount.bacenCode.toString(),3,'0')}00000         "
        remittance.split("/n").find() == expected
        remittance.split("/n").last() == expected
    }

    def 'should create file bach lines'(){
        given:
        def generator = new RemittanceGenerator()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def account = issuer.paymentAccount
        def batch = new RemittanceBatch(){{ add('codigoBanco',account.bankAccount.bacenCode.toString()) }}

        when:
        String remittance = generator
                .addBatch(batch).build()

        then:
        def expected = "${StringUtils.leftPad(account.bankAccount.bacenCode.toString(),3,'0')}00000         "
        remittance.split("/n")[1]  == expected
    }

    def 'should create file bach lines with header trailer'(){
        given:
        def generator = new RemittanceGenerator()
        Issuer issuer = Fixture.from(Issuer.class).gimme("valid")
        def account = issuer.paymentAccount
        def header = new RemittanceFileHeader(){{ add('codigoBanco',account.bankAccount.bacenCode.toString()) }}
        def batch = new RemittanceBatch(){{ add('codigoBanco',account.bankAccount.bacenCode.toString()) }}
        def trailer = new RemittanceFileTrailer(){{ add('codigoBanco',account.bankAccount.bacenCode.toString()) }}
        when:
        String remittance = generator
                .addHeader(header)
                .addBatch(batch)
                .addTrailer(trailer).build()

        then:
        def expected = "${StringUtils.leftPad(account.bankAccount.bacenCode.toString(),3,'0')}00000         "
        remittance.split("/n").find()  == expected
        remittance.split("/n").last()  == expected
    }
}
