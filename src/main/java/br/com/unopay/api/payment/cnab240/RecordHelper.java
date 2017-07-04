package br.com.unopay.api.payment.cnab240;

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
                .reduce(initialValue, (first, last) -> first.concat(RemittanceRecord.SEPARATOR).concat(last));
    }
}
