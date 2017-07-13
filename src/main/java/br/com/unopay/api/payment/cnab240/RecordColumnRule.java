package br.com.unopay.api.payment.cnab240;

import lombok.Getter;

public class RecordColumnRule {

    @Getter private Integer order;
    @Getter private Integer length;
    @Getter private ColumnType columnType = ColumnType.ALPHA;
    @Getter private String defaultValue;

    public RecordColumnRule(Integer order, Integer length, ColumnType columnType) {
        this.order = order;
        this.length = length;
        this.columnType = columnType;
    }

    public RecordColumnRule(Integer order, Integer length, String defaultValue, ColumnType columnType) {
        this.order = order;
        this.length = length;
        this.columnType = columnType;
        this.defaultValue = defaultValue;
    }
}
