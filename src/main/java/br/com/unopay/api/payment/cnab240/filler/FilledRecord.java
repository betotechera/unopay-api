package br.com.unopay.api.payment.cnab240.filler;

import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static br.com.unopay.api.uaa.exception.Errors.LAYOUT_COLUMN_NOT_FILLED;

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
        checkAllFieldsFilled();
        return columns.stream()
                .sorted(Comparator.comparing(RecordColumn::getField))
                .map(RecordColumn::getValue).collect(Collectors.joining());
    }

    private void checkAllFieldsFilled() {
        Set<Integer> fields = columns.stream().map(RecordColumn::getField).collect(Collectors.toSet());
        Set<String> unfilledKeys = layout.entrySet().stream()
                .filter(entry -> !fields.contains(entry.getValue().getField()))
                .map(Map.Entry::getKey).collect(Collectors.toSet());
        if(!unfilledKeys.isEmpty()){
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(LAYOUT_COLUMN_NOT_FILLED.withArguments(unfilledKeys));
        }
    }
}
