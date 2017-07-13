package br.com.unopay.api.payment.cnab240

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.model.BatchClosing
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchHeader
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchSegmentA
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchSegmentB
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getBatchTrailer
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.payment.cnab240.RemittanceLayout.getRemittanceTrailer
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.AGENCIA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.AVISO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.BAIRRO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.BANCO_COMPENSACAO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.BANCO_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CEP
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CIDADE
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_DOCUMENTO_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_REMESSA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO_CEP
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CONVEIO_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_PAGAMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_VENCIMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DENSIDADE_GRAVACAO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_CONTA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DOCUMENTO_EMPRESA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.ESTADO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FIM_FEBRABAN
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FINALIDADE_DOC
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FINALIDADE_TED
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FORMA_LANCAMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INFORMACAO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INICIO_FEBRABAN
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INSTITUICAO_MOVIMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LAYOUT_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOGRADOURO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOTE_SERVICO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MEIO_FEBRABAN
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MENSAGEM
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_EMPRESA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_CONTA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_REGISTRO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.OCORRENCIAS
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_CONTAS
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_LOTES
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_MOEDA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_MOEDAS
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_REGISTROS
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_BANCO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_EMPRESA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SEGMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SOMATORIA_VALORES
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_INSCRICAO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_INSCRICAO_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_MOEDA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_MOVIMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_OPERACAO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_REGISTRO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_SERVICO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_ABATIMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_DESCONTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_DOCUMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_MORA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_MULTA
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_PAGAMENTO
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO
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
                fill(BANCO_COMPENSACAO, bankAccount.bacenCode)
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
            fill(BANCO_COMPENSACAO, bankAccount.bacenCode)
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
            fill(BANCO_COMPENSACAO, bankAccount.bacenCode)
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

    def 'should create segment A'(){
        given:
        BatchClosing batchClosing = Fixture.from(BatchClosing.class).gimme("valid")
        def currentDate = instant("now")
        def generator = new Cnab240Generator(currentDate)

        when:
        String cnab240 = generator.generate(batchClosing)

        then:
        def bankAccount = batchClosing.establishment.bankAccount
        def establishment = batchClosing.establishment
        def person = establishment.person
        def record = new FilledRecord(batchSegmentA) {{
            fill(BANCO_COMPENSACAO, bankAccount.bacenCode)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            fill(NUMERO_REGISTRO, "1")
            defaultFill(SEGMENTO)
            defaultFill(TIPO_MOVIMENTO)
            defaultFill(INSTITUICAO_MOVIMENTO)
            defaultFill(CAMARA_CENTRALIZADORA)
            fill(BANCO_FAVORECIDO, bankAccount.getBacenCode())
            fill(AGENCIA, bankAccount.agentDvFirstDigit())
            fill(DIGITO_AGENCIA, bankAccount.agentDvLastDigit())
            fill(NUMERO_CONTA, bankAccount.getAccountNumber())
            fill(DIGITO_CONTA, bankAccount.accountDvFirstDigit())
            fill(DIGITO_AGENCIA_CONTA, bankAccount.accountDvLastDigit())
            fill(NOME_FAVORECIDO, person.name)
            fill(DOCUMENTO_EMPRESA, person.document.number)
            fill(DATA_PAGAMENTO, currentDate.format("ddMMyyyy"))
            defaultFill(TIPO_MOEDA)
            fill(QUANTIDADE_MOEDA, batchClosing.getValue().toString())
            fill(VALOR_PAGAMENTO, batchClosing.getValue().toString())
            defaultFill(DOCUMENTO_ATRIBUIDO_BANCO)
            defaultFill(DATA_REAL_PAGAMENTO)
            fill(VALOR_REAL_PAGAMENTO, batchClosing.getValue().toString())
            defaultFill(INFORMACAO)
            defaultFill(FINALIDADE_DOC)
            defaultFill(FINALIDADE_TED)
            defaultFill(FIM_FEBRABAN)
            defaultFill(AVISO)
            defaultFill(OCORRENCIAS)
        }}
        cnab240.split(SEPARATOR)[2] == record.build()
    }


    def 'should create segment B'(){
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
        def record = new FilledRecord(batchSegmentB) {{
            fill(BANCO_COMPENSACAO, bankAccount.bacenCode)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            fill(NUMERO_REGISTRO, "1")
            defaultFill(SEGMENTO)
            defaultFill(INICIO_FEBRABAN)
            defaultFill(TIPO_INSCRICAO_FAVORECIDO)
            fill(NUMERO_INSCRICAO_FAVORECIDO,person.getDocument().getNumber())
            fill(LOGRADOURO, address.getStreetName())
            fill(NUMERO, address.getNumber())
            fill(COMPLEMENTO, address.getComplement())
            fill(BAIRRO, address.getDistrict())
            fill(CIDADE, address.getCity())
            fill(CEP, address.firstZipCode())
            fill(COMPLEMENTO_CEP, address.lastZipeCode())
            fill(ESTADO, address.getState().name())
            fill(DATA_VENCIMENTO, currentDate.format("ddMMyyyy"))
            fill(VALOR_DOCUMENTO, batchClosing.getValue().toString())
            defaultFill(VALOR_ABATIMENTO)
            defaultFill(VALOR_DESCONTO)
            defaultFill(VALOR_MORA)
            defaultFill(VALOR_MULTA)
            fill(CODIGO_DOCUMENTO_FAVORECIDO, person.getDocument().getNumber())
            defaultFill(FIM_FEBRABAN)
        }}
        cnab240.split(SEPARATOR)[3] == record.build()
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
            fill(BANCO_COMPENSACAO, bankAccount.bacenCode)
            fill(LOTE_SERVICO, "0009")
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
            fill(SOMATORIA_VALORES,"1")
            fill(QUANTIDADE_MOEDAS, batchClosing.value.toString())
            defaultFill(NUMERO_AVISO_DEBITO)
            defaultFill(FIM_FEBRABAN)
            defaultFill(OCORRENCIAS)
        }}
        cnab240.split(SEPARATOR)[4] == record.build()
    }
}
