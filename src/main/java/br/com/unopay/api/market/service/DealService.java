package br.com.unopay.api.market.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.csv.ContractorCsv;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.config.Queues;
import br.com.unopay.api.infra.Notifier;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.Deal;
import br.com.unopay.api.model.PaymentInstrument;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.Product;
import br.com.unopay.api.service.ContractInstallmentService;
import br.com.unopay.api.service.ContractService;
import br.com.unopay.api.service.PaymentInstrumentService;
import br.com.unopay.api.service.ProductService;
import br.com.unopay.api.uaa.model.UserDetail;
import br.com.unopay.api.uaa.service.UserDetailService;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import javax.validation.Validator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static br.com.unopay.api.uaa.exception.Errors.EXISTING_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.FILE_WIHOUT_LINES_OR_HEADER;

@Slf4j
@Service
public class DealService {

    private ContractService contractService;
    private HirerService hirerService;
    private ContractorService contractorService;
    private ProductService productService;
    private PaymentInstrumentService paymentInstrumentService;
    private UserDetailService userDetailService;
    private ContractInstallmentService installmentService;
    private AuthorizedMemberService authorizedMemberService;
    private Validator validator;
    private Notifier notifier;

    public static final String EMPTY = "";
    private static final char SEMICOLON = ';';

    @Autowired
    public DealService(ContractService contractService, HirerService hirerService,
                       ContractorService contractorService, ProductService productService,
                       PaymentInstrumentService paymentInstrumentService,
                       UserDetailService userDetailService,
                       ContractInstallmentService installmentService,
                       AuthorizedMemberService authorizedMemberService,
                       Validator validator, Notifier notifier) {
        this.contractService = contractService;
        this.hirerService = hirerService;
        this.contractorService = contractorService;
        this.productService = productService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.userDetailService = userDetailService;
        this.installmentService = installmentService;
        this.authorizedMemberService = authorizedMemberService;
        this.validator = validator;
        this.notifier = notifier;
    }

    @Transactional
    public Contract closeWithIssuerAsHirer(final Deal deal){
        checkContractor(deal.getPerson().documentNumber());
        Contractor contractor = contractorService.create(new Contractor(deal.getPerson()));
        return close(deal, contractor);
    }

    @Transactional
    public Contract close(final Person person, final String productCode, final String hirerDocument){
        Deal deal = new Deal(person, hirerDocument, productCode);
        checkContractor(person.documentNumber());
        Contractor contractor = contractorService.create(new Contractor(person));
        return close(deal, contractor);
    }


    @SneakyThrows
    @Transactional
    public void closeFromCsv(String hirerDocument, MultipartFile file) {
        List<ContractorCsv> dealCsvs = getDealsCsv(file);
        validate(dealCsvs);
        dealCsvs.forEach(line -> close(line.toPerson(), line.getProduct(), hirerDocument));
    }

    private void validate(List<ContractorCsv> dealCsvs) {
        if(dealCsvs.isEmpty()){
            throw UnovationExceptions.badRequest().withErrors(FILE_WIHOUT_LINES_OR_HEADER);
        }
        final int[] lineNumber = {0};
        dealCsvs.forEach(line -> {
            lineNumber[0]++;
            line.validate(validator, lineNumber[0]);
        });
    }

    @Transactional
    public void closeFromCsvForCurrentUser(String email, MultipartFile file){
        UserDetail currentUser = userDetailService.getByEmail(email);
        closeFromCsv(currentUser.myHirer().map(Hirer::getDocumentNumber).orElse(EMPTY), file);
    }

    private List<ContractorCsv> getDealsCsv(MultipartFile multipartFile) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(multipartFile.getInputStream());
        return new CsvToBeanBuilder<ContractorCsv>(inputStreamReader)
                .withType(ContractorCsv.class).withSeparator(SEMICOLON).build().parse();
    }

    private Contract close(Deal deal, Contractor contractor) {
        Product product = productService.findByCode(deal.getProductCode());
        Contract contract = createContract(deal, contractor, product);
        paymentInstrumentService.save(new PaymentInstrument(contractor, product));
        userDetailService.create(new UserDetail(contractor), product.getIssuer().documentNumber());
        sendContractorToPartner(contractor, product);
        markInstallmentAsPaidWhenRequired(product, contract);
        createMembers(deal, contract);
        return contract;
    }

    private void sendContractorToPartner(Contractor contractor, Product product) {
        if(product.withClub()) {
            log.info("Notifier contractor created.");
            notifier.notify(Queues.CONTRACTOR_CREATED, contractor);
        }
    }

    private Contract createContract(Deal deal, Contractor contractor, Product product) {
        Hirer hirer = getHirer(deal.getHirerDocument(), product);
        Contract contract = new Contract(product, deal.getMembers().size());
        contract.setHirer(hirer);
        contract.setContractor(contractor);
        return contractService.create(contract, deal.hasHirerDocument());
    }

    private void createMembers(Deal deal, Contract contract) {
        deal.getMembers().forEach(candidate ->
                authorizedMemberService.create(candidate.toAuthorizedMember(contract)));
    }

    private void markInstallmentAsPaidWhenRequired(Product product, Contract contract) {
        if(!contract.withMembershipFee()) {
            installmentService.markAsPaid(contract.getId(), product.installmentTotal(contract.getMemberTotal()));
        }
    }

    private Hirer getHirer(String hirerDocument, Product product) {
        if(hirerDocument != null) {
            return hirerService.findByDocumentNumber(hirerDocument);
        }
        return hirerService.findByDocumentNumber(product.getIssuer().documentNumber());
    }

    private void checkContractor(String documentNumber) {
        Optional<Contractor> contractor = contractorService.getOptionalByDocument(documentNumber);
        contractor.ifPresent(c -> {
            throw UnovationExceptions.conflict().withErrors(EXISTING_CONTRACTOR.withOnlyArgument(documentNumber));
        });
    }
}