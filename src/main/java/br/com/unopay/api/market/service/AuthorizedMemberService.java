package br.com.unopay.api.market.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.csv.AuthorizedMemberCsv;
import br.com.unopay.api.bacen.model.filter.AuthorizedMemberFilter;
import br.com.unopay.api.market.model.AuthorizedMember;
import br.com.unopay.api.market.repository.AuthorizedMemberRepository;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.api.uaa.exception.Errors;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import br.com.unopay.bootcommons.jsoncollections.UnovationPageRequest;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class AuthorizedMemberService {

    private AuthorizedMemberRepository repository;
    private PaymentInstrumentService paymentInstrumentService;
    private ContractService contractService;
    private ProductService productService;
    private static final char SEMICOLON = ';';

    @Autowired
    public AuthorizedMemberService(AuthorizedMemberRepository repository,
                                   PaymentInstrumentService paymentInstrumentService,
                                   ContractService contractService,
                                   ProductService productService) {
        this.repository = repository;
        this.paymentInstrumentService = paymentInstrumentService;
        this.contractService = contractService;
        this.productService = productService;
    }

    public AuthorizedMember create(AuthorizedMember authorizedMember, Contractor contractor) {
        contractService.getByIdAndContractorId(authorizedMember.contractId(), contractor);
        return create(authorizedMember);
    }

    public AuthorizedMember create(AuthorizedMember authorizedMember, Hirer hirer) {
        contractService.findByIdForHirer(authorizedMember.contractId(), hirer);
        return create(authorizedMember);
    }

    public AuthorizedMember create(AuthorizedMember authorizedMember) {
        if(!authorizedMember.withInstrument()) {
            authorizedMember.setPaymentInstrument(findDigitalWalletByContractorDocument(authorizedMember
                    .contractorDocumentNumber()));
        }
        authorizedMember.setCreatedDateTime(new Date());
        authorizedMember.validateMe();
        validateReferences(authorizedMember);
        authorizedMember.validatePaymentInstrument();
        return save(authorizedMember);
    }

    private AuthorizedMember save(AuthorizedMember authorizedMember) {
        return repository.save(authorizedMember);
    }

    private void validateReferences(AuthorizedMember authorizedMember) {
        authorizedMember.setPaymentInstrument(paymentInstrumentService.findById(authorizedMember.instrumentId()));
        authorizedMember.setContract(contractService.findById(authorizedMember.contractId()));
    }

    public AuthorizedMember findById(String id) {
        Optional<AuthorizedMember> authorizedMember = repository.findById(id);
        return authorizedMember.orElseThrow(()-> UnovationExceptions.notFound().withErrors(
                Errors.AUTHORIZED_MEMBER_NOT_FOUND));
    }

    public Integer countByContract(String contractId) {
        return repository.countByContractId(contractId);
    }

    public AuthorizedMember findByIdForContractor(String id, Contractor contractor) {
        Optional<AuthorizedMember> authorizedMember = repository.findByIdAndContractContractorId(id,contractor.getId());
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

    private void update(AuthorizedMember current, AuthorizedMember authorizedMember) {
        current.updateMe(authorizedMember);
        validateReferences(current);
        current.validateMe();
        save(current);
    }

    public void updateForContractor(String id, Contractor contractor, AuthorizedMember authorizedMember) {
        AuthorizedMember current = findByIdForContractor(id, contractor);
        update(current, authorizedMember);
    }

    public void updateForHirer(String id, Hirer hirer, AuthorizedMember authorizedMember) {
        AuthorizedMember current = findByIdForHirer(id, hirer);
        update(current, authorizedMember);
    }

    public void deleteForHirer(String id, Hirer hirer) {
        AuthorizedMember toBeDeleted = findByIdForHirer(id, hirer);
        delete(toBeDeleted.getId());
    }

    public void deleteForContractor(String id, Contractor contractor) {
        AuthorizedMember toBeDeleted = findByIdForContractor(id, contractor);
        delete(toBeDeleted.getId());
    }

    public void delete(String id) {
        findById(id);
        repository.delete(id);
    }

    public Page<AuthorizedMember> findByFilter(AuthorizedMemberFilter filter, UnovationPageRequest pageable) {
        return repository.findAll(filter, new PageRequest(pageable.getPageStartingAtZero(), pageable.getSize()));
    }

    @SneakyThrows
    @Transactional
    public void createFromCsvForHirer(String hirerDocument, MultipartFile multipartFile) {
        List<AuthorizedMemberCsv> csvLines = getAuthorizedMemberCsvs(multipartFile);
        csvLines.forEach(csvLine -> csvLine.setHirerDocumentNumber(hirerDocument));
        createFromCsvList(csvLines);
    }

    @SneakyThrows
    @Transactional
    public void createFromCsv(MultipartFile multipartFile) {
        List<AuthorizedMemberCsv> csvLines = getAuthorizedMemberCsvs(multipartFile);
        createFromCsvList(csvLines);
    }

    private void createFromCsvList(List<AuthorizedMemberCsv> csvLines) {
        csvLines.forEach(csvLine ->  {
            AuthorizedMember authorizedMember = csvLine.toAuthorizedMember();
            authorizedMember.setContract(getContractByCsv(csvLine));
            if(csvLine.withInstrumentNumber()) {
                authorizedMember
                        .setPaymentInstrument(findPaymentInstrumentByNumber(csvLine.getPaymentInstrumentNumber()));
            }
            create(authorizedMember);
        });
    }

    public Contract getContractByCsv(AuthorizedMemberCsv csvSource) {
        Product product = productService.findByCode(csvSource.getProductCode());
        Contract contract = getContractByContractorAndProduct(csvSource.getContractorDocumentNumber(), product.getId());

        if(!contract.hirerDocumentEquals(csvSource.getHirerDocumentNumber())) {
            throw UnovationExceptions.notFound().withErrors(Errors.CONTRACT_NOT_FOUND);
        }

        return contract;
    }

    private Contract getContractByContractorAndProduct(String contractorDocumentNumber, String productId) {
        return contractService.findByContractorAndProductCode(contractorDocumentNumber, productId)
                .orElseThrow(()->
                        UnovationExceptions.notFound().withErrors(Errors.CONTRACT_NOT_FOUND));
    }

    private PaymentInstrument findDigitalWalletByContractorDocument(String document) {
        return paymentInstrumentService.findDigitalWalletByContractorDocument(document)
                .orElseThrow(() -> UnovationExceptions.unprocessableEntity()
                .withErrors(Errors.PREVIOUS_DIGITAL_WALLET_OR_PAYMENT_INSTRUMENT_REQUIRED));
    }

    private List<AuthorizedMemberCsv> getAuthorizedMemberCsvs(MultipartFile multipartFile) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(multipartFile.getInputStream());
        return new CsvToBeanBuilder<AuthorizedMemberCsv>(inputStreamReader)
                .withType(AuthorizedMemberCsv.class).withSeparator(SEMICOLON).build().parse();
    }

    private PaymentInstrument findPaymentInstrumentByNumber(String number) {
        return paymentInstrumentService.findByNumber(number);
    }

}
