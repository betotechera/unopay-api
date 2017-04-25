package br.com.unopay.api.controller;

import br.com.unopay.api.model.Person;
import br.com.unopay.api.model.filter.PersonFilter;
import br.com.unopay.api.service.PersonService;
import br.com.unopay.api.uaa.model.validationsgroups.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // NOSONAR

@Slf4j
@RestController
public class PersonController {

    @Autowired
    PersonService personService;

    @JsonView(Views.Public.class)
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/persons", method = RequestMethod.GET)
    public ResponseEntity<Person> findPerson( PersonFilter filter) {
        log.info("find Person  with filter={}", filter);
        Person person = personService.findByDocument(filter);
        return ResponseEntity.ok(person);
    }

}
