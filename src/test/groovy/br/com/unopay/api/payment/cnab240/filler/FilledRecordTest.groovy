package br.com.unopay.api.payment.cnab240.filler

import br.com.unopay.api.FixtureApplicationTest
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
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_EMPRESA
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
import br.com.unopay.bootcommons.exception.UnprocessableEntityException

class FilledRecordTest extends FixtureApplicationTest {

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
            defaultFill(INICIO_FEBRABAN)
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
            defaultFill(SEQUENCIAL_ARQUIVO)
            defaultFill(LAYOUT_ARQUIVO)
            defaultFill(DENSIDADE_GRAVACAO)
            defaultFill(RESERVADO_BANCO)
            defaultFill(RESERVADO_EMPRESA)
            defaultFill(FIM_FEBRABAN)
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
            defaultFill(QUANTIDADE_CONTAS)
            defaultFill(FIM_FEBRABAN)
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
            defaultFill(CONVEIO_BANCO)
            defaultFill(AGENCIA)
            defaultFill(DIGITO_AGENCIA)
            defaultFill(NUMERO_CONTA)
            defaultFill(DIGITO_CONTA)
            defaultFill(DIGITO_AGENCIA_CONTA)
            defaultFill(NOME_EMPRESA)
            defaultFill(MENSAGEM)
            defaultFill(LOGRADOURO)
            defaultFill(NUMERO)
            defaultFill(COMPLEMENTO)
            defaultFill(CIDADE)
            defaultFill(CEP)
            defaultFill(COMPLEMENTO_CEP)
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
                defaultFill(INSTITUICAO_MOVIMENTO)
                defaultFill(CAMARA_CENTRALIZADORA)
                defaultFill(BANCO_FAVORECIDO)
                defaultFill(AGENCIA)
                defaultFill(DIGITO_AGENCIA)
                defaultFill(NUMERO_CONTA)
                defaultFill(DIGITO_CONTA)
                defaultFill(DIGITO_AGENCIA_CONTA)
                defaultFill(NOME_FAVORECIDO)
                defaultFill(DOCUMENTO_ATRIBUIDO_EMPRESA)
                defaultFill(DATA_PAGAMENTO)
                defaultFill(TIPO_MOEDA)
                defaultFill(QUANTIDADE_MOEDA)
                defaultFill(VALOR_PAGAMENTO)
                defaultFill(DOCUMENTO_ATRIBUIDO_BANCO)
                defaultFill(DATA_REAL_PAGAMENTO)
                defaultFill(VALOR_REAL_PAGAMENTO)
                defaultFill(INFORMACAO)
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

    def 'batch segment B should be 240 characters'(){
        when:
        def record = new FilledRecord(batchSegmentB) {
            {
                defaultFill(BANCO_COMPENSACAO)
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(NUMERO_REGISTRO)
                defaultFill(SEGMENTO)
                defaultFill(INICIO_FEBRABAN)
                defaultFill(TIPO_INSCRICAO_FAVORECIDO)
                defaultFill(NUMERO_INSCRICAO_FAVORECIDO)
                defaultFill(LOGRADOURO)
                defaultFill(NUMERO)
                defaultFill(COMPLEMENTO)
                defaultFill(BAIRRO)
                defaultFill(CIDADE)
                defaultFill(CEP)
                defaultFill(COMPLEMENTO_CEP)
                defaultFill(ESTADO)
                defaultFill(DATA_VENCIMENTO)
                defaultFill(VALOR_DOCUMENTO)
                defaultFill(VALOR_ABATIMENTO)
                defaultFill(VALOR_DESCONTO)
                defaultFill(VALOR_MORA)
                defaultFill(VALOR_MULTA)
                defaultFill(CODIGO_DOCUMENTO_FAVORECIDO)
                defaultFill(FIM_FEBRABAN)
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
            defaultFill(SOMATORIA_VALORES)
            defaultFill(QUANTIDADE_MOEDAS)
            defaultFill(NUMERO_AVISO_DEBITO)
            defaultFill(QUANTIDADE_REGISTROS)
            defaultFill(FIM_FEBRABAN)
            defaultFill(OCORRENCIAS)
        }}

        then:
        record.build().size() == 240
    }

    def 'given a record without all layout keys filled should return error'(){
        given:
        def record = new FilledRecord(batchSegmentB) {
            {
                defaultFill(BANCO_COMPENSACAO)
                defaultFill(LOTE_SERVICO)
                defaultFill(TIPO_REGISTRO)
                defaultFill(NUMERO_REGISTRO)
                defaultFill(SEGMENTO)
                defaultFill(INICIO_FEBRABAN)
                defaultFill(TIPO_INSCRICAO_FAVORECIDO)
                defaultFill(NUMERO_INSCRICAO_FAVORECIDO)
                defaultFill(LOGRADOURO)
                defaultFill(NUMERO)
                defaultFill(COMPLEMENTO)
                defaultFill(CIDADE)
                defaultFill(CEP)
                defaultFill(COMPLEMENTO_CEP)
                defaultFill(ESTADO)
                defaultFill(DATA_VENCIMENTO)
                defaultFill(VALOR_DOCUMENTO)
                defaultFill(VALOR_ABATIMENTO)
                defaultFill(VALOR_DESCONTO)
                defaultFill(VALOR_MORA)
                defaultFill(VALOR_MULTA)
                defaultFill(CODIGO_DOCUMENTO_FAVORECIDO)
                defaultFill(FIM_FEBRABAN)
            }
        }
        when:
        record.build()

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'LAYOUT_COLUMN_NOT_FILLED'
        assert ex.errors.first().arguments.find() == '[bairro]'
    }

    def 'given a unknown key should return error when fill'(){
        when:
        new FilledRecord(new HashMap<String, RecordColumnRule>()).defaultFill("key")

        then:
        def ex = thrown(UnprocessableEntityException)
        assert ex.errors.first().logref == 'RULE_COLUMN_REQUIRED'
    }
}
