package br.com.unopay.api.bacen.model.filter;

import br.com.unopay.bootcommons.repository.filter.SearchableField;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

@Data
public class AuthorizedMemberFilter implements Serializable{

    public static final Long serialVersionUID = 1L;

    @SearchableField
    private String name;

    @SearchableField
    private String email;

    @SearchableField(field = "document.number")
    private String documentNumber;

    @SearchableField(field = "contract.hirer.id")
    private String hirerId;

    @SearchableField(field = "contract.contractor.id")
    private String contractorId;

    @SearchableField(field = "paymentInstrument.number")
    private String paymentInstrumentNumber;

    @SearchableField
    private Date birthDate;
}
