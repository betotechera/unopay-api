package br.com.unopay.api.wingoo.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.wingoo.model.Password;
import br.com.unopay.api.wingoo.model.Student;
import java.util.HashMap;
import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Data
@Service
public class WingooService {

    public static final String INSERT_STUDENT = "/inserirAlunos";
    public static final String APPLICATION = "Application";
    public static final String ACCOUNT_UPDATE_PASSWORD = "/account/updatePassword";
    @Autowired
    @Qualifier("wingooRestTemplate")
    private RestTemplate wingooTemplate;

    @Value("${client.wingoo.application}")
    private String appId;

    @Value("${client.wingoo.api}")
    private String wingooApi;

    private HttpHeaders headers = new HttpHeaders();


    @PostConstruct
    public void setup(){
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(APPLICATION, appId);
    }

    public Student create(Contractor contractor){
        HttpEntity<Student> entity = new HttpEntity<>(Student.fromContractor(contractor), headers);
        return wingooTemplate.postForObject(wingooApi + INSERT_STUDENT, entity, Student.class, new HashMap<>());
    }

    public void update(Password password){
        HttpEntity<Password> entity = new HttpEntity<>(password, headers);
        wingooTemplate.put(wingooApi + ACCOUNT_UPDATE_PASSWORD, entity, Password.class, new HashMap<>());
    }
}
