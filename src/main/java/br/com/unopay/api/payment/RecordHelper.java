package br.com.unopay.api.payment;

import static br.com.unopay.api.payment.RemittanceRecord.SEPARATOR;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecordHelper {

    public static String getRecord(Collection<RecordColumn> columns) {
        return columns.stream()
                .sorted(Comparator.comparing(RecordColumn::getOrder))
                .map(RecordColumn::getValue).collect(Collectors.joining());
    }

    public static String getRecords(String initialValue, List<RemittanceRecord> records) {
        return records.stream()
                .map(RemittanceRecord::getRecord)
                .reduce(initialValue, (first, last) -> first.concat(SEPARATOR).concat(last));
    }
}
