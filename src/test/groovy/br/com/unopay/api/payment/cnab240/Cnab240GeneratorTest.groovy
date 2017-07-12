package br.com.unopay.api.payment.cnab240

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.model.BatchClosing
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchHeader
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchTrailer
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceTrailer
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.AGENCIA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CEP
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CIDADE
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_REMESSA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO_CEP
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CONVEIO_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DENSIDADE_GRAVACAO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_CONTA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.ESTADO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FIM_FEBRABAN
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FORMA_LANCAMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INICIO_FEBRABAN
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LAYOUT_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOGRADOURO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOTE_SERVICO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MEIO_FEBRABAN
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MENSAGEM
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_EMPRESA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_CONTA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.OCORRENCIAS
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_CONTAS
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_LOTES
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_MOEDAS
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_REGISTROS
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_EMPRESA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SOMATORIA_VALORES
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_INSCRICAO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_OPERACAO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_REGISTRO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_SERVICO
import static br.com.unopay.api.payment.cnab240.RemittanceRecord.SEPARATOR

class Cnab240GeneratorTest extends FixtureApplicationTest{

    def 'should create remittance header'(){
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

        cnab240.split(SEPARATOR).first() == record.build()
    }

    def 'should create remittance trailer'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid")
        def currentDate = instant("now")
        def generator = new Cnab240Generator(currentDate)

        when:
        String cnab240 = generator.generate(batchClosing)

        then:
        def bankAccount = batchClosing.issuer.paymentAccount.bankAccount
        def record = new FilledRecord(remittanceTrailer) {{
            fill(CODIGO_BANCO, bankAccount.bacenCode)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
            fill(QUANTIDADE_LOTES,"1")
            fill(QUANTIDADE_REGISTROS, "4")
            defaultFill(QUANTIDADE_CONTAS)
            defaultFill(FIM_FEBRABAN)
        }}
        cnab240.split(SEPARATOR).last() == record.build()
    }

    def 'should create batch header'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid")
        def currentDate = instant("now")
        def generator = new Cnab240Generator(currentDate)

        when:
        String cnab240 = generator.generate(batchClosing)

        then:
        def bankAccount = batchClosing.issuer.paymentAccount.bankAccount
        def address = batchClosing.issuer.person.address
        def person = batchClosing.issuer.person
        def record = new FilledRecord(batchHeader) {{
            fill(CODIGO_BANCO, bankAccount.bacenCode)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            defaultFill(TIPO_OPERACAO)
            defaultFill(TIPO_SERVICO)
            fill(FORMA_LANCAMENTO,"1")
            defaultFill(LAYOUT_ARQUIVO)
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
            defaultFill(MENSAGEM)
            fill(LOGRADOURO, address.streetName)
            fill(NUMERO, address.number)
            fill(COMPLEMENTO, address.complement)
            fill(CIDADE, address.city)
            fill(CEP, address.firstZipCode())
            fill(COMPLEMENTO_CEP,address.lastZipeCode())
            fill(ESTADO, address.state.name())
            defaultFill(FIM_FEBRABAN)
            defaultFill(OCORRENCIAS)
        }}
        cnab240.split(SEPARATOR)[1] == record.build()
    }

    def 'should create batch trailer'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid")
        def currentDate = instant("now")
        def generator = new Cnab240Generator(currentDate)

        when:
        String cnab240 = generator.generate(batchClosing)

        then:
        def bankAccount = batchClosing.issuer.paymentAccount.bankAccount
        def record = new FilledRecord(batchTrailer) {{
            fill(CODIGO_BANCO, bankAccount.bacenCode)
            fill(LOTE_SERVICO, "0009")
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
            fill(SOMATORIA_VALORES,"1")
            fill(QUANTIDADE_MOEDAS, batchClosing.value.toString())
            defaultFill(NUMERO_AVISO_DEBITO)
            defaultFill(FIM_FEBRABAN)
            defaultFill(OCORRENCIAS)
        }}
        cnab240.split(SEPARATOR)[2] == record.build()
    }
}
