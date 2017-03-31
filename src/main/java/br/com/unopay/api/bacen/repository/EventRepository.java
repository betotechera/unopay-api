package br.com.unopay.api.bacen.repository;

import br.com.unopay.api.bacen.model.Event;
import org.springframework.data.repository.CrudRepository;

public interface EventRepository extends CrudRepository<Event,String>{
}
