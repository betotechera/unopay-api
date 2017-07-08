package br.com.unopay.api.payment.repository;

import br.com.unopay.api.payment.model.PaymentRemittanceItem;
import org.springframework.data.repository.CrudRepository;

public interface PaymentRemittanceItemRepository extends CrudRepository<PaymentRemittanceItem,String> {
}
