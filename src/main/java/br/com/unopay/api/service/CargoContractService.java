package br.com.unopay.api.service;

import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.repository.CargoContractRepository;
import static br.com.unopay.api.uaa.exception.Errors.CARGO_CONTRACT_NOT_FOUND;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CargoContractService {

    private CargoContractRepository repository;

    @Autowired
    public CargoContractService(CargoContractRepository repository) {
        this.repository = repository;
    }

    public CargoContract save(CargoContract cargoContract) {
        return repository.save(cargoContract);
    }


    public CargoContract findById(String id) {
        Optional<CargoContract> cargoContract = repository.findById(id);
        return cargoContract.orElseThrow(()-> UnovationExceptions.notFound().withErrors(CARGO_CONTRACT_NOT_FOUND));
    }

    public Optional<CargoContract> findByPartnerId(String partnerId){
        return repository.findByPartnerId(partnerId);
    }

    public List<CargoContract> findAll(){
        return repository.findAll();
    }
}
