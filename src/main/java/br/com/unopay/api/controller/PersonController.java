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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
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
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/persons", method = RequestMethod.GET)
    public ResponseEntity<Person> findPerson(PersonFilter filter) {
        log.info("find Person  with filter={}", filter);
        Person person = personService.findByFilter(filter);
        return ResponseEntity.ok(person);
    }

    @JsonView(Views.Person.Detail.class)
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("isAuthenticated()")
    @RequestMapping(value = "/persons/{documentNumber}", method = RequestMethod.GET)
    public ResponseEntity<Person> findPersonByDocument(@PathVariable String documentNumber) {
        log.info("find Person  by document={}", documentNumber);
        Person person = personService.findByDocument(documentNumber);
        return ResponseEntity.ok(person);
    }

}
