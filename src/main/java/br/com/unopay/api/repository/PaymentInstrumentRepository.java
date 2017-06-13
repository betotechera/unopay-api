package br.com.unopay.api.repository;

import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentInstrumentRepository
                            extends UnovationFilterRepository<PaymentInstrument,String, PaymentInstrumentFilter> {
    Optional<PaymentInstrument> findById(String id);

    List<PaymentInstrument> findByContractorId(String contractorId);
}
