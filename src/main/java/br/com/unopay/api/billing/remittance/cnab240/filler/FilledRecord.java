package br.com.unopay.api.billing.remittance.cnab240.filler;

import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

import static br.com.unopay.api.uaa.exception.Errors.LAYOUT_COLUMN_NOT_FILLED;

@Slf4j
public class FilledRecord implements RemittanceRecord {

    public static final String ASCII = "[^\\p{ASCII}]";
    public static final String ALPHA_P_DIGIT = "[^\\p{Alpha}\\p{Digit}]+";
    private List<RecordColumn> columns;
    private Map<String, RecordColumnRule> layout;

    public FilledRecord(Map<String, RecordColumnRule> layout){
        this.layout = layout;
        this.columns = new ArrayList<>();
    }

    public FilledRecord fill(String ruleKey, String value) {
        String normalizedValue = getNormalizedValue(value);
        RecordColumnRule columnRule = layout.get(ruleKey);
        columns.add(new RecordColumn(columnRule,normalizedValue));
        if(columnRule == null){
            log.warn("Missing column rule={}", ruleKey);
        }
        return this;
    }

    private String getNormalizedValue(String value) {
        String alphabetics=  Normalizer
                .normalize(value, Normalizer.Form.NFD)
                .replaceAll(ASCII, "");
        return alphabetics.replaceAll(ALPHA_P_DIGIT," ");
    }

    public FilledRecord fill(String ruleKey, Integer value) {
        fill(ruleKey, String.valueOf(value));
        return this;
    }

    public FilledRecord defaultFill(String ruleKey) {
        columns.add(new RecordColumn(layout.get(ruleKey)));
        return this;
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
                    .withErrors(LAYOUT_COLUMN_NOT_FILLED.withOnlyArgument(unfilledKeys));
        }
    }
}
