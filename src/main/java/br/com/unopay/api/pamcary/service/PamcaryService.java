package br.com.unopay.api.pamcary.service;

import br.com.unopay.api.pamcary.transactional.RequestTO;
import br.com.unopay.api.pamcary.transactional.WSTransacional;
import br.com.unopay.api.pamcary.transactional.WSTransacional_Service;
import com.sun.xml.internal.ws.developer.JAXWSProperties;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.ws.BindingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PamcaryService {

    private WSTransacional_Service service;
    private SSLSocketFactory sslConnectionSocketFactory;
    private WSTransacional binding;

    @Autowired
    public PamcaryService(WSTransacional_Service service,
                          SSLSocketFactory sslConnectionSocketFactory) {
        this.service = service;
        this.sslConnectionSocketFactory = sslConnectionSocketFactory;
        binding = service.getWSTransacional();
        ((BindingProvider) binding).getRequestContext()
                .put(JAXWSProperties.SSL_SOCKET_FACTORY, sslConnectionSocketFactory);
    }

    private void execute() {
        binding.execute(new RequestTO());
    }
}
