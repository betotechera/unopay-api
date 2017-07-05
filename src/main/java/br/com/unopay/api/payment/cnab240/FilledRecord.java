package br.com.unopay.api.payment.cnab240;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FilledRecord implements RemittanceRecord {

    private List<RecordColumn> columns;
    private Map<String, RecordColumnRule> layout;

    public FilledRecord(Map<String, RecordColumnRule> layout){
        this.layout = layout;
        this.columns = new ArrayList<>();
    }

    public void fill(String ruleKey, String value) {
        columns.add(new RecordColumn(layout.get(ruleKey),value));
    }

    public void defaultFill(String ruleKey) {
        columns.add(new RecordColumn(layout.get(ruleKey)));
    }

    public String getRecord() {
        return columns.stream()
                .sorted(Comparator.comparing(RecordColumn::getOrder))
                .map(RecordColumn::getValue).collect(Collectors.joining());
    }
}
