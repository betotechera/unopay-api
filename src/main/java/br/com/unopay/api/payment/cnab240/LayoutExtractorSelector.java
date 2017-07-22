package br.com.unopay.api.payment.cnab240;

import br.com.unopay.api.payment.cnab240.filler.RecordColumnRule;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class LayoutExtractorSelector {

    public RemittanceExtractor define(Map<String, RecordColumnRule> layout, String cnab240){
        return new RemittanceExtractor(layout, cnab240);
    }
}
