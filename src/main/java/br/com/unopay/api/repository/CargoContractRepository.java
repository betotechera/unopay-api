package br.com.unopay.api.repository;

import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.uaa.model.UserType;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface CargoContractRepository extends CrudRepository<CargoContract, String> {
    List<CargoContract> findAll();
}
