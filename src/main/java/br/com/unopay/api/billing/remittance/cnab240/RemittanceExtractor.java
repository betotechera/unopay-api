package br.com.unopay.api.billing.remittance.cnab240;

import br.com.unopay.api.billing.remittance.cnab240.filler.RecordColumnRule;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Map;

import static br.com.unopay.api.billing.remittance.cnab240.filler.RemittanceRecord.SEPARATOR;
import static br.com.unopay.api.uaa.exception.Errors.RULE_COLUMN_REQUIRED;

public class RemittanceExtractor {

    public static final int ADJUSTMENT = 1;
    public static final int FIRST_LINE = 1;
    private Map<String, RecordColumnRule> layout;
    private String cnab240;

    public RemittanceExtractor(Map<String, RecordColumnRule> layout, String cnab240){
        this.layout = layout;
        this.cnab240 = cnab240;
    }

    public String extractOnFirstLine(String ruleKey) {
        RecordColumnRule rule = getValidRule(this.layout,ruleKey);
        return extractOnLine(cnab240.split(SEPARATOR)[FIRST_LINE - ADJUSTMENT], rule);
    }

    public String extractOnLine(String ruleKey, Integer lineNumber) {
        RecordColumnRule rule = getValidRule(this.layout,ruleKey);
        return extractOnLine(cnab240.split(SEPARATOR)[lineNumber - ADJUSTMENT], rule);
    }

    private String extractOnLine(String line, RecordColumnRule rule) {
        return line.substring(rule.getBegin() - ADJUSTMENT,rule.getEnd());
    }

    private RecordColumnRule getValidRule(Map<String, RecordColumnRule> layout, String ruleKey) {
        RecordColumnRule rule = layout.get(ruleKey);
        if(rule == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(RULE_COLUMN_REQUIRED);
        }
        return rule;
    }
}
