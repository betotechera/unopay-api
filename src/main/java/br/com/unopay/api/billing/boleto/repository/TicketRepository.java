package br.com.unopay.api.billing.boleto.repository;

import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.TicketFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface TicketRepository extends UnovationFilterRepository<Ticket,String, TicketFilter> {

    Optional<Ticket> findByNumberAndProcessedAtIsNull(String number);

    Optional<Ticket> findByNumberAndIssuerDocumentAndProcessedAtIsNull(String number, String issuerDocument);

    Integer countByNumber(String number);
}
