package br.com.unopay.api.billing.remittance.cnab240

import br.com.six2six.fixturefactory.Fixture
import br.com.six2six.fixturefactory.Rule
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord
import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getBatchHeader
import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getBatchSegmentA
import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getBatchSegmentB
import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getBatchTrailer
import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.billing.remittance.cnab240.filler.BradescoRemittanceLayout.getRemittanceTrailer
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AVISO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BAIRRO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_FAVORECIDO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CEP
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CIDADE
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_DOCUMENTO_FAVORECIDO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_REMESSA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO_CEP
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CONVEIO_BANCO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_VENCIMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DENSIDADE_GRAVACAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_EMPRESA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.ESTADO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DOC
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FORMA_LANCAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INFORMACAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INSTITUICAO_MOVIMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LAYOUT_ARQUIVO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOGRADOURO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.MEIO_FEBRABAN
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.MENSAGEM
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_BANCO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_EMPRESA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_FAVORECIDO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_REGISTRO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_CONTAS
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_LOTES
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDAS
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.RESERVADO_BANCO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.RESERVADO_EMPRESA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEGMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SOMATORIA_VALORES
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO_FAVORECIDO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOEDA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOVIMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_OPERACAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_SERVICO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_ABATIMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_DESCONTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_DOCUMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_MORA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_MULTA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceRecord.SEPARATOR
import br.com.unopay.api.billing.remittance.model.PaymentOperationType
import br.com.unopay.api.billing.remittance.model.PaymentRemittance
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem
import br.com.unopay.api.billing.remittance.model.RemittancePayer
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.util.Rounder
import spock.lang.Unroll

class BradescoCnab240GeneratorTest extends FixtureApplicationTest{

    @Unroll
    'should create remittance header for #operation operation'(){
        given:
        def operationType = operation
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems", new Rule(){{
            add("operationType", operationType)
        }})
        def currentDate = instant("now")
        def generator = new BradescoCnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        def payer = remittance.payer
        def record = new FilledRecord(remittanceHeader) {{
                fill(BANCO_COMPENSACAO, payer.getBankCode())
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(INICIO_FEBRABAN)
                defaultFill(TIPO_INSCRICAO)
                fill(NUMERO_INSCRICAO_EMPRESA, payer.documentNumber)
                if(!remittance.forDebit()) {
                    fill(CONVEIO_BANCO, payer.bankAgreementNumberForCredit)
                }
                if(remittance.forDebit()) {
                    fill(CONVEIO_BANCO, payer.bankAgreementNumberForDebit)
                }
                fill(AGENCIA, payer.agency)
                fill(DIGITO_AGENCIA, payer.agentDvFirstDigit())
                fill(NUMERO_CONTA, payer.accountNumber)
                fill(DIGITO_CONTA, payer.accountDvFirstDigit())
                fill(DIGITO_AGENCIA_CONTA, payer.accountDvLastDigit())
                fill(NOME_EMPRESA, payer.name)
                fill(NOME_BANCO, payer.getBankName())
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

        where:
        _ | operation
        _ | PaymentOperationType.CREDIT
        _ | PaymentOperationType.DEBIT
    }

    def 'should create remittance trailer'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new BradescoCnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        def segments = 2
        def headersAndTrailers = 4
        def record = new FilledRecord(remittanceTrailer) {{
            fill(BANCO_COMPENSACAO, remittance.payer.getBankCode()).
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
        def generator = new BradescoCnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        RemittancePayer payer = remittance.payer
        def record = new FilledRecord(batchHeader) {{
            fill(BANCO_COMPENSACAO, payer.getBankCode()).
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            fill(TIPO_OPERACAO, remittance.operationType.code)
            fill(TIPO_SERVICO, remittance.paymentServiceType.code)
            fill(FORMA_LANCAMENTO,remittance.remittanceItems.find().transferOption.code)
            defaultFill(LAYOUT_ARQUIVO)
            defaultFill(INICIO_FEBRABAN)
            defaultFill(TIPO_INSCRICAO)
            fill(NUMERO_INSCRICAO_EMPRESA, payer.documentNumber)
            fill(CONVEIO_BANCO, payer.bankAgreementNumberForCredit)
            fill(AGENCIA, payer.agency)
            fill(DIGITO_AGENCIA, payer.agentDvFirstDigit())
            fill(NUMERO_CONTA, payer.accountNumber)
            fill(DIGITO_CONTA, payer.accountDvFirstDigit())
            fill(DIGITO_AGENCIA_CONTA, payer.accountDvLastDigit())
            fill(NOME_EMPRESA, payer.name)
            defaultFill(MENSAGEM)
            fill(LOGRADOURO, payer.streetName)
            fill(NUMERO, payer.number)
            fill(COMPLEMENTO, payer.complement)
            fill(CIDADE, payer.city)
            fill(CEP, payer.firstZipCode())
            fill(COMPLEMENTO_CEP,payer.lastZipCode())
            fill(ESTADO, payer.state.name())
            defaultFill(FIM_FEBRABAN)
            defaultFill(OCORRENCIAS)
        }}
        cnab240.split(SEPARATOR)[1] == record.build()
    }

    def 'should create segment A'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new BradescoCnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)
        then:
        int index = 0
        List<FilledRecord> records = remittance.remittanceItems.collect {
            def segmentA = createSegmentA(currentDate, it, index)
            index+=4
            segmentA
        }
        cnab240.split(SEPARATOR)[2] == records.find().build()
        cnab240.split(SEPARATOR)[6] == records.last().build()
    }

    def 'should create segment B'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new BradescoCnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        int index = 0
        List<FilledRecord> records = remittance.remittanceItems.collect {
            def segmentB = createSegmentB(currentDate, it, index)
            index=+4
            segmentB
        }
        cnab240.split(SEPARATOR)[3] == records.first().build()
        cnab240.split(SEPARATOR)[7] == records.last().build()
    }

    def 'should create batch trailer'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new BradescoCnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        def segments = 2
        def HEADERS_AND_TRAILERS = 4
        def record = new FilledRecord(batchTrailer) {{
            fill(BANCO_COMPENSACAO, remittance.payer.getBankCode()).
            fill(LOTE_SERVICO, "8")
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
            fill(SOMATORIA_VALORES,Rounder.roundToString(remittance.getTotal()))
            fill(QUANTIDADE_MOEDAS, Rounder.roundToString(remittance.getTotal()))
            defaultFill(NUMERO_AVISO_DEBITO)
            fill(QUANTIDADE_REGISTROS, remittance.getRemittanceItems().size() * segments + HEADERS_AND_TRAILERS);
            defaultFill(FIM_FEBRABAN)
            defaultFill(OCORRENCIAS)
        }}
        cnab240.split(SEPARATOR)[8] == record.build()
    }


    private FilledRecord createSegmentA(Date currentDate, PaymentRemittanceItem item, Integer index) {
        def headers = 2
        def payee = item.payee
        def payer = item.paymentRemittance.payer
        new FilledRecord(batchSegmentA) {
            {
                fill(BANCO_COMPENSACAO, payer.getBankCode())
                fill(LOTE_SERVICO, index + headers)
                defaultFill(TIPO_REGISTRO)
                fill(NUMERO_REGISTRO, index + headers)
                defaultFill(SEGMENTO)
                defaultFill(TIPO_MOVIMENTO)
                defaultFill(INSTITUICAO_MOVIMENTO)
                defaultFill(CAMARA_CENTRALIZADORA)
                fill(BANCO_FAVORECIDO, payee.getBankCode())
                fill(AGENCIA, payee.agentDvFirstDigit())
                fill(DIGITO_AGENCIA, payee.agentDvLastDigit())
                fill(NUMERO_CONTA, payee.getAccountNumber())
                fill(DIGITO_CONTA, payee.accountDvFirstDigit())
                fill(DIGITO_AGENCIA_CONTA, payee.accountDvLastDigit())
                fill(NOME_FAVORECIDO, payee.name)
                fill(DOCUMENTO_ATRIBUIDO_EMPRESA, payee.documentNumber)
                fill(DATA_PAGAMENTO, currentDate.format("ddMMyyyy"))
                defaultFill(TIPO_MOEDA)
                defaultFill(QUANTIDADE_MOEDA)
                fill(VALOR_PAGAMENTO, Rounder.roundToString(item.getValue()))
                defaultFill(DOCUMENTO_ATRIBUIDO_BANCO)
                defaultFill(DATA_REAL_PAGAMENTO)
                fill(VALOR_REAL_PAGAMENTO, Rounder.roundToString(item.getValue()))
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
        def latest = 3
        def payee = item.payee
        def payer = item.paymentRemittance.payer
        new FilledRecord(batchSegmentB) {
            {
                fill(BANCO_COMPENSACAO, payer.getBankCode())
                fill(LOTE_SERVICO, latest + index)
                defaultFill(TIPO_REGISTRO)
                fill(NUMERO_REGISTRO, latest + index)
                defaultFill(SEGMENTO)
                defaultFill(INICIO_FEBRABAN)
                defaultFill(TIPO_INSCRICAO_FAVORECIDO)
                fill(NUMERO_INSCRICAO_FAVORECIDO, payee.documentNumber)
                fill(LOGRADOURO, payee.getStreetName())
                fill(NUMERO, payee.getNumber())
                fill(COMPLEMENTO, payee.getComplement())
                fill(BAIRRO, payee.getDistrict())
                fill(CIDADE, payee.getCity())
                fill(CEP, payee.firstZipCode())
                fill(COMPLEMENTO_CEP, payee.lastZipCode())
                fill(ESTADO, payee.state.name())
                fill(DATA_VENCIMENTO, currentDate.format("ddMMyyyy"))
                fill(VALOR_DOCUMENTO, Rounder.roundToString(item.getValue()))
                defaultFill(VALOR_ABATIMENTO)
                defaultFill(VALOR_DESCONTO)
                defaultFill(VALOR_MORA)
                defaultFill(VALOR_MULTA)
                fill(CODIGO_DOCUMENTO_FAVORECIDO, payee.documentNumber)
                defaultFill(FIM_FEBRABAN)
            }
        }
    }
}
