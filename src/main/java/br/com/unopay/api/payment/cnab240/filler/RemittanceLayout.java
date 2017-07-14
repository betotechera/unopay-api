package br.com.unopay.api.payment.cnab240.filler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RemittanceLayout {

    private static final Map<String, RecordColumnRule> remittanceHeader = new HashMap<String, RecordColumnRule>(){{
        put(RemittanceLayoutKeys.BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.LOTE_SERVICO, new RecordColumnRule(2,4, "0000", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.TIPO_REGISTRO, new RecordColumnRule(3,1, "0", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.INICIO_FEBRABAN, new RecordColumnRule(4,9, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.TIPO_INSCRICAO, new RecordColumnRule(5,1, "2", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA, new RecordColumnRule(6,14, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.CONVEIO_BANCO, new RecordColumnRule(7,20, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.AGENCIA, new RecordColumnRule(8,5, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.DIGITO_AGENCIA, new RecordColumnRule(9,1, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.NUMERO_CONTA, new RecordColumnRule(10,12, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.DIGITO_CONTA, new RecordColumnRule(11,1, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA, new RecordColumnRule(12,1, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.NOME_EMPRESA, new RecordColumnRule(13,30, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.NOME_BANCO, new RecordColumnRule(14,30, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.MEIO_FEBRABAN, new RecordColumnRule(15,10, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.CODIGO_REMESSA, new RecordColumnRule(16,1, "1", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO, new RecordColumnRule(17,8, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO, new RecordColumnRule(18,6, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO, new RecordColumnRule(19,6, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.LAYOUT_ARQUIVO, new RecordColumnRule(20,3, "080", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.DENSIDADE_GRAVACAO, new RecordColumnRule(21,5, "1600", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.RESERVADO_BANCO, new RecordColumnRule(22,20, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.RESERVADO_EMPRESA, new RecordColumnRule(23,20, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.FIM_FEBRABAN, new RecordColumnRule(24,29, ColumnType.ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchHeader = new HashMap<String, RecordColumnRule>(){{
        put(RemittanceLayoutKeys.BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.LOTE_SERVICO, new RecordColumnRule(2,4, "0001", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.TIPO_REGISTRO, new RecordColumnRule(3,1, "1", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.TIPO_OPERACAO, new RecordColumnRule(4,1, "C", ColumnType.ALPHA));
        put(RemittanceLayoutKeys.TIPO_SERVICO, new RecordColumnRule(5,2, "20", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.FORMA_LANCAMENTO, new RecordColumnRule(6,2, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.LAYOUT_ARQUIVO, new RecordColumnRule(7,3, "040", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.INICIO_FEBRABAN, new RecordColumnRule(8,9, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.TIPO_INSCRICAO, new RecordColumnRule(9,1, "2", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA, new RecordColumnRule(10,14, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.CONVEIO_BANCO, new RecordColumnRule(11,20, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.AGENCIA, new RecordColumnRule(12,5, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.DIGITO_AGENCIA, new RecordColumnRule(13,1, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.NUMERO_CONTA, new RecordColumnRule(14,12, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.DIGITO_CONTA, new RecordColumnRule(15,1, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA, new RecordColumnRule(16,1, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.NOME_EMPRESA, new RecordColumnRule(17,30, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.MENSAGEM, new RecordColumnRule(18,40, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.LOGRADOURO, new RecordColumnRule(19,30, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.NUMERO, new RecordColumnRule(20,5, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.COMPLEMENTO, new RecordColumnRule(21,15, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.CIDADE, new RecordColumnRule(22,20, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.CEP, new RecordColumnRule(23,5, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.COMPLEMENTO_CEP, new RecordColumnRule(24,3, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.ESTADO, new RecordColumnRule(25,2, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.FIM_FEBRABAN, new RecordColumnRule(26,8, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.OCORRENCIAS, new RecordColumnRule(27,10, ColumnType.ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchSegmentA = new HashMap<String, RecordColumnRule>(){{
        put(RemittanceLayoutKeys.BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.LOTE_SERVICO, new RecordColumnRule(2,4, "0002", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.TIPO_REGISTRO, new RecordColumnRule(3,1, "3", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.NUMERO_REGISTRO, new RecordColumnRule(4,5, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.SEGMENTO, new RecordColumnRule(5,1, "A", ColumnType.ALPHA));
        put(RemittanceLayoutKeys.TIPO_MOVIMENTO, new RecordColumnRule(6,2, "0", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.INSTITUICAO_MOVIMENTO, new RecordColumnRule(7,2,"23", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.CAMARA_CENTRALIZADORA, new RecordColumnRule(8,3, "18", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.BANCO_FAVORECIDO, new RecordColumnRule(9,3, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.AGENCIA, new RecordColumnRule(10,5, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.DIGITO_AGENCIA, new RecordColumnRule(11,1, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.NUMERO_CONTA, new RecordColumnRule(12,12, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.DIGITO_CONTA, new RecordColumnRule(13,1, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA, new RecordColumnRule(14,1, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.NOME_FAVORECIDO, new RecordColumnRule(15,30, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.DOCUMENTO_EMPRESA, new RecordColumnRule(16,20, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.DATA_PAGAMENTO, new RecordColumnRule(17,8, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.TIPO_MOEDA, new RecordColumnRule(18,3, "BRL",  ColumnType.ALPHA));
        put(RemittanceLayoutKeys.QUANTIDADE_MOEDA, new RecordColumnRule(19,15, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.VALOR_PAGAMENTO, new RecordColumnRule(20,15, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.DOCUMENTO_ATRIBUIDO_BANCO, new RecordColumnRule(21,20, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.DATA_REAL_PAGAMENTO, new RecordColumnRule(22,8, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.VALOR_REAL_PAGAMENTO, new RecordColumnRule(23,15, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.INFORMACAO, new RecordColumnRule(24,40, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.FINALIDADE_DOC, new RecordColumnRule(25,2,"01", ColumnType.ALPHA));
        put(RemittanceLayoutKeys.FINALIDADE_TED, new RecordColumnRule(26,5, "12345",  ColumnType.ALPHA));
        put(RemittanceLayoutKeys.FIM_FEBRABAN, new RecordColumnRule(27,5, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.AVISO, new RecordColumnRule(28,1, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.OCORRENCIAS, new RecordColumnRule(29,10, ColumnType.ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchSegmentB = new HashMap<String, RecordColumnRule>(){{
        put(RemittanceLayoutKeys.BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.LOTE_SERVICO, new RecordColumnRule(2,4, "0003", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.TIPO_REGISTRO, new RecordColumnRule(3,1, "3", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.NUMERO_REGISTRO, new RecordColumnRule(4,5, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.SEGMENTO, new RecordColumnRule(5,1, "B", ColumnType.ALPHA));
        put(RemittanceLayoutKeys.INICIO_FEBRABAN, new RecordColumnRule(6,3, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.TIPO_INSCRICAO_FAVORECIDO, new RecordColumnRule(7,1, "2", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.NUMERO_INSCRICAO_FAVORECIDO, new RecordColumnRule(8,14, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.LOGRADOURO, new RecordColumnRule(9,30, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.NUMERO, new RecordColumnRule(10,5, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.COMPLEMENTO, new RecordColumnRule(11,15, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.BAIRRO, new RecordColumnRule(12,15, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.CIDADE, new RecordColumnRule(13,20, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.CEP, new RecordColumnRule(14,5, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.COMPLEMENTO_CEP, new RecordColumnRule(15,3, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.ESTADO, new RecordColumnRule(16,2, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.DATA_VENCIMENTO, new RecordColumnRule(17,8, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.VALOR_DOCUMENTO, new RecordColumnRule(18,15, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.VALOR_ABATIMENTO, new RecordColumnRule(19,15, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.VALOR_DESCONTO, new RecordColumnRule(20,15, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.VALOR_MORA, new RecordColumnRule(21,15, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.VALOR_MULTA, new RecordColumnRule(22,15, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.CODIGO_DOCUMENTO_FAVORECIDO, new RecordColumnRule(23,15, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.FIM_FEBRABAN, new RecordColumnRule(24,15, ColumnType.ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchTrailer = new HashMap<String, RecordColumnRule>(){{
        put(RemittanceLayoutKeys.BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.LOTE_SERVICO, new RecordColumnRule(2,4, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.TIPO_REGISTRO, new RecordColumnRule(3,1, "5", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.INICIO_FEBRABAN, new RecordColumnRule(4,9, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.QUANTIDADE_REGISTROS, new RecordColumnRule(5,6, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.SOMATORIA_VALORES, new RecordColumnRule(6,18, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.QUANTIDADE_MOEDAS, new RecordColumnRule(7,18, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.NUMERO_AVISO_DEBITO, new RecordColumnRule(8,6, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.FIM_FEBRABAN, new RecordColumnRule(9,165, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.OCORRENCIAS, new RecordColumnRule(10,10, ColumnType.ALPHA));
    }};

    private static final HashMap<String, RecordColumnRule> remittanceTrailer = new HashMap<String, RecordColumnRule>(){{
        put(RemittanceLayoutKeys.BANCO_COMPENSACAO, new RecordColumnRule(1,3, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.LOTE_SERVICO, new RecordColumnRule(2,4, "9999", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.TIPO_REGISTRO, new RecordColumnRule(3,1, "9", ColumnType.NUMBER));
        put(RemittanceLayoutKeys.INICIO_FEBRABAN, new RecordColumnRule(4,9, ColumnType.ALPHA));
        put(RemittanceLayoutKeys.QUANTIDADE_LOTES, new RecordColumnRule(5,6, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.QUANTIDADE_REGISTROS, new RecordColumnRule(6,6, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.QUANTIDADE_CONTAS, new RecordColumnRule(7,6, ColumnType.NUMBER));
        put(RemittanceLayoutKeys.FIM_FEBRABAN, new RecordColumnRule(8,205, ColumnType.ALPHA));
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
