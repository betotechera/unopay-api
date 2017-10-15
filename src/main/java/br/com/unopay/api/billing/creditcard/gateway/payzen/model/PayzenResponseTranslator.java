package br.com.unopay.api.billing.creditcard.gateway.payzen.model;

import br.com.unopay.api.billing.creditcard.model.TransactionStatus;
import eu.payzen.webservices.sdk.ServiceResult;
import java.util.Arrays;
import java.util.Objects;
import org.springframework.stereotype.Component;

@Component
public class PayzenResponseTranslator {

    public TransactionStatus translate(ServiceResult serviceResult) {
        String responseLabel = serviceResult.getCommonResponse().getTransactionStatusLabel();
        Integer responseCode = serviceResult.getCommonResponse().getResponseCode();
        if(Objects.equals(responseLabel, "AUTHORISED")) {
            return TransactionStatus.CAPTURED;
        }
        if(Objects.equals(responseLabel, "REFUSED")){
            return TransactionStatus.DENIED;
        }
        if(Arrays.asList(2,3,10,11,12,13,14,15,20,21,22,23,24,25,30,31,
                32,33,34,35,36,40,41,50,51,52,53,54,55,56,97,98,99).contains(responseCode)){
            return TransactionStatus.ERROR;
        }
        return null;
    }
}
