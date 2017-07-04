package br.com.unopay.api.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FilledRecord implements RemittanceRecord {

    private Map<String, RecordColumnRule> layout;

    public FilledRecord(Map<String, RecordColumnRule> layout){
        this.layout = layout;
    }
    private final List<RecordColumn> columns = new ArrayList<>();

    public void fill(String ruleKey, String value) {
        columns.add(new RecordColumn(layout.get(ruleKey),value));
    }

    public void fill(String ruleKey) {
        columns.add(new RecordColumn(layout.get(ruleKey)));
    }

    public String getRecord(){
        return RecordHelper.getRecord(columns);
    }
}
