package br.com.unopay.api.payment;

import static br.com.unopay.api.uaa.exception.Errors.REMITTANCE_COLUMN_LENGTH_NOT_MET;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.apache.commons.lang3.StringUtils;

public class RemittanceColumn {

    private RemittanceColumnRule rule;
    private String value;

    public RemittanceColumn(RemittanceColumnRule rule, String value) {
        this.rule = rule;
        setValue(value);
    }

    private void setValue(String value){
        if(value != null && value.length() > this.rule.getLength()){
            throw UnovationExceptions.unprocessableEntity().withErrors(REMITTANCE_COLUMN_LENGTH_NOT_MET);
        }
        this.value = value;
    }

    public String getValue(){
        String value = this.value != null ? this.value : this.rule.getLeftPad().getValue();
        return StringUtils.leftPad(value,this.rule.getLength(), this.rule.getLeftPad().getValue());
    }

    public Integer getOrder() {
        return this.rule.getOrder();
    }
}
