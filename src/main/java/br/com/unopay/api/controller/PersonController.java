package br.com.unopay.api.controller;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.filter.PersonFilter;
import br.com.unopay.api.model.validation.group.Views;
import br.com.unopay.api.service.PersonService;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PersonController {

    @Autowired
    PersonService personService;

    @JsonView(Views.Person.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/persons", method = RequestMethod.GET)
    public ResponseEntity<Person> findPerson( PersonFilter filter) {
        log.info("find Person  with filter={}", filter);
        Person person = personService.findByDocument(filter);
        return ResponseEntity.ok(person);
    }

}
