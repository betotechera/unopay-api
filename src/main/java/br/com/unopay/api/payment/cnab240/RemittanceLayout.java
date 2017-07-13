package br.com.unopay.api.payment.cnab240;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static br.com.unopay.api.payment.cnab240.ColumnType.ALPHA;
import static br.com.unopay.api.payment.cnab240.ColumnType.NUMBER;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.AVISO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.BAIRRO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.BANCO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CEP;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CIDADE;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_DOCUMENTO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_REMESSA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO_CEP;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CONVEIO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_VENCIMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DENSIDADE_GRAVACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DOCUMENTO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.ESTADO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FINALIDADE_DOC;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FINALIDADE_TED;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FORMA_LANCAMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INFORMACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INSTITUICAO_MOVIMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOGRADOURO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MEIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MENSAGEM;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_CONTAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_LOTES;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_MOEDA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_MOEDAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SEGMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SOMATORIA_VALORES;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_MOEDA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_MOVIMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_OPERACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_SERVICO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_ABATIMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_DESCONTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_DOCUMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_MORA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_MULTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO;

public class RemittanceLayout {

    private static final Map<String, RecordColumnRule> remittanceHeader = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0000", NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "0", NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, ALPHA));
        put(TIPO_INSCRICAO, new RecordColumnRule(5,1, "2", NUMBER));
        put(NUMERO_INSCRICAO_EMPRESA, new RecordColumnRule(6,14, NUMBER));
        put(CONVEIO_BANCO, new RecordColumnRule(7,20, ALPHA));
        put(AGENCIA, new RecordColumnRule(8,5, NUMBER));
        put(DIGITO_AGENCIA, new RecordColumnRule(9,1, ALPHA));
        put(NUMERO_CONTA, new RecordColumnRule(10,12, NUMBER));
        put(DIGITO_CONTA, new RecordColumnRule(11,1, ALPHA));
        put(DIGITO_AGENCIA_CONTA, new RecordColumnRule(12,1, ALPHA));
        put(NOME_EMPRESA, new RecordColumnRule(13,30, ALPHA));
        put(NOME_BANCO, new RecordColumnRule(14,30, ALPHA));
        put(MEIO_FEBRABAN, new RecordColumnRule(15,10, ALPHA));
        put(CODIGO_REMESSA, new RecordColumnRule(16,1, "1", NUMBER));
        put(DATA_GERACAO_ARQUIVO, new RecordColumnRule(17,8, NUMBER));
        put(HORA_GERACAO_ARQUIVO, new RecordColumnRule(18,6, NUMBER));
        put(SEQUENCIAL_ARQUIVO, new RecordColumnRule(19,6, NUMBER));
        put(LAYOUT_ARQUIVO, new RecordColumnRule(20,3, "080", NUMBER));
        put(DENSIDADE_GRAVACAO, new RecordColumnRule(21,5, "1600", NUMBER));
        put(RESERVADO_BANCO, new RecordColumnRule(22,20, ALPHA));
        put(RESERVADO_EMPRESA, new RecordColumnRule(23,20, ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(24,29, ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchHeader = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0001", NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "1", NUMBER));
        put(TIPO_OPERACAO, new RecordColumnRule(4,1, "C", ALPHA));
        put(TIPO_SERVICO, new RecordColumnRule(5,2, "20", NUMBER));
        put(FORMA_LANCAMENTO, new RecordColumnRule(6,2, NUMBER));
        put(LAYOUT_ARQUIVO, new RecordColumnRule(7,3, "040", NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(8,9, ALPHA));
        put(TIPO_INSCRICAO, new RecordColumnRule(9,1, "2", NUMBER));
        put(NUMERO_INSCRICAO_EMPRESA, new RecordColumnRule(10,14, NUMBER));
        put(CONVEIO_BANCO, new RecordColumnRule(11,20, ALPHA));
        put(AGENCIA, new RecordColumnRule(12,5, NUMBER));
        put(DIGITO_AGENCIA, new RecordColumnRule(13,1, ALPHA));
        put(NUMERO_CONTA, new RecordColumnRule(14,12, NUMBER));
        put(DIGITO_CONTA, new RecordColumnRule(15,1, ALPHA));
        put(DIGITO_AGENCIA_CONTA, new RecordColumnRule(16,1, ALPHA));
        put(NOME_EMPRESA, new RecordColumnRule(17,30, ALPHA));
        put(MENSAGEM, new RecordColumnRule(18,40, ALPHA));
        put(LOGRADOURO, new RecordColumnRule(19,30, ALPHA));
        put(NUMERO, new RecordColumnRule(20,5, NUMBER));
        put(COMPLEMENTO, new RecordColumnRule(21,15, ALPHA));
        put(CIDADE, new RecordColumnRule(22,20, ALPHA));
        put(CEP, new RecordColumnRule(23,5, NUMBER));
        put(COMPLEMENTO_CEP, new RecordColumnRule(24,3, ALPHA));
        put(ESTADO, new RecordColumnRule(25,2, ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(26,8, ALPHA));
        put(OCORRENCIAS, new RecordColumnRule(27,10, ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchSegmentA = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0002", NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "3", NUMBER));
        put(NUMERO_REGISTRO, new RecordColumnRule(4,5, NUMBER));
        put(SEGMENTO, new RecordColumnRule(5,1, "A", ALPHA));
        put(TIPO_MOVIMENTO, new RecordColumnRule(6,2, "0", NUMBER));
        put(INSTITUICAO_MOVIMENTO, new RecordColumnRule(7,2,"23", NUMBER));
        put(CAMARA_CENTRALIZADORA, new RecordColumnRule(8,3, "18", NUMBER));
        put(BANCO_FAVORECIDO, new RecordColumnRule(9,3, NUMBER));
        put(AGENCIA, new RecordColumnRule(10,5, NUMBER));
        put(DIGITO_AGENCIA, new RecordColumnRule(11,1, ALPHA));
        put(NUMERO_CONTA, new RecordColumnRule(12,12, NUMBER));
        put(DIGITO_CONTA, new RecordColumnRule(13,1, ALPHA));
        put(DIGITO_AGENCIA_CONTA, new RecordColumnRule(14,1, ALPHA));
        put(NOME_FAVORECIDO, new RecordColumnRule(15,30, ALPHA));
        put(DOCUMENTO_EMPRESA, new RecordColumnRule(16,20, ALPHA));
        put(DATA_PAGAMENTO, new RecordColumnRule(17,8, NUMBER));
        put(TIPO_MOEDA, new RecordColumnRule(18,3, "BRL",  ALPHA));
        put(QUANTIDADE_MOEDA, new RecordColumnRule(19,15, NUMBER));
        put(VALOR_PAGAMENTO, new RecordColumnRule(20,15, NUMBER));
        put(DOCUMENTO_ATRIBUIDO_BANCO, new RecordColumnRule(21,20, ALPHA));
        put(DATA_REAL_PAGAMENTO, new RecordColumnRule(22,8, NUMBER));
        put(VALOR_REAL_PAGAMENTO, new RecordColumnRule(23,15, NUMBER));
        put(INFORMACAO, new RecordColumnRule(24,40, ALPHA));
        put(FINALIDADE_DOC, new RecordColumnRule(25,2,"01", ALPHA));
        put(FINALIDADE_TED, new RecordColumnRule(26,5, "12345",  ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(27,5, ALPHA));
        put(AVISO, new RecordColumnRule(28,1, NUMBER));
        put(OCORRENCIAS, new RecordColumnRule(29,10, ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchSegmentB = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0003", NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "3", NUMBER));
        put(NUMERO_REGISTRO, new RecordColumnRule(4,5, NUMBER));
        put(SEGMENTO, new RecordColumnRule(5,1, "B", ALPHA));
        put(INICIO_FEBRABAN, new RecordColumnRule(6,3, ALPHA));
        put(TIPO_INSCRICAO_FAVORECIDO, new RecordColumnRule(7,1, "2",NUMBER));
        put(NUMERO_INSCRICAO_FAVORECIDO, new RecordColumnRule(8,14, NUMBER));
        put(LOGRADOURO, new RecordColumnRule(9,30, ALPHA));
        put(NUMERO, new RecordColumnRule(10,5, NUMBER));
        put(COMPLEMENTO, new RecordColumnRule(11,15, ALPHA));
        put(BAIRRO, new RecordColumnRule(12,15, ALPHA));
        put(CIDADE, new RecordColumnRule(13,20, ALPHA));
        put(CEP, new RecordColumnRule(14,5, NUMBER));
        put(COMPLEMENTO_CEP, new RecordColumnRule(15,3, ALPHA));
        put(ESTADO, new RecordColumnRule(16,2, ALPHA));
        put(DATA_VENCIMENTO, new RecordColumnRule(17,8, NUMBER));
        put(VALOR_DOCUMENTO, new RecordColumnRule(18,15, NUMBER));
        put(VALOR_ABATIMENTO, new RecordColumnRule(19,15, NUMBER));
        put(VALOR_DESCONTO, new RecordColumnRule(20,15, NUMBER));
        put(VALOR_MORA, new RecordColumnRule(21,15, NUMBER));
        put(VALOR_MULTA, new RecordColumnRule(22,15, NUMBER));
        put(CODIGO_DOCUMENTO_FAVORECIDO, new RecordColumnRule(23,15, ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(24,15, ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchTrailer = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "5", NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, ALPHA));
        put(QUANTIDADE_REGISTROS, new RecordColumnRule(5,6, NUMBER));
        put(SOMATORIA_VALORES, new RecordColumnRule(6,18, NUMBER));
        put(QUANTIDADE_MOEDAS, new RecordColumnRule(7,18, NUMBER));
        put(NUMERO_AVISO_DEBITO, new RecordColumnRule(8,6, NUMBER));
        put(FIM_FEBRABAN, new RecordColumnRule(9,165, ALPHA));
        put(OCORRENCIAS, new RecordColumnRule(10,10, ALPHA));
    }};

    private static final HashMap<String, RecordColumnRule> remittanceTrailer = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "9999", NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "9", NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, ALPHA));
        put(QUANTIDADE_LOTES, new RecordColumnRule(5,6, NUMBER));
        put(QUANTIDADE_REGISTROS, new RecordColumnRule(6,6, NUMBER));
        put(QUANTIDADE_CONTAS, new RecordColumnRule(7,6, NUMBER));
        put(FIM_FEBRABAN, new RecordColumnRule(8,205, ALPHA));
    }};

    public static Map<String, RecordColumnRule> getRemittanceHeader(){
        return Collections.unmodifiableMap(remittanceHeader);
    }

    public static Map<String, RecordColumnRule> getBatchHeader(){
        return Collections.unmodifiableMap(batchHeader);
    }

    public static Map<String, RecordColumnRule> getBatchSegmentA(){
        return Collections.unmodifiableMap(batchSegmentA);
    }

    public static Map<String, RecordColumnRule> getBatchSegmentB(){
        return Collections.unmodifiableMap(batchSegmentB);
    }

    public static Map<String, RecordColumnRule> getBatchTrailer(){
        return Collections.unmodifiableMap(batchTrailer);
    }

    public static Map<String, RecordColumnRule> getRemittanceTrailer(){
        return Collections.unmodifiableMap(remittanceTrailer);
    }
}
