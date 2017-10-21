package br.com.unopay.api.bacen.model.csv;

import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.DocumentType;
import br.com.unopay.api.model.Gender;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonType;
import br.com.unopay.api.model.PhysicalPersonDetail;
import br.com.unopay.api.model.State;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.util.Date;
import lombok.Data;

@Data
public class ContractorCsv {

    @CsvBindByName
    private String document;

    @CsvBindByName
    private String email;

    @CsvBindByName
    private String shortName;

    @CsvBindByName
    private String fullName;

    @CsvDate("dd/MM/yyyy")
    @CsvBindByName
    private Date birthDate;

    @CsvBindByName
    private String gender;

    @CsvBindByName
    private String cellPhone;

    @CsvBindByName
    private String telephone;

    @CsvBindByName
    private String zipCode;

    @CsvBindByName
    private String streetName;

    @CsvBindByName
    private String number;

    @CsvBindByName
    private String complement;

    @CsvBindByName
    private String district;

    @CsvBindByName
    private String city;

    @CsvBindByName
    private String state;

    @CsvBindByName
    private String product;

    public Person toPerson(){
        Person person = new Person();
        Document personDocument = new Document();
        personDocument.setNumber(document);
        personDocument.setType(DocumentType.CPF);
        PhysicalPersonDetail physicalPersonDetail = new PhysicalPersonDetail();
        physicalPersonDetail.setBirthDate(birthDate);
        physicalPersonDetail.setEmail(email);
        physicalPersonDetail.setGender(Gender.valueOf(gender));
        person.setPhysicalPersonDetail(physicalPersonDetail);
        person.setDocument(personDocument);
        person.setType(PersonType.PHYSICAL);
        person.setName(fullName);
        person.setShortName(shortName);
        person.setCellPhone(cellPhone);
        person.setTelephone(telephone);
        Address address = new Address();
        address.setZipCode(zipCode);
        address.setStreetName(streetName);
        address.setNumber(number);
        address.setComplement(complement);
        address.setDistrict(district);
        address.setCity(city);
        address.setState(State.valueOf(state));
        person.setAddress(address);
        return person;
    }
}
