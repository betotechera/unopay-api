package br.com.unopay.api.bacen.model.csv;

import br.com.unopay.api.network.model.EstablishmentEvent;
import br.com.unopay.api.network.model.Event;
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

    @CsvBindByName
    private BigDecimal privateValue;

    @CsvDate("dd/MM/yyyy")
    @CsvBindByName
    private Date expiration;

    public EstablishmentEvent toEstablishmentEventFee(Event event) {
        EstablishmentEvent establishmentEvent = new EstablishmentEvent();
        establishmentEvent.setEvent(event);
        establishmentEvent.setValue(this.value);
        establishmentEvent.setExpiration(this.expiration);
        establishmentEvent.setPrivateValue(this.privateValue);
        return establishmentEvent;
    }
}
