package br.com.unopay.api.uaa.service;

import br.com.unopay.api.network.repository.AccreditedNetworkRepository;
import br.com.unopay.api.bacen.repository.InstitutionRepository;
import br.com.unopay.api.bacen.repository.IssuerRepository;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.api.uaa.model.Group;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.model.UserType;
import br.com.unopay.api.uaa.model.UserTypeNames;
import br.com.unopay.api.uaa.repository.GroupRepository;
import br.com.unopay.api.uaa.repository.UserTypeRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.ACCREDITED_NETWORK_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.INSTITUTION_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.ISSUER_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.USER_TYPE_NOT_FOUND;
import static br.com.unopay.api.uaa.exception.Errors.USER_TYPE_REQUIRED;

@Service
public class UserTypeService {

    private UserTypeRepository repository;
    private GroupRepository groupRepository;
    private IssuerRepository issuerRepository;
    private AccreditedNetworkRepository accreditedNetworkRepository;
    private InstitutionRepository institutionRepository;

    @Autowired
    public UserTypeService(UserTypeRepository repository,
                           GroupRepository groupRepository,
                           IssuerRepository issuerRepository,
                           AccreditedNetworkRepository accreditedNetworkRepository,
                           InstitutionRepository institutionRepository) {
        this.repository = repository;
        this.groupRepository = groupRepository;
        this.issuerRepository = issuerRepository;
        this.accreditedNetworkRepository = accreditedNetworkRepository;
        this.institutionRepository = institutionRepository;
    }

    public List<UserType> findAll(){
        return repository.findAll();
    }

    public List<Group> findUserTypeGroups(String userTypeId) {
        return groupRepository.findByUserTypeId(userTypeId);
    }

    public void validateUserType(UserDetail user) {
        if(user.getType() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(USER_TYPE_REQUIRED);
        }

        UserType type = repository.findById(user.getType().getId());
        if(type == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(USER_TYPE_NOT_FOUND);
        }

        if(type.getName().equals(UserTypeNames.INSTITUTION)) {
            validateInstitution(user);
        }
        if(type.getName().equals(UserTypeNames.ACCREDITED_NETWORK)) {
            validateAccreditedNetwork(user);
        }
        if(type.getName().equals(UserTypeNames.ISSUER)) {
            validateIssuer(user);
        }

    }

    private void validateIssuer(UserDetail user) {
        if(user.getIssuer() == null || user.getIssuer().getId() == null) {
            throw UnovationExceptions.unprocessableEntity().withErrors(Errors.USER_TYPE_MUST_SET_AN_ISSUER);
        } else {
            user.setIssuer(issuerRepository.findById(user.getIssuer().getId()).orElseThrow(()->
                    UnovationExceptions.notFound().withErrors(ISSUER_NOT_FOUND)));
        }
    }

    private void validateAccreditedNetwork(UserDetail user) {
        if(user.getAccreditedNetwork() == null || user.getAccreditedNetwork().getId() == null) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.USER_TYPE_MUST_SET_AN_ACCREDITED_NETWORK);
        } else {
            user.setAccreditedNetwork(accreditedNetworkRepository.findById(user.getAccreditedNetwork().getId())
                    .orElseThrow(()-> UnovationExceptions.notFound().withErrors(ACCREDITED_NETWORK_NOT_FOUND)));
        }
    }

    private void validateInstitution(UserDetail user) {
        if(user.getInstitution() == null || user.getInstitution().getId() == null) {
            throw UnovationExceptions.unprocessableEntity()
                    .withErrors(Errors.USER_TYPE_MUST_SET_AN_INSTITUTION);
        } else {
            user.setInstitution(institutionRepository.findById(user.getInstitution().getId())
                    .orElseThrow(()-> UnovationExceptions.notFound().withErrors(INSTITUTION_NOT_FOUND)));
        }

    }


}
