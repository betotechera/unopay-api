package br.com.unopay.api.payment.cnab240;

import br.com.unopay.api.payment.cnab240.filler.RecordColumnRule;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Map;

import static br.com.unopay.api.payment.cnab240.filler.RemittanceRecord.SEPARATOR;
import static br.com.unopay.api.uaa.exception.Errors.RULE_COLUMN_REQUIRED;

public class RemittanceFile {

    private Map<String, RecordColumnRule> layout;
    private String cnab240;

    public RemittanceFile(Map<String, RecordColumnRule> layout, String cnab240){
        this.layout = layout;
        this.cnab240 = cnab240;
    }

    public Object extract(String ruleKey, Integer lineNumber) {
        RecordColumnRule rule = getValidRule(ruleKey);
        int adjustment = 1;
        String line = cnab240.split(SEPARATOR)[lineNumber - adjustment];
        return line.substring(rule.getBegin() - adjustment,rule.getEnd());
    }

    private RecordColumnRule getValidRule(String ruleKey) {
        RecordColumnRule rule = layout.get(ruleKey);
        if(rule == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(RULE_COLUMN_REQUIRED);
        }
        return rule;
    }
}
