package br.com.unopay.api.billing.boleto.repository;

import br.com.unopay.api.billing.boleto.model.Boleto;
import br.com.unopay.api.billing.boleto.model.filter.BoletoFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.Optional;

public interface BoletoRepository extends UnovationFilterRepository<Boleto,String, BoletoFilter> {
    Optional<Boleto> findFirstByOrderByCreateDateTimeDesc();

    Optional<Boleto> findByNumber(String number);
}
