package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.AccreditedNetworkIssuer;
import br.com.unopay.api.bacen.model.filter.AccreditedNetworkIssuerFilter;
import br.com.unopay.api.bacen.repository.AccreditedNetworkIssuerRepository;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AccreditedNetworkIssuerService {

    private AccreditedNetworkIssuerRepository repository;
    private AccreditedNetworkService accreditedNetworkService;
    private UserDetailService userDetailService;
    private IssuerService issuerService;

    @Autowired
    public AccreditedNetworkIssuerService(AccreditedNetworkIssuerRepository repository,
                                          AccreditedNetworkService accreditedNetworkService,
                                          UserDetailService userDetailService,
                                          IssuerService issuerService) {
        this.repository = repository;
        this.accreditedNetworkService = accreditedNetworkService;
        this.userDetailService = userDetailService;
        this.issuerService = issuerService;
    }

    public AccreditedNetworkIssuer create(String currentUserMail, AccreditedNetworkIssuer networkIssuer) {
        setReferences(currentUserMail, networkIssuer);
        networkIssuer.setCreatedDateTime(new Date());
        return repository.save(networkIssuer);
    }

    private void setReferences(String currentUserMail, AccreditedNetworkIssuer networkIssuer) {
        networkIssuer.setAccreditedNetwork(accreditedNetworkService.getById(networkIssuer.networkId()));
        networkIssuer.setIssuer(issuerService.findById(networkIssuer.issuerId()));
        networkIssuer.setUser(userDetailService.getByEmail(currentUserMail));
    }

    public AccreditedNetworkIssuer findById(String id) {
        return repository.findOne(id);
    }

    public Page<AccreditedNetworkIssuer> findByFilter(AccreditedNetworkIssuerFilter filter,
                                                      UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }
}
