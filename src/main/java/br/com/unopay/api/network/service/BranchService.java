package br.com.unopay.api.network.service;

import br.com.unopay.api.geo.service.GeoService;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.network.model.Branch;
import br.com.unopay.api.network.model.BranchServicePeriod;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.network.model.filter.BranchFilter;
import br.com.unopay.api.network.repository.BranchRepository;
import br.com.unopay.api.repository.AddressRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Collection;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.ADDRESS_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.BRANCH_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK;

@Service
public class BranchService {

    private AddressRepository addressRepository;
    private BranchRepository repository;
    private AccreditedNetworkService accreditedNetworkService;
    private BranchServicePeriodService branchServicePeriodService;
    @Setter
    private GeoService geoService;
    private EstablishmentService establishmentService;

    @Autowired
    public BranchService(EstablishmentService establishmentService,
                         AddressRepository addressRepository,
                         BranchRepository repository,
                         AccreditedNetworkService accreditedNetworkService,
                         BranchServicePeriodService branchServicePeriodService,
                         GeoService geoService){
        this.addressRepository = addressRepository;
        this.repository = repository;
        this.establishmentService = establishmentService;
        this.accreditedNetworkService = accreditedNetworkService;
        this.branchServicePeriodService = branchServicePeriodService;
        this.geoService = geoService;
    }

    public Branch create(Branch branch, AccreditedNetwork accreditedNetwork) {
        AccreditedNetwork network = accreditedNetworkService.getById(accreditedNetwork.getId());
        Establishment headOffice = establishmentService.findByIdAndNetworks(branch.getHeadOffice().getId(), network);
        branch.setHeadOffice(headOffice);
        return create(branch);
    }

    @Transactional
    public Branch create(Branch branch) {
        branch.validateCreate();
        geoService.defineAddressLatLong(branch);
        saveAddress(branch);
        validateExistingReferences(branch);
        Collection<BranchServicePeriod> periods = branch.cutServicePeriods();
        Branch current = repository.save(branch);
        persistServicePeriods(periods, current);
        return current;
    }

    private void persistServicePeriods(Collection<BranchServicePeriod> periods, Branch current) {
        periods.forEach(period -> period.setBranch(current));
        current.setServicePeriods(branchServicePeriodService.create(periods));
    }

    public void update(String id, Branch branch, AccreditedNetwork accreditedNetwork) {
        Establishment establishment = checkEstablishmentOwner(id, accreditedNetwork);
        branch.setHeadOffice(establishment);
        update(id, branch);
    }

    public void update(String id, Branch branch) {
        Branch current = findById(id);
        branch.validateUpdate(current);
        validateExistingReferences(branch);
        current.updateMe(branch);
        saveAddress(branch);
        updateServicePeriods(branch.getServicePeriods(), current);
        repository.save(current);
    }

    private void updateServicePeriods(Collection<BranchServicePeriod> periods, Branch current) {
        periods.forEach(period -> period.setBranch(current));
        branchServicePeriodService.updateOrCreate(periods);
    }

    public Branch findById(String id, AccreditedNetwork accreditedNetwork) {
        Optional<Branch> branch = repository.findByIdAndHeadOfficeNetworkId(id, accreditedNetwork.getId());
        return branch.orElseThrow(() -> UnovationExceptions.notFound().withErrors(BRANCH_NOT_FOUND));
    }

    public Branch findById(String id) {
        Optional<Branch> branch = repository.findById(id);
        return branch.orElseThrow(() -> UnovationExceptions.notFound().withErrors(BRANCH_NOT_FOUND));
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    private void saveAddress(Branch branch) {
        addressRepository.save(branch.getAddress());
    }

    private void validateExistingReferences(Branch branch) {
        branch.setHeadOffice(establishmentService.findById(branch.getHeadOffice().getId()));
        getAddressById(branch);
    }

    private Address getAddressById(Branch branch) {
        return addressRepository.findById(branch.getAddress().getId()).orElseThrow(() -> UnovationExceptions.notFound().withErrors(ADDRESS_NOT_FOUND));
    }

    private Establishment checkEstablishmentOwner(String id, AccreditedNetwork accreditedNetwork) {
        Branch branch = findById(id);
        Establishment currentEstablishment = establishmentService.findById(branch.getHeadOffice().getId());
        AccreditedNetwork currentNetwork = accreditedNetworkService.getById(accreditedNetwork.getId());
        if(!currentNetwork.getId().equals(currentEstablishment.getNetwork().getId())){
            throw UnovationExceptions.forbidden().withErrors(ESTABLISHMENT_BRANCH_BELONG_TO_ANOTHER_NETWORK);
        }
        return currentEstablishment;
    }

    public Page<Branch> findByFilter(BranchFilter filter, AccreditedNetwork network, UnovationPageRequest pageable) {
        filter.setNetwork(network.getId());
        return findByFilter(filter, pageable);
    }

    public Page<Branch> findByFilter(BranchFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
