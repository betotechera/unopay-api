package br.com.unopay.api.payment.cnab240;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RemittanceLayout {

    public static final String CODIGO_BANCO = "codigoBanco";
    public static final String LOTE_SERVICO = "loteServico";
    public static final String TIPO_REGISTRO = "tipoRegistro";
    public static final String TIPO_INSCRICAO = "tipoInscricao";
    public static final String NUMERO_INSCRICAO_EMPRESA = "numeroInscricaoEmpresa";
    public static final String CONVEIO_BANCO = "conveioBanco";
    public static final String AGENCIA = "agencia";
    public static final String DIGITO_AGENCIA = "digitoAgencia";
    public static final String NUMERO_CONTA = "numeroConta";
    public static final String DIGITO_CONTA = "digitoConta";
    public static final String DIGITO_AGENCIA_CONTA = "digitoAgenciaConta";
    public static final String NOME_EMPRESA = "nomeEmpresa";
    public static final String NOME_BANCO = "nomeBanco";
    public static final String CODIGO_REMESSA = "codigoRemessa";
    public static final String DATA_GERACAO_ARQUIVO = "dataGeracaoArquivo";
    public static final String HORA_GERACAO_ARQUIVO = "horaGeracaoArquivo";
    public static final String SEQUENCIAL_ARQUIVO = "sequencialArquivo";
    public static final String LAYOUT_ARQUIVO = "layoutArquivo";
    public static final String DENSIDADE_GRAVACAO = "densidadeGravacao";
    public static final String RESERVADO_BANCO = "reservadoBanco";
    public static final String RESERVADO_EMPRESA = "reservadoEmpresa";
    public static final String INICIO_FEBRABAN = "inicioFebraban";
    public static final String MEIO_FEBRABAN = "meioFebraban";
    public static final String FIM_FEBRABAN = "fimFebraban";

    private static final Map<String, RecordColumnRule> remittanceHeader = new HashMap<String, RecordColumnRule>(){{
        put(CODIGO_BANCO, new RecordColumnRule(1,3));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0000"));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "0"));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, LeftPadType.SPACE));
        put(TIPO_INSCRICAO, new RecordColumnRule(5,1, "2"));
        put(NUMERO_INSCRICAO_EMPRESA, new RecordColumnRule(6,14));
        put(CONVEIO_BANCO, new RecordColumnRule(7,20));
        put(AGENCIA, new RecordColumnRule(8,5));
        put(DIGITO_AGENCIA, new RecordColumnRule(9,1));
        put(NUMERO_CONTA, new RecordColumnRule(10,12));
        put(DIGITO_CONTA, new RecordColumnRule(11,1));
        put(DIGITO_AGENCIA_CONTA, new RecordColumnRule(12,1));
        put(NOME_EMPRESA, new RecordColumnRule(13,30));
        put(NOME_BANCO, new RecordColumnRule(14,30));
        put(MEIO_FEBRABAN, new RecordColumnRule(15,10, LeftPadType.SPACE));
        put(CODIGO_REMESSA, new RecordColumnRule(16,1, "1"));
        put(DATA_GERACAO_ARQUIVO, new RecordColumnRule(17,8));
        put(HORA_GERACAO_ARQUIVO, new RecordColumnRule(18,6));
        put(SEQUENCIAL_ARQUIVO, new RecordColumnRule(19,6));
        put(LAYOUT_ARQUIVO, new RecordColumnRule(20,3, "080"));
        put(DENSIDADE_GRAVACAO, new RecordColumnRule(21,5, "1600"));
        put(RESERVADO_BANCO, new RecordColumnRule(22,20));
        put(RESERVADO_EMPRESA, new RecordColumnRule(23,20));
        put(FIM_FEBRABAN, new RecordColumnRule(24,29, LeftPadType.SPACE));
    }};

    private static final Map<String, RecordColumnRule> batchHeader = new HashMap<String, RecordColumnRule>(){{
        put(CODIGO_BANCO, new RecordColumnRule(1,3));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0000"));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "0"));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, LeftPadType.SPACE));
    }};

    private static final Map<String, RecordColumnRule> batchSegment = new HashMap<String, RecordColumnRule>(){{
        put(CODIGO_BANCO, new RecordColumnRule(1,3));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0000"));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "0"));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, LeftPadType.SPACE));
    }};

    private static final Map<String, RecordColumnRule> batchTrailer = new HashMap<String, RecordColumnRule>(){{
        put(CODIGO_BANCO, new RecordColumnRule(1,3));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0000"));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "0"));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, LeftPadType.SPACE));
    }};

    private static final HashMap<String, RecordColumnRule> remittanceTrailer = new HashMap<String, RecordColumnRule>(){{
        put(CODIGO_BANCO, new RecordColumnRule(1,3));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0000"));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "0"));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, LeftPadType.SPACE));
    }};

    public static Map<String, RecordColumnRule> getRemittanceHeader(){
        return Collections.unmodifiableMap(remittanceHeader);
    }

    public static Map<String, RecordColumnRule> getBatchHeader(){
        return Collections.unmodifiableMap(batchHeader);
    }

    public static Map<String, RecordColumnRule> getBatchSegment(){
        return Collections.unmodifiableMap(batchSegment);
    }

    public static Map<String, RecordColumnRule> getBatchTrailer(){
        return Collections.unmodifiableMap(batchTrailer);
    }

    public static Map<String, RecordColumnRule> getRemittanceTrailer(){
        return Collections.unmodifiableMap(remittanceTrailer);
    }
}
