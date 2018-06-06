package br.com.unopay.api.wingoo.service;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.wingoo.model.Password;
import br.com.unopay.api.wingoo.model.Student;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static org.springframework.http.HttpMethod.POST;

@Data
@Service
public class WingooService {

    public static final String INSERT_STUDENT = "/inserirAlunos";
    public static final String APPLICATION = "Application";
    public static final String ACCOUNT_UPDATE_PASSWORD = "/alterarSenha";
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

    public List<Student> create(Contractor contractor){
        ParameterizedTypeReference<List<Student>> typeReference = new ParameterizedTypeReference<List<Student>>() {};
        HttpEntity<List<Student>> entity = new HttpEntity<>(Arrays.asList(Student.fromContractor(contractor)), headers);
        String uri = wingooApi + INSERT_STUDENT;
        ResponseEntity<List<Student>> exchange = wingooTemplate.exchange(uri, POST, entity, typeReference, new HashMap<>());
        return exchange.getBody();
    }

    public void update(Password password){
        HttpEntity<Password> entity = new HttpEntity<>(password, headers);
        wingooTemplate.put(wingooApi + ACCOUNT_UPDATE_PASSWORD, entity, Password.class, new HashMap<>());
    }
}
