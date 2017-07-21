package br.com.unopay.api.payment.repository;

import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import br.com.unopay.api.payment.model.RemittanceSituation;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRemittanceItemRepository extends CrudRepository<PaymentRemittanceItem,String> {

    Optional<PaymentRemittanceItem> findByEstablishmentIdAndSituation(String establishmentId,
                                                                      RemittanceSituation situation);
    Optional<PaymentRemittanceItem> findByEstablishmentPersonDocumentNumber(String document);
}
