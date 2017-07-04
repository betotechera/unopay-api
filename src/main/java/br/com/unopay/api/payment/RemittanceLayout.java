package br.com.unopay.api.payment;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RemittanceLayout {

    private static final Map<String, RemittanceColumnRule> header = new HashMap<String, RemittanceColumnRule>(){{
        put("codigoBanco", new RemittanceColumnRule(1,3));
        put("loteServico", new RemittanceColumnRule(2,4, "0000"));
        put("tipoRegistro", new RemittanceColumnRule(3,1, "0"));
        put("febraban", new RemittanceColumnRule(4,9, LeftPadType.SPACE));
    }};

    private static final Map<String, RemittanceColumnRule> segment = new HashMap<String, RemittanceColumnRule>(){{
        put("codigoBanco", new RemittanceColumnRule(1,3));
        put("loteServico", new RemittanceColumnRule(2,4, "0000"));
        put("tipoRegistro", new RemittanceColumnRule(3,1, "0"));
        put("febraban", new RemittanceColumnRule(4,9, LeftPadType.SPACE));
    }};

    private static final HashMap<String, RemittanceColumnRule> trailer = new HashMap<String, RemittanceColumnRule>(){{
        put("codigoBanco", new RemittanceColumnRule(1,3));
        put("loteServico", new RemittanceColumnRule(2,4, "0000"));
        put("tipoRegistro", new RemittanceColumnRule(3,1, "0"));
        put("febraban", new RemittanceColumnRule(4,9, LeftPadType.SPACE));
    }};

    public static Map<String, RemittanceColumnRule> getHeader(){
        return Collections.unmodifiableMap(header);
    }

    public static Map<String, RemittanceColumnRule> getSegment(){
        return Collections.unmodifiableMap(segment);
    }

    public static Map<String, RemittanceColumnRule> getTrailer(){
        return Collections.unmodifiableMap(trailer);
    }
}
