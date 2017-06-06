package br.com.unopay.api.repository;

import br.com.unopay.api.model.TravelDocument;
import org.springframework.data.repository.CrudRepository;

public interface TravelDocumentRepository extends CrudRepository<TravelDocument, String> {
}
