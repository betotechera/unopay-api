package br.com.unopay.api.repository;

import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.PaymentInstrumentType;
import br.com.unopay.api.model.filter.PaymentInstrumentFilter;
import br.com.unopay.bootcommons.repository.filter.UnovationFilterRepository;
import java.util.List;
import java.util.Optional;

public interface PaymentInstrumentRepository
                            extends UnovationFilterRepository<PaymentInstrument,String, PaymentInstrumentFilter> {
    Optional<PaymentInstrument> findById(String id);

    Optional<PaymentInstrument> findByIdForIssuer(String id, String issuerId);

    List<PaymentInstrument> findByContractorId(String contractorId);

    Optional<PaymentInstrument> findByIdAndContractorId(String id, String contractorId);
    List<PaymentInstrument> findByContractorPersonDocumentNumber(String contractorDocumentNumber);

    Optional<PaymentInstrument> findFirstByContractorPersonDocumentNumberAndType(String contractorDocumentNumber,
                                                                             PaymentInstrumentType type);

    Integer countByNumber(String instrumentNumber);
}
