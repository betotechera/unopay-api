package br.com.unopay.api.bacen.model.csv;

import br.com.unopay.api.bacen.model.EstablishmentEvent;
import br.com.unopay.api.bacen.model.Event;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class EstablishmentEventFeeCsv {

    @CsvBindByName(column = "event")
    private String eventName;

    @CsvBindByName(column = "establishment")
    private String establishmentDocument;

    @CsvBindByName
    private BigDecimal value;

    @CsvDate("dd/MM/yyyy")
    @CsvBindByName
    private Date expiration;

    public EstablishmentEvent toEstablishmentEventFee(Event event) {
        EstablishmentEvent establishmentEvent = new EstablishmentEvent();
        establishmentEvent.setEvent(event);
        establishmentEvent.setValue(this.value);
        establishmentEvent.setExpiration(this.expiration);
        return establishmentEvent;
    }
}
