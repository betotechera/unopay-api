package br.com.unopay.api.payment.repository;

import br.com.unopay.api.payment.model.RemittancePayer;
import org.springframework.data.repository.CrudRepository;

public interface RemittancePayerRepository
                            extends CrudRepository<RemittancePayer,String> {
}
