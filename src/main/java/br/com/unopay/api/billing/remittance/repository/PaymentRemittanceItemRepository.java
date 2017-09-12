package br.com.unopay.api.billing.remittance.repository;

import br.com.unopay.api.billing.remittance.model.PaymentRemittanceItem;
import br.com.unopay.api.billing.remittance.model.RemittanceSituation;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRemittanceItemRepository extends CrudRepository<PaymentRemittanceItem,String> {

    Optional<PaymentRemittanceItem> findByPayeeDocumentNumberAndSituation(String establishmentId,
                                                                          RemittanceSituation situation);
    Optional<PaymentRemittanceItem> findByPayeeDocumentNumber(String document);
}
