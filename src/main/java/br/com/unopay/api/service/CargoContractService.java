package br.com.unopay.api.service;

import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.repository.CargoContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CargoContractService {

    private CargoContractRepository repository;

    @Autowired
    public CargoContractService(CargoContractRepository repository) {
        this.repository = repository;
    }

    public CargoContract create(CargoContract cargoContract) {
        return repository.save(cargoContract);
    }

    public CargoContract findById(String id) {
        return repository.findOne(id);
    }
}
