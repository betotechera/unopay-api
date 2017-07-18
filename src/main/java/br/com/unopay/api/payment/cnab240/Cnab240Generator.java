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
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class Cnab240Generator {

    public static final int HEADERS_POSITION = 2;
    public static final String DATE_FORMAT = "ddMMyyyy";
    public static final String HOUR_FORMAT = "hhmmss";
    private Date currentDate;

    public String generate(PaymentRemittance remittance, Date currentDate) {
        this.currentDate = currentDate;
        FilledRecord remittanceHeader = new RemittanceHeader(currentDate).create(remittance);
        WrappedRecord batch = createBatch(remittance);
        FilledRecord remittanceTrailer = new RemittanceTrailer().create(remittance);
        return new WrappedRecord()
                .createHeader(remittanceHeader)
                .addRecord(batch)
                .createTrailer(remittanceTrailer)
                .build();
    }

    private WrappedRecord createBatch(PaymentRemittance remittance) {
        FilledRecord batchHeader = new BatchHeader().create(remittance);
        WrappedRecord remittanceRecords = new WrappedRecord().createHeader(batchHeader);
        int[] currentPosition = {HEADERS_POSITION};
        remittance.getRemittanceItems().forEach(paymentRemittanceItem ->  {
            currentPosition[0]++;
            remittanceRecords
                    .addRecord(new SegmentA(currentDate).create(paymentRemittanceItem, currentPosition[0]))
                    .addRecord(new SegmentB(currentDate).create(paymentRemittanceItem, currentPosition[0]));

        });
        return remittanceRecords.createTrailer(new BatchTrailer().create(remittance));
    }
}
