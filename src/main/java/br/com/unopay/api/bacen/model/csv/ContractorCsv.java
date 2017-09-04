package br.com.unopay.api.bacen.model.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.util.Date;
import lombok.Data;

@Data
public class ContractorCsv {

    @CsvBindByName
    private String name;

    @CsvBindByName
    private String shortName;

    @CsvBindByName
    private String document;

    @CsvBindByName
    private String email;

    @CsvDate("dd/MM/yyyy")
    @CsvBindByName
    private Date birthDate;

    @CsvBindByName
    private String gender;

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
    private String telephone;

    @CsvBindByName
    private String cellPhone;
}
