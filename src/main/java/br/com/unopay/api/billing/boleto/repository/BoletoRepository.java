package br.com.unopay.api.billing.boleto.repository;

import br.com.unopay.api.billing.boleto.model.Boleto;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface BoletoRepository extends CrudRepository<Boleto,String> {
    Optional<Boleto> findFirstByOrderByCreateDateTimeDesc();
}
