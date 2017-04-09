package br.com.unopay.api.repository;

import br.com.unopay.api.model.Contact;
import org.springframework.data.repository.CrudRepository;

public interface ContactRepository extends CrudRepository<Contact, String>{
}
