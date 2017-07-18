package br.com.unopay.api.payment.cnab240.filler;

import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static br.com.unopay.api.uaa.exception.Errors.REMITTANCE_COLUMN_LENGTH_NOT_MET;
import static br.com.unopay.api.uaa.exception.Errors.RULE_COLUMN_REQUIRED;

@Slf4j
public class RecordColumn {

    private RecordColumnRule rule;
    private String value;
    private static final Pattern NUMBER_PATTERN = Pattern.compile("[^\\d\\*]");

    public RecordColumn(RecordColumnRule rule) {
        validateRule(rule);
        this.rule = rule;
    }

    public RecordColumn(RecordColumnRule rule, String value) {
        validateRule(rule);
        this.rule = rule;
        setValue(value);
    }

    public String getValue(){
        String value = this.value != null ? this.value : this.rule.getDefaultValue();
        value = value != null ? value : this.rule.getColumnType().getLeftPad();
        return StringUtils.leftPad(value,this.rule.getLength(), this.rule.getColumnType().getLeftPad());
    }

    public Integer getPosition() {
        return this.rule.getField();
    }

    private void validateRule(RecordColumnRule rule) {
        if(rule == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(RULE_COLUMN_REQUIRED);
        }
    }

    private void setValue(String value){
        if(value != null && value.length() > this.rule.getLength()){
            log.error("cnab240 rule={} unexpected length={}", getPosition(), value.length());
            throw UnovationExceptions.unprocessableEntity().withErrors(REMITTANCE_COLUMN_LENGTH_NOT_MET);
        }
        if(value != null && ColumnType.NUMBER.equals(rule.getColumnType())){
            value = NUMBER_PATTERN.matcher(value).replaceAll("");
        }
        this.value = value;
    }
}
