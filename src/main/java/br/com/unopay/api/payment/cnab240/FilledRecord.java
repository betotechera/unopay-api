package br.com.unopay.api.payment.cnab240;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FilledRecord implements RemittanceRecord {

    private List<RecordColumn> columns;
    private Map<String, RecordColumnRule> layout;

    public FilledRecord(Map<String, RecordColumnRule> layout){
        this.layout = layout;
        this.columns = new ArrayList<>();
    }

    public void fill(String ruleKey, String value) {
        RecordColumnRule columnRule = layout.get(ruleKey);

        columns.add(new RecordColumn(columnRule,value));
    }

    public void fill(String ruleKey, Integer value) {
        fill(ruleKey, String.valueOf(value));
    }

    public void defaultFill(String ruleKey) {
        columns.add(new RecordColumn(layout.get(ruleKey)));
    }

    public String build() {
        return columns.stream()
                .sorted(Comparator.comparing(RecordColumn::getOrder))
                .map(RecordColumn::getValue).collect(Collectors.joining());
    }
}
