package br.com.unopay.api.pamcary.service;

import br.com.unopay.api.pamcary.transactional.FieldTO;
import br.com.unopay.api.pamcary.transactional.RequestTO;
import br.com.unopay.api.pamcary.transactional.WSTransacional;
import br.com.unopay.api.pamcary.transactional.WSTransacional_Service;
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

    @Value("${soap.pamcary.partner-number:}")
    private String partnerNumber;

    private final String partnerKey = "parceiro.documento.numero";

    @Autowired
    public PamcaryService(WSTransacional_Service service,
                          SSLSocketFactory sslConnectionSocketFactory) {
        this.service = service;
        this.sslConnectionSocketFactory = sslConnectionSocketFactory;
        binding = service.getWSTransacional();
        ((BindingProvider) binding).getRequestContext()
                .put(JAXWSProperties.SSL_SOCKET_FACTORY, sslConnectionSocketFactory);
    }

    public List<FieldTO> searchDoc(List<FieldTO> fieldTOS){
        return execute("SearchDoc", fieldTOS);
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
