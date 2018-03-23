package br.com.unopay.api.bacen.model.csv;

import br.com.unopay.api.bacen.model.AuthorizedMember;
import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.DocumentType;
import br.com.unopay.api.model.Gender;
import br.com.unopay.api.model.Relatedness;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.util.Date;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AuthorizedMemberCsv {

    @CsvDate("dd/MM/yyyy")
    @CsvBindByName
    private Date birthDate;

    @CsvBindByName
    private String name;

    @CsvBindByName
    @Pattern(message = "invalid gender (F|M)", regexp = "^(?i)(f|m)")
    private String gender;

    @CsvBindByName
    @Pattern(message = "invalid relatedness (F|M)", regexp = "^(?i)(av|ti|m|pai|irm|filh|sobrinh)")
    private String relatedness;

    @CsvBindByName
    private String email;

    @CsvBindByName
    private String documentNumber;

    @CsvBindByName(column = "instrumentNumber")
    private String paymentInstrumentNumber;

    @CsvBindByName(column = "contractor")
    private String contractorDocumentNumber;

    @CsvBindByName(column = "hirer")
    private String hirerDocumentNumber;

    @CsvBindByName(column = "product")
    private String productCode;

    public AuthorizedMember toAuthorizedMember () {
        Document authorizedMemberDocument = new Document();
        authorizedMemberDocument.setNumber(documentNumber);
        authorizedMemberDocument.setType(DocumentType.CPF);
        AuthorizedMember authorizedMember = new AuthorizedMember();
        authorizedMember.setDocument(authorizedMemberDocument);
        authorizedMember.setBirthDate(birthDate);
        authorizedMember.setName(name);
        authorizedMember.setRelatedness(Relatedness.fromPt(relatedness));
        authorizedMember.setEmail(email);
        authorizedMember.setGender(Gender.fromPt(gender));
        return authorizedMember;
    }

    public boolean withInstrumentNumber() {
        return paymentInstrumentNumber != null;
    }
}
