package br.com.unopay.api.service;

import br.com.unopay.api.model.Contact;
import br.com.unopay.api.repository.ContactRepository;
import br.com.unopay.bootcommons.exception.UnovationExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static br.com.unopay.api.uaa.exception.Errors.CONTACT_NOT_FOUND;

@Service
public class ContactService {

    @Autowired
    private ContactRepository repository;

    public Contact create(Contact contact){
        return repository.save(contact);
    }

    public Contact findById(String id){
        Contact contact = repository.findOne(id);
        if(contact == null) throw UnovationExceptions.notFound().withErrors(CONTACT_NOT_FOUND);
        return contact;
    }
}