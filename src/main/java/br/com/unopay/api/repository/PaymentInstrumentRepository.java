package br.com.unopay.api.repository;

import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.api.repository.filter.UnovationFilterRepository;

public interface PaymentInstrumentRepository
                            extends UnovationFilterRepository<PaymentInstrument,String, PaymentInstrumentFilter> {}
