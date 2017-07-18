package br.com.unopay.api.payment.cnab240.filler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.AVISO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BAIRRO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.BANCO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CEP;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CIDADE;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CODIGO_DOCUMENTO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CODIGO_REMESSA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO_CEP;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.CONVEIO_BANCO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DATA_VENCIMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DENSIDADE_GRAVACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.ESTADO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DOC;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.FORMA_LANCAMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INFORMACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.INSTITUICAO_MOVIMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOGRADOURO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.MEIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.MENSAGEM;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NOME_BANCO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NOME_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.NUMERO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_CONTAS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_LOTES;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDAS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.RESERVADO_BANCO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.RESERVADO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEGMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.SOMATORIA_VALORES;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_MOEDA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_MOVIMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_OPERACAO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.TIPO_SERVICO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_ABATIMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_DESCONTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_DOCUMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_MORA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_MULTA;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_PAGAMENTO;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO;

public class RemittanceLayout {

    private static final Map<String, RecordColumnRule> remittanceHeader = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0000", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "0", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, ColumnType.ALPHA));
        put(TIPO_INSCRICAO, new RecordColumnRule(5,1, "2", ColumnType.NUMBER));
        put(NUMERO_INSCRICAO_EMPRESA, new RecordColumnRule(6,14, ColumnType.NUMBER));
        put(CONVEIO_BANCO, new RecordColumnRule(7,20, ColumnType.ALPHA));
        put(AGENCIA, new RecordColumnRule(8,5, ColumnType.NUMBER));
        put(DIGITO_AGENCIA, new RecordColumnRule(9,1, ColumnType.ALPHA));
        put(NUMERO_CONTA, new RecordColumnRule(10,12, ColumnType.NUMBER));
        put(DIGITO_CONTA, new RecordColumnRule(11,1, ColumnType.ALPHA));
        put(DIGITO_AGENCIA_CONTA, new RecordColumnRule(12,1, ColumnType.ALPHA));
        put(NOME_EMPRESA, new RecordColumnRule(13,30, ColumnType.ALPHA));
        put(NOME_BANCO, new RecordColumnRule(14,30, ColumnType.ALPHA));
        put(MEIO_FEBRABAN, new RecordColumnRule(15,10, ColumnType.ALPHA));
        put(CODIGO_REMESSA, new RecordColumnRule(16,1, "1", ColumnType.NUMBER));
        put(DATA_GERACAO_ARQUIVO, new RecordColumnRule(17,8, ColumnType.NUMBER));
        put(HORA_GERACAO_ARQUIVO, new RecordColumnRule(18,6, ColumnType.NUMBER));
        put(SEQUENCIAL_ARQUIVO, new RecordColumnRule(19,6, ColumnType.NUMBER));
        put(LAYOUT_ARQUIVO, new RecordColumnRule(20,3, "080", ColumnType.NUMBER));
        put(DENSIDADE_GRAVACAO, new RecordColumnRule(21,5, "1600", ColumnType.NUMBER));
        put(RESERVADO_BANCO, new RecordColumnRule(22,20, ColumnType.ALPHA));
        put(RESERVADO_EMPRESA, new RecordColumnRule(23,20, ColumnType.ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(24,29, ColumnType.ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchHeader = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0001", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "1", ColumnType.NUMBER));
        put(TIPO_OPERACAO, new RecordColumnRule(4,1, "C", ColumnType.ALPHA));
        put(TIPO_SERVICO, new RecordColumnRule(5,2, "20", ColumnType.NUMBER));
        put(FORMA_LANCAMENTO, new RecordColumnRule(6,2, ColumnType.NUMBER));
        put(LAYOUT_ARQUIVO, new RecordColumnRule(7,3, "040", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(8,9, ColumnType.ALPHA));
        put(TIPO_INSCRICAO, new RecordColumnRule(9,1, "2", ColumnType.NUMBER));
        put(NUMERO_INSCRICAO_EMPRESA, new RecordColumnRule(10,14, ColumnType.NUMBER));
        put(CONVEIO_BANCO, new RecordColumnRule(11,20, ColumnType.ALPHA));
        put(AGENCIA, new RecordColumnRule(12,5, ColumnType.NUMBER));
        put(DIGITO_AGENCIA, new RecordColumnRule(13,1, ColumnType.ALPHA));
        put(NUMERO_CONTA, new RecordColumnRule(14,12, ColumnType.NUMBER));
        put(DIGITO_CONTA, new RecordColumnRule(15,1, ColumnType.ALPHA));
        put(DIGITO_AGENCIA_CONTA, new RecordColumnRule(16,1, ColumnType.ALPHA));
        put(NOME_EMPRESA, new RecordColumnRule(17,30, ColumnType.ALPHA));
        put(MENSAGEM, new RecordColumnRule(18,40, ColumnType.ALPHA));
        put(LOGRADOURO, new RecordColumnRule(19,30, ColumnType.ALPHA));
        put(NUMERO, new RecordColumnRule(20,5, ColumnType.NUMBER));
        put(COMPLEMENTO, new RecordColumnRule(21,15, ColumnType.ALPHA));
        put(CIDADE, new RecordColumnRule(22,20, ColumnType.ALPHA));
        put(CEP, new RecordColumnRule(23,5, ColumnType.NUMBER));
        put(COMPLEMENTO_CEP, new RecordColumnRule(24,3, ColumnType.ALPHA));
        put(ESTADO, new RecordColumnRule(25,2, ColumnType.ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(26,8, ColumnType.ALPHA));
        put(OCORRENCIAS, new RecordColumnRule(27,10, ColumnType.ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchSegmentA = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0002", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "3", ColumnType.NUMBER));
        put(NUMERO_REGISTRO, new RecordColumnRule(4,5, ColumnType.NUMBER));
        put(SEGMENTO, new RecordColumnRule(5,1, "A", ColumnType.ALPHA));
        put(TIPO_MOVIMENTO, new RecordColumnRule(6,2, "0", ColumnType.NUMBER));
        put(INSTITUICAO_MOVIMENTO, new RecordColumnRule(7,2,"23", ColumnType.NUMBER));
        put(CAMARA_CENTRALIZADORA, new RecordColumnRule(8,3, "18", ColumnType.NUMBER));
        put(BANCO_FAVORECIDO, new RecordColumnRule(9,3, ColumnType.NUMBER));
        put(AGENCIA, new RecordColumnRule(10,5, ColumnType.NUMBER));
        put(DIGITO_AGENCIA, new RecordColumnRule(11,1, ColumnType.ALPHA));
        put(NUMERO_CONTA, new RecordColumnRule(12,12, ColumnType.NUMBER));
        put(DIGITO_CONTA, new RecordColumnRule(13,1, ColumnType.ALPHA));
        put(DIGITO_AGENCIA_CONTA, new RecordColumnRule(14,1, ColumnType.ALPHA));
        put(NOME_FAVORECIDO, new RecordColumnRule(15,30, ColumnType.ALPHA));
        put(DOCUMENTO_EMPRESA, new RecordColumnRule(16,20, ColumnType.ALPHA));
        put(DATA_PAGAMENTO, new RecordColumnRule(17,8, ColumnType.NUMBER));
        put(TIPO_MOEDA, new RecordColumnRule(18,3, "BRL",  ColumnType.ALPHA));
        put(QUANTIDADE_MOEDA, new RecordColumnRule(19,15, ColumnType.NUMBER));
        put(VALOR_PAGAMENTO, new RecordColumnRule(20,15, ColumnType.NUMBER));
        put(DOCUMENTO_ATRIBUIDO_BANCO, new RecordColumnRule(21,20, ColumnType.ALPHA));
        put(DATA_REAL_PAGAMENTO, new RecordColumnRule(22,8, ColumnType.NUMBER));
        put(VALOR_REAL_PAGAMENTO, new RecordColumnRule(23,15, ColumnType.NUMBER));
        put(INFORMACAO, new RecordColumnRule(24,40, ColumnType.ALPHA));
        put(FINALIDADE_DOC, new RecordColumnRule(25,2,"01", ColumnType.ALPHA));
        put(FINALIDADE_TED, new RecordColumnRule(26,5, "12345",  ColumnType.ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(27,5, ColumnType.ALPHA));
        put(AVISO, new RecordColumnRule(28,1, ColumnType.NUMBER));
        put(OCORRENCIAS, new RecordColumnRule(29,10, ColumnType.ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchSegmentB = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0003", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "3", ColumnType.NUMBER));
        put(NUMERO_REGISTRO, new RecordColumnRule(4,5, ColumnType.NUMBER));
        put(SEGMENTO, new RecordColumnRule(5,1, "B", ColumnType.ALPHA));
        put(INICIO_FEBRABAN, new RecordColumnRule(6,3, ColumnType.ALPHA));
        put(TIPO_INSCRICAO_FAVORECIDO, new RecordColumnRule(7,1, "2", ColumnType.NUMBER));
        put(NUMERO_INSCRICAO_FAVORECIDO, new RecordColumnRule(8,14, ColumnType.NUMBER));
        put(LOGRADOURO, new RecordColumnRule(9,30, ColumnType.ALPHA));
        put(NUMERO, new RecordColumnRule(10,5, ColumnType.NUMBER));
        put(COMPLEMENTO, new RecordColumnRule(11,15, ColumnType.ALPHA));
        put(BAIRRO, new RecordColumnRule(12,15, ColumnType.ALPHA));
        put(CIDADE, new RecordColumnRule(13,20, ColumnType.ALPHA));
        put(CEP, new RecordColumnRule(14,5, ColumnType.NUMBER));
        put(COMPLEMENTO_CEP, new RecordColumnRule(15,3, ColumnType.ALPHA));
        put(ESTADO, new RecordColumnRule(16,2, ColumnType.ALPHA));
        put(DATA_VENCIMENTO, new RecordColumnRule(17,8, ColumnType.NUMBER));
        put(VALOR_DOCUMENTO, new RecordColumnRule(18,15, ColumnType.NUMBER));
        put(VALOR_ABATIMENTO, new RecordColumnRule(19,15, ColumnType.NUMBER));
        put(VALOR_DESCONTO, new RecordColumnRule(20,15, ColumnType.NUMBER));
        put(VALOR_MORA, new RecordColumnRule(21,15, ColumnType.NUMBER));
        put(VALOR_MULTA, new RecordColumnRule(22,15, ColumnType.NUMBER));
        put(CODIGO_DOCUMENTO_FAVORECIDO, new RecordColumnRule(23,15, ColumnType.ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(24,15, ColumnType.ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchTrailer = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "5", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, ColumnType.ALPHA));
        put(QUANTIDADE_REGISTROS, new RecordColumnRule(5,6, ColumnType.NUMBER));
        put(SOMATORIA_VALORES, new RecordColumnRule(6,18, ColumnType.NUMBER));
        put(QUANTIDADE_MOEDAS, new RecordColumnRule(7,18, ColumnType.NUMBER));
        put(NUMERO_AVISO_DEBITO, new RecordColumnRule(8,6, ColumnType.NUMBER));
        put(FIM_FEBRABAN, new RecordColumnRule(9,165, ColumnType.ALPHA));
        put(OCORRENCIAS, new RecordColumnRule(10,10, ColumnType.ALPHA));
    }};

    private static final HashMap<String, RecordColumnRule> remittanceTrailer = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "9999", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "9", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, ColumnType.ALPHA));
        put(QUANTIDADE_LOTES, new RecordColumnRule(5,6, ColumnType.NUMBER));
        put(QUANTIDADE_REGISTROS, new RecordColumnRule(6,6, ColumnType.NUMBER));
        put(QUANTIDADE_CONTAS, new RecordColumnRule(7,6, ColumnType.NUMBER));
        put(FIM_FEBRABAN, new RecordColumnRule(8,205, ColumnType.ALPHA));
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
