package br.com.unopay.api.credit.repository;

import br.com.unopay.api.credit.model.InstrumentBalance;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface InstrumentBalanceRepository extends CrudRepository<InstrumentBalance, String>{
        Optional<InstrumentBalance> findByPaymentInstrumentId(String instrumentId);
}
