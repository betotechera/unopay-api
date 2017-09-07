package br.com.unopay.api.billing.remittance.model.filter;

import java.util.Date;
import lombok.Data;

@Data
public class RemittanceFilter {

    private String id;

    private Date at = new Date();

}
