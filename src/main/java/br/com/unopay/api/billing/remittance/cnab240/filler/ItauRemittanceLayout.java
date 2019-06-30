package br.com.unopay.api.billing.remittance.cnab240.filler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AVISO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BAIRRO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CEP;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CIDADE;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_DOCUMENTO_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_OCORRENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_REMESSA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO_CEP;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CONVEIO_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_VENCIMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DENSIDADE_GRAVACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.ESTADO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DOC;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FORMA_LANCAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.IDENTIFICACAO_TITULO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INFORMACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INSTITUICAO_MOVIMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOGRADOURO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.MEIO_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.MENSAGEM;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_REGISTRO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_CONTAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_LOTES;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_MOEDAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.RESERVADO_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.RESERVADO_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEGMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SOMATORIA_VALORES;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOEDA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOVIMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_OPERACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_ABATIMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_DESCONTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_DOCUMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_MORA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_MULTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO;

public abstract class ItauRemittanceLayout {

    private static final Map<String, RecordColumnRule> remittanceHeader = new HashMap<String, RecordColumnRule>(){

     {
        put(BANCO_COMPENSACAO, new RecordColumnRule(1, 1, 3, 3, "341", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2, 4, 7, 4, "0000", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3, 8, 8, 1, "0", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4, 9, 17, 9, ColumnType.ALPHA));
        put(TIPO_INSCRICAO, new RecordColumnRule(5, 18, 18, 1, "1", ColumnType.NUMBER));
        put(NUMERO_INSCRICAO_EMPRESA, new RecordColumnRule(6, 19, 32, 14, ColumnType.NUMBER));
        put(CONVEIO_BANCO, new RecordColumnRule(7, 33, 52, 20, ColumnType.ALPHA));
        put(AGENCIA, new RecordColumnRule(8, 53, 57, 5, ColumnType.NUMBER));
        put(DIGITO_AGENCIA, new RecordColumnRule(9, 58, 58, 1, ColumnType.ALPHA));
        put(NUMERO_CONTA, new RecordColumnRule(10, 59, 70, 12, ColumnType.NUMBER));
        put(DIGITO_CONTA, new RecordColumnRule(11, 71, 71, 1, ColumnType.ALPHA));
        put(DIGITO_AGENCIA_CONTA, new RecordColumnRule(12, 72, 72, 1, ColumnType.ALPHA));
        put(NOME_EMPRESA, new RecordColumnRule(13, 73, 102, 30, ColumnType.ALPHA));
        put(NOME_BANCO, new RecordColumnRule(14, 103, 132, 30, ColumnType.ALPHA));
        put(MEIO_FEBRABAN, new RecordColumnRule(15, 133, 142, 10, ColumnType.ALPHA));
        put(CODIGO_REMESSA, new RecordColumnRule(16, 143, 143, 1, "1", ColumnType.NUMBER));
        put(DATA_GERACAO_ARQUIVO, new RecordColumnRule(17, 144, 151, 8, ColumnType.NUMBER));
        put(HORA_GERACAO_ARQUIVO, new RecordColumnRule(18, 152, 157, 6, ColumnType.NUMBER));
        put(SEQUENCIAL_ARQUIVO, new RecordColumnRule(19, 158, 163, 6, ColumnType.NUMBER));
        put(LAYOUT_ARQUIVO, new RecordColumnRule(20, 164, 166, 3, "080", ColumnType.NUMBER));
        put(DENSIDADE_GRAVACAO, new RecordColumnRule(21, 167, 171, 5, "1600", ColumnType.NUMBER));
        put(RESERVADO_BANCO, new RecordColumnRule(22, 172, 191, 20, ColumnType.ALPHA));
        put(RESERVADO_EMPRESA, new RecordColumnRule(23, 192, 211, 20, ColumnType.ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(24, 212, 240, 29, ColumnType.ALPHA));
    }};

    public static final String IDENTIFICACAO_LANCAMENTO = "identificacao_lancamento";
    public static final String BRANCOS_1 = "brancos_1";
    public static final String BRANCOS_2 = "brancos_2";
    private static final Map<String, RecordColumnRule> batchHeader = new HashMap<String, RecordColumnRule>(){
    {
        put(BANCO_COMPENSACAO, new RecordColumnRule(1, 1, 3, 3, "237", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2, 4, 7, 4, "0001", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3, 8, 8, 1, "1", ColumnType.NUMBER));
        put(TIPO_OPERACAO, new RecordColumnRule(4, 9, 9, 1, "C", ColumnType.ALPHA));
        put(TIPO_SERVICO, new RecordColumnRule(5, 10, 11, 2, "20", ColumnType.NUMBER));
        put(FORMA_LANCAMENTO, new RecordColumnRule(6, 12, 13, 2, ColumnType.NUMBER));
        put(LAYOUT_ARQUIVO, new RecordColumnRule(7, 14, 16, 3, "040", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(8, 17, 17, 1, ColumnType.ALPHA));
        put(TIPO_INSCRICAO, new RecordColumnRule(9, 18, 18, 1, "2", ColumnType.NUMBER));
        put(NUMERO_INSCRICAO_EMPRESA, new RecordColumnRule(10, 19, 32, 14, ColumnType.NUMBER));
        put(IDENTIFICACAO_LANCAMENTO, new RecordColumnRule(11, 33, 36, 4, ColumnType.ALPHA));
        put(BRANCOS_1, new RecordColumnRule(12, 37, 52, 16, ColumnType.ALPHA));
        put(AGENCIA, new RecordColumnRule(13, 53, 57, 5, ColumnType.NUMBER));
        put(BRANCOS_2, new RecordColumnRule(14, 58, 58, 1, ColumnType.ALPHA));

        put(NUMERO_CONTA, new RecordColumnRule(15, 59, 70, 12, ColumnType.NUMBER));
        put(DIGITO_CONTA, new RecordColumnRule(16, 71, 71, 1, ColumnType.ALPHA));
        put(DIGITO_AGENCIA_CONTA, new RecordColumnRule(17, 72, 72, 1, ColumnType.ALPHA));
        put(NOME_EMPRESA, new RecordColumnRule(18, 73, 102, 30, ColumnType.ALPHA));
        put(MENSAGEM, new RecordColumnRule(19, 103, 142, 40, ColumnType.ALPHA));
        put(LOGRADOURO, new RecordColumnRule(20, 143, 172, 30, ColumnType.ALPHA));
        put(NUMERO, new RecordColumnRule(21, 173, 177, 5, ColumnType.NUMBER));
        put(COMPLEMENTO, new RecordColumnRule(22, 178, 192, 15, ColumnType.ALPHA));
        put(CIDADE, new RecordColumnRule(23, 193, 212, 20, ColumnType.ALPHA));
        put(CEP, new RecordColumnRule(24, 213, 217, 5, ColumnType.NUMBER));
        put(COMPLEMENTO_CEP, new RecordColumnRule(25, 218, 220, 3, ColumnType.ALPHA));
        put(ESTADO, new RecordColumnRule(26, 221, 222, 2, ColumnType.ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(27, 223, 230, 8, ColumnType.ALPHA));
        put(OCORRENCIAS, new RecordColumnRule(28, 231, 240, 10, ColumnType.ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchSegmentA = new HashMap<String, RecordColumnRule>(){
    {
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,1,3,3, "237", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4,7,4, "0002", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,8,8,1, "3", ColumnType.NUMBER));
        put(NUMERO_REGISTRO, new RecordColumnRule(4,9,13,5, ColumnType.NUMBER));
        put(SEGMENTO, new RecordColumnRule(5,14,14,1, "A", ColumnType.ALPHA));
        put(TIPO_MOVIMENTO, new RecordColumnRule(6,15,15,1, "0", ColumnType.NUMBER));
        put(INSTITUICAO_MOVIMENTO, new RecordColumnRule(7,16,17,2,"23", ColumnType.NUMBER));
        put(CAMARA_CENTRALIZADORA, new RecordColumnRule(8,18,20,3, "18", ColumnType.NUMBER));
        put(BANCO_FAVORECIDO, new RecordColumnRule(9,21,23,3, ColumnType.NUMBER));
        put(AGENCIA, new RecordColumnRule(10,24,28,5, ColumnType.NUMBER));
        put(DIGITO_AGENCIA, new RecordColumnRule(11,29,29,1, ColumnType.ALPHA));
        put(NUMERO_CONTA, new RecordColumnRule(12,30,41,12, ColumnType.NUMBER));
        put(DIGITO_CONTA, new RecordColumnRule(13,42,42,1, ColumnType.ALPHA));
        put(DIGITO_AGENCIA_CONTA, new RecordColumnRule(14,43,43,1, ColumnType.ALPHA));
        put(NOME_FAVORECIDO, new RecordColumnRule(15,44,73,30, ColumnType.ALPHA));
        put(DOCUMENTO_ATRIBUIDO_EMPRESA, new RecordColumnRule(16,74,93,20, ColumnType.ALPHA));
        put(DATA_PAGAMENTO, new RecordColumnRule(17,94,101,8, ColumnType.NUMBER));
        put(TIPO_MOEDA, new RecordColumnRule(18,102,104,3, "BRL",  ColumnType.ALPHA));
        put(QUANTIDADE_MOEDA, new RecordColumnRule(19,105,119,15, ColumnType.NUMBER));
        put(VALOR_PAGAMENTO, new RecordColumnRule(20,120,134,15, ColumnType.NUMBER));
        put(DOCUMENTO_ATRIBUIDO_BANCO, new RecordColumnRule(21,135,154,20, ColumnType.ALPHA));
        put(DATA_REAL_PAGAMENTO, new RecordColumnRule(22,155,162,8, ColumnType.NUMBER));
        put(VALOR_REAL_PAGAMENTO, new RecordColumnRule(23,163,177,15, ColumnType.NUMBER));
        put(INFORMACAO, new RecordColumnRule(24,178,217,40, ColumnType.ALPHA));
        put(FINALIDADE_DOC, new RecordColumnRule(25,218,219,2,"01", ColumnType.ALPHA));
        put(FINALIDADE_TED, new RecordColumnRule(26,220,224,5, "12345",  ColumnType.ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(27,225,229,5, ColumnType.ALPHA));
        put(AVISO, new RecordColumnRule(28,230,230,1, ColumnType.NUMBER));
        put(OCORRENCIAS, new RecordColumnRule(29,231,240,10, ColumnType.ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchSegmentB = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,1,3,3, "237", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4,7,4, "0003", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,8,8,1, "3", ColumnType.NUMBER));
        put(NUMERO_REGISTRO, new RecordColumnRule(4,9,13,5, ColumnType.NUMBER));
        put(SEGMENTO, new RecordColumnRule(5,14,14,1, "B", ColumnType.ALPHA));
        put(INICIO_FEBRABAN, new RecordColumnRule(6,15,17,3, ColumnType.ALPHA));
        put(TIPO_INSCRICAO_FAVORECIDO, new RecordColumnRule(7,18,18,1, "2", ColumnType.NUMBER));
        put(NUMERO_INSCRICAO_FAVORECIDO, new RecordColumnRule(8,19,32,14, ColumnType.NUMBER));
        put(LOGRADOURO, new RecordColumnRule(9,33,62,30, ColumnType.ALPHA));
        put(NUMERO, new RecordColumnRule(10,63,67,5, ColumnType.NUMBER));
        put(COMPLEMENTO, new RecordColumnRule(11,68,82,15, ColumnType.ALPHA));
        put(BAIRRO, new RecordColumnRule(12,83,97,15, ColumnType.ALPHA));
        put(CIDADE, new RecordColumnRule(13,98,117,20, ColumnType.ALPHA));
        put(CEP, new RecordColumnRule(14,118,122,5, ColumnType.NUMBER));
        put(COMPLEMENTO_CEP, new RecordColumnRule(15,123,125,3, ColumnType.ALPHA));
        put(ESTADO, new RecordColumnRule(16,126,127,2, ColumnType.ALPHA));
        put(DATA_VENCIMENTO, new RecordColumnRule(17,128,135,8, ColumnType.NUMBER));
        put(VALOR_DOCUMENTO, new RecordColumnRule(18,136,150,15, ColumnType.NUMBER));
        put(VALOR_ABATIMENTO, new RecordColumnRule(19,151,165,15, ColumnType.NUMBER));
        put(VALOR_DESCONTO, new RecordColumnRule(20,166,180,15, ColumnType.NUMBER));
        put(VALOR_MORA, new RecordColumnRule(21,181,195,15, ColumnType.NUMBER));
        put(VALOR_MULTA, new RecordColumnRule(22,196,210,15, ColumnType.NUMBER));
        put(CODIGO_DOCUMENTO_FAVORECIDO, new RecordColumnRule(23,211,225,15, ColumnType.ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(24,226,240,15, ColumnType.ALPHA));
    }};



    public static Map<String, RecordColumnRule> getBatchSegmentT() {
        return batchSegmentT;
    }
    private static final Map<String, RecordColumnRule> batchSegmentT = new HashMap<String, RecordColumnRule>(){
        {
            put(CODIGO_OCORRENCIA, new RecordColumnRule(1,16,17,2, ColumnType.ALPHA));
            put(IDENTIFICACAO_TITULO, new RecordColumnRule(2,55,69,15, ColumnType.NUMBER));
        }};

    private static final Map<String, RecordColumnRule> batchTrailer = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,1,3,3, "237", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4,7,4, ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,8,8,1, "5", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9,17,9, ColumnType.ALPHA));
        put(QUANTIDADE_REGISTROS, new RecordColumnRule(5,18,23,6, ColumnType.NUMBER));
        put(SOMATORIA_VALORES, new RecordColumnRule(6,24,41,18, ColumnType.NUMBER));
        put(QUANTIDADE_MOEDAS, new RecordColumnRule(7,42,59,18, ColumnType.NUMBER));
        put(NUMERO_AVISO_DEBITO, new RecordColumnRule(8,60,65,6, ColumnType.NUMBER));
        put(FIM_FEBRABAN, new RecordColumnRule(9,66,230,165, ColumnType.ALPHA));
        put(OCORRENCIAS, new RecordColumnRule(10,231,240,10, ColumnType.ALPHA));
    }};

    private static final HashMap<String, RecordColumnRule> remittanceTrailer = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,1,3,3, "237", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4,7,4, "9999", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,8,8,1, "9", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9,17,9, ColumnType.ALPHA));
        put(QUANTIDADE_LOTES, new RecordColumnRule(5,18,23,6, ColumnType.NUMBER));
        put(QUANTIDADE_REGISTROS, new RecordColumnRule(6,24,29,6, ColumnType.NUMBER));
        put(QUANTIDADE_CONTAS, new RecordColumnRule(7,30,35,6, ColumnType.NUMBER));
        put(FIM_FEBRABAN, new RecordColumnRule(8,36,240,205, ColumnType.ALPHA));
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
