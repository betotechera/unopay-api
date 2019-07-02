package br.com.unopay.api.billing.remittance.cnab240.filler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AGENCIA_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.AVISO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_COMPENSACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BANCO_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_1;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_2;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.BRANCOS_3;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CAMARA_CENTRALIZADORA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CEP;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CIDADE;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_ISPB;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CODIGO_REMESSA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.COMPLEMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.CONVEIO_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DATA_REAL_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DENSIDADE_GRAVACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.ESTADO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_COD;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DETALHE;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_DOC;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FINALIDADE_TED;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.FORMA_LANCAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.HISTORICO_CC;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.IDENTIFICACAO_LANCAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOGRADOURO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.MEIO_FEBRABAN;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOME_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NOSSO_NUMERO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_DOCUMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.NUMERO_REGISTRO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_LOTES;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.RESERVADO_BANCO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SEGMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.SOMATORIA_VALORES;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOEDA;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_MOVIMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_OPERACAO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.TIPO_SERVICO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_PAGAMENTO;
import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO;

public abstract class ItauRemittanceLayout {

    private static final Map<String, RecordColumnRule> remittanceHeader = new HashMap<String, RecordColumnRule>(){

     {
        put(BANCO_COMPENSACAO, new RecordColumnRule(1, 1, 3, 3, "341", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2, 4, 7, 4, "0000", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3, 8, 8, 1, "0", ColumnType.NUMBER));
        put(BRANCOS_1, new RecordColumnRule(4, 9, 14, 6, ColumnType.ALPHA));
        put(LAYOUT_ARQUIVO, new RecordColumnRule(4, 15, 17, 3, "081",ColumnType.ALPHA));
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
        put(BRANCOS_2, new RecordColumnRule(19, 158, 166, 9, ColumnType.NUMBER));
        put(DENSIDADE_GRAVACAO, new RecordColumnRule(20, 167, 171, 5, "1600", ColumnType.NUMBER));
        put(RESERVADO_BANCO, new RecordColumnRule(21, 172, 240, 69, ColumnType.ALPHA));
    }};


    private static final Map<String, RecordColumnRule> batchHeader = new HashMap<String, RecordColumnRule>(){
    {
        put(BANCO_COMPENSACAO, new RecordColumnRule(1, 1, 3, 3, "341", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2, 4, 7, 4, "0001", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3, 8, 8, 1, "1", ColumnType.NUMBER));
        put(TIPO_OPERACAO, new RecordColumnRule(4, 9, 9, 1, "C", ColumnType.ALPHA));
        put(TIPO_SERVICO, new RecordColumnRule(5, 10, 11, 2, "20", ColumnType.NUMBER));
        put(FORMA_LANCAMENTO, new RecordColumnRule(6, 12, 13, 2, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.LAYOUT_ARQUIVO, new RecordColumnRule(7, 14, 16, 3, "040", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(8, 17, 17, 1, ColumnType.ALPHA));
        put(TIPO_INSCRICAO, new RecordColumnRule(9, 18, 18, 1, "2", ColumnType.NUMBER));
        put(NUMERO_INSCRICAO_EMPRESA, new RecordColumnRule(10, 19, 32, 14, ColumnType.NUMBER));
        put(IDENTIFICACAO_LANCAMENTO, new RecordColumnRule(11, 33, 36, 4, ColumnType.ALPHA));
        put(BRANCOS_1, new RecordColumnRule(12, 37, 52, 16, ColumnType.ALPHA));
        put(AGENCIA, new RecordColumnRule(13, 53, 57, 5, ColumnType.NUMBER));
        put(BRANCOS_2, new RecordColumnRule(14, 58, 58, 1, ColumnType.ALPHA));
        put(NUMERO_CONTA, new RecordColumnRule(15, 59, 70, 12, ColumnType.NUMBER));
        put(BRANCOS_3, new RecordColumnRule(16, 71, 71, 1, ColumnType.ALPHA));
        put(DIGITO_CONTA, new RecordColumnRule(17, 72, 72, 1, ColumnType.ALPHA));
        put(NOME_EMPRESA, new RecordColumnRule(18, 73, 102, 30, ColumnType.ALPHA));
        put(FINALIDADE_COD, new RecordColumnRule(19, 103, 132, 30, ColumnType.ALPHA));
        put(HISTORICO_CC, new RecordColumnRule(20, 133, 142, 10, ColumnType.ALPHA));
        put(LOGRADOURO, new RecordColumnRule(21, 143, 172, 30, ColumnType.ALPHA));
        put(NUMERO, new RecordColumnRule(22, 173, 177, 5, ColumnType.NUMBER));
        put(COMPLEMENTO, new RecordColumnRule(23, 178, 192, 15, ColumnType.ALPHA));
        put(CIDADE, new RecordColumnRule(24, 193, 212, 20, ColumnType.ALPHA));
        put(CEP, new RecordColumnRule(25, 213, 220, 8, ColumnType.NUMBER));
        put(ESTADO, new RecordColumnRule(27, 221, 222, 2, ColumnType.ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(28, 223, 230, 8, ColumnType.ALPHA));
        put(OCORRENCIAS, new RecordColumnRule(29, 231, 240, 10, ColumnType.ALPHA));
    }};


    private static final Map<String, RecordColumnRule> batchSegmentA = new HashMap<String, RecordColumnRule>(){
    {
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,1,3,3, "341", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4,7,4, "0002", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,8,8,1, "3", ColumnType.NUMBER));
        put(NUMERO_REGISTRO, new RecordColumnRule(4,9,13,5, ColumnType.NUMBER));
        put(SEGMENTO, new RecordColumnRule(5,14,14,1, "A", ColumnType.ALPHA));
        put(TIPO_MOVIMENTO, new RecordColumnRule(6,15,17,3,  ColumnType.NUMBER));
        put(CAMARA_CENTRALIZADORA, new RecordColumnRule(7,18,20,3, "18", ColumnType.NUMBER));
        put(BANCO_FAVORECIDO, new RecordColumnRule(8,21,23,3, ColumnType.NUMBER));
        put(AGENCIA_CONTA, new RecordColumnRule(9,24,43,20, ColumnType.ALPHA));
        put(NOME_FAVORECIDO, new RecordColumnRule(10,44,73,30, ColumnType.ALPHA));
        put(DOCUMENTO_ATRIBUIDO_EMPRESA, new RecordColumnRule(11,74,93,20, ColumnType.ALPHA));
        put(DATA_PAGAMENTO, new RecordColumnRule(12,94,101,8, ColumnType.NUMBER));
        put(TIPO_MOEDA, new RecordColumnRule(13,102,104,3, "REA",  ColumnType.ALPHA));
        put(CODIGO_ISPB, new RecordColumnRule(14,105,112,8, ColumnType.NUMBER));
        put(BRANCOS_1, new RecordColumnRule(15,113,119,7, ColumnType.NUMBER));
        put(VALOR_PAGAMENTO, new RecordColumnRule(16,120,134,15, ColumnType.NUMBER));
        put(NOSSO_NUMERO, new RecordColumnRule(17,135,149,15, ColumnType.ALPHA));
        put(BRANCOS_2, new RecordColumnRule(18,150,154,5, ColumnType.ALPHA));
        put(DATA_REAL_PAGAMENTO, new RecordColumnRule(19,155,162,8, ColumnType.NUMBER));
        put(VALOR_REAL_PAGAMENTO, new RecordColumnRule(20,163,177,15, ColumnType.NUMBER));
        put(FINALIDADE_DETALHE, new RecordColumnRule(21,178,195,18, ColumnType.ALPHA));
        put(BRANCOS_3, new RecordColumnRule(22,196,197,2, ColumnType.ALPHA));
        put(NUMERO_DOCUMENTO, new RecordColumnRule(23,198,203,6, ColumnType.NUMBER));
        put(NUMERO_INSCRICAO_FAVORECIDO, new RecordColumnRule(24,204,217,14, ColumnType.NUMBER));
        put(FINALIDADE_DOC, new RecordColumnRule(25,218,219,2,"01", ColumnType.ALPHA));
        put(FINALIDADE_TED, new RecordColumnRule(26,220,224,5, "12345",  ColumnType.ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(27,225,229,5, ColumnType.ALPHA));
        put(AVISO, new RecordColumnRule(28,230,230,1, ColumnType.NUMBER));
        put(OCORRENCIAS, new RecordColumnRule(29,231,240,10, ColumnType.ALPHA));
    }};



    private static final Map<String, RecordColumnRule> batchTrailer = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,1,3,3, "341", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4,7,4, ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,8,8,1, "5", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9,17,9, ColumnType.ALPHA));
        put(QUANTIDADE_REGISTROS, new RecordColumnRule(5,18,23,6, ColumnType.NUMBER));
        put(SOMATORIA_VALORES, new RecordColumnRule(6,24,41,18, ColumnType.NUMBER));
        put(BRANCOS_1, new RecordColumnRule(7,42,59,18, ColumnType.NUMBER));
        put(BRANCOS_2, new RecordColumnRule(8,60,230,171, ColumnType.ALPHA));
        put(OCORRENCIAS, new RecordColumnRule(10,231,240,10, ColumnType.ALPHA));
    }};

    private static final HashMap<String, RecordColumnRule> remittanceTrailer = new HashMap<String, RecordColumnRule>(){{
        put(BANCO_COMPENSACAO, new RecordColumnRule(1,1,3,3, "341", ColumnType.NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4,7,4, "9999", ColumnType.NUMBER));
        put(TIPO_REGISTRO, new RecordColumnRule(3,8,8,1, "9", ColumnType.NUMBER));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9,17,9, ColumnType.ALPHA));
        put(QUANTIDADE_LOTES, new RecordColumnRule(5,18,23,6, ColumnType.NUMBER));
        put(QUANTIDADE_REGISTROS, new RecordColumnRule(6,24,29,6, ColumnType.NUMBER));
        put(BRANCOS_1, new RecordColumnRule(7,30,240,211, ColumnType.ALPHA));
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

    public static Map<String, RecordColumnRule> getBatchTrailer(){
        return Collections.unmodifiableMap(batchTrailer);
    }

    public static Map<String, RecordColumnRule> getRemittanceTrailer(){
        return Collections.unmodifiableMap(remittanceTrailer);
    }
}
