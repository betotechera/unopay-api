package br.com.unopay.api.wingoo.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.uaa.config.PasswordEncoderConfig;
import br.com.wingoo.userclient.model.User;
import java.io.Serializable;
import lombok.Data;

@Data
public class WingooUserMapping implements Serializable{

    public static final String FORMAT = "%s-%s";

    public static User fromContractor(Contractor contractor){
        User user = new User();
        user.setName(contractor.getPerson().getShortName());
        user.setLastName(contractor.getPerson().getName().replaceAll(contractor.getPerson().getShortName(), "").trim());
        user.setEmail(contractor.getPerson().getPhysicalPersonDetail().getEmail());
        user.setBirthDate(contractor.getPerson().getPhysicalPersonDetail().getBirthDate());
        user.setDocumentNumber(contractor.getDocumentNumber());
        user.setCellphone(contractor.getPerson().getCellPhone());
        if(contractor.getPerson().getPhysicalPersonDetail().getGender() != null) {
            user.setGender(contractor.getPerson().getPhysicalPersonDetail().getGender().getDescription());
        }
        Address address = contractor.getPerson().getAddress();
        String originalZipCode = address.getZipCode();
        user.setZipCode(String.format(FORMAT, originalZipCode.substring(0, 5), originalZipCode.substring(5)));
        user.setStreet(address.getStreetName());
        user.setNumber(address.getNumber());
        user.setComplement(address.getComplement());
        user.setDistrict(address.getDistrict());
        user.setCity(address.getCity());
        user.setState(address.getState().name());
        user.setStudentId(contractor.getPaymentInstrumentNumber());
        if(contractor.getPassword() != null) {
            user.setEncryptedPassword(new PasswordEncoderConfig().passwordEncoder().encode(contractor.getPassword()));
        }
        user.setHirerDocument(contractor.getHirerDocument());
        return user;
    }
}
