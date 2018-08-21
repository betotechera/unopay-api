package br.com.unopay.api.billing.boleto.model.filter;

import br.com.unopay.bootcommons.model.Period;
import br.com.unopay.bootcommons.repository.filter.SearchableField;
import com.google.common.collect.Sets;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.Data;

@Data
public class TicketFilter implements Serializable {

    public static final Long serialVersionUID = 1L;

    @SearchableField(field = "sourceId")
    private Set<String> orderId;

    @SearchableField
    private String issuerDocument;

    @SearchableField(field = "payerDocument")
    private String clientDocument;

    @SearchableField
    private Period expirationDateTime;

    @SearchableField
    private String number;

    @SearchableField
    private Set<String> occurrenceCode;

    @SearchableField
    private Period createDateTime;

    public void setEveryNotPaid(Boolean everyNotPaid){
        if(everyNotPaid) {
            this.occurrenceCode = Sets.newHashSet("02", "09");
        }
    }

    public Set<String> getOrderId(){
        Optional<Set<String>> list = Optional.ofNullable(this.orderId);
        return list.orElse(new HashSet<>());
    }
}
