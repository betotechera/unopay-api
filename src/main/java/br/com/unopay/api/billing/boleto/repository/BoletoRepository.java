package br.com.unopay.api.billing.boleto.repository;

import br.com.unopay.api.billing.boleto.model.Ticket;
import br.com.unopay.api.billing.boleto.model.filter.BoletoFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface BoletoRepository extends UnovationFilterRepository<Ticket,String, BoletoFilter> {
    Optional<Ticket> findFirstByOrderByCreateDateTimeDesc();

    Optional<Ticket> findByNumber(String number);
}
