package br.com.unopay.api.wingoo.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.wingoo.userclient.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class WingooUserMapping implements Serializable{

    public static User fromContractor(Contractor contractor, String instrumentNumber){
        User user = new User();
        user.setName(contractor.getPerson().getShortName());
        user.setLastName(contractor.getPerson().getName());
        user.setEmail(contractor.getPerson().getPhysicalPersonDetail().getEmail());
        user.setBirthDate(contractor.getPerson().getPhysicalPersonDetail().getBirthDate());
        user.setDocumentNumber(contractor.getPerson().getDocument().getNumber());
        user.setCellphone(contractor.getPerson().getCellPhone());
        user.setGender(contractor.getPerson().getPhysicalPersonDetail().getGender().name());
        user.setZipCode(contractor.getPerson().getAddress().getZipCode());
        user.setStudentId(instrumentNumber);
        return user;
    }
}
