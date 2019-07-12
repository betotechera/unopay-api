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
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

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
        int[] currentPosition = {1};
        remittance.getRemittanceItems().forEach(paymentRemittanceItem -> {
            records.addRecord(createBatch(remittance, paymentRemittanceItem, currentPosition[0]));
            currentPosition[0]+= BATCH_LINES;
        });
    }

    private WrappedRecord createBatch(PaymentRemittance remittance, PaymentRemittanceItem item, Integer position) {
        FilledRecord batchHeader = new ItauBatchHeader().create(remittance, position++);
        return new WrappedRecord().createHeader(batchHeader)
                    .addRecord(new ItauSegmentA().create(item, position++))
                    .createTrailer(new ItauBatchTrailer().create(remittance, position));
    }
}
