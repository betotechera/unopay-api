package br.com.unopay.api.pamcary.service;

import br.com.unopay.api.model.TravelDocument;
import br.com.unopay.api.model.filter.TravelDocumentFilter;
import br.com.unopay.api.pamcary.model.TravelDocumentsWrapper;
import br.com.unopay.api.pamcary.transactional.FieldTO;
import br.com.unopay.api.pamcary.transactional.RequestTO;
import br.com.unopay.api.pamcary.transactional.WSTransacional;
import br.com.unopay.api.pamcary.transactional.WSTransacional_Service;
import br.com.unopay.api.pamcary.translate.KeyValueTranslator;
import com.sun.xml.internal.ws.developer.JAXWSProperties;
import java.util.List;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PamcaryService {

    private WSTransacional_Service service;
    private SSLSocketFactory sslConnectionSocketFactory;
    private WSTransacional binding;
    private KeyValueTranslator translator;

    @Value("${soap.pamcary.partner-number:}")
    private String partnerNumber;

    private final String partnerKey = "parceiro.documento.numero";


    @Autowired
    public PamcaryService(WSTransacional_Service service,
                          SSLSocketFactory sslConnectionSocketFactory, KeyValueTranslator translator) {
        this.service = service;
        this.translator = translator;
        this.sslConnectionSocketFactory = sslConnectionSocketFactory;
        binding = service.getWSTransacional();
        ((BindingProvider) binding).getRequestContext()
                .put(JAXWSProperties.SSL_SOCKET_FACTORY, sslConnectionSocketFactory);
    }

    public TravelDocumentsWrapper searchDoc(TravelDocumentFilter travelDocumentFilter){
        travelDocumentFilter.defineTransaction();
        List<FieldTO> fieldTOS = translator.extractFields(travelDocumentFilter);
        List<FieldTO> result = execute("SearchDoc", fieldTOS);
        return translator.populate(TravelDocumentsWrapper.class,result);
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
