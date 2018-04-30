package br.com.unopay.api.market.service;

import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.market.repository.ContractorBonusRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_BONUS_NOT_FOUND;

@Service
public class ContractorBonusService {

    private ContractorBonusRepository contractorBonusRepository;

    @Autowired
    public ContractorBonusService(ContractorBonusRepository contractorBonusRepository) {
        this.contractorBonusRepository = contractorBonusRepository;
    }

    public ContractorBonus save(ContractorBonus contractorBonus) {
        return contractorBonusRepository.save(contractorBonus);
    }

    private ContractorBonus getContractorBonus(String id, Supplier<Optional<ContractorBonus>> contractorBonus){
        Optional<ContractorBonus> bonus = contractorBonus.get();
        return bonus.orElseThrow(() ->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_BONUS_NOT_FOUND.withOnlyArgument(id)));
    }

    public ContractorBonus findById(String id) {
        return getContractorBonus(id, () -> contractorBonusRepository.findById(id));
    }

}
