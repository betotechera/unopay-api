package br.com.unopay.api.billing.remittance.cnab240;

import br.com.unopay.api.billing.remittance.cnab240.filler.FilledRecord;
import br.com.unopay.api.billing.remittance.cnab240.filler.WrappedRecord;
import br.com.unopay.api.billing.remittance.cnab240.mapped.bradesco.BradescoBatchHeader;
import br.com.unopay.api.billing.remittance.cnab240.mapped.bradesco.BradescoBatchTrailer;
import br.com.unopay.api.billing.remittance.cnab240.mapped.bradesco.BradescoRemittanceHeader;
import br.com.unopay.api.billing.remittance.cnab240.mapped.bradesco.BradescoRemittanceTrailer;
import br.com.unopay.api.billing.remittance.cnab240.mapped.bradesco.BradescoSegmentA;
import br.com.unopay.api.billing.remittance.cnab240.mapped.bradesco.BradescoSegmentB;
import br.com.unopay.api.billing.remittance.model.PaymentRemittance;
import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem;
import java.util.Date;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

@Component
public class BradescoCnab240Generator {

    public static final String DATE_FORMAT = "ddMMyyyy";
    public static final String HOUR_FORMAT = "hhmmss";
    public static final int BATCH_LINES = 4;
    private Date currentDate;

    public BradescoCnab240Generator(){}

    public String generate(PaymentRemittance remittance, Date currentDate) {
        this.currentDate = ObjectUtils.clone(currentDate);
        FilledRecord remittanceHeader = new BradescoRemittanceHeader(currentDate).create(remittance);
        WrappedRecord records = new WrappedRecord().createHeader(remittanceHeader);
        addBatches(remittance, records);
        FilledRecord remittanceTrailer = new BradescoRemittanceTrailer().create(remittance);
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
        FilledRecord batchHeader = new BradescoBatchHeader().create(remittance,item, position++);
        return new WrappedRecord().createHeader(batchHeader)
                    .addRecord(new BradescoSegmentA(currentDate).create(item, position++))
                    .addRecord(new BradescoSegmentB(currentDate).create(item, position++))
                    .createTrailer(new BradescoBatchTrailer().create(remittance, position));
    }
}
