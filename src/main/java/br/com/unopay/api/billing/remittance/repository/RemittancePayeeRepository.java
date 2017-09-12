package br.com.unopay.api.billing.remittance.repository;

import br.com.unopay.api.billing.remittance.model.RemittancePayee;
import org.springframework.data.repository.CrudRepository;

public interface RemittancePayeeRepository
                            extends CrudRepository<RemittancePayee,String> {
}
