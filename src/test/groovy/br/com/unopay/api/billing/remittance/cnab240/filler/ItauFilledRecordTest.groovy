package br.com.unopay.api.billing.remittance.cnab240.filler

import br.com.unopay.api.FixtureApplicationTest
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
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CEP
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CIDADE
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_ISPB
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_REMESSA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CONVEIO_BANCO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DENSIDADE_GRAVACAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_EMPRESA
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
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.MEIO_FEBRABAN
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
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SOMATORIA_VALORES
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOEDA
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOVIMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_OPERACAO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_SERVICO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_PAGAMENTO
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class ItauFilledRecordTest extends FixtureApplicationTest {

    def 'given a unknown key should return error'(){
        when:
        new FilledRecord(new HashMap<String, RecordColumnRule>()).fill("key", "1")

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RULE_COLUMN_REQUIRED'
    }

    def 'remittance header should be 240 characters'(){
        when:
        def record = new FilledRecord(remittanceHeader) {{
            defaultFill(BANCO_COMPENSACAO)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            defaultFill(BRANCOS_1)
            defaultFill(LAYOUT_ARQUIVO)
            defaultFill(TIPO_INSCRICAO)
            defaultFill(NUMERO_INSCRICAO_EMPRESA)
            defaultFill(CONVEIO_BANCO)
            defaultFill(AGENCIA)
            defaultFill(DIGITO_AGENCIA)
            defaultFill(NUMERO_CONTA)
            defaultFill(DIGITO_CONTA)
            defaultFill(DIGITO_AGENCIA_CONTA)
            defaultFill(NOME_EMPRESA)
            defaultFill(NOME_BANCO)
            defaultFill(MEIO_FEBRABAN)
            defaultFill(CODIGO_REMESSA)
            defaultFill(DATA_GERACAO_ARQUIVO)
            defaultFill(HORA_GERACAO_ARQUIVO)
            defaultFill(BRANCOS_2)
            defaultFill(DENSIDADE_GRAVACAO)
            defaultFill(RESERVADO_BANCO)
        }}
        then:
        record.build().size() == 240
    }

    def 'remittance trailer should be 240 characters'(){
        when:
        def record = new FilledRecord(remittanceTrailer) {{
            defaultFill(BANCO_COMPENSACAO)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
            defaultFill(QUANTIDADE_LOTES)
            defaultFill(QUANTIDADE_REGISTROS)
            defaultFill(BRANCOS_1)
        }}
        then:
        record.build().size() == 240
    }

    def 'batch header should be 240 characters'(){
        when:
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
            defaultFill(NUMERO_INSCRICAO_EMPRESA)
            defaultFill(IDENTIFICACAO_LANCAMENTO)
            defaultFill(BRANCOS_1)
            defaultFill(AGENCIA)
            defaultFill(BRANCOS_2)
            defaultFill(NUMERO_CONTA)
            defaultFill(BRANCOS_3)
            defaultFill(DIGITO_CONTA)
            defaultFill(NOME_EMPRESA)
            defaultFill(FINALIDADE_COD)
            defaultFill(HISTORICO_CC)
            defaultFill(LOGRADOURO)
            defaultFill(NUMERO)
            defaultFill(COMPLEMENTO)
            defaultFill(CIDADE)
            defaultFill(CEP)
            defaultFill(ESTADO)
            defaultFill(FIM_FEBRABAN)
            defaultFill(OCORRENCIAS)
        }}

        then:
        record.build().size() == 240
    }

    def 'batch segment A should be 240 characters'(){
        when:
        def record =  new FilledRecord(batchSegmentA) {
            {
                defaultFill(BANCO_COMPENSACAO)
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(NUMERO_REGISTRO)
                defaultFill(SEGMENTO)
                defaultFill(TIPO_MOVIMENTO)
                defaultFill(CAMARA_CENTRALIZADORA)
                defaultFill(BANCO_FAVORECIDO)
                defaultFill(AGENCIA_CONTA)
                defaultFill(NOME_FAVORECIDO)
                defaultFill(DOCUMENTO_ATRIBUIDO_EMPRESA)
                defaultFill(DATA_PAGAMENTO)
                defaultFill(TIPO_MOEDA)
                defaultFill(CODIGO_ISPB)
                defaultFill(BRANCOS_1)
                defaultFill(VALOR_PAGAMENTO)
                defaultFill(NOSSO_NUMERO)
                defaultFill(BRANCOS_2)
                defaultFill(DATA_REAL_PAGAMENTO)
                defaultFill(VALOR_REAL_PAGAMENTO)
                defaultFill(FINALIDADE_DETALHE)
                defaultFill(BRANCOS_3)
                defaultFill(NUMERO_DOCUMENTO)
                defaultFill(NUMERO_INSCRICAO_FAVORECIDO)
                defaultFill(FINALIDADE_DOC)
                defaultFill(FINALIDADE_TED)
                defaultFill(FIM_FEBRABAN)
                defaultFill(AVISO)
                defaultFill(OCORRENCIAS)
            }
        }

        then:
        record.build().size() == 240
    }


    def 'batch trailer should be 240 characters'(){
        when:
        def record = new FilledRecord(batchTrailer) {{
            defaultFill(BANCO_COMPENSACAO)
            defaultFill(LOTE_SERVICO)
            defaultFill(TIPO_REGISTRO)
            defaultFill(INICIO_FEBRABAN)
            defaultFill(QUANTIDADE_REGISTROS)
            defaultFill(SOMATORIA_VALORES)
            defaultFill(BRANCOS_1)
            defaultFill(BRANCOS_2)
            defaultFill(OCORRENCIAS)
        }}

        then:
        record.build().size() == 240
    }

    def 'given a record without all layout keys filled should return error'(){
        given:
        def record =  new FilledRecord(batchSegmentA) {
            {
                defaultFill(BANCO_COMPENSACAO)
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(NUMERO_REGISTRO)
                defaultFill(TIPO_MOVIMENTO)
                defaultFill(CAMARA_CENTRALIZADORA)
                defaultFill(BANCO_FAVORECIDO)
                defaultFill(AGENCIA_CONTA)
                defaultFill(NOME_FAVORECIDO)
                defaultFill(DOCUMENTO_ATRIBUIDO_EMPRESA)
                defaultFill(DATA_PAGAMENTO)
                defaultFill(TIPO_MOEDA)
                defaultFill(CODIGO_ISPB)
                defaultFill(BRANCOS_1)
                defaultFill(VALOR_PAGAMENTO)
                defaultFill(NOSSO_NUMERO)
                defaultFill(BRANCOS_2)
                defaultFill(DATA_REAL_PAGAMENTO)
                defaultFill(VALOR_REAL_PAGAMENTO)
                defaultFill(FINALIDADE_DETALHE)
                defaultFill(BRANCOS_3)
                defaultFill(NUMERO_DOCUMENTO)
                defaultFill(NUMERO_INSCRICAO_FAVORECIDO)
                defaultFill(FINALIDADE_DOC)
                defaultFill(FINALIDADE_TED)
                defaultFill(FIM_FEBRABAN)
                defaultFill(AVISO)
                defaultFill(OCORRENCIAS)
            }
        }
        when:
        record.build()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'LAYOUT_COLUMN_NOT_FILLED'
        assert ex.errors.first().arguments.find() == '[segmento]'
    }

    def 'given a unknown key should return error when fill'(){
        when:
        new FilledRecord(new HashMap<String, RecordColumnRule>()).defaultFill("key")

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RULE_COLUMN_REQUIRED'
    }
}
