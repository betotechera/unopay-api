package br.com.unopay.api.market.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.network.model.Partner;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.market.model.BonusSituation;
import br.com.unopay.api.market.model.ContractorBonus;
import br.com.unopay.api.market.model.filter.ContractorBonusFilter;
import br.com.unopay.api.market.repository.ContractorBonusRepository;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.market.model.BonusSituation.CANCELED;
import static br.com.unopay.api.market.model.BonusSituation.FOR_PROCESSING;
import static br.com.unopay.api.uaa.exception.Errors.CONTRACTOR_BONUS_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.INVALID_BONUS_SITUATION;

@Service
public class ContractorBonusService {

    private ContractorBonusRepository contractorBonusRepository;
    private ProductService productService;
    private ContractorService contractorService;
    private PersonService personService;

    private static final int EQUAL = 0;

    @Autowired
    public ContractorBonusService(ContractorBonusRepository contractorBonusRepository,
                                  ProductService productService,
                                  ContractorService contractorService,
                                  PersonService personService) {
        this.contractorBonusRepository = contractorBonusRepository;
        this.productService = productService;
        this.contractorService = contractorService;
        this.personService = personService;
    }

    public ContractorBonus save(ContractorBonus contractorBonus) {
        return contractorBonusRepository.save(contractorBonus);
    }

    public ContractorBonus create(ContractorBonus contractorBonus) {
        defineValidReferences(contractorBonus);
        contractorBonus.setupMyCreate();
        return save(contractorBonus);
    }

    public ContractorBonus update(String id, ContractorBonus contractorBonus) {
        ContractorBonus current = findById(id);
        contractorBonus.setupMyUpdate();
        defineValidReferences(contractorBonus);
        current.updateMe(contractorBonus);
        return save(current);
    }

    public ContractorBonus updateForEstablishment(String id, Establishment establishment,
                                                  ContractorBonus contractorBonus) {
        ContractorBonus current = findByIdForPerson(id, establishment.getPerson());
        checkIfValidSituationChange(current, contractorBonus);
        current.updateOnly(contractorBonus, "situation");
        return save(current);
    }

    public void delete(String id) {
        findById(id);
        contractorBonusRepository.delete(id);
    }

    private ContractorBonus getContractorBonus(String id, Supplier<Optional<ContractorBonus>> contractorBonus){
        Optional<ContractorBonus> bonus = contractorBonus.get();
        return bonus.orElseThrow(() ->
                UnovationExceptions.notFound().withErrors(CONTRACTOR_BONUS_NOT_FOUND.withOnlyArgument(id)));
    }

    public ContractorBonus findById(String id) {
        return getContractorBonus(id, () -> contractorBonusRepository.findById(id));
    }

    public ContractorBonus findByIdForContractor(String id, Contractor contractor) {
        return getContractorBonus(id, () -> contractorBonusRepository.findByIdAndContractorId(id, contractor.getId()));
    }

    public ContractorBonus findByIdForPerson(String id, Person person) {
        return getContractorBonus(id, () -> contractorBonusRepository.findByIdAndPayerId(id, person.getId()));
    }

    public Page<ContractorBonus> findByFilter(ContractorBonusFilter filter, UnovationPageRequest pageable){
        return contractorBonusRepository
                .findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    public List<Person> getPayersWithBonusToProcess() {
        return  getPayersWithBonusToProcessForIssuer(null);
    }

    public List<Person> getPayersWithBonusToProcessForIssuer(String id) {
        ContractorBonusFilter filter = new ContractorBonusFilter();
        filter.setSituation(BonusSituation.FOR_PROCESSING);
        filter.setIssuer(id);
        Stream<ContractorBonus> bonuses = contractorBonusRepository.findAll(filter).stream();
        return bonuses.map(ContractorBonus::getPayer).distinct().collect(Collectors.toList());
    }

    public List<ContractorBonus> getBonusesToProcessForPayer(String documentNumber) {
        ContractorBonusFilter filter = new ContractorBonusFilter();
        filter.setSituation(BonusSituation.FOR_PROCESSING);
        filter.setPayer(documentNumber);
        return contractorBonusRepository.findAll(filter);
    }
    public ContractorBonus createForEstablishment(Establishment establishment, ContractorBonus contractorBonus) {
        contractorBonus.setPayer(establishment.getPerson());
        return create(contractorBonus);
    }

    public ContractorBonus createForPartner(Partner partner, ContractorBonus contractorBonus) {
        contractorBonus.setPayer(partner.getPerson());
        return create(contractorBonus);
    }

    public ContractorBonus createForServiceAuthorize(ServiceAuthorize serviceAuthorize) {
        ContractorBonus contractorBonus = new ContractorBonus(serviceAuthorize);
        return create(contractorBonus);
    }

    private void defineValidProduct(ContractorBonus contractorBonus) {
        contractorBonus.setProduct(productService.findById(contractorBonus.productId()));
    }

    private void defineValidContractor(ContractorBonus contractorBonus) {
       contractorBonus.setContractor(contractorService.getById(contractorBonus.contractorId()));
    }

    private void defineValidPayer(ContractorBonus contractorBonus) {
        contractorBonus.setPayer(personService.findById(contractorBonus.payerId()));
    }

    private void defineValidReferences(ContractorBonus contractorBonus) {
        defineValidProduct(contractorBonus);
        defineValidContractor(contractorBonus);
        defineValidPayer(contractorBonus);
    }

    private void checkIfValidSituationChange(ContractorBonus current, ContractorBonus contractorBonus) {
        if (!current.getSituation().equals(contractorBonus.getSituation())
                && !(FOR_PROCESSING.equals(current.getSituation()) && CANCELED.equals(contractorBonus.getSituation()))) {
            throw UnovationExceptions.conflict().withErrors(INVALID_BONUS_SITUATION);
        }
    }

}
