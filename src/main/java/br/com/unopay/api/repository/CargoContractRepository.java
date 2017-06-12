package br.com.unopay.api.repository;

import br.com.unopay.api.model.CargoContract;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface CargoContractRepository extends CrudRepository<CargoContract, String> {
    List<CargoContract> findAll();

    Optional<CargoContract> findById(String id);
    Optional<CargoContract> findByPartnerId(String partnerId);
}
