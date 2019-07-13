package br.com.unopay.api.billing.remittance.cnab240;

import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord;
import br.com.unopay.api.billing.remittance.cnab240.filler.WrappedRecord;
import br.com.unopay.api.billing.remittance.cnab240.mapped.itau.ItauBatchHeader;
import br.com.unopay.api.billing.remittance.cnab240.mapped.itau.ItauBatchTrailer;
import br.com.unopay.api.billing.remittance.cnab240.mapped.itau.ItauRemittanceHeader;
import br.com.unopay.api.billing.remittance.cnab240.mapped.itau.ItauRemittanceTrailer;
import br.com.unopay.api.billing.remittance.cnab240.mapped.itau.ItauSegmentA;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import scala.Int;

@Component
public class ItauCnab240Generator {

    public static final String DATE_FORMAT = "ddMMyyyy";
    public static final String HOUR_FORMAT = "hhmmss";
    public static final int BATCH_LINES = 3;

    public ItauCnab240Generator(){}

    public String generate(PaymentRemittance remittance, Date currentDate) {
        FilledRecord remittanceHeader = new ItauRemittanceHeader(ObjectUtils.clone(currentDate)).create(remittance);
        WrappedRecord records = new WrappedRecord().createHeader(remittanceHeader);
        addBatches(remittance, records);
        FilledRecord remittanceTrailer = new ItauRemittanceTrailer().create(remittance);
        return records
                .createTrailer(remittanceTrailer)
                .build();
    }

    private void addBatches(PaymentRemittance remittance, WrappedRecord records) {
        ConcurrentMap<String, Integer> positions = new ConcurrentHashMap<>();
        positions.put("segmentPosition",1);
        positions.put("batchNumber",1);

        remittance.getRemittanceItems().forEach(paymentRemittanceItem -> {
            records.addRecord(createBatch(remittance, paymentRemittanceItem, positions.get("segmentPosition"), positions.get("batchNumber")));
            positions.compute("segmentPosition", (key, value) -> value += BATCH_LINES);
            positions.compute("batchNumber", (key, value) -> value+=1);
        });
    }

    private WrappedRecord createBatch(PaymentRemittance remittance, PaymentRemittanceItem item, Integer position, Integer batchNumber) {
        Integer segmentPosition = position + 1;
        FilledRecord batchHeader = new ItauBatchHeader().create(remittance, batchNumber);
        return new WrappedRecord().createHeader(batchHeader)
                    .addRecord(new ItauSegmentA().create(item, segmentPosition, batchNumber))
                    .createTrailer(new ItauBatchTrailer().create(remittance, batchNumber));
    }
}
