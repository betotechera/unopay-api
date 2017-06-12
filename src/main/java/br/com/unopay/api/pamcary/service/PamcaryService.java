package br.com.unopay.api.pamcary.service;

import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.model.filter.TravelDocumentFilter;
import br.com.unopay.api.pamcary.transactional.FieldTO;
import br.com.unopay.api.pamcary.transactional.RequestTO;
import br.com.unopay.api.pamcary.transactional.WSTransacional;
import br.com.unopay.api.pamcary.transactional.WSTransacionalService;
import br.com.unopay.api.pamcary.translate.KeyValueTranslator;
import static com.sun.xml.ws.developer.JAXWSProperties.SSL_SOCKET_FACTORY;
import java.util.List;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PamcaryService {

    private WSTransacional binding;
    private KeyValueTranslator translator;

    @Value("${soap.pamcary.partner-number:}")
    private String partnerNumber;

    private static final String partnerKey = "parceiro.documento.numero";


    @Autowired
    public PamcaryService(WSTransacionalService service,
                          SSLSocketFactory sslConnectionSocketFactory, KeyValueTranslator translator) {
        this.translator = translator;
        binding = service.getWSTransacional();
        ((BindingProvider) binding).getRequestContext()
                .put(SSL_SOCKET_FACTORY, sslConnectionSocketFactory);
    }

    public CargoContract searchDoc(TravelDocumentFilter travelDocumentFilter){
        travelDocumentFilter.defineTransaction();
        List<FieldTO> fieldTOS = translator.extractFields(travelDocumentFilter);
        List<FieldTO> result = execute("SearchDoc", fieldTOS);
        return translator.populate(CargoContract.class,result);
    }

    private List<FieldTO> execute(final String contextParam, final List<FieldTO> fieldsParam) {
        fieldsParam.add(new FieldTO(){{ setKey(partnerKey); setValue(partnerNumber);}});
        RequestTO requestTO = new RequestTO() {{
            setContext(contextParam);
            fieldsParam.forEach(fieldTO ->
                getFields().add(fieldTO)
            );
        }};
        return binding.execute(requestTO).getFields();
    }
}
