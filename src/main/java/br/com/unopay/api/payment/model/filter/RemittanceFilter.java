package br.com.unopay.api.payment.model.filter;

import java.util.Date;
import lombok.Data;

@Data
public class RemittanceFilter {

    private String id;
    private Date at;
}