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

    @SearchableField(field = "contract.hirer.person.document.number")
    private String hirerDocumentNumber;

    @SearchableField(field = "contract.contractor.person.document.number")
    private String contractorDocumentNumber;

    @SearchableField
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private Date birthDate;
}
