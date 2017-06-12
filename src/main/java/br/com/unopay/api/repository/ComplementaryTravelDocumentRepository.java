package br.com.unopay.api.repository;

import br.com.unopay.api.model.ComplementaryTravelDocument;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ComplementaryTravelDocumentRepository  extends CrudRepository<ComplementaryTravelDocument, String> {

    List<ComplementaryTravelDocument> findAll();
}
