package br.com.unopay.api.bacen.model.csv;

import br.com.unopay.api.bacen.model.AuthorizedMember;
import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.DocumentType;
import br.com.unopay.api.model.Gender;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import lombok.Data;

import java.util.Date;

@Data
public class AuthorizedMemberCsv {

    @CsvDate("dd/MM/yyyy")
    @CsvBindByName
    private Date birthDate;

    @CsvBindByName(column = "contract")
    private Long contractCode;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private String gender;

    @CsvBindByName
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
        authorizedMember.setRelatedness(relatedness);
        authorizedMember.setEmail(email);
        defineGender(authorizedMember);
        return authorizedMember;
    }

    private void defineGender(AuthorizedMember authorizedMember) {
        if(gender.equals(Gender.FEMALE.getDescription())) {
            authorizedMember.setGender(Gender.FEMALE);
        }

        if(gender.equals(Gender.MALE.getDescription())) {
            authorizedMember.setGender(Gender.MALE);
        }
    }

    public boolean withInstrumentNumber() {
        return paymentInstrumentNumber != null;
    }
}
