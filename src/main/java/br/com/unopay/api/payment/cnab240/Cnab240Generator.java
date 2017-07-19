package br.com.unopay.api.payment.cnab240;

import br.com.unopay.api.payment.cnab240.filler.FilledRecord;
import br.com.unopay.api.payment.cnab240.filler.WrappedRecord;
import br.com.unopay.api.payment.cnab240.mapped.BatchHeader;
import br.com.unopay.api.payment.cnab240.mapped.BatchTrailer;
import br.com.unopay.api.payment.cnab240.mapped.RemittanceHeader;
import br.com.unopay.api.payment.cnab240.mapped.RemittanceTrailer;
import br.com.unopay.api.payment.cnab240.mapped.SegmentA;
import br.com.unopay.api.payment.cnab240.mapped.SegmentB;
import br.com.unopay.api.payment.model.PaymentRemittance;
import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class Cnab240Generator {

    public static final String DATE_FORMAT = "ddMMyyyy";
    public static final String HOUR_FORMAT = "hhmmss";
    public static final int BATCH_LINES = 4;
    private Date currentDate;

    public String generate(PaymentRemittance remittance, Date currentDate) {
        this.currentDate = currentDate;
        FilledRecord remittanceHeader = new RemittanceHeader(currentDate).create(remittance);
        WrappedRecord records = new WrappedRecord().createHeader(remittanceHeader);
        addBatches(remittance, records);
        FilledRecord remittanceTrailer = new RemittanceTrailer().create(remittance);
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
        FilledRecord batchHeader = new BatchHeader().create(remittance, position++);
        return new WrappedRecord().createHeader(batchHeader)
                    .addRecord(new SegmentA(currentDate).create(item, position++))
                    .addRecord(new SegmentB(currentDate).create(item, position++))
                    .createTrailer(new BatchTrailer().create(remittance, position));
    }
}
