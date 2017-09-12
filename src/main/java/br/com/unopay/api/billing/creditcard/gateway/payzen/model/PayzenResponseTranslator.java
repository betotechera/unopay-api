package br.com.unopay.api.billing.creditcard.gateway.payzen.model;

import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import eu.payzen.webservices.sdk.ServiceResult;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class PayzenResponseTranslator {

    public TransactionStatus translate(ServiceResult serviceResult) {
        Integer responseCode = serviceResult.getCommonResponse().getResponseCode();
        if(responseCode == 0) {
            return TransactionStatus.AUTHORIZED;
        }
        if(Arrays.asList(1,42,43,26).contains(responseCode)){
            return TransactionStatus.DENIED;
        }
        if(Arrays.asList(2,3,10,11,12,13,14,15,20,21,22,23,24,25,30,31,
                32,33,34,35,36,40,41,50,51,52,53,54,55,56,97,98,99).contains(responseCode)){
            return TransactionStatus.ERROR;
        }
        return null;
    }
}
