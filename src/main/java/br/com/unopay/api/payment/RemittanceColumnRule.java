package br.com.unopay.api.payment;

import lombok.Getter;

public class RemittanceColumnRule {

    @Getter private Integer order;
    @Getter private Integer length;
    @Getter private LeftPadType leftPad = LeftPadType.ZERO;
    @Getter private String defaultValue;

    public RemittanceColumnRule(Integer order, Integer length) {
        this.order = order;
        this.length = length;
    }

    public RemittanceColumnRule(Integer order, Integer length, LeftPadType leftPad) {
        this.order = order;
        this.length = length;
        this.leftPad = leftPad;
    }

    public RemittanceColumnRule(Integer order, Integer length, String defaultValue) {
        this.order = order;
        this.length = length;
        this.defaultValue = defaultValue;
    }

    public RemittanceColumnRule(Integer order, Integer length, LeftPadType leftPad, String defaultValue) {
        this.order = order;
        this.length = length;
        this.leftPad = leftPad;
        this.defaultValue = defaultValue;
    }
}
