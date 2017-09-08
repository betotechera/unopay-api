package br.com.unopay.api.credit.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreditProcessed {

    private String document;
    private BigDecimal value;
    private CreditInsertionType insertionType;
    private CreditTarget target;

    public boolean forHirer(){
        return CreditTarget.HIRER.equals(target);
    }
}
