package br.com.unopay.api.billing.remittance.cnab240.filler;

public enum ColumnType {
    NUMBER("0"), ALPHA(" ");

    private String leftPad;

    ColumnType(String leftPad){
        this.leftPad = leftPad;
    }

    public String getLeftPad() {
        return leftPad;
    }


}
