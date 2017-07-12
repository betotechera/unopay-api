package br.com.unopay.api.payment.cnab240;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static br.com.unopay.api.payment.cnab240.ColumnType.*;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.*;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.AGENCIA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CEP;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CIDADE;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CODIGO_REMESSA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.COMPLEMENTO_CEP;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.CONVEIO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DATA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DENSIDADE_GRAVACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_AGENCIA_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.DIGITO_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.ESTADO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FIM_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.FORMA_LANCAMENTO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.HORA_GERACAO_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.INICIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LAYOUT_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOGRADOURO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.LOTE_SERVICO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MEIO_FEBRABAN;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.MENSAGEM;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NOME_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_AVISO_DEBITO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_CONTA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.NUMERO_INSCRICAO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.OCORRENCIAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_CONTAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_LOTES;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_MOEDAS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.QUANTIDADE_REGISTROS;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_BANCO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.RESERVADO_EMPRESA;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SEQUENCIAL_ARQUIVO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.SOMATORIA_VALORES;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_INSCRICAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_OPERACAO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_REGISTRO;
import static br.com.unopay.api.payment.cnab240.RemittanceLayoutKeys.TIPO_SERVICO;

public class RemittanceLayout {

    private static final Map<String, RecordColumnRule> remittanceHeader = new HashMap<String, RecordColumnRule>(){{
        put(CODIGO_BANCO, new RecordColumnRule(1,3, NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0000"));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "0"));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, ALPHA));
        put(TIPO_INSCRICAO, new RecordColumnRule(5,1, "2"));
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
        put(CODIGO_REMESSA, new RecordColumnRule(16,1, "1"));
        put(DATA_GERACAO_ARQUIVO, new RecordColumnRule(17,8, NUMBER));
        put(HORA_GERACAO_ARQUIVO, new RecordColumnRule(18,6, NUMBER));
        put(SEQUENCIAL_ARQUIVO, new RecordColumnRule(19,6, NUMBER));
        put(LAYOUT_ARQUIVO, new RecordColumnRule(20,3, "080"));
        put(DENSIDADE_GRAVACAO, new RecordColumnRule(21,5, "1600"));
        put(RESERVADO_BANCO, new RecordColumnRule(22,20, ALPHA));
        put(RESERVADO_EMPRESA, new RecordColumnRule(23,20, ALPHA));
        put(FIM_FEBRABAN, new RecordColumnRule(24,29, ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchHeader = new HashMap<String, RecordColumnRule>(){{
        put(CODIGO_BANCO, new RecordColumnRule(1,3, NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0001"));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "1"));
        put(TIPO_OPERACAO, new RecordColumnRule(4,1, "C"));
        put(TIPO_SERVICO, new RecordColumnRule(5,2, "20"));
        put(FORMA_LANCAMENTO, new RecordColumnRule(6,2, NUMBER));
        put(LAYOUT_ARQUIVO, new RecordColumnRule(7,3, "040"));
        put(INICIO_FEBRABAN, new RecordColumnRule(8,9, ALPHA));
        put(TIPO_INSCRICAO, new RecordColumnRule(9,1, "2"));
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

    private static final Map<String, RecordColumnRule> batchSegment = new HashMap<String, RecordColumnRule>(){{
        put(CODIGO_BANCO, new RecordColumnRule(1,3, NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "0000"));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "0"));
        put(INICIO_FEBRABAN, new RecordColumnRule(4,9, ALPHA));
    }};

    private static final Map<String, RecordColumnRule> batchTrailer = new HashMap<String, RecordColumnRule>(){{
        put(CODIGO_BANCO, new RecordColumnRule(1,3, NUMBER));
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
        put(CODIGO_BANCO, new RecordColumnRule(1,3, NUMBER));
        put(LOTE_SERVICO, new RecordColumnRule(2,4, "9999"));
        put(TIPO_REGISTRO, new RecordColumnRule(3,1, "9"));
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
