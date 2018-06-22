package br.com.unopay.api.wingoo.model;

import br.com.unopay.api.bacen.model.Contractor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class Student implements Serializable{

    private static final long serialVersionUID = 2847458826624619595L;
    public static final String DEFAULT_PWD = "123456";

    @JsonProperty("nome")
    private String name;

    @JsonProperty("sobrenome")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("data_nascimento")
    private Date brithDate;

    @JsonProperty("cpf")
    private String cpf;

    @JsonProperty("ra")
    private String ra;

    @JsonProperty("celular")
    private String cellphone;

    @JsonProperty("nome_mae")
    private String motherName;

    @JsonProperty("sexo")
    private String gender;

    @JsonProperty("cep")
    private String zipCode;

    @JsonProperty("senha")
    private String password = DEFAULT_PWD;

    public static Student fromContractor(Contractor contractor, String instrumentNumber){
        Student student = new Student();
        student.name = contractor.getPerson().getShortName();
        student.fullName = contractor.getPerson().getName();
        student.email = contractor.getPerson().getPhysicalPersonDetail().getEmail();
        student.brithDate = contractor.getPerson().getPhysicalPersonDetail().getBirthDate();
        student.cpf = contractor.getPerson().getDocument().getNumber();
        student.cellphone = contractor.getPerson().getCellPhone();
        student.gender = contractor.getPerson().getPhysicalPersonDetail().getGender().name();
        student.zipCode = contractor.getPerson().getAddress().getZipCode();
        student.ra = instrumentNumber;
        return student;
    }
}
