package br.com.unopay.api.billing.remittance.repository;

import br.com.unopay.api.billing.remittance.model.RemittancePayer;
import org.springframework.data.repository.CrudRepository;

public interface RemittancePayerRepository
                            extends CrudRepository<RemittancePayer,String> {
}
