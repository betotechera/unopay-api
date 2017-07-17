package br.com.unopay.api.pamcary.service;

import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.model.ServiceAuthorize;
import br.com.unopay.api.model.filter.CargoContractFilter;
import br.com.unopay.api.pamcary.transactional.FieldTO;
import br.com.unopay.api.pamcary.transactional.RequestTO;
import br.com.unopay.api.pamcary.transactional.WSTransacional;
import br.com.unopay.api.pamcary.transactional.WSTransacionalService;
import br.com.unopay.api.pamcary.translate.KeyValueTranslator;
import java.util.List;
import java.util.Objects;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.sun.xml.ws.developer.JAXWSProperties.SSL_SOCKET_FACTORY;

@Slf4j
@Service
public class PamcaryService {

    private WSTransacional binding;
    private KeyValueTranslator translator;

    @Value("${soap.pamcary.partner-number:}")
    private String unovationDocumentNumber;

    private static final String PARTNER_KEY = "parceiro.documento.numero";
    private static final String NSU_KEY = "viagem.transacao.nsu";
    private static final String ESTABLISHMENT_DOCUMENT_KEY = "pontoapoio.documento.numero";
    private static final String MESSAGE_KEY = "mensagem.codigo";
    private static final String MESSAGE_DESCRIPTION = "mensagem.descricao";
    private static final String SUCCESS_RESULT = "0";

    @Autowired
    public PamcaryService(WSTransacionalService service,
                          SSLSocketFactory sslConnectionSocketFactory, KeyValueTranslator translator) {
        this.translator = translator;
        binding = service.getWSTransacional();
        ((BindingProvider) binding).getRequestContext()
                .put(SSL_SOCKET_FACTORY, sslConnectionSocketFactory);
    }

    public CargoContract searchDoc(CargoContractFilter cargoContractFilter){
        log.info("searchDoc request={}", cargoContractFilter);
        List<FieldTO> fieldTOS = translator.extractFields(cargoContractFilter);
        List<FieldTO> result = execute("SearchDoc", fieldTOS);
        log.info("searchDoc result={}", getMessageDescription(result));
        return translator.populate(CargoContract.class,result);
    }

    public CargoContract generateVoucherDelivery(String establishmentDocument, CargoContract cargoContract){
        log.info("generateVoucherDelivery request={}", cargoContract);
        List<FieldTO> fields = translator.extractFields(cargoContract);
        addEstablishmentDocument(establishmentDocument,fields);
        List<FieldTO> result = execute("GenerateVoucherDelivery", fields);
        log.info("generateVoucherDelivery result={}", getMessageDescription(result));
        return translator.populate(CargoContract.class,result);
    }

    public void confirmDocDelivery(String establishmentDocument, CargoContract cargoContract){
        log.info("confirmDocDelivery request={}", cargoContract);
        List<FieldTO> fields = translator.extractFields(cargoContract);
        addEstablishmentDocument(establishmentDocument,fields);
        List<FieldTO> result = execute("ConfirmDocDelivery", fields);
        log.info("confirmDocDelivery result={}", getMessageDescription(result));
        checkResult(result);
    }

    public void supplyConfirm(String establishmentDocument, ServiceAuthorize serviceAuthorize){
        log.info("supplyConfirm request={}", serviceAuthorize);
        List<FieldTO> fields = translator.extractFields(serviceAuthorize);
        addEstablishmentDocument(establishmentDocument,fields);
        List<FieldTO> result = execute("SupplyConfirm", fields);
        log.info("supplyConfirm result={}", getMessageDescription(result));
        checkResult(result);
    }

    public void updateDoc(String establishmentDocument, CargoContract cargoContract){
        log.info("updateDoc request={}", cargoContract);
        List<FieldTO> fields = translator.extractFields(cargoContract);
        addEstablishmentDocument(establishmentDocument,fields);
        List<FieldTO> result = execute("UpdateDoc", fields);
        log.info("updateDoc result={}", getMessageDescription(result));
        checkResult(result);
    }

    private List<FieldTO> execute(final String contextParam, final List<FieldTO> fieldsParam) {
        addHeaders(fieldsParam);
        RequestTO requestTO = new RequestTO() {{
            setContext(contextParam);
            fieldsParam.forEach(fieldTO ->
                getFields().add(fieldTO)
            );
        }};
        return binding.execute(requestTO).getFields();
    }

    private void addEstablishmentDocument(String document, List<FieldTO> fields) {
        fields.add(new FieldTO(ESTABLISHMENT_DOCUMENT_KEY, document));
        log.info("establishment document={}", document);
    }

    private void addHeaders(List<FieldTO> fieldTOS) {
        String nsu = getNsu();
        fieldTOS.add(new FieldTO(PARTNER_KEY, unovationDocumentNumber));
        fieldTOS.add(new FieldTO(NSU_KEY, nsu));
        log.info("partner document={}, nsu={}", unovationDocumentNumber,nsu);
    }

    private String getNsu(){
        return String.valueOf(new java.security.SecureRandom().nextInt(999999999));
    }

    private boolean isErrorMessage(FieldTO field) {
        return isMessageKey(field) && !Objects.equals(field.getValue(), SUCCESS_RESULT);
    }

    private String getMessageDescription(List<FieldTO> result) {
        return result.stream().filter(this::isMessageDescription).findFirst().get().getValue();
    }

    private boolean isMessageKey(FieldTO field) {
        return Objects.equals(field.getKey(), MESSAGE_KEY);
    }
    private boolean isMessageDescription(FieldTO field) {
        return Objects.equals(field.getKey(), MESSAGE_DESCRIPTION);
    }

    private void checkResult(List<FieldTO> result) {
        if(result.stream().anyMatch(this::isErrorMessage)){
            throw new AmqpRejectAndDontRequeueException(getMessageDescription(result));
        }
    }
}
