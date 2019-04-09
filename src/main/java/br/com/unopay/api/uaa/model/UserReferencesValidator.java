package br.com.unopay.api.uaa.model;

import br.com.unopay.api.network.model.AccreditedNetwork;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.network.model.Establishment;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.Institution;
import br.com.unopay.api.bacen.model.Issuer;
import br.com.unopay.api.network.model.Partner;
import br.com.unopay.api.network.repository.AccreditedNetworkRepository;
import br.com.unopay.api.bacen.repository.ContractorRepository;
import br.com.unopay.api.network.repository.EstablishmentRepository;
import br.com.unopay.api.bacen.repository.HirerRepository;
import br.com.unopay.api.bacen.repository.InstitutionRepository;
import br.com.unopay.api.bacen.repository.IssuerRepository;
import br.com.unopay.api.network.repository.PartnerRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.service.GroupService;
import br.com.unopay.api.uaa.service.UserTypeService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static br.com.unopay.api.uaa.exception.Errors.ESTABLISHMENT_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ISSUER_NOT_FOUND;

@Component
public class UserReferencesValidator {

    private UserTypeService userTypeService;
    private IssuerRepository issuerRepository;
    private AccreditedNetworkRepository accreditedNetworkRepository;
    private InstitutionRepository institutionRepository;
    private EstablishmentRepository establishmentRepository;
    private HirerRepository hirerRepository;
    private PartnerRepository partnerRepository;
    private ContractorRepository contractorRepository;
    private GroupService groupService;

    @Autowired
    public UserReferencesValidator(UserTypeService userTypeService,
                                   IssuerRepository issuerRepository,
                                   AccreditedNetworkRepository accreditedNetworkRepository,
                                   InstitutionRepository institutionRepository,
                                   EstablishmentRepository establishmentRepository,
                                   HirerRepository hirerRepository,
                                   PartnerRepository partnerRepository,
                                   ContractorRepository contractorRepository,
                                   GroupService groupService) {
        this.userTypeService = userTypeService;
        this.issuerRepository = issuerRepository;
        this.accreditedNetworkRepository = accreditedNetworkRepository;
        this.institutionRepository = institutionRepository;
        this.establishmentRepository = establishmentRepository;
        this.hirerRepository = hirerRepository;
        this.partnerRepository = partnerRepository;
        this.contractorRepository = contractorRepository;
        this.groupService = groupService;
    }

    public void defineValidReferences(UserDetail user) {
        userTypeService.validateUserType(user);
        defineInstitutionWhenRequired(user);
        defineNetworkWhenRequired(user);
        defineEstablishmentWhenRequired(user);
        defineIssuerWhenRequired(user);
        defineHirerWhenRequired(user);
        defineContractorWhenRequired(user);
        definePartnerWhenRequired(user);
        user.setGroups(groupService.loadKnownUserGroups(user));
    }

    private void definePartnerWhenRequired(UserDetail user) {
        if(user.isPartnerType()) {
            user.setPartner(findPartnerById(user.partnerId()));
        }
    }

    private void defineContractorWhenRequired(UserDetail user) {
        if(user.isContractorType()) {
            user.setContractor(findContractorById(user.contractorId()));
        }
    }

    private void defineHirerWhenRequired(UserDetail user) {
        if(user.isHirerType()) {
            user.setHirer(findHirerById(user.hirerId()));
        }
    }

    private void defineIssuerWhenRequired(UserDetail user) {
        if(user.isIssuerType()) {
            user.setIssuer(findIssuerById(user.issuerId()));
        }
    }

    private void defineEstablishmentWhenRequired(UserDetail user) {
        if(user.isEstablishmentType()) {
            user.setEstablishment(findEstablishmentById(user.establishmentId()));
        }
    }

    private void defineNetworkWhenRequired(UserDetail user) {
        if(user.isAccreditedNetworkType()) {
            user.setAccreditedNetwork(findAccreditedNetworkById(user.accreditedNetworkId()));
        }
    }

    private void defineInstitutionWhenRequired(UserDetail user) {
        if(user.isInstitutionType()) {
            user.setInstitution(findInstitutionById(user.institutionId()));
        }
    }


    private Partner findPartnerById(String id) {
        return partnerRepository.findById(id).orElseThrow(()->
                UnovationExceptions.notFound().withErrors(Errors.PARTNER_NOT_FOUND));
    }

    private Contractor findContractorById(String id) {
        return contractorRepository.findById(id).orElseThrow(()->
                UnovationExceptions.notFound().withErrors(Errors.CONTRACTOR_NOT_FOUND));
    }

    private Hirer findHirerById(String id) {
        return hirerRepository.findById(id).orElseThrow(()->
                UnovationExceptions.notFound().withErrors(Errors.HIRER_NOT_FOUND));
    }

    private Establishment findEstablishmentById(String id) {
        return establishmentRepository.findById(id).orElseThrow(()->
                UnovationExceptions.notFound().withErrors(ESTABLISHMENT_NOT_FOUND));
    }

    private Institution findInstitutionById(String id) {
        return institutionRepository.findById(id).orElseThrow(()->
                UnovationExceptions.notFound().withErrors(Errors.INSTITUTION_NOT_FOUND));
    }

    private Issuer findIssuerById(String id) {
        return issuerRepository.findById(id).orElseThrow(()->
                UnovationExceptions.notFound().withErrors(ISSUER_NOT_FOUND));
    }

    private AccreditedNetwork findAccreditedNetworkById(String id) {
        return accreditedNetworkRepository.findById(id).orElseThrow(()->
                UnovationExceptions.notFound().withErrors(Errors.ACCREDITED_NETWORK_NOT_FOUND));
    }
}
