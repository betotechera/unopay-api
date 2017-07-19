package br.com.unopay.api.payment.cnab240

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.payment.cnab240.filler.FilledRecord
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchHeader
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentB
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchTrailer
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getRemittanceTrailer
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.AGENCIA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.AVISO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BAIRRO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CEP
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CIDADE
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CODIGO_DOCUMENTO_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CODIGO_REMESSA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO_CEP
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CONVEIO_BANCO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_PAGAMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_VENCIMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DENSIDADE_GRAVACAO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_EMPRESA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.ESTADO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DOC
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FORMA_LANCAMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INFORMACAO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INSTITUICAO_MOVIMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LAYOUT_ARQUIVO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOGRADOURO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.MEIO_FEBRABAN
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.MENSAGEM
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NOME_BANCO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NOME_EMPRESA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NOME_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_REGISTRO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_CONTAS
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_LOTES
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDAS
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.RESERVADO_BANCO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.RESERVADO_EMPRESA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEGMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SOMATORIA_VALORES
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO_FAVORECIDO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_MOEDA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_MOVIMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_OPERACAO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_SERVICO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_ABATIMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_DESCONTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_DOCUMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_MORA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_MULTA
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_PAGAMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO
import static br.com.unopay.api.payment.cnab240.filler.RemittanceRecord.SEPARATOR
import br.com.unopay.api.payment.model.PaymentRemittance
import br.com.unopay.api.payment.model.PaymentRemittanceItem

class Cnab240GeneratorTest extends FixtureApplicationTest{

    def 'should create remittance header'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new Cnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        def bankAccount = remittance.issuer.paymentAccount.bankAccount
        def person = remittance.issuer.person
        def record = new FilledRecord(remittanceHeader) {{
                defaultFill(BANCO_COMPENSACAO)
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
                defaultFill(TIPO_INSCRICAO)
                fill(NUMERO_INSCRICAO_EMPRESA, person.document.number)
                fill(CONVEIO_BANCO, remittance.issuer.paymentAccount.bankAgreementNumber)
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
                fill(SEQUENCIAL_ARQUIVO, remittance.number)
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
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new Cnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        def segments = 2
        def headersAndTrailers = 4
        def record = new FilledRecord(remittanceTrailer) {{
            defaultFill(BANCO_COMPENSACAO)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
            fill(QUANTIDADE_LOTES,"1")
            fill(QUANTIDADE_REGISTROS, remittance.getRemittanceItems().size() * segments + headersAndTrailers)
            defaultFill(QUANTIDADE_CONTAS)
            defaultFill(FIM_FEBRABAN)
        }}
        cnab240.split(SEPARATOR).last() == record.build()
    }

    def 'should create batch header'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new Cnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        def bankAccount = remittance.issuer.paymentAccount.bankAccount
        def address = remittance.issuer.person.address
        def person = remittance.issuer.person
        def record = new FilledRecord(batchHeader) {{
            defaultFill(BANCO_COMPENSACAO)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            fill(TIPO_OPERACAO, remittance.operationType.code)
            fill(TIPO_SERVICO, remittance.paymentServiceType.code)
            fill(FORMA_LANCAMENTO,remittance.transferOption.code)
            defaultFill(LAYOUT_ARQUIVO)
            defaultFill(INICIO_FEBRABAN)
            defaultFill(TIPO_INSCRICAO)
            fill(NUMERO_INSCRICAO_EMPRESA, person.document.number)
            fill(CONVEIO_BANCO, remittance.issuer.paymentAccount.bankAgreementNumber)
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
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new Cnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)
        then:
        List<FilledRecord> records = remittance.remittanceItems.withIndex().collect {
            it, index -> createSegmentA(currentDate, it, index)
        }
        cnab240.split(SEPARATOR)[2] == records.find().build()
        cnab240.split(SEPARATOR)[4] == records.last().build()
    }

    def 'should create segment B'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new Cnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        List<FilledRecord> records = remittance.remittanceItems.withIndex().collect {
            it, index -> createSegmentB(currentDate, it, index)
        }
        cnab240.split(SEPARATOR)[3] == records.first().build()
        cnab240.split(SEPARATOR)[5] == records.last().build()
    }

    def 'should create batch trailer'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new Cnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        def segments = 2
        def myPosition = 1
        def headers = 2
        def HEADERS_AND_TRAILERS = 4
        def record = new FilledRecord(batchTrailer) {{
            defaultFill(BANCO_COMPENSACAO)
            fill(LOTE_SERVICO, remittance.remittanceItems.size() * segments + headers + myPosition)
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
            fill(SOMATORIA_VALORES,remittance.total().toString())
            fill(QUANTIDADE_MOEDAS, remittance.total().toString())
            defaultFill(NUMERO_AVISO_DEBITO)
            fill(QUANTIDADE_REGISTROS, remittance.getRemittanceItems().size() * segments + HEADERS_AND_TRAILERS);
            defaultFill(FIM_FEBRABAN)
            defaultFill(OCORRENCIAS)
        }}
        cnab240.split(SEPARATOR)[6] == record.build()
    }


    private FilledRecord createSegmentA(Date currentDate, PaymentRemittanceItem item, Integer index) {
        def headers = 3
        def establishment = item.establishment
        def bankAccount = establishment.bankAccount
        def person = establishment.person
        new FilledRecord(batchSegmentA) {
            {
                defaultFill(BANCO_COMPENSACAO)
                fill(LOTE_SERVICO, index + headers)
                defaultFill(TIPO_REGISTRO)
                fill(NUMERO_REGISTRO, index + headers)
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
                fill(QUANTIDADE_MOEDA, item.getValue().toString())
                fill(VALOR_PAGAMENTO, item.getValue().toString())
                defaultFill(DOCUMENTO_ATRIBUIDO_BANCO)
                defaultFill(DATA_REAL_PAGAMENTO)
                fill(VALOR_REAL_PAGAMENTO, item.getValue().toString())
                defaultFill(INFORMACAO)
                defaultFill(FINALIDADE_DOC)
                defaultFill(FINALIDADE_TED)
                defaultFill(FIM_FEBRABAN)
                defaultFill(AVISO)
                defaultFill(OCORRENCIAS)
            }
        }
    }

    private FilledRecord createSegmentB(Date currentDate, PaymentRemittanceItem item, Integer index) {
        def latest = 4
        def establishment = item.establishment
        def address = establishment.person.address
        def person = establishment.person
        new FilledRecord(batchSegmentB) {
            {
                defaultFill(BANCO_COMPENSACAO)
                fill(LOTE_SERVICO, latest + index)
                defaultFill(TIPO_REGISTRO)
                fill(NUMERO_REGISTRO, latest + index)
                defaultFill(SEGMENTO)
                defaultFill(INICIO_FEBRABAN)
                defaultFill(TIPO_INSCRICAO_FAVORECIDO)
                fill(NUMERO_INSCRICAO_FAVORECIDO, person.getDocument().getNumber())
                fill(LOGRADOURO, address.getStreetName())
                fill(NUMERO, address.getNumber())
                fill(COMPLEMENTO, address.getComplement())
                fill(BAIRRO, address.getDistrict())
                fill(CIDADE, address.getCity())
                fill(CEP, address.firstZipCode())
                fill(COMPLEMENTO_CEP, address.lastZipeCode())
                fill(ESTADO, address.getState().name())
                fill(DATA_VENCIMENTO, currentDate.format("ddMMyyyy"))
                fill(VALOR_DOCUMENTO, item.getValue().toString())
                defaultFill(VALOR_ABATIMENTO)
                defaultFill(VALOR_DESCONTO)
                defaultFill(VALOR_MORA)
                defaultFill(VALOR_MULTA)
                fill(CODIGO_DOCUMENTO_FAVORECIDO, person.getDocument().getNumber())
                defaultFill(FIM_FEBRABAN)
            }
        }
    }
}