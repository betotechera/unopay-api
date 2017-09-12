package br.com.unopay.api.billing.remittance.cnab240.filler;

import lombok.Getter;

public class RecordColumnRule {

    @Getter private Integer field;
    @Getter private Integer begin;
    @Getter private Integer end;
    @Getter private Integer length;
    @Getter private ColumnType columnType = ColumnType.ALPHA;
    @Getter private String defaultValue;

    public RecordColumnRule(Integer field,Integer begin, Integer end, Integer length, ColumnType columnType) {
        this.field = field;
        this.begin = begin;
        this.end = end;
        this.length = length;
        this.columnType = columnType;
    }

    public RecordColumnRule(Integer field,Integer begin, Integer end,
                            Integer length, String defaultValue, ColumnType columnType) {
        this.field = field;
        this.begin = begin;
        this.end = end;
        this.length = length;
        this.columnType = columnType;
        this.defaultValue = defaultValue;
    }
}
