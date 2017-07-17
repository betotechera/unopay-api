package br.com.unopay.api.payment.cnab240.filler;

import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.ArrayList;
import java.util.List;

import static br.com.unopay.api.uaa.exception.Errors.HEADER_REQUIRED_ON_WRAPPED_RECORD;
import static br.com.unopay.api.uaa.exception.Errors.TRAILER_REQUIRED_ON_WRAPPED_RECORD;

public class WrappedRecord implements RemittanceRecord {

    private RemittanceRecord header;
    private RemittanceRecord trailer;
    private List<RemittanceRecord> batchRecords;

    public WrappedRecord(){
        batchRecords = new ArrayList<>();
    }

    public String build() {
        validate();
        String header = this.header.build();
        String batch = getRecords(header);
        String trailer = this.trailer.build();
        return batch.concat(SEPARATOR).concat(trailer);
    }

    public WrappedRecord createHeader(RemittanceRecord header) {
       this.header = header;
        return this;
    }

    public WrappedRecord createTrailer(RemittanceRecord trailer) {
        this.trailer = trailer;
        return this;
    }

    public WrappedRecord addRecord(RemittanceRecord segment) {
        this.batchRecords.add(segment);
        return this;
    }

    private void validate() {
        if(header == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(HEADER_REQUIRED_ON_WRAPPED_RECORD);
        }
        if(trailer == null){
            throw UnovationExceptions.unprocessableEntity().withErrors(TRAILER_REQUIRED_ON_WRAPPED_RECORD);
        }
    }

    private String getRecords(String initialValue) {
        return batchRecords.stream()
                .map(RemittanceRecord::build)
                .reduce(initialValue, (first, last) -> first.concat(RemittanceRecord.SEPARATOR).concat(last));
    }
}

