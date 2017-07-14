package br.com.unopay.api.payment.cnab240.filler;

import lombok.Getter;

public class RecordColumnRule {

    @Getter private Integer position;
    @Getter private Integer length;
    @Getter private ColumnType columnType = ColumnType.ALPHA;
    @Getter private String defaultValue;

    public RecordColumnRule(Integer position, Integer length, ColumnType columnType) {
        this.position = position;
        this.length = length;
        this.columnType = columnType;
    }

    public RecordColumnRule(Integer position, Integer length, String defaultValue, ColumnType columnType) {
        this.position = position;
        this.length = length;
        this.columnType = columnType;
        this.defaultValue = defaultValue;
    }
}
