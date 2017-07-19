package br.com.unopay.api.payment.cnab240;

import br.com.unopay.api.payment.cnab240.filler.RecordColumnRule;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

import static br.com.unopay.api.payment.cnab240.filler.RemittanceLayout.getBatchSegmentB;
import static br.com.unopay.api.payment.cnab240.filler.RemittanceRecord.SEPARATOR;
import static br.com.unopay.api.uaa.exception.Errors.RULE_COLUMN_REQUIRED;

public class RemittanceFile {

    public static final int ADJUSTMENT = 1;
    private Map<String, RecordColumnRule> layout;
    private String cnab240;

    public RemittanceFile(Map<String, RecordColumnRule> layout, String cnab240){
        this.layout = layout;
        this.cnab240 = cnab240;
    }

    public String extract(String ruleKey, Integer lineNumber) {
        RecordColumnRule rule = getValidRule(this.layout,ruleKey);
        return extractOnLine(cnab240.split(SEPARATOR)[lineNumber - ADJUSTMENT], rule);
    }

    public String extractOnLine(String line, RecordColumnRule rule) {
        return line.substring(rule.getBegin() - ADJUSTMENT,rule.getEnd());
    }

    private RecordColumnRule getValidRule(Map<String, RecordColumnRule> layout, String ruleKey) {
        RecordColumnRule rule = layout.get(ruleKey);
        if(rule == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(RULE_COLUMN_REQUIRED);
        }
        return rule;
    }

    public String findSegmentLine(String ruleKey, String value) {
        RecordColumnRule rule =getValidRule(getBatchSegmentB(),ruleKey);
        return Stream.of(cnab240.split(SEPARATOR))
                .filter(line -> Objects.equals(extractOnLine(line, rule), leftPad(value, rule)))
                .findFirst().orElse(null);
    }

    private String leftPad(String value, RecordColumnRule rule) {
        return StringUtils.leftPad(value,rule.getLength(), rule.getColumnType().getLeftPad());
    }
}
