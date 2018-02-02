package br.com.unopay.api.billing.boleto.repository;

import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.TicketFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface TicketRepository extends UnovationFilterRepository<Ticket,String, TicketFilter> {

    Optional<Ticket> findByNumber(String number);

    Optional<Ticket> findByNumberAndIssuerDocument(String number, String issuerDocument);
}
