package br.com.unopay.api.payment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RemittanceGenerator {

    private RemittanceLine header;
    private RemittanceLine trailer;
    private List<RemittanceLine> segments;

    public RemittanceGenerator(){
        segments = new ArrayList<>();
        header = new RemittanceFileHeader();
        trailer = new RemittanceFileTrailer();
    }

    public String build() {
        String header = getLine(this.header.getValues());
        String batch = segments.stream()
                .map(remittanceLine -> getLine(remittanceLine.getValues()))
                .reduce(header, (first, last) -> first.concat("/n").concat(last));
        String trailer = getLine(this.trailer.getValues());
        return batch.concat("/n").concat(trailer);
    }

    private String getLine(Collection<RemittanceColumn> columns) {
        return columns.stream()
                .sorted(Comparator.comparing(RemittanceColumn::getOrder))
                .map(RemittanceColumn::getValue).collect(Collectors.joining());
    }

    public RemittanceGenerator addHeader(RemittanceFileHeader header) {
       this.header = header;
        return this;
    }

    public RemittanceGenerator addTrailer(RemittanceFileTrailer trailer) {
        this.trailer = trailer;
        return this;
    }

    public RemittanceGenerator addBatch(RemittanceBatch segment) {
        this.segments.add(segment);
        return this;
    }
}

