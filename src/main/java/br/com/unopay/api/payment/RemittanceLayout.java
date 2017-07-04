package br.com.unopay.api.payment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RemittanceLayout {

    private static final Map<String, RecordColumnRule> remittanceHeader = new HashMap<String, RecordColumnRule>(){{
        put("codigoBanco", new RecordColumnRule(1,3));
        put("loteServico", new RecordColumnRule(2,4, "0000"));
        put("tipoRegistro", new RecordColumnRule(3,1, "0"));
        put("febraban", new RecordColumnRule(4,9, LeftPadType.SPACE));
    }};

    private static final Map<String, RecordColumnRule> batchHeader = new HashMap<String, RecordColumnRule>(){{
        put("codigoBanco", new RecordColumnRule(1,3));
        put("loteServico", new RecordColumnRule(2,4, "0000"));
        put("tipoRegistro", new RecordColumnRule(3,1, "0"));
        put("febraban", new RecordColumnRule(4,9, LeftPadType.SPACE));
    }};

    private static final Map<String, RecordColumnRule> batchSegment = new HashMap<String, RecordColumnRule>(){{
        put("codigoBanco", new RecordColumnRule(1,3));
        put("loteServico", new RecordColumnRule(2,4, "0000"));
        put("tipoRegistro", new RecordColumnRule(3,1, "0"));
        put("febraban", new RecordColumnRule(4,9, LeftPadType.SPACE));
    }};

    private static final Map<String, RecordColumnRule> batchTrailer = new HashMap<String, RecordColumnRule>(){{
        put("codigoBanco", new RecordColumnRule(1,3));
        put("loteServico", new RecordColumnRule(2,4, "0000"));
        put("tipoRegistro", new RecordColumnRule(3,1, "0"));
        put("febraban", new RecordColumnRule(4,9, LeftPadType.SPACE));
    }};

    private static final HashMap<String, RecordColumnRule> remittanceTrailer = new HashMap<String, RecordColumnRule>(){{
        put("codigoBanco", new RecordColumnRule(1,3));
        put("loteServico", new RecordColumnRule(2,4, "0000"));
        put("tipoRegistro", new RecordColumnRule(3,1, "0"));
        put("febraban", new RecordColumnRule(4,9, LeftPadType.SPACE));
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
