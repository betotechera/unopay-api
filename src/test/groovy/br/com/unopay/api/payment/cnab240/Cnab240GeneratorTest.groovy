package br.com.unopay.api.payment.cnab240

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.model.BatchClosing
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.AGENCIA
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.CODIGO_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.CODIGO_REMESSA
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.CONVEIO_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.DATA_GERACAO_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.DENSIDADE_GRAVACAO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.DIGITO_AGENCIA
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.DIGITO_AGENCIA_CONTA
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.DIGITO_CONTA
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.FIM_FEBRABAN
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.HORA_GERACAO_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.INICIO_FEBRABAN
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.LAYOUT_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.LOTE_SERVICO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.MEIO_FEBRABAN
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.NOME_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.NOME_EMPRESA
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.NUMERO_CONTA
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.NUMERO_INSCRICAO_EMPRESA
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.RESERVADO_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.RESERVADO_EMPRESA
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.SEQUENCIAL_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.TIPO_INSCRICAO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.TIPO_REGISTRO
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceHeader

class Cnab240GeneratorTest extends FixtureApplicationTest{

    def 'should create file header'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid")
        def currentDate = instant("now")
        def generator = new Cnab240Generator(currentDate)

        when:
        String cnab240 = generator.generate(batchClosing)

        then:
        def bankAccount = batchClosing.issuer.paymentAccount.bankAccount
        def person = batchClosing.issuer.person
        def record = new FilledRecord(remittanceHeader) {{
                fill(CODIGO_BANCO, bankAccount.bacenCode)
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
                defaultFill(TIPO_INSCRICAO)
                fill(NUMERO_INSCRICAO_EMPRESA, person.document.number)
                fill(CONVEIO_BANCO, batchClosing.issuer.paymentAccount.bankAgreementNumber)
                fill(AGENCIA, bankAccount.agency)
                fill(DIGITO_AGENCIA, bankAccount.agentDvFirstDigit())
                fill(NUMERO_CONTA, bankAccount.accountNumber)
                fill(DIGITO_CONTA, bankAccount.accountDvFirstDigit())
                fill(DIGITO_AGENCIA_CONTA, bankAccount.accountDvLastDigit())
                fill(NOME_EMPRESA, person.name)
                fill(NOME_BANCO, bankAccount.bank.name)
                defaultFill(MEIO_FEBRABAN)
                defaultFill(CODIGO_REMESSA)
                fill(DATA_GERACAO_ARQUIVO, currentDate.format("ddMMyyyy"))
                fill(HORA_GERACAO_ARQUIVO, currentDate.format("hhmmss"))
                fill(SEQUENCIAL_ARQUIVO, '001')
                defaultFill(LAYOUT_ARQUIVO)
                defaultFill(DENSIDADE_GRAVACAO)
                defaultFill(RESERVADO_BANCO)
                defaultFill(RESERVADO_EMPRESA)
                defaultFill(FIM_FEBRABAN)
            }}

        cnab240 == record.build()
    }
}
