package br.com.unopay.api.wingoo.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.uaa.config.PasswordEncoderConfig;
import br.com.wingoo.userclient.model.User;
import java.io.Serializable;
import lombok.Data;

@Data
public class WingooUserMapping implements Serializable{

    public static final String TYPE = "Aluno";
    public static final int STATUS = 3;
    public static final String FORMAT = "%s-%s";

    public static User fromContractor(Contractor contractor){
        User user = new User();
        user.setName(contractor.getPerson().getShortName());
        user.setLastName(contractor.getPerson().getName());
        user.setEmail(contractor.getPerson().getPhysicalPersonDetail().getEmail());
        user.setBirthDate(contractor.getPerson().getPhysicalPersonDetail().getBirthDate());
        user.setDocumentNumber(contractor.getDocumentNumber());
        user.setCellphone(contractor.getPerson().getCellPhone());
        user.setGender(contractor.getPerson().getPhysicalPersonDetail().getGender().getDescription());
        String originalZipCode = contractor.getPerson().getAddress().getZipCode();
        user.setZipCode(String.format(FORMAT, originalZipCode.substring(0, 5), originalZipCode.substring(5)));
        user.setStudentId(contractor.getPaymentInstrumentNumber());
        user.setStatus(STATUS);
        user.setType(TYPE);
        user.setEncryptedPassword(new PasswordEncoderConfig().passwordEncoder().encode(contractor.getPassword()));
        user.setHirerDocument(contractor.getHirerDocument());
        return user;
    }
}
