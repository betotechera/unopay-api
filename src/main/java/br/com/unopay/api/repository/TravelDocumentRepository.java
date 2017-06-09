package br.com.unopay.api.repository;

import br.com.unopay.api.model.TravelDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface TravelDocumentRepository extends CrudRepository<TravelDocument, String> {

    List<TravelDocument> findAll();

    Optional<TravelDocument> findById(String id);
}
