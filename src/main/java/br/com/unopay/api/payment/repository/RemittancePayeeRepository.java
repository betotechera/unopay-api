package br.com.unopay.api.payment.repository;

import br.com.unopay.api.payment.model.RemittancePayee;
import org.springframework.data.repository.CrudRepository;

public interface RemittancePayeeRepository
                            extends CrudRepository<RemittancePayee,String> {
}
