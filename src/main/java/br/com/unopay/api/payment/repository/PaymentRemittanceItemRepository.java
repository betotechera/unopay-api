package br.com.unopay.api.payment.repository;

import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.model.RemittanceSituation;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRemittanceItemRepository extends CrudRepository<PaymentRemittanceItem,String> {

    Optional<PaymentRemittanceItem> findByPayeeDocumentNumberAndSituation(String establishmentId,
                                                                          RemittanceSituation situation);
    Optional<PaymentRemittanceItem> findByPayeeDocumentNumber(String document);
}
