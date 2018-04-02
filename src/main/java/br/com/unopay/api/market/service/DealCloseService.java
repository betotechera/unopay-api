package br.com.unopay.api.market.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Hirer;
import br.com.unopay.api.bacen.model.csv.ContractorCsv;
import br.com.unopay.api.bacen.service.ContractorService;
import br.com.unopay.api.bacen.service.HirerService;
import br.com.unopay.api.model.Contract;
import br.com.unopay.api.model.DealClose;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static br.com.unopay.api.uaa.exception.Errors.EXISTING_CONTRACTOR;
import static br.com.unopay.api.uaa.exception.Errors.FILE_WIHOUT_LINES_OR_HEADER;

@Service
public class DealCloseService {

    private ContractService contractService;
    private HirerService hirerService;
    private ContractorService contractorService;
    private ProductService productService;
    private PaymentInstrumentService paymentInstrumentService;
    private UserDetailService userDetailService;
    private ContractInstallmentService installmentService;
    private AuthorizedMemberService authorizedMemberService;
    private Validator validator;

    private static final char SEMICOLON = ';';

    @Autowired
    public DealCloseService(ContractService contractService, HirerService hirerService,
                           ContractorService contractorService, ProductService productService,
                           PaymentInstrumentService paymentInstrumentService,
                           UserDetailService userDetailService,
                           ContractInstallmentService installmentService,
                           AuthorizedMemberService authorizedMemberService,
                           Validator validator) {
        this.contractService = contractService;
        this.hirerService = hirerService;
        this.contractorService = contractorService;
        this.productService = productService;
        this.paymentInstrumentService = paymentInstrumentService;
        this.userDetailService = userDetailService;
        this.installmentService = installmentService;
        this.authorizedMemberService = authorizedMemberService;
        this.validator = validator;
    }

    @Transactional
    public Contract dealCloseWithIssuerAsHirer(final DealClose dealClose){
        checkContractor(dealClose.getPerson().documentNumber());
        Contractor contractor = contractorService.create(new Contractor(dealClose.getPerson()));
        return dealClose(dealClose, contractor);
    }

    @Transactional
    public Contract doDealClose(final Person person, final String productCode, final String hirerDocument){
        DealClose dealClose = new DealClose(person, hirerDocument, productCode);
        checkContractor(person.documentNumber());
        Contractor contractor = contractorService.create(new Contractor(person));
        return dealClose(dealClose, contractor);
    }


    @SneakyThrows
    @Transactional
    public void dealCloseFromCsv(String hirerDocument, MultipartFile file) {
        List<ContractorCsv> dealCloseCsvs = getDealCloseCsvs(file);
        final int[] lineNumber = {0};
        dealCloseCsvs.forEach(line -> {
            lineNumber[0]++;
            line.validate(validator, lineNumber[0]);
            doDealClose(line.toPerson(), line.getProduct(), hirerDocument);
        });
        if(dealCloseCsvs.isEmpty()){
            throw UnovationExceptions.badRequest().withErrors(FILE_WIHOUT_LINES_OR_HEADER);
        }
    }

    @Transactional
    public void dealCloseFromCsvForCurrentUser(String email, MultipartFile file){
        UserDetail currentUser = userDetailService.getByEmail(email);
        dealCloseFromCsv(currentUser.myHirer().map(Hirer::getDocumentNumber).orElse(""), file);
    }

    private List<ContractorCsv> getDealCloseCsvs(MultipartFile multipartFile) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(multipartFile.getInputStream());
        return new CsvToBeanBuilder<ContractorCsv>(inputStreamReader)
                .withType(ContractorCsv.class).withSeparator(SEMICOLON).build().parse();
    }

    private Contract dealClose(DealClose dealClose, Contractor contractor) {
        Product product = productService.findByCode(dealClose.getProductCode());
        Hirer hirer = getHirer(dealClose.getHirerDocument(), product);
        Contract contract = new Contract(product,dealClose.getMembers().size());
        contract.setHirer(hirer);
        contract.setContractor(contractor);
        paymentInstrumentService.save(new PaymentInstrument(contractor, product));
        userDetailService.create(new UserDetail(contractor));
        Contract created = contractService.create(contract, dealClose.hasHirerDocument());
        if(!contract.withMembershipFee()) {
            installmentService.markAsPaid(created.getId(), product.installmentTotal(contract.getMemberTotal()));
        }
        dealClose.getMembers().forEach(candidate ->
                authorizedMemberService.create(candidate.toAuthorizedMember(contract)));
        return contract;
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
