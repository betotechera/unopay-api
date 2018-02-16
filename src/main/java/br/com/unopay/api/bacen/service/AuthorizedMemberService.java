package br.com.unopay.api.bacen.service;

import br.com.unopay.api.bacen.model.AuthorizedMember;
import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.csv.AuthorizedMemberCsv;
import br.com.unopay.api.bacen.model.filter.AuthorizedMemberFilter;
import br.com.unopay.api.bacen.repository.AuthorizedMemberRepository;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AuthorizedMemberService {

    @Autowired
    AuthorizedMemberRepository repository;

    @Autowired
    PaymentInstrumentService paymentInstrumentService;

    @Autowired
    ContractService contractService;

    public AuthorizedMember create(AuthorizedMember authorizedMember) {
        authorizedMember.validateMe();
        validateReferences(authorizedMember);
        return save(authorizedMember);
    }

    private AuthorizedMember save(AuthorizedMember authorizedMember) {
        return repository.save(authorizedMember);
    }

    private void validateReferences(AuthorizedMember authorizedMember) {
        authorizedMember.setPaymentInstrument(paymentInstrumentService.findById(authorizedMember.paymentInstrumentId()));
        authorizedMember.setContract(contractService.findById(authorizedMember.contractId()));
    }

    public AuthorizedMember findById(String id) {
        Optional<AuthorizedMember> authorizedMember = repository.findById(id);
        return authorizedMember.orElseThrow(()-> UnovationExceptions.notFound().withErrors(
                Errors.AUTHORIZED_MEMBER_NOT_FOUND));
    }

    public AuthorizedMember findByIdForContractor(String id, Contractor contractor) {
        Optional<AuthorizedMember> authorizedMember = repository.findByIdAndContractContractorId(id, contractor.getId());
        return authorizedMember.orElseThrow(()-> UnovationExceptions.notFound().withErrors(
                Errors.AUTHORIZED_MEMBER_NOT_FOUND.withOnlyArgument(id)));
    }

    public AuthorizedMember findByIdForHirer(String id, Hirer hirer) {
        Optional<AuthorizedMember> authorizedMember = repository.findByIdAndContractHirerId(id, hirer.getId());
        return authorizedMember.orElseThrow(()-> UnovationExceptions.notFound().withErrors(
                Errors.AUTHORIZED_MEMBER_NOT_FOUND.withOnlyArgument(id)));
    }

    public void update(String id, AuthorizedMember authorizedMember) {
        AuthorizedMember current = findById(id);
        update(current, authorizedMember);
    }

    public void updateForHirer(String id, Hirer hirer, AuthorizedMember authorizedMember) {
        AuthorizedMember current = findByIdForHirer(id, hirer);
        update(current, authorizedMember);
    }

    public void updateForContractor(String id, Contractor contractor, AuthorizedMember authorizedMember) {
        AuthorizedMember current = findByIdForContractor(id, contractor);
        update(current, authorizedMember);
    }

    private void update(AuthorizedMember current, AuthorizedMember authorizedMember) {
        current.updateMe(authorizedMember);
        current.validateMe();
        validateReferences(current);
        save(current);
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<AuthorizedMember> findByFilter(AuthorizedMemberFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    private PaymentInstrument findCsvPaymentInstrument(AuthorizedMemberCsv csv) {
        String instrumentNumber = csv.getPaymentInstrumentNumber();
        return paymentInstrumentService.findByNumber(instrumentNumber);
    }

    private PaymentInstrument findDigitalWallet(AuthorizedMember authorizedMember) {
        return paymentInstrumentService.findDigitalWalletByContractorDocument(
                authorizedMember.getContract().getContractor().getDocumentNumber()).get();
    }

    @SneakyThrows
    @Transactional
    public void createFromCsv(MultipartFile multipartFile) {
        List<AuthorizedMemberCsv> csvLines = getAuthorizedMemberCsvs(multipartFile);
        csvLines.forEach(csvLine ->  {
            AuthorizedMember authorizedMember = csvLine.toAuthorizedMember();
            authorizedMember.setContract(contractService.findByCode(csvLine.getContractCode()));

            if(csvLine.getPaymentInstrumentNumber() != null) {
                authorizedMember.setPaymentInstrument(findCsvPaymentInstrument(csvLine));
            }
            else {
                authorizedMember.setPaymentInstrument(findDigitalWallet(authorizedMember));
            }
            create(authorizedMember);
        });
    }

    private List<AuthorizedMemberCsv> getAuthorizedMemberCsvs(MultipartFile multipartFile) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(multipartFile.getInputStream());
        return new CsvToBeanBuilder<AuthorizedMemberCsv>(inputStreamReader)
                .withType(AuthorizedMemberCsv.class).build().parse();
    }
}
