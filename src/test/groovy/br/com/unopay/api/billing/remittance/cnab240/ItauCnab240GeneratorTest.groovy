package br.com.unopay.api.billing.remittance.cnab240

import br.com.six2six.fixturefactory.Fixture
import br.com.unopay.api.FixtureApplicationTest
import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord
import static br.com.unopay.api.billing.remittance.cnab240.filler.ItauRemittanceLayout.getBatchHeader
import static br.com.unopay.api.billing.remittance.cnab240.filler.ItauRemittanceLayout.getBatchSegmentA
import static br.com.unopay.api.billing.remittance.cnab240.filler.ItauRemittanceLayout.getBatchTrailer
import static br.com.unopay.api.billing.remittance.cnab240.filler.ItauRemittanceLayout.getRemittanceHeader
import static br.com.unopay.api.billing.remittance.cnab240.filler.ItauRemittanceLayout.getRemittanceTrailer
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA_CONTA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AVISO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_FAVORECIDO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_1
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_2
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_3
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_5
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_6
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCO_4
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CEP
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CIDADE
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_ISPB
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_REMESSA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DENSIDADE_GRAVACAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.ESTADO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_COD
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DETALHE
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DOC
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FORMA_LANCAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.HISTORICO_CC
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.IDENTIFICACAO_LANCAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LAYOUT_ARQUIVO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOGRADOURO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_BANCO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_EMPRESA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_FAVORECIDO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOSSO_NUMERO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_DOCUMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_REGISTRO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_LOTES
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.RESERVADO_BANCO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEGMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEU_NUMERO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SOMATORIA_VALORES
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOEDA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOVIMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_OPERACAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_SERVICO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceRecord.SEPARATOR
import br.com.unopay.api.billing.remittance.model.ItauAccountField
import br.com.unopay.api.billing.remittance.model.PaymentRemittance
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem
import br.com.unopay.api.billing.remittance.model.RemittancePayer
import static br.com.unopay.api.function.FixtureFunctions.instant
import br.com.unopay.api.util.Rounder

class ItauCnab240GeneratorTest extends FixtureApplicationTest{

    def 'should create remittance header'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new ItauCnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        def payer = remittance.payer
        def record = new FilledRecord(remittanceHeader) {{
                defaultFill(BANCO_COMPENSACAO)
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(BRANCOS_1)
                defaultFill(LAYOUT_ARQUIVO)
                defaultFill(TIPO_INSCRICAO)
                fill(NUMERO_INSCRICAO_EMPRESA, payer.documentNumber)
                defaultFill(BRANCOS_2)
                fill(AGENCIA, payer.agency)
                defaultFill(BRANCOS_3)
                fill(NUMERO_CONTA, payer.accountNumber)
                defaultFill(BRANCO_4)
                fill(DIGITO_AGENCIA_CONTA, payer.accountDvLastDigit())
                fill(NOME_EMPRESA, payer.name)
                fill(NOME_BANCO, payer.getBankName())
                defaultFill(BRANCOS_5)
                defaultFill(CODIGO_REMESSA)
                fill(DATA_GERACAO_ARQUIVO, currentDate.format("ddMMyyyy"))
                fill(HORA_GERACAO_ARQUIVO, currentDate.format("hhmmss"))
                defaultFill(BRANCOS_6)
                defaultFill(DENSIDADE_GRAVACAO)
                defaultFill(RESERVADO_BANCO)
            }}

        cnab240.split(SEPARATOR).first() == record.build()

    }

    def 'should create remittance trailer'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new ItauCnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        def RemittanceHeaderAndTrailer = 2
        def batchItems = 3
        def record = new FilledRecord(remittanceTrailer) {{
            defaultFill(BANCO_COMPENSACAO)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
            fill(QUANTIDADE_LOTES,remittance.getRemittanceItems().size())
            fill(QUANTIDADE_REGISTROS, (remittance.getRemittanceItems().size() *  batchItems) + RemittanceHeaderAndTrailer)
            defaultFill(BRANCOS_1)
        }}
        cnab240.split(SEPARATOR).last() == record.build()
    }

    def 'should create batch header'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new ItauCnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        RemittancePayer payer = remittance.payer
        def record = new FilledRecord(batchHeader) {{
            defaultFill(BANCO_COMPENSACAO)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            defaultFill(TIPO_OPERACAO)
            defaultFill(TIPO_SERVICO)
            defaultFill(FORMA_LANCAMENTO)
            defaultFill(LAYOUT_ARQUIVO)
            defaultFill(INICIO_FEBRABAN)
            defaultFill(TIPO_INSCRICAO)
            fill(NUMERO_INSCRICAO_EMPRESA, payer.documentNumber)
            defaultFill(IDENTIFICACAO_LANCAMENTO)
            defaultFill(BRANCOS_1)
            fill(AGENCIA, payer.agency)
            defaultFill(BRANCOS_2)
            fill(NUMERO_CONTA, payer.accountNumber)
            defaultFill(BRANCOS_3)
            fill(DIGITO_AGENCIA_CONTA, payer.accountDvLastDigit())
            fill(NOME_EMPRESA, payer.name)
            defaultFill(FINALIDADE_COD)
            defaultFill(HISTORICO_CC)
            fill(LOGRADOURO, payer.streetName)
            fill(NUMERO, payer.number)
            fill(COMPLEMENTO, payer.complement)
            fill(CIDADE, payer.city)
            fill(CEP, payer.zipCode)
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
        def generator = new ItauCnab240Generator()
        def bachLines = 3
        when:
        String cnab240 = generator.generate(remittance, currentDate)
        then:
        int index = 0
        List<FilledRecord> records = remittance.remittanceItems.collect {
            def segmentA = createSegmentA(it, index)
            index+= bachLines
            segmentA
        }
        cnab240.split(SEPARATOR)[2] == records.find().build()
        cnab240.split(SEPARATOR)[6] == records.last().build()
    }

    def 'should create batch trailer'(){
        given:
        PaymentRemittance remittance = Fixture.from(PaymentRemittance.class).gimme("withItems")
        def currentDate = instant("now")
        def generator = new ItauCnab240Generator()

        when:
        String cnab240 = generator.generate(remittance, currentDate)

        then:
        def segments = 1
        def HEADERS_AND_TRAILERS = 4
        def record = new FilledRecord(batchTrailer) {{
            defaultFill(BANCO_COMPENSACAO)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
            fill(QUANTIDADE_REGISTROS, remittance.getRemittanceItems().size() * segments + HEADERS_AND_TRAILERS)
            fill(SOMATORIA_VALORES,Rounder.roundToString(remittance.getTotal()))
            defaultFill(BRANCOS_1)
            defaultFill(BRANCOS_2)
            defaultFill(OCORRENCIAS)
        }}
        cnab240.split(SEPARATOR)[7] == record.build()
    }


    private FilledRecord createSegmentA(PaymentRemittanceItem item, Integer index) {
        def headers = 2
        def payee = item.payee
        new FilledRecord(batchSegmentA) {
            {
                defaultFill(BANCO_COMPENSACAO)
                fill(LOTE_SERVICO, index + headers)
                defaultFill(TIPO_REGISTRO)
                fill(NUMERO_REGISTRO, index + headers)
                defaultFill(SEGMENTO)
                defaultFill(TIPO_MOVIMENTO)
                defaultFill(CAMARA_CENTRALIZADORA)
                fill(BANCO_FAVORECIDO, payee.getBankCode())
                fill(AGENCIA_CONTA, new ItauAccountField(payee).get())
                fill(NOME_FAVORECIDO, payee.getName())
                defaultFill(SEU_NUMERO)
                defaultFill(DATA_PAGAMENTO)
                defaultFill(TIPO_MOEDA)
                defaultFill(CODIGO_ISPB)
                defaultFill(BRANCOS_1)
                fill(VALOR_PAGAMENTO, Rounder.roundToString(item.getValue()))
                defaultFill(NOSSO_NUMERO)
                defaultFill(BRANCOS_2)
                defaultFill(DATA_REAL_PAGAMENTO)
                defaultFill(VALOR_REAL_PAGAMENTO)
                defaultFill(FINALIDADE_DETALHE)
                defaultFill(BRANCOS_3)
                defaultFill(NUMERO_DOCUMENTO)
                fill(NUMERO_INSCRICAO_FAVORECIDO, payee.documentNumber)
                defaultFill(FINALIDADE_DOC)
                defaultFill(FINALIDADE_TED)
                defaultFill(FIM_FEBRABAN)
                defaultFill(AVISO)
                defaultFill(OCORRENCIAS)
            }
        }
    }

}
