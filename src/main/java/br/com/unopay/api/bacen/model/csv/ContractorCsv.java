package br.com.unopay.api.bacen.model.csv;

import br.com.unopay.api.model.Address;
import br.com.unopay.api.model.Document;
import br.com.unopay.api.model.DocumentType;
import br.com.unopay.api.model.Gender;
import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.PersonType;
import br.com.unopay.api.model.PhysicalPersonDetail;
import br.com.unopay.api.model.State;
import br.com.unopay.bootcommons.exception.BadRequestException;
import br.com.unopay.bootcommons.exception.UnovationError;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class ContractorCsv {

    @NotNull
    @Size(max = 14)
    @CsvBindByName
    private String document;

    @NotNull
    @Size(max = 100)
    @CsvBindByName
    private String email;

    @NotNull
    @Size(max = 20)
    @CsvBindByName
    private String shortName;

    @NotNull
    @Size(max = 200)
    @CsvBindByName
    private String fullName;

    @NotNull
    @CsvDate("dd/MM/yyyy")
    @CsvBindByName
    private Date birthDate;

    @NotNull
    @CsvBindByName
    @Pattern(message = "invalid gender (F|M)", regexp = "^(F|M)")
    private String gender;

    @NotNull
    @CsvBindByName
    @Pattern(regexp = "\\d{11}")
    private String cellPhone;

    @CsvBindByName
    @Pattern(regexp = "^[-() 0-9]+$")
    private String telephone;

    @NotNull
    @CsvBindByName
    @Pattern(regexp = "\\d{8}", message = "invalid zipCode!")
    private String zipCode;

    @NotNull
    @CsvBindByName
    @Size(max = 200)
    private String streetName;

    @NotNull
    @CsvBindByName
    @Size(max = 20)
    private String number;

    @CsvBindByName
    @Size(max = 100)
    private String complement;

    @NotNull
    @CsvBindByName
    @Size(max = 100)
    private String district;

    @NotNull
    @CsvBindByName
    @Size(max = 100)
    private String city;

    public Person toPerson(){
        Person person = new Person();
        Document personDocument = new Document();
        personDocument.setNumber(document);
        personDocument.setType(DocumentType.CPF);
        PhysicalPersonDetail physicalPersonDetail = new PhysicalPersonDetail();
        physicalPersonDetail.setBirthDate(birthDate);
        physicalPersonDetail.setEmail(email);
        physicalPersonDetail.setGender(Gender.fromPt(gender));
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

    @NotNull
    @CsvBindByName
    @Pattern(regexp = "\\w{2}", message = "invalid state!")
    private String state;

    @NotNull
    @CsvBindByName
    @Size(max = 4)
    private String product;

    @CsvBindByName
    private Boolean createUser = true;

    public void validate(Validator validator, Integer line) {
        Set<ConstraintViolation<ContractorCsv>> constraintViolations = validator.validate(this);
        if(!constraintViolations.isEmpty()){
            BadRequestException badRequestException = new BadRequestException();
            List<UnovationError> errors = constraintViolations.stream()
                    .map(constraint ->
                            new UnovationError(constraint.getPropertyPath().toString(),
                                    constraint.getMessage()).withOnlyArgument(String.format("linha: %s valor: %s", line, constraint.getInvalidValue())))
                    .collect(Collectors.toList());
            throw badRequestException.withErrors(errors);
        }
    }
}
