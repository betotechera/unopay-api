package br.com.unopay.api.wingoo.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.market.model.ContractorProduct;
import br.com.unopay.api.model.Address;
import br.com.unopay.api.uaa.config.PasswordEncoderConfig;
import br.com.wingoo.userclient.model.User;
import java.io.IOException;
import java.io.Serializable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

@Slf4j
@Data
public class WingooUserMapping implements Serializable{

    public static final String FORMAT = "%s-%s";

    public static WingooPaymentInfo fromContractor(ContractorProduct contractorProduct){
        Contractor contractor = contractorProduct.getContractor();
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
        try {
            log.info("Sending a user to the Wingoo system");
            String userAsString = new ObjectMapper().writeValueAsString(user);
            log.info(userAsString);
        } catch (IOException e) {
            log.warn("Error when trying to serializer the wingoo user model {}", e.getMessage());
        }
        return new WingooPaymentInfo(user, new WingooProductInformation(contractorProduct.getProduct()));
    }
}
